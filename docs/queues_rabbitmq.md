# Organisation des queues RabbitMQ

## �changes et queues RabbitMQ

### Flux principal

Le diagramme ci-dessous r�sume la configuration RabbitMQ utilis�e pour le flux principal.
Par "flux principal", on entend les �changes de messages � l'initiative d'un SI m�tier.
Exemples :
un message de cr�ation d'une d�marche,
un message d'ajout d'un document � une d�marche existante.

![](./images/queues_rabbitmq_1.jpg)

Avoir une seule queue `all-to-enu-main-q` pour tous les messages est pr�cieux :
tant la programmation de `enu-mediation` que sa configuration sont simplifi�s.
En particulier, l'ajout d'une nouvelle prestation ou d'un nouveau SI m�tier ne n�cessite
aucun d�veloppement dans `enu-mediation`, tout se jouant dans les param�tres de d�ploiement.

### Flux inverse

Le diagramme ci-dessous r�sume la configuration RabbitMQ utilis�e pour le flux inverse.
Par "flux inverse", on entend les �changes de messages � l'initiative de l'ENU.
Exemples :
un message de notification de destruction par l'usager d'un brouillon de d�marche,
un message de notification de la consultation d'un document par l'usager.

![](./images/queues_rabbitmq_2.jpg)

L'application `backend-end`, qui grosso modo est FormServices, ne conna�t pas la notion
de SI m�tier : en tant que serveur de formulaire, elle ne conna�t que les prestations.
Il en d�coule un traitement en deux phases, alors que le flux principal ne n�cessite
qu'une seule phase :
- phase 1 : envoi du message de `enu-backend` � RabbitMQ
- phase 2 : consommation du message par `enu-mediation` qui, en mode passe-plat, renvoie
le message inchang� vers la queue `enu-to-all-main`, mais avec une clef de routage
d�signant le SI m�tier cible.
  
Ce flux est tr�s semblable � la partie "r�ponse" du flux principal ;
on aurait m�me pu simplement en r�utiser les �changes et les queues.
Cependant, cr�er un jeu s�par� d'�changes et de queues permet au SI m�tier d'avoir deux
queues, aux propos distincts, c'est-�-dire ne m�lant pas les notifications de `enu-backend`
aux r�ponses de `enu-mediation`.

La communication entre `enu-backend` et `enu-mediation` aurait pu �viter RabbitMQ et se faire
de fa�on directe, via des appels de services REST.
Cependant l'utilisation de RabbitMQ permet � `enu-backend` d'�tre s�re du traitement du message,
m�me si `enu-mediation` est arr�t�e ;
sans cela, il aurait fallu trouver un moyen de mettre les messages en attente du c�t� de
`enu-backend`.

Pour l'instant, on ne consid�re pas de messages de r�ponse, ni de bo�te aux lettres morte,
dans le flux inverse.

## S�curit�

### Confidentialit�

La confidentialit� des �changes de messages est un aspect important de la configuration :
il faut par exemple garantir que les donn�es fiscales des citoyens int�gr�es dans
des messages produits par un SI de l'administration fiscale ne puissent jamais
�tre vues par un autre SI m�tier.
Cette confidentialit� est obtenue en cr�ant un �change RabbitMQ pour chaque SI m�tier,
avec acc�s gard� par TLS (SSL).
Chaque SI m�tier ne peut donc voir que "son" �change RabbitMQ. 

### Droits d'acc�s

L'application `enu-mediation`, consommatrice de la plupart des messages RabbitMQ,
doit garantir que tout message provient bien du SI m�tier attendu.
Or le cloisonnement des �changes RabbitMQ et l'utilisation de TLS ne garantissent
pas cette authenticit�.
En effet, rien n'emp�che � ce stade un SI m�tier d'envoyer dans son propre �change RabbitMQ
un message dont le champ `idPrestation` indique une prestation d'un autre
SI m�tier ;
par exemple, un SI m�tier de la sant� pourrait produire un message de taxation d'imp�ts.
Pour �viter ce genre d'incident, on a muni l'application `enu-mediation` d'une liste
des prestations possibles pour chaque SI m�tier.
Lors de la consommation d'un message, l'application `enu-mediation` confronte
la valeur de `idPrestation` du message avec cette liste et, le cas �ch�ant, �met une r�ponse
d'erreur.
