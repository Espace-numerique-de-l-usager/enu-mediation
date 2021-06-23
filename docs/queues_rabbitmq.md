# Organisation des queues RabbitMQ

## Échanges et queues RabbitMQ

### Flux principal

Le diagramme ci-dessous résume la configuration RabbitMQ utilisée pour le flux principal.
Par "flux principal", on entend les échanges de messages à l'initiative d'un SI métier.
Exemples :
un message de création d'une démarche,
un message d'ajout d'un document à une démarche existante.

![](./images/queues_rabbitmq_1.jpg)

Avoir une seule queue `all-to-enu-main-q` pour tous les messages est précieux :
tant la programmation de `enu-mediation` que sa configuration sont simplifiées.
En particulier, l'ajout d'une nouvelle prestation ou d'un nouveau SI métier ne nécessite
aucun développement dans `enu-mediation`, tout se jouant dans les paramètres de déploiement.

### Flux inverse

Le diagramme ci-dessous résume la configuration RabbitMQ utilisée pour le flux inverse.
Par "flux inverse", on entend les échanges de messages à l'initiative de l'ENU.
Exemples :
un message de notification de destruction par l'usager d'un brouillon de démarche,
un message de notification de la consultation d'un document par l'usager.

![](./images/queues_rabbitmq_2.jpg)

L'application `backend-end`, qui grosso modo est FormServices, ne connaît pas la notion
de SI métier : en tant que serveur de formulaire, elle ne connaît que les prestations.
Il en découle un traitement en deux phases, alors que le flux principal ne nécessite
qu'une seule phase :
- phase 1 : envoi du message de `enu-backend` à RabbitMQ
- phase 2 : consommation du message par `enu-mediation` qui, en mode passe-plat, renvoie
le message inchangé vers la queue `enu-to-all-main`, mais avec une clef de routage
désignant le SI métier cible.
  
Ce flux est très semblable à la partie "réponse" du flux principal ;
on aurait même pu simplement en réutiser les échanges et les queues.
Cependant, créer un jeu séparé d'échanges et de queues permet au SI métier d'avoir deux
queues, aux propos distincts, c'est-à-dire ne mêlant pas les notifications de `enu-backend`
aux réponses de `enu-mediation`.

La communication entre `enu-backend` et `enu-mediation` aurait pu éviter RabbitMQ et se faire
de façon directe, via des appels de services REST.
Cependant l'utilisation de RabbitMQ permet à `enu-backend` d'être sûre du traitement du message,
même si `enu-mediation` est arrêtée ;
sans cela, il aurait fallu trouver un moyen de mettre les messages en attente du côté de
`enu-backend`.

Pour l'instant, on ne considère pas de messages de réponse, ni de boîte aux lettres morte,
dans le flux inverse.

### Flux principal et flux inverse : boîte aux lettres morte

La boîte aux lettres morte est une queue dans laquelle on poste des messages dont le traitement
a échoué, sans espoir immédiat de résoudre l'erreur.

Le projet se prête à la configuration de boîtes mortes pour traiter plusieurs situations :

| Problème rencontré par | Nature du problème | Queue d'où le message a été extrait | Boîte morte (DLQ) |
| ---------------------- | ------------------ | ----------------------------------- | ----------------- |
| `enu-mediation` | erreur technique lors du traitement d'un message d'un SI métier (flux principal). Note : les erreurs métiers sont envoyées dans une autre queue - la queue de réponse | `all-to-enu-main-q` | `enu-dlq-q` |
| `enu-mediation` | erreur lors du traitement d'un message de `enu-backend` destiné à un SI métier (flux inverse) | `backend-to-mediation-q` | `enu-dlq-q` |
| SI métier | erreur lors du traitement d'un message de réponse reçu de `enu-mediation` (flux principal) | `enu-to-simetier-X-reply-q` | aucune (cas pas traité) |
| SI métier | erreur lors du traitement d'un message de notification reçu de `enu-mediation` (flux inverse) | `enu-to-simetier-X-main-q` | aucune (cas pas traité) |

Le diagramme suivant représente la configuration pour la première situation :

![](./images/queues_rabbitmq_3.jpg)

Note : un traitement fréquent des messages contenus dans une boîte morte est de reverser en vrac les messages
dans leur queue d'origine.
Il semblerait donc préférable de définir une boîte morte par queue d'origine.
La configuration actuelle avec une seule boîte morte n'est sans doute pas idéale : il en faudrait deux.

## Sécurité

### Confidentialité

La confidentialité des échanges de messages est un aspect important de la configuration :
il faut par exemple garantir que les données fiscales des citoyens intégrées dans
des messages produits par un SI de l'administration fiscale ne puissent jamais
être vues par un autre SI métier.
Cette confidentialité est obtenue en créant, pour chaque SI métier,
un utilisateur RabbitMQ, 
puis un échange RabbitMQ gardé par le mot de passe de cet utilisateur.
Chaque SI métier ne peut donc voir que "son" échange RabbitMQ.

La communication se fait sous TLS (SSL).

### Droits d'accès

L'application `enu-mediation`, consommatrice de la plupart des messages RabbitMQ,
doit garantir que tout message provient bien du SI métier attendu.
Or le cloisonnement des échanges RabbitMQ et l'utilisation de TLS ne garantissent
pas cette authenticité.
En effet, rien n'empêche à ce stade un SI métier d'envoyer dans son propre échange RabbitMQ
un message dont le champ `idPrestation` indique une prestation d'un autre
SI métier ;
par exemple, un SI métier de la santé pourrait produire un message de taxation d'impôts.
Pour éviter ce genre d'incident, on a muni l'application `enu-mediation` d'une liste
des prestations possibles pour chaque SI métier.
Lors de la consommation d'un message, l'application `enu-mediation` confronte
la valeur de `idPrestation` du message avec cette liste et, le cas échéant, émet une réponse
d'erreur.
