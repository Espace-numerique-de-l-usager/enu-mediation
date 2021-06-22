# Production et consommation de messages

Du point de vue d'un SI métier, les interactions avec l'Espace numérique de l'usager se font
uniquement avec RabbitMQ,
au moyen de la production et de la consommation de messages JSON via le protocole AMQP.
Ces messages sont décrits ici.

## Messages du SI métier pour l'Espace numérique

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

En-têtes nécessaires :
- `ContentType` = `application/new-demarche-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| `idPrestation` | identifiant de la prestation | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier | oui | `AEL-100000` | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères |
| `etat` | état de la démarche | oui | `BROUILLON` | Doit valoir soit `BROUILLON`, soit `DEPOSEE`, soit `EN_TRAITEMENT` |
| `dateDepot` | date de soumission de la démarche | oui si `etat` = `DEPOSEE` ou `EN_TRAITEMENT`, pas applicable sinon | `2021-02-19T12:15:00.000Z` | - | 
| `dateMiseEnTraitement` | date de mise en traitement de la démarche | oui si `etat` = `EN_TRAITEMENT`, pas applicable sinon | `2021-02-20T12:15:00.000Z` | - | 
| `libelleAction` | description de l'opération proposée à l'usager sur la démarche | non. Applicable uniquement si `etat` = `EN_TRAITEMENT` | `Compléter votre démarche` | Taille maximale : 250 caractères |
| `urlAction` | URL de l'opération proposée à l'usager sur la démarche | oui si `etat` = `BROUILLON` ou si `libelleAction` est fourni, pas applicable sinon | `https://etc...` | - |
| `typeAction` | type de l'opération proposée à l'usager sur la démarche | non. Pas applicable si `etat` = `BROUILLON` ou si `libelleAction` n'est pas fourni  | `ENRICHISSEMENT_DE_DEMANDE` | Doit valoir soit `ENRICHISSEMENT_DE_DEMANDE`, soit `REPONSE_DEMANDE_RENSEIGNEMENT` |
| `dateEcheanceAction` | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui si `urlAction` est fournie, pas applicable sinon | `2021-02-18` | La date uniquement, sans l'heure |

Exemple : voir [newdemarche/MessageSender](https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/newdemarche/MessageSender.java).
(TODO : lien à mettre à jour ci-dessus lors du passage à GitHub)

### Changement d'état d'une démarche : message JSON

Il s'agit ici de changer l'état d'une démarche qui a été précédemment créée via un
message comme ci-dessus.

En-têtes nécessaires :

- `Content-Type` = `application/status-change-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| `idPrestation` | identifiant de la prestation | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier | oui | `AEL-100000` | Doit doit être unique, pour la prestation donnée et pour l'usager donné. Maximum 50 caractères |
| `nouvelEtat` | nouvel état de la démarche | oui | `DEPOSEE` | Doit valoir soit `DEPOSEE`, soit `EN_TRAITEMENT`, soit `TERMINEE` |
| `dateNouvelEtat` | date à laquelle la démarche a changé d'état| oui | `2020-02-19` | La date uniquement, sans l'heure |

Champs supplémentaires si `nouvelEtat` = `DEPOSEE` ou si `nouvelEtat` = `EN_TRAITEMENT` :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| `libelleAction` | description de l'opération proposée à l'usager sur la démarche | non | `Compléter votre démarche` | Taille maximale : 250 caractères |
| `typeAction` | type de l'opération proposée à l'usager sur la démarche | non. Pas applicable si `libelleAction` n'est pas fourni  | `ENRICHISSEMENT_DE_DEMANDE` | Doit valoir soit `ENRICHISSEMENT_DE_DEMANDE`, soit `REPONSE_DEMANDE_RENSEIGNEMENT` |
| `urlAction` | URL de l'opération proposée à l'usager sur la démarche | oui si `typeAction` est fourni, pas applicable sinon | `https://etc...` | - |
| `dateEcheanceAction` | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui si `typeAction` est fourni, pas applicable sinon | `2021-02-18` | La date uniquement, sans l'heure |

Champs supplémentaires si `nouvelEtat` = `TERMINEE` :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| `urlRenouvellementDemarche` | URL à présenter à l'usager pour qu'il puisse recréer une démarche du même type | non | `https://ge.ch/bla-bla-bla` | - |

