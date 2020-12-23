# Espace numérique de l'usager : médiation

Ce projet définit les médiations pour le système Espace numérique de l'usager :

SI métier <---> RabbitMQ <---> médiation <---> FormServices

## Présentation

Une vue d'ensemble du système Espace numérique de l'usager est disponible
[ici](https://github.com/Espace-numerique-de-l-usager/enu-geneve).


## Exécution

## Configuration d'un nouveau service métier

### Travail à mener par l'équipe médiation

### Travail à mener par l'équipe SI métier

## Production et consommation de messages

Du point de vue d'un SI métier, l'interaction avec l'Espace numérique de l'usager se fait
uniquement avec RabbitMQ,
au moyen de la production et de la consommation de messages JSON via le protocole AMQP.

### Échanges du SI métier avec enu-mediation

Le tableau ci-dessous fournit la liste des messages échangés, pour un SI métier dont le code (fourni par l'équipe
médiation) est "SI1".

| Message | Producteur | Consommateur | Exchange RabbitMQ / queue RabbitMQ |
| ------- | ---------- | ------------ | ---------------------------------- |
| création d'une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| changement d'état d'une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| ajout d'un document à une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| message d'erreur | enu-mediation | SI métier | si1-to-enu / si1-to-enu-reply |

Pour tous ces messages, l'exchange RabbitMQ aura pour nom 

#### Message JSON de création d'une démarche

En-tête nécessaire : `ContentType` = `application/json-new-demarche`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_SOCIAL_INDICATEL | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager propriétaire de la démarche | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idClientDemande | identifiant de la démarche dans le SI métier | oui | AEL-100000 | Pour une prestation donnée et pour un usager donné, doit doit être unique |
| etat | état de la démarche | oui | BROUILLON | Doit valoir soit BROUILLON, soit DEPOSEE, soit EN_TRAITEMENT |
| libelleAction | description de l'opération proposée à l'usager sur la démarche | non | Compléter votre démarche | - |
| urlAction | URL de l'opération proposée à l'usager sur la démarche | oui, si libelleAction est fourni, sinon inutile | `http://etc...` | - |
| dateEcheanceAction | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui, si libelleAction est fourni, sinon inutile | 2021-02-18 | La date uniquement, sans les heures |

Exemple : voir [newdemarche/MessageSender](https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/newdemarche/MessageSender.java).

(TODO : lien à mettre à jour lors du passage à GitHub)

#### Message JSON de changement d'état d'une démarche

Il s'agit ici de changer l'état d'une démarche qui a été précédemment créée via un
message comme ci-dessus.

En-tête nécessaire : `Content-Type` = `application/json-status-change`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_SOCIAL_INDICATEL | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager propriétaire de la démarche | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idClientDemande | identifiant de la démarche dans le SI métier | oui | AEL-100000 | Doit doit être unique, pour une prestation donnée et pour un usager donné |
| nouvelEtat | nouvel état de la démarche | oui | DEPOSEE | Doit valoir soit DEPOSEE, soit EN_TRAITEMENT, soit TERMINEE |
| dateNouvelEtat | date à laquelle la démarche a changé d'état| oui | 2020-02-19 | - |

Champs supplémentaires si `nouvelEtat` = `DEPOSEE` ou si `nouvelEtat` = `EN_TRAITEMENT` :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| typeAction | type de l'opération proposée à l'usager sur la démarche | oui | ENRICHISSEMENT_DE_DEMANDE | Doit valoir soit ENRICHISSEMENT_DE_DEMANDE, soit REPONSE_DEMANDE_RENSEIGNEMENT | 
| libelleAction | description de l'opération proposée à l'usager sur la démarche | oui, si typeAction est fourni, sinon inutile | Compléter votre démarche | - |
| urlAction | URL de l'opération proposée à l'usager sur la démarche | oui, si typeAction est fourni, sinon inutile | `http://etc...` | - |
| echeanceAction | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui, si typeAction est fourni, sinon inutile | 2021-02-18 | La date uniquement, sans les heures |

Champs supplémentaires si `nouvelEtat` = `TERMINEE` :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| urlRenouvellementDemarche | URL à présenter à l'usager pour qu'il recrée une démarche du même type | oui | `http://etc...` | - |

Exemples : voir [statuschange/MessageSender](https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/statuschange/MessageSender.java).

(TODO : lien à mettre à jour lors du passage à GitHub)

#### Message JSON d'ajout d'un document à une démarche

Il s'agit ici de compléter une démarche qui a été précédemment créée.

En-tête nécessaire : `Content-Type` = `application/json-document`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_SOCIAL_INDICATEL | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager propriétaire de la démarche | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idClientDemande | identifiant de la démarche dans le SI métier | oui | AEL-100000 | Doit doit être unique, pour une prestation donnée et pour un usager donné |
| typeDocument | type de document | oui | RECAPITULATIF | Doit valoir soit RECAPITULATIF, soit JUSTIFICATIF |
| libelleDocument | titre du document, déterminant le nom du fichier | oui | Decision administration 2020-02-19 | Maximum 50 caractères |
| idClientDocument | identifiant permettant au SI métier d'identifier son document | non | DOC-123456789 | Maximum 50 caractères |
| mime | type MIME du fichier | oui | application/pdf | - |
| contenu | contenu du fichier en base64 | oui | - | - |

### Échanges de enu-backend avec le SI métier

L'essentiel du trafic se fait dans le sens SI métier -> Espace numérique,
cependant certains messages vont dans l'autre sens.

| Message | Producteur | Consommateur | Exchange RabbitMQ / queue RabbitMQ |
| ------- | ---------- | ------------ | ---------------------------------- |
| destruction d'une démarche brouillon | enu-backend | SI métier | enu-to-si1 / enu-to-si1-main |

#### Message JSON de destruction d'une démarche brouillon

TODO

## Test

Pour tester enu-mediation, deux outils fonctionnellement à peu près équivalents sont proposés :
enu-mediation et XXX.

L'outil enu-mediation est une simple Java sans interface utilisateur.
Il est plutôt adapté pour un développeur de enu-mediation ou d'un SI métier.
Les messages JSON y sont explicites. 

L'outil XXX est une petite application HTML 5.
Il est plutôt adapté pour un analyste métier.
Les messages JSON y sont cachés à l'utilisateur.

Ces deux outils fonctionnent par l'envoi de messages JSON dans une queue RabbitMQ.

## Divers

### Console RabbitMQ
La console du RabbitMQ utilisé dans les sources est ici : http://lab-rh712stdc1133a:15672 (matheg/matheg).

### Keystore et Truststore TLS
Voir src/main/resources/application.yml: camel.ssl.config.key-managers.

La configuration actuelle attend un store JKS dans: /pki/lab.jks

Ajuster au besoin soit dans application.yml soit via une variable d'environnement système (par ex. CAMEL_SSL_CONFIG_KEY-MANAGERS_etc. ). Elle remplacera automatiquement la valeur du application.yml. (Doc complète: https://docs.spring.io/spring-boot/docs/2.3.5.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config)

### Doc Spring Boot + Camel:

* [Using Apache Camel with Spring Boot](https://camel.apache.org/camel-spring-boot/latest/spring-boot.html)
