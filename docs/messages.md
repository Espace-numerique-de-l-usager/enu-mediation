# Production et consommation de messages

Du point de vue d'un SI métier, les interactions avec l'Espace numérique de l'usager se font
uniquement avec RabbitMQ,
au moyen de la production et de la consommation de messages JSON via le protocole AMQP.
Ces messages sont décrits ici.

## Échanges du SI métier avec enu-mediation

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

### Création d'une démarche : message JSON

En-tête nécessaire : `ContentType` = `application/new-demarche-v1.0+json`.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_SOCIAL_INDICATEL | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager propriétaire de la démarche | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idDemarcheSiMetier | identifiant de la démarche dans le SI métier | oui | AEL-100000 | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères |
| etat | état de la démarche | oui | BROUILLON | Doit valoir soit BROUILLON, soit DEPOSEE, soit EN_TRAITEMENT |
| dateDepot | date de soumission de la démarche | oui si `etat` = `DEPOSEE` ou `EN_TRAITEMENT`, pas applicable sinon | 2021-02-19T12:15:00.000Z | - | 
| dateMiseEnTraitement | date de mise en traitement de la démarche | oui si `etat` = `EN_TRAITEMENT`, pas applicable sinon | 2021-02-20T12:15:00.000Z | - | 
| libelleAction | description de l'opération proposée à l'usager sur la démarche | non. Applicable uniquement si `etat` = `EN_TRAITEMENT` | Compléter votre démarche | Taille maximale : 250 caractères |
| urlAction | URL de l'opération proposée à l'usager sur la démarche | oui si `etat` = `BROUILLON` ou si `libelleAction` est fourni, pas applicable sinon | `https://etc...` | - |
| typeAction | type de l'opération proposée à l'usager sur la démarche | non. Pas applicable si `etat` = `BROUILLON` ou si `libelleAction` n'est pas fourni  | ENRICHISSEMENT_DE_DEMANDE | Doit valoir soit ENRICHISSEMENT_DE_DEMANDE, soit REPONSE_DEMANDE_RENSEIGNEMENT |
| dateEcheanceAction | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui si `urlAction` est fournie, pas applicable sinon | 2021-02-18 | La date uniquement, sans l'heure |

Exemple : voir [newdemarche/MessageSender](https://argon.ceti.etat-ge.ch/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/newdemarche/MessageSender.java).
(TODO : lien à mettre à jour ci-dessus lors du passage à GitHub)

### Changement d'état d'une démarche : message JSON

Il s'agit ici de changer l'état d'une démarche qui a été précédemment créée via un
message comme ci-dessus.

En-tête nécessaire : `Content-Type` = `application/status-change-v1.0+json`.

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
| libelleAction | description de l'opération proposée à l'usager sur la démarche | non | Compléter votre démarche | Taille maximale : 250 caractères |
| typeAction | type de l'opération proposée à l'usager sur la démarche | non. Pas applicable si `libelleAction` n'est pas fourni  | ENRICHISSEMENT_DE_DEMANDE | Doit valoir soit ENRICHISSEMENT_DE_DEMANDE, soit REPONSE_DEMANDE_RENSEIGNEMENT |
| urlAction | URL de l'opération proposée à l'usager sur la démarche | oui si `typeAction` est fourni, pas applicable sinon | `https://etc...` | - |
| dateEcheanceAction | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui si `typeAction` est fourni, pas applicable sinon | 2021-02-18 | La date uniquement, sans les heures |

Champs supplémentaires si `nouvelEtat` = `TERMINEE` :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| urlRenouvellementDemarche | URL à présenter à l'usager pour qu'il puisse recréer une démarche du même type | non | `https://ge.ch/bla-bla-bla` | - |

Exemples : voir [statuschange/MessageSender](https://argon.ceti.etat-ge.ch/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/statuschange/MessageSender.java).

(TODO : lien à mettre à jour lors du passage à GitHub)

### Ajout d'un document à une démarche : message JSON

Il s'agit ici de compléter une démarche qui a été précédemment créée.

En-tête nécessaire : `Content-Type` = `application/new-document-v1.0+json`.

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

### Création d'un courrier : message JSON

Il s'agit ici de créer un courrier, c'est-à-dire l'équivalent numérique d'un envoi postal à l'usager.
Le courrier est constitué d'un ou plusieurs documents, ainsi que d'un en-tête.
Le courrier peut soit porter sur une démarche qui a été précédemment créée, soit ne porter sur aucune démarche.

En-tête nécessaire : `Content-Type` = `application/new-courrier-v1.0+json`.

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

### Création d'une suggestion de démarche : message JSON

En-tête nécessaire : `ContentType` = `application/new-suggestion-v1.0+json`.

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

Exemple : voir [newdemarche/MessageSender](https://argon.ceti.etat-ge.ch/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/newdemarche/MessageSender.java).
(TODO : lien à mettre à jour ci-dessus lors du passage à GitHub)

## Échanges de enu-backend avec le SI métier

L'essentiel du trafic se fait dans le sens SI métier -> Espace numérique,
cependant certains messages vont dans l'autre sens.

| Message | Producteur | Consommateur | Exchange RabbitMQ / queue RabbitMQ |
| ------- | ---------- | ------------ | ---------------------------------- |
| destruction d'une démarche brouillon | enu-backend | SI métier | enu-to-si1 / enu-to-si1-main |
| consultation d'un document par l'usager | enu-backend | SI métier | enu-to-si1 / enu-to-si1-main |
| changement de mode de réception des documents par l'usager | enu-backend | SI métier | enu-to-si1 / enu-to-si1-main |

### Destruction d'une démarche brouillon : message JSON

Cas d'usage : l'usager avait une ou plusieurs suggestions de démarches.
Il a décidé d'en détruire une plutôt que de la compléter.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_TER_PERMISPECHE | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idDemarcheSiMetier | identifiant de la démarche dans le SI métier | oui | AEL-100000 | Taille maximale : 50 caractères |

### Consultation d'un document par l'usager : message JSON

Cas d'usage : l'usager a consulté un document, ou bien le délai qui lui était imparti pour consulter
ce document est écoulé.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| idPrestation | identifiant de la prestation | oui | FL_TER_PERMISPECHE | Fourni par l'équipe médiation |
| idUsager | identifiant de l'usager | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| idDocumentSiMetier | identifiant du document dans le SI métier | oui | DOC-123456789 | Taille maximale : 50 caractères |

### Changement de mode de réception des documents par l'usager : message JSON

Cas d'usage : l'usager a modifié son choix du mode de réception de ses documents.

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| idUsager | identifiant de l'usager | oui | CGE-1000000 | Cet usager doit être connu de Gina |
| choixReception | mode de réception des documents adressés à l'usager par l'administration | oui | ELECTRONIQUE | Doit valoir soit ELECTRONIQUE (= en version numérique uniquement, c'est-à-dire dans l'ENU seulement), soit TOUT (= en version numérique et par voie postale) |