Exemples : voir [statuschange/MessageSender](https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/statuschange/MessageSender.java).

(TODO : lien à mettre à jour lors du passage à GitHub)

### Ajout d'un document à une démarche : message JSON

Il s'agit ici de compléter une démarche qui a été précédemment créée.

En-têtes nécessaires :
- `Content-Type` = `application/new-document-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| `idPrestation` | identifiant de la prestation | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier | oui | `AEL-100000` | Maximum 50 caractères |
| `typeDocument` | type de document | oui | `RECAPITULATIF` | Doit valoir soit `RECAPITULATIF`, soit `JUSTIFICATIF` |
| `libelleDocument` | titre du document, déterminant le nom du fichier | oui | `Décision administration 2020-02-19` | Maximum 50 caractères |
| `idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée et pour l'usager donné. Maximum 50 caractères |
| `mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, la seule valeur possible est `application/pdf` |
| `contenu` | contenu du fichier en base64 | oui si `ged` est absent, pas applicable sinon | - | Maximum 10'000'000 caractères |
| `ged` | données GED du document | oui si `contenu` est absent, pas applicable sinon | - | Voir le chapitre consacré à la GED, au bas de cette page |
| `ged.fournisseur` | identifiant d'une GED | oui | `DATA_CONTENT` | Actuellement la seule valeur possible est `DATA_CONTENT` |
| `ged.version` | version de l'interfaçage à la GED | oui | `1` | Actuellement la seule valeur possible est `1` |
| `ged.idDocument` | identifiant du document dans la GED | oui | `123456` | Cette valeur est été fournie par la GED lorsque le document a été stocké dans la GED |
| `ged.algorithmeHash` | algorithme utilisé par la GED pour calculer l'empreinte du document | oui | `SHA-256` | |
| `ged.hash` | empreinte du document dans la GED | oui | - | |

### Création d'un courrier : message JSON

Il s'agit ici de créer un courrier, c'est-à-dire l'équivalent numérique d'un envoi postal à l'usager.
Le courrier est constitué d'un ou plusieurs documents, ainsi que d'un en-tête.
Le courrier peut soit porter sur une démarche qui a été précédemment créée, soit ne porter sur aucune démarche.

En-têtes nécessaires :
- `Content-Type` = `application/new-courrier-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ----------- | ------- | ----------- |
| `idPrestation` | identifiant de la prestation, et donc de la catégorie du courrier | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager à qui le courrier est destiné. Si `type` vaut `LIE`, l'usager doit être le propriétaire de la démarche | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier. Il s'agit de la démarche à laquelle le courrier est rattaché. Si ce champ n'est pas fourni, le courrier est un courrier hors démarche | non | `AEL-100000` | Maximum 50 caractères |
| `libelleCourrier` | titre du courrier | oui | `Notification de l'impôt` | Maximum 50 caractères |
| `documents[i].libelleDocument` | titre du document, déterminant le nom du fichier | oui | `Décision administration 2020-02-19` | Maximum 50 caractères |
| `documents[i].idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée et pour l'usager donné. Maximum 50 caractères |
| `documents[i].mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, la seule valeur possible est `application/pdf` |
| `documents[i].contenu` | contenu du fichier en base64 | oui si `ged` est absent, pas applicable sinon | - | Maximum 10'000'000 caractères |
| `documents[i].ged` | données GED du document | oui si `contenu` est absent, pas applicable sinon | - | Voir le chapitre consacré à la GED, au bas de cette page|
| `documents[i].ged.fournisseur` | identifiant d'une GED | oui | `DATA_CONTENT` | Actuellement la seule valeur possible est `DATA_CONTENT` |
| `documents[i].ged.version` | version de l'interfaçage à la GED | oui | `1` | Actuellement la seule valeur possible est `1` |
| `documents[i].ged.idDocument` | identifiant du document dans la GED | oui | `123456` | Cette valeur est été fournie par la GED lorsque le document a été stocké dans la GED |
| `documents[i].ged.algorithmeHash` | algorithme utilisé par la GED pour calculer l'empreinte du document | oui | `SHA-256` | |
| `documents[i].ged.hash` | empreinte du document dans la GED | oui | - | |

L'indice `i` ci-dessus commence à 0, pour le premier document du courrier.

Le courrier doit contenir au minimum 1 document et au maximum 20 documents.

