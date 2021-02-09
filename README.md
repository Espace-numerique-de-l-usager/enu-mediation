% Espace numérique de l'usager : médiation

# Espace numérique de l'usager : médiation

Ce projet définit les médiations pour le système Espace numérique de l'usager :

SI métier <---> RabbitMQ <---> médiation <---> FormServices

où "SI métier" signifie "un système informatique de l'État", par exemple un système
de l'Administration fiscale cantonale.

Une vue d'ensemble du système Espace numérique de l'usager est disponible sur
[GitHub](https://github.com/Espace-numerique-de-l-usager/enu-geneve).
Le rôle de l'application de médiation y est clairement présenté.

## Exécution

### Construction

L'application `enu-mediation` se construit avec Maven 3 et Java 8+, via la commande
```
mvn clean install
```
### Déploiement

Une fois que l'application est lancée, elle consomme les messages qu'elle trouve dans
les queues RabbitMQ.
Elle ne s'arrête que lorsqu'on l'arrête explicitement (par exemple, par un "contrôle-C").

#### Exécution en développement

Dans un environnement de développement comme IntelliJ, il suffit d'exécuter la classe
`MediationApplication`.

#### Exécution sur un serveur JEE

L'application est un "main" Java. Elle n'est en rien une application Web.
Cependant, pour se conformer aux usages de l'équipe de production de l'État de Genève, 
il a été préféré de déployer l'application comme un fichier WAR sur un serveur Tomcat.

Toute propriété définie dans le fichier `src/main/resources/enu-mediation-default.properties`
peut être redéfinie. Il suffit pour cela de la définir dans un fichier appelé (obligatoirement)
`enu-mediation.properties`, à placer (obligatoirement) dans le sous-répertoire `conf` de Tomcat.

### Propriétés

TODO: mettre ici les infos fournies dans le
[wiki](https://prod.etat-ge.ch/wikiadm/pages/viewpage.action?pageId=1812824302)
et dans [ENU-704](***REMOVED***/browse/ENU-704). 

## Configuration RabbitMQ de base

Il s'agit de la configuration RabbitMQ qui sera valable pour tous les services métier.

_Exchanges, queues et bindings :_

Aucun.

## Configuration d'un nouveau service métier

Il s'agit ici de la configuration à réaliser chaque fois qu'un nouveau service métier est ajouté
à l'ENU.

On commence par convenir d'un identifiant pour le nouveau service métier,
par exemple "afc" ou "ael-form".
Dans la suite, on considère que l'identifiant est "simetier".

### Travail à mener par l'équipe RabbitMQ

Il s'agit de la création d'exchanges et de queues RabbitMQ.

Attention : il faut respecter scrupuleusement les noms fournis, car le code de `enu-mediation` en dépend.

TODO: ajouter la sécurité

_Exchanges :_

| Nom de l'exchange | Producteur | Consommateur | Description |
| ----------------- | ---------- | ------------ | ----------- |
| simetier-to-enu-main | SI métier | enu-mediation | Pour les messages JSON de création de démarche, de changement d'état d'une démarche, d'ajout de document à une démarche, etc. |
| enu-to-simetier-main | enu-mediation | SI métier | Pour les messages JSON de destruction d'un brouillon de démarche. |
| enu-to-simetier-reply | enu-mediation | SI métier | Pour les messages JSON d'erreur. Il s'agit des erreurs métier détectées par `enu-mediation` dans des messages JSON envoyés par le service métier. |
| simetier-to-enu-reply | SI métier | enu-mediation | Pour les messages JSON d'erreur. Il s'agit des erreurs métier détectées par le SI métier dans des message JSON envoyés par `enu-mediation`. |
| enu-internal-error | enu-mediation | un humain, exploitant de `enu-mediation` | Boîte aux lettres morte pour les messages JSON que `enu-mediation` n'est pas parvenue à traiter, suite à une anomalie : erreur dans le code, rupture de la connexion à RabbitMQ, épuisement de la mémoire, etc. |
| simetier-internal-error | système métier | un humain, exploitant du SI métier | Boîte aux lettres morte pour les messages JSON que le SI métier n'est pas parvenue à traiter, suite à une anomalie : erreur dans le code, rupture de la connexion à RabbitMQ, épuisement de la mémoire, etc. |

_Queues :_

| Nom de la queue | Commentaires |
| --------------- | ------------ |
| simetier-to-enu-main-q | Dans `Arguments`, spécifier que `Dead letter exchange` vaut `enu-to-simetier-reply` et que `Dead letter routing key` vaut `enu-to-simetier-reply-q`. |
| enu-to-simetier-main-q | Dans `Arguments`, spécifier que `Dead letter exchange` vaut `simetier-to-enu-reply` et que `Dead letter routing key` vaut `simetier-to-enu-reply-q`. |
| enu-to-simetier-reply-q | Dans `Arguments`, spécifier que `Dead letter exchange` vaut `enu-internal-error` et que `Dead letter routing key` vaut `enu-internal-error-q`. |
| simetier-to-enu-reply-q | Dans `Arguments`, spécifier que `Dead letter exchange` vaut `simetier-internal-error` et que `Dead letter routing key` vaut `simetier-internal-error-q`. |
| enu-internal-error-q | - |
| simetier-internal-error-q | - |

_Bindings :_

Aucun binding entre exchanges et queues n'est nécessaire.

### Travail à mener par l'équipe médiation

### Travail à mener par l'équipe SI métier

## Production et consommation de messages

Du point de vue d'un SI métier, les interactions avec l'Espace numérique de l'usager se font
uniquement avec RabbitMQ,
au moyen de la production et de la consommation de messages JSON via le protocole AMQP.
Ces messages sont décrits ici.

### Échanges du SI métier avec enu-mediation

Le tableau ci-dessous fournit la liste des messages échangés, pour un SI métier dont le code (fourni par l'équipe
médiation) est "SI1".

| Message | Producteur | Consommateur | Exchange RabbitMQ / queue RabbitMQ |
| ------- | ---------- | ------------ | ---------------------------------- |
| création d'une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| changement d'état d'une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| ajout d'un document à une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| création d'un courrier, lié ou non à une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| création d'une suggestion de démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| message d'erreur | enu-mediation | SI métier | si1-to-enu / si1-to-enu-reply |

Note : dans les tableaux ci-dessous, la mention "inutile" pour un champ signifie en fait "non pertient", "interdit".

#### Création d'une démarche : message JSON

En-tête nécessaire : `ContentType` = `application/json-new-demarche`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_SOCIAL_INDICATEL | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager propriétaire de la démarche | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idDemarcheSiMetier | identifiant de la démarche dans le SI métier | oui | AEL-100000 | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères |
| etat | état de la démarche | oui | BROUILLON | Doit valoir soit BROUILLON, soit DEPOSEE, soit EN_TRAITEMENT |
| dateDepot | date de soumission de la démarche | oui si `etat` = `DEPOSEE` ou `EN_TRAITEMENT`, inutile sinon | 2021-02-19T12:15:00.000Z | - | 
| dateMiseEnTraitement | date de mise en traitement de la démarche | oui si `etat` = `EN_TRAITEMENT`, inutile sinon | 2021-02-20T12:15:00.000Z | - | 
| libelleAction | description de l'opération proposée à l'usager sur la démarche | non. Inutile si `etat` = `BROUILLON` | Compléter votre démarche | Taille maximale : 25 caractères |
| urlAction | URL de l'opération proposée à l'usager sur la démarche | oui si `etat` = `BROUILLON` ou si `libelleAction` est fourni, inutile sinon | `https://etc...` | - |
| typeAction | type de l'opération proposée à l'usager sur la démarche | non. Inutile si `etat` = `BROUILLON` ou si `libelleAction` n'est pas fourni  | ENRICHISSEMENT_DE_DEMANDE | Doit valoir soit ENRICHISSEMENT_DE_DEMANDE, soit REPONSE_DEMANDE_RENSEIGNEMENT |
| dateEcheanceAction | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui si `urlAction` est fournie, inutile sinon | 2021-02-18 | La date uniquement, sans l'heure |

Exemple : voir [newdemarche/MessageSender](https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/newdemarche/MessageSender.java).
(TODO : lien à mettre à jour ci-dessus lors du passage à GitHub)

#### Changement d'état d'une démarche : message JSON

Il s'agit ici de changer l'état d'une démarche qui a été précédemment créée via un
message comme ci-dessus.

En-tête nécessaire : `Content-Type` = `application/json-status-change`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_SOCIAL_INDICATEL | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager propriétaire de la démarche | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idDemarcheSiMetier | identifiant de la démarche dans le SI métier | oui | AEL-100000 | Doit doit être unique, pour la prestation donnée et pour l'usager donné. Maximum 50 caractères |
| nouvelEtat | nouvel état de la démarche | oui | DEPOSEE | Doit valoir soit DEPOSEE, soit EN_TRAITEMENT, soit TERMINEE |
| dateNouvelEtat | date à laquelle la démarche a changé d'état| oui | 2020-02-19 | - |

Champs supplémentaires si `nouvelEtat` = `DEPOSEE` ou si `nouvelEtat` = `EN_TRAITEMENT` :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| libelleAction | description de l'opération proposée à l'usager sur la démarche | non | Compléter votre démarche | Taille maximale : 25 caractères |
| typeAction | type de l'opération proposée à l'usager sur la démarche | non. Inutile si `libelleAction` n'est pas fourni  | ENRICHISSEMENT_DE_DEMANDE | Doit valoir soit ENRICHISSEMENT_DE_DEMANDE, soit REPONSE_DEMANDE_RENSEIGNEMENT |
| urlAction | URL de l'opération proposée à l'usager sur la démarche | oui si `typeAction` est fourni, inutile sinon | `https://etc...` | - |
| dateEcheanceAction | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui si `typeAction` est fourni, inutile sinon | 2021-02-18 | La date uniquement, sans les heures |

Champs supplémentaires si `nouvelEtat` = `TERMINEE` :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| urlRenouvellementDemarche | URL à présenter à l'usager pour qu'il puisse recréer une démarche du même type | non | `https://ge.ch/bla-bla-bla` | - |

Exemples : voir [statuschange/MessageSender](https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/statuschange/MessageSender.java).

(TODO : lien à mettre à jour lors du passage à GitHub)

#### Ajout d'un document à une démarche : message JSON

Il s'agit ici de compléter une démarche qui a été précédemment créée.

En-tête nécessaire : `Content-Type` = `application/json-new-document`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_SOCIAL_INDICATEL | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager propriétaire de la démarche | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idDemarcheSiMetier | identifiant de la démarche dans le SI métier | oui | AEL-100000 | Maximum 50 caractères |
| typeDocument | type de document | oui | RECAPITULATIF | Doit valoir soit RECAPITULATIF, soit JUSTIFICATIF |
| libelleDocument | titre du document, déterminant le nom du fichier | oui | Décision administration 2020-02-19 | Maximum 50 caractères |
| idDocumentSiMetier | identifiant permettant au SI métier d'identifier son document | oui | DOC-123456789 | Doit doit être unique, pour la prestation donnée et pour l'usager donné. Maximum 50 caractères |
| mime | type MIME du fichier | oui | application/pdf | Actuellement, seule la valeur "application/pdf" est prise en charge |
| contenu | contenu du fichier en base64 | oui | - | Maximum 10'000'000 caractères |

#### Création d'un courrier : message JSON

Il s'agit ici de créer un courrier, c'est-à-dire l'équivalent numérique d'un envoi postal à l'usager.
Le courrier est constitué d'un ou plusieurs documents, ainsi que d'un en-tête. 
Le courrier peut soit porter sur une démarche qui a été précédemment créée, soit ne porter sur aucune démarche.

En-tête nécessaire : `Content-Type` = `application/json-new-courrier`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| idPrestation | identifiant de la prestation, et donc de la catégorie du courrier | oui | FL_SOCIAL_INDICATEL | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager à qui le courrier est destiné. Si `type` vaut `LIE`, l'usager doit être le propriétaire de la démarche | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idDemarcheSiMetier | identifiant de la démarche dans le SI métier. Il s'agit de la démarche à laquelle le courrier est rattaché. Si ce champ n'est pas fourni, le courrier est un courrier hors démarche | non | AEL-100000 | Maximum 50 caractères |
| libelleCourrier | titre du courrier | oui | Notification de l'impôt | Maximum 50 caractères |
| documents[i].libelleDocument | titre du document, déterminant le nom du fichier | oui | Décision administration 2020-02-19 | Maximum 50 caractères |
| documents[i].idDocumentSiMetier | identifiant permettant au SI métier d'identifier son document | oui | DOC-123456789 | Doit doit être unique, pour la prestation donnée et pour l'usager donné. Maximum 50 caractères |
| documents[i].mime | type MIME du fichier | oui | application/pdf | Actuellement, seule la valeur "application/pdf" est prise en charge |
| documents[i].contenu | contenu du fichier en base64 | oui | - | Maximum 10'000'000 caractères |

L'indice `i` ci-dessus commence à 0, pour le premier document du courrier.
Le courrier doit contenir au moins 1 document et au maximum 20 documents.

#### Création d'une suggestion de démarche : message JSON

En-tête nécessaire : `ContentType` = `application/json-new-suggestion`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_TER_PERMISPECHE | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| libelleAction | description courte de l'opération de création de démarche | oui | Obtenir un permis de pêche | Taille maximale : 25 caractères |
| urlAction | URL de création de démarche | oui | `https://etc...` | - |
| dateEcheanceAction | date avant laquelle l'usager est sensé créer la démarche | oui | 2021-02-18 | La date uniquement, sans l'heure |
| descriptionAction | description complète de l'opération de création de démarche | oui | Obtenir un permis de pêche | Taille maximale : 150 caractères |
| urlPrestation | URL du livret de la démarche | oui | `https://etc...` | Ce lien pointe normalement vers une page de l'État qui fournit une explication de la démarche |

Exemple : voir [newdemarche/MessageSender](https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/newdemarche/MessageSender.java).
(TODO : lien à mettre à jour ci-dessus lors du passage à GitHub)

### Échanges de enu-backend avec le SI métier

L'essentiel du trafic se fait dans le sens SI métier -> Espace numérique,
cependant certains messages vont dans l'autre sens.

| Message | Producteur | Consommateur | Exchange RabbitMQ / queue RabbitMQ |
| ------- | ---------- | ------------ | ---------------------------------- |
| destruction d'une démarche brouillon | enu-backend | SI métier | enu-to-si1 / enu-to-si1-main |

#### Destruction d'une démarche brouillon : message JSON

TODO

## Test

Pour tester enu-mediation, deux outils fonctionnellement à peu près équivalents sont proposés :
enu-mediation-client et XXX.

L'outil enu-mediation est une simple application Java sans interface utilisateur.
Il est plutôt adapté pour un développeur de enu-mediation ou pour un développeur d'un SI métier
devant interfacer son système avec l'Espace numérique de l'usager.
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
