# Organisation des queues RabbitMQ

## �changes et queues RabbitMQ

Le diagramme ci-dessous r�sume la configuration utilis�es pour tous les messages.

![](./images/queues_rabbitmq.jpg)

## S�curit�

### Confidentialit�

La confidentialit� des �changes de messages est un aspect important de la configuration :
il faut par exemple garantir que les donn�es fiscales des citoyens int�gr�es dans
des messages produits par un SI de l'administration fiscale ne puissent jamais
�tre vues par un autre SI m�tier.
Cette confidentialit� est obtenue en cr�ant un �change RabbitMQ par SI m�tier,
avec acc�s gard� par TLS (SSL).
Chaque SI m�tier ne peut donc voir que "son" �change RabbitMQ. 

### Authenticit�

L'application `enu-mediation`, consommatrice de la plupart des messages RabbitMQ,
doit garantir que tout message provient bien du SI m�tier attendu.
Or le cloisonnement des �changes RabbitMQ et l'utilisation de TLS ne garantissent
pas cette authenticit�.
En effet, rien n'emp�che � ce stade un SI m�tier d'envoyer dans son �change RabbitMQ
un message dont le champ `idPrestation` indique une prestation d'un autre
SI m�tier.
Par exemple, un SI m�tier de la sant� pourrait produire un message de taxation d'imp�ts.
Pour �viter ce genre d'incident, l'application `enu-mediation` poss�de une liste
des prestations possibles pour chaque SI m�tier.
Lors de la consommation d'un message, l'application `enu-mediation` confronte
la valeur de `idPrestation` du message avec cette liste.