### Création d'une suggestion de démarche : message JSON

En-têtes nécessaires :
- `ContentType` = `application/new-suggestion-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| `idPrestation` | identifiant de la prestation | oui | `FL_TER_PERMISPECHE` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `libelleAction` | description courte de l'opération de création de démarche | oui | `Obtenir un permis de pêche` | Taille maximale : 25 caractères |
| `urlAction` | URL de création de démarche | oui | `https://etc...` | - |
| `dateEcheanceAction` | date avant laquelle l'usager est sensé créer la démarche | oui | `2021-02-18` | La date uniquement, sans l'heure |
| `descriptionAction` | description complète de l'opération de création de démarche | oui | `Obtenir un permis de pêche` | Taille maximale : 150 caractères |
| `urlPrestation` | URL du livret de la démarche | oui | `https://etc...` | Ce lien pointe normalement vers une page de l'État qui fournit une explication de la démarche |

Exemple : voir [newdemarche/MessageSender](https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/enu-mediation-client/-/blob/master/src/main/java/ch/ge/ael/enu/mediationclient/newdemarche/MessageSender.java).
(TODO : lien à mettre à jour ci-dessus lors du passage à GitHub)

## Messages de l'Espace numérique pour le SI métier

L'essentiel du trafic se fait dans le sens SI métier -> Espace numérique,
cependant certains messages vont dans l'autre sens.

| Message | Producteur | Consommateur | Exchange RabbitMQ / queue RabbitMQ |
| ------- | ---------- | ------------ | ---------------------------------- |
| réponse à un message | enu-mediation | SI métier | enu-to-si1 / enu-to-si1-main |
| destruction d'une démarche brouillon | enu-backend (1)| SI métier | enu-to-si1 / enu-to-si1-main |
| consultation d'un document par l'usager | enu-backend (1) | SI métier | enu-to-si1 / enu-to-si1-main |
| changement de mode de réception des documents par l'usager | enu-backend (1) | SI métier | enu-to-si1 / enu-to-si1-main |

(1) enu-backend est le producteur initial du message. En fait, le message est relayé, sans modification,
par enu-mediation. 

### Réponse à un message

Cas d'usage : le SI métier a envoyé un message à l'ENU, par exemple un message de création d'une démarche.
L'ENU a traité ce message d'origine et en retour envoie au SI métier un message ; celui-ci est un message de
réussite ou d'échec.

En-têtes garantis :
- `ContentType` = `application/reply-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| resultat | résultat du traitement du message d'origine | oui | OK | Doit valoir soit `OK`, soit `KO`|
| description | description du résultat | oui si `resultat` vaut `KO`, pas applicable sinon | Le champ "idPrestation" est obligatoire | - | 

En l'occurrence, l'identifiant de corrélation n'est pas créé par le producteur `enu-mediation`, mais est
identique à l'identifiant contenu dans le message d'origine.

### Destruction d'une démarche brouillon : message JSON

Cas d'usage : l'usager a une ou plusieurs suggestions de démarches à compléter.
Il décide d'en détruire une plutôt que de la compléter.

En-têtes garantis :
- `ContentType` = `application/brouillon-deletion-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| `idPrestation` | identifiant de la prestation | oui | `FL_TER_PERMISPECHE` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier | oui | `AEL-100000` | Taille maximale : 50 caractères |

### Consultation d'un document par l'usager : message JSON

Cas d'usage : l'usager a consulté un document, ou bien le délai qui lui était imparti pour consulter
ce document est écoulé.

En-têtes garantis :
- `ContentType` = `application/document-access-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| `idPrestation` | identifiant de la prestation | oui | `FL_TER_PERMISPECHE` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDocumentSiMetier` | identifiant du document dans le SI métier | oui | `DOC-123456789` | Taille maximale : 50 caractères |

### Changement de mode de réception des documents par l'usager : message JSON

Cas d'usage : l'usager a modifié son choix du mode de réception de ses documents.

En-têtes garantis :
- `ContentType` = `application/document-reception-mode-v1.0+json`
- `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | ----------- | ------- | ----------- | ----------- |
| `idUsager` | identifiant de l'usager | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `choixReception` | mode de réception des documents adressés à l'usager par l'administration | oui | `ELECTRONIQUE` | Doit valoir soit `ELECTRONIQUE` (= en version numérique uniquement, c'est-à-dire dans l'ENU seulement), soit `TOUT` (= en version numérique et par voie postale) |

## Identifiant de corrélation

Dans chaque message JSON, un identifiant de corrélation est attendu.
Cet identifiant est une chaîne de caractères, de taille inférieure à 50 caractères et dont la valeur est entièrement
à la discrétion du producteur du message.
Il est utilisé par le consommateur - en général l'application `enu-mediation` - dans les messages envoyés en retour
au producteur.
Un message de retour, qu'il contienne un signal de traitement réussi ou un message d'erreur, inclut
l'identifiant de corrélation et permet au producteur initial de croiser ses données, c'est-à-dire de déterminer
auquel de ses messages correspond le message de retour.
En effet, le message de retour ne contient pas une copie du message initial, mais uniquement l'information
de réussite ou d'erreur, plus l'identifiant de corrélation.

## Gestion électronique des documents (GED)

Dans le flux nominal de traitement des documents (y compris les courriers), le SI métier fournit l'intégralité des données
du document, y compris son contenu binaire dans le champ `contenu` des messages JSON.
À l'aval, l'ENU se charge d'enregistrer les métadonnées du document dans sa base relationnelle
et le contenu du document dans la GED. 
Toutefois, cette approche n'est pas satisfaisante pour les gros fichiers : une solution de messagerie comme RabbitMQ
n'est pas conçue pour traiter d'énormes messages, mais plutôt pour traiter une grande quantité de petits messages.
En pratique, on limite donc dans l'ENU la taille de `contenu` à XXX ko.

Pour les documents plus gros que la limite ci-dessus, il incombe au SI métier de préalablement stocker le message
dans la GED.
Par exemple, le SI métier de l'Administration fiscale cantonale (AFC) doit préalablement stocker
ses gros documents dans une de ses bases GED sur laquelle l'ENU aura un accès en lecture.
Ensuite, dans son message JSON, au lieu de fournir le champ `contenu`, le SI métier fournit le champ `ged`.
Ce champ contient l'identifiant unique du document dans la GED du SI métier.
Il contient également l'empreinte (hash) du fichier, dans le but d’assurer le transfert entre les 2 SI du bon
document binaire.

Le message JSON pour un gros document est donc extrêmement petit, car il ne contient que des métadonnées et des
identifiants, et non le contenu binaire.

À l'aval, l'ENU pourrait se contenter de récupérer les identifiants GED et de les stocker.
Cependant, cette solution ne serait fonctionnellement pas satisfaisante, car une fois qu'un document est arrivé dans le
périmètre de l'ENU, il importe qu'aucun autre système ne puisse l'altérer.
Or à simplement stocker les identifiants, l'ENU permettrait au SI métier de l'AFC de modifier ou de supprimer le
document.
Pour assurer son contrôle complet sur le document, l'ENU procède ainsi : ayant reçu un message JSON avec 
l'identifiant GED du document, il interroge la GED pour récupérer le document, puis le restocke dans une autre base,
privée, de la GED.
Techniquement, ce stockage à double du contenu du document n'est pas idéal, mais il permet de remplir les
conditions d'isolation fonctionnelle.

Le tableau suivant résume la configuration GED :

| Base GED | Accès en écriture | Accès en lecture |
| -------- | ---------------- | ----------------- |
| base GED principale de l'ENU | ENU | ENU |
| base GED de transfert du SI métier de l'AFC vers l'ENU | AFC | AFC, ENU |

La base GED principale de l'ENU stocke en définitive tous les documents : les petits documents parvenus via le
champ `contenu` ; les gros documents parvenus via le champ `ged` et recopiés d'une base GED à l'autre.

La base GED de transfert peut être purgée par le SI métier, une fois que celui-ci sait que le document a été traité
par l'ENU.

Dans un second temps, la solution de GED DataContent devrait offrir une notion de document virtuel permettant d’avoir
plusieurs documents pointant sur un même binaire ;
fonctionnellement, chaque SI aura bien son propre document, mais techniquement, DataContent ne stockera qu’une seule
version binaire de ce document.
Cette solution permettra un réel transfert du binaire du document entre 2 SI, sans pour autant faire transiter
le binaire dans l’ensemble du réseau.
Le document sera transmis par référence et DataContent assurera, en tant que tiers de confiance, la transmission
du binaire entre les 2 SI métier.
