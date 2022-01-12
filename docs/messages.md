# Contrats entre les SI métiers et l'espace e-démarches (ENU)


## Introduction

Bienvenue dans la documentation sur l'échange de messages entre **l'espace e-démarches (ENU) et les SI Métiers**.

Quelques concepts:

### Espace e-démarches

L'espace e-démarches est le nom de l'application auprès du grand public; l'espace numérique de l'usager (ENU) est le nom du projet.  
Dans ce document, nous utilisons indifféremment l'un ou l'autre terme.

### RabbitMQ

Techniquement parlant, l'échange se fait à travers une plateforme, RabbitMQ, qui gère les messages de manière asynchrone.  
L'envoi de ces messages est un envoi de données.

### Message et contrat (RabbitMQ)

Dans le cadre de l'espace e-démarches, les messages correspondent aux données et fichiers transmis via RabbitMQ entre l'espace e-démarches et les SI Métiers.  
Un contrat est la description du format et des données attendues dans un message.

### SI Métier

Par SI Métier on entend tout systèmes qui gère des démarches et va interagir avec l'espace e-démarches.  
Exemple: le SI de l'AFC (impôts).

### Usager

Un usager peut être indifféremment un citoyen (**personne physique**) ou une entreprise (**personne morale**).  
Cet usager possède un identifiant unique, identique à celui utilisé dans la e-démarche.  
A noter qu'un usager n'est pas nécessairement domicilié à Genève (ex: frontaliers, vaudois).
Exemples: M. Dupont pour un citoyen, "Transports Publics Genevois" pour une entreprise.

### Prestation

Prestation de l'Etat de Genève; dans le cadre de l'espace e-démarches, les prestations qui nous intéressent sont les e-démarches.  
Chaque prestation a un identifiant unique dans l'espace e-démarches.  
Exemple: demande d'un chèque annuel de formation.

### Démarche

Une démarche est une demande envoyée par un usager. Chaque démarche effectuée par cet usager a un identifiant différent.  
Exemple: un usager effectue trois demandes pour un chèque annuel de formation. Chacune de ses demandes aura un identifiant séparé.

### Courrier

Un courrier est un document PDF envoyé par l'administration à l'usager.  
Il prend en général la forme d'une lettre, et peut-être constitué de plusieurs documents (lettre + annexes).  
Dans l'espace e-démarche, ce courrier se trouve sous "mes documents" ainsi que dans le détail de l'e-démarche auquel il est lié.  
Exemple: une lettre de réponse de l'administration à une demande de chèque annuel de formation.

### Document

Dans le cadre de l'espace e-démarches, un document est un fichier fourni par l'usager ainsi qu'un récapitulatif de sa demande.  
Ces fichiers (PDF ou autres) proviennent de l'e-démarche.  
Exemple: un document PDF récapitulant les champs saisis par l'usager dans la e-démarche + les documents ajoutés par l'usagers en pièces jointes.

### Notifications

Dans le cadre de l'espace e-démarches, une notification correspond à l'envoi d'un SMS et/ou d'un email à l'usager pour lui indiquer qu'il a reçu quelque chose sur son espace e-démarches.  
Exemple: la réception d'un courrier.


---

## Production et consommation de messages

Du point de vue d'un SI métier, les interactions avec l'espace e-démarches se font uniquement avec RabbitMQ, au moyen de la production et de la consommation de messages JSON via le protocole AMQP.

Ces messages sont décrits ici.

### Messages du SI métier pour l'espace e-démarches

Le tableau ci-dessous fournit la liste des messages échangés, pour un SI métier dont le code (fourni par l'équipe  
médiation) est "SI1" (cet exemple vaut un autre).

| Message | Producteur | Consommateur | Exchange RabbitMQ / queue RabbitMQ |
| ------- | ---------- | ------------ | ---------------------------------- |
| **Démarches** |  |  |  |
| dépôt d'une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| annonce du traitement d'une démarche par l'administration | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| demande d'une action complémentaire à l'usager sur une démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| annonce que la démarche est terminée | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| annonce de l'abandon de la démarche | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| **Suggestions** |  |  |  |
| transmettre une suggestion de démarche à l'usager | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| abandonner la suggestion | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| **Brouillon de démarche** |  |  |  |
| afficher le brouillon d'une démarche; l'usager pourra ainsi compléter sa demande ultérieurement | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| abandon du brouillon d'une démarche | dans les 2 sens | enu-mediation | si1-to-enu / si1-to-enu-main |
| **Courrier** |  |  |  |
| envoi d'un courrier, lié à une démarche, envoi par la GED | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| envoi d'un courrier, sans lien à une démarche, envoi par la GED | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| envoi d'un courrier, lié à une démarche, envoi en binaire | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| envoi d'un courrier, sans lien à une démarche, envoi en binaire | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| **Document de l'usager** |  |  |  |
| ajout d'un document à une démarche, envoi en binaire | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| ajout d'un document à une démarche, envoi par la GED | SI métier | enu-mediation | si1-to-enu / si1-to-enu-main |
| **Autres messages** |  |  |  |
| changement de préférence de l'usager | enu-mediation | dans les 2 sens | si1-to-enu / si1-to-enu-reply |
| liste des préférences de tous les usagers | enu-mediation | espace e-démarches | enu-to-si1 / enu-to-si1-reply |
| message d'erreur | enu-mediation | dans les 2 sens | si1-to-enu / si1-to-enu-reply |

---

## Détails des contrats (messages)

Nous allons maintenant revoir chacun des messages échangés, leurs formats ainsi que leurs règles de gestion.


### dépôt d'une démarche (état = Démarche déposée) : message JSON

Important: merci d'envoyer la démarche déposée à l'espace e-démarche **dès que le SI Métier reçoit l'information**.  
En effet, une fois que l'usager a envoyé sa demande (via une e-démarche), celui-ci s'attend à la trouver immédiatement dans son espace e-démarches.  
Le but est d'éviter toutes questions inutile à nos métiers.

En-têtes nécessaires :

*   `ContentType` = `application/demarche-deposee-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant unique de la prestation; clé primaire (clé à 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | numéro de la démarche dans le SI métier; clé primaire (clé à 3 champs) | oui | `AEL-100000` | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères |
| `dateDepot` | date de soumission de la démarche par l'usager | oui | `2021-02-19T12:15:00.000Z` | format de la date; timezone UTC (universel). La date ne peut pas être dans le future; mais une date passée est possible (pour un chargement d'historique). |

Règles :

*   Une démarche DEPOSEE peut être uniquement créée.
*   Renvoi de données, contenu identique (idempotence): si tous les champs sont identiques à l'exception du timestamp et du CorrelationId, l'ENU ne fait rien (pas d'erreur ni d'écriture, uniquement un message de confirmation).
*   Renvoi de données, contenu différent: si les trois identifiants (idPrestation, idUsager, idDemarcheSIMetier) existent déjà et que les autres champs diffèrent (à part le timestamp et le CorrelationId), il y a erreur. En effet, il est possible que deux démarches aient le même identifiant.
*   Statut suivant possible: EN\_TRAITEMENT ou ABANDONNEE
*   Statut précédent possible: aucun (n'existe pas), ou autre objet BROUILLON ou SUGGESTION

Règles UX:

*   Lors de la création d'une démarche, le SI Métier doit effacer le BROUILLON et/ou la SUGGESTION s'ils existent.

---

### démarche en traitement : message JSON

Une démarche passe à "en traitement" lorsqu'un collaborateur de l'Etat (ou un système) **commence à traiter la demande de l'usager**.

En-têtes nécessaires :

*   `ContentType` = `application/demarche-en-traitement-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant unique de la prestation; clé primaire (clé à 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | numéro de la démarche dans le SI métier; clé primaire (clé à 3 champs) | oui | `AEL-100000` | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères |
| `dateTraitement` | date de soumission de la démarche par l'usager | oui | `2021-02-19T12:15:00.000Z` | format de la date; timezone UTC (universel). La date ne peut pas être dans le future; mais une date passée est possible (pour un chargement d'historique). |

Règles :

*   Une démarche EN\_TRAITEMENT est uniquement la mise à jour d'une démarche existante, dont voici les cas possibles:
    *   La démarche existe (basé sur la PK de ces 3 champs : idPrestation, idUsager, idDemarcheSIMetier (sinon il y a erreur car la démarche n'existe pas)
    *   Le statut de la démarche qui existe est DEPOSEE ou EN\_TRAITEMENT ou ACTION\_REQUISE (sinon il y a erreur dans la succession des statuts)
*   Renvoi de données, contenu identique (idempotence): si tous les champs sont identiques à l'exception du timestamp et du CorrelationId, l'ENU ne fait rien (pas d'erreur ni d'écriture, uniquement un message de confirmation).
*   Renvoi de données, contenu différent: les nouvelles données mettent à jour les anciennes.
*   Statut suivant possible: EN\_TRAITEMENT ou ABANDONNEE ou ACTION\_REQUISE ou TERMINEE
*   Statut précédent possible: DEPOSEE ou EN\_TRAITEMENT ou ACTION\_REQUISE

Règles UX:

*   Pour information: l'espace e-démarches va effacer l'état actuel de la démarche et les actions ("à faire") requises à l'usager (s'il y en a), pour afficher "en traitement".

---

### démarche "action requise" : message JSON

Une démarche passe à "action requise" lorsqu'une **action est requise par l'usager** (par exemple, lorsqu'un complément d'information est demandé).
Dans l'espace e-démarche, lorsqu'une action est requise, une URL est affichée, permettant à l'usager de compléter sa démarche. 
La partie "compléter la démarche" se fait côté SI Métier (ou JWAY, dans le cadre des formulaires standards).

En-têtes nécessaires :

*   `ContentType` = `application/demarche-action-requise-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant unique de la prestation; clé primaire (clé à 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | numéro de la démarche dans le SI métier; clé primaire (clé à 3 champs) | oui | `AEL-100000` | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères |
| `dateActionRequise` | date à laquelle l'action a été demandée par l'administration | oui | `2021-02-19T12:15:00.000Z` | format de la date; timezone UTC (universel). La date ne peut pas être dans le future; mais une date passée est possible (pour un chargement d'historique). |
| `libelleAction` | courte description de l'opération proposée à l'usager sur la démarche | oui | `Merci de compléter votre demande.` | Maximum 250 caractères |
| `urlAction` | URL de l'opération proposée à l'usager sur la démarche | oui | `https://etc...` | Règles iso sur le format d'une URL. |
| `typeAction` | type de l'opération proposée à l'usager sur la démarche | oui | `ENRICHISSEMENT_DE_DEMANDE` | Doit valoir soit ENRICHISSEMENT_DE_DEMANDE, soit REPONSE_DEMANDE_RENSEIGNEMENT |
| `dateEcheanceAction` | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui | `2021-02-19` | La date uniquement, sans l'heure. |

Règles :

*   Une démarche ACTION_REQUISE est uniquement la mise à jour d'une démarche existante, dont voici les cas possibles:
    *   La démarche existe (basé sur la PK de ces 3 champs : idPrestation, idUsager, idDemarcheSIMetier (sinon il y a erreur car la démarche n'existe pas)
    *   Le statut de la démarche qui existe est EN\_TRAITEMENT ou ACTION\_REQUISE (sinon il y a erreur dans la succession des statuts)
*   Renvoi de données, contenu identique (idempotence): si tous les champs sont identiques à l'exception du timestamp et du CorrelationId, l'ENU ne fait rien (pas d'erreur ni d'écriture, uniquement un message de confirmation).
*   Renvoi de données, contenu différent: les nouvelles données mettent à jour les anciennes.
*   Statut suivant possible: EN\_TRAITEMENT ou ABANDONNEE ou ACTION\_REQUISE ou TERMINEE
*   Statut précédent possible: EN\_TRAITEMENT ou ACTION\_REQUISE

Règles UX; pour information:

*   L'espace e-démarches va effacer l'état actuel de la démarche et les actions ("à faire") requises à l'usager (s'il y en a), pour afficher "En attente de complément de votre part" et mettre en avant l'action à réaliser dans la section "à faire". 
*   Lorsque la date d'échéance est passée (J+1), l'action mentionnée dans "à faire" disparaît.
*   L'usager peut également effacer l'action de la section "à faire".

---

### démarche terminée : message JSON

Une démarche passe à "terminée" lorsque la demande de l'usager a été **complétement traitée**, et qu'une réponse (positive ou négative) lui a été envoyée.
A noter que la réponse se fait à travers un message de "courrier"; il s'agit en général d'un document PDF.

En-têtes nécessaires :

*   `ContentType` = `application/demarche-terminee-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant unique de la prestation; clé primaire (clé à 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | numéro de la démarche dans le SI métier; clé primaire (clé à 3 champs) | oui | `AEL-100000` | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères |
| `dateCloture` | date de clôture de la demande | non | `2021-02-19T12:15:00.000Z` | Par défaut, la date terminée est celle du jour; si une date existe dans le message c'est celle-ci qui est utilisée. Ce mécanisme permet de charger des données historiques. |
| `urlRenouvellement` | URL de renouvellement de la démarche | non | `https://etc...` | Règles iso sur le format d'une URL. |


Règles :

*   Une démarche TERMINEE est uniquement la mise à jour d'une démarche existante, dont voici les cas possibles:
    *   La démarche existe (basé sur la PK de ces 3 champs : idPrestation, idUsager, idDemarcheSIMetier (sinon il y a erreur car la démarche n'existe pas)
    *   Le statut de la démarche qui existe est EN\_TRAITEMENT (sinon il y a erreur dans la succession des statuts)
*   Renvoi de données, contenu identique (idempotence): si tous les champs sont identiques à l'exception du timestamp et du CorrelationId, l'ENU ne fait rien (pas d'erreur ni d'écriture, uniquement un message de confirmation).
*   Renvoi de données, contenu différent: il y a erreur car une démarche précédemment terminée est "frozen".
*   Statut suivant possible: aucun.
*   Statut précédent possible: EN\_TRAITEMENT 

Règles UX:

*   Lors de la clôture d'une démarche, l'espace e-démarches va effacer l'état actuel et les actions ("à faire") requises à l'usager, afficher "terminée" dans la section "mes démarches passées".

---

### démarche abandonnée : message JSON

Une démarche passe à "abandonnée" lorsque l'administration indique que **l'usager abandonne** la démarche dans le SI Métier.
Le mécanisme est similaire à une démarche terminée, mais peut être abandonnée à n'importe quel moment.

En-têtes nécessaires :

*   `ContentType` = `application/demarche-abandonnee-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant unique de la prestation; clé primaire (clé à 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | numéro de la démarche dans le SI métier; clé primaire (clé à 3 champs) | oui | `AEL-100000` | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères |
| `dateAbandon` | date d'abandon de la demande | non | `2021-02-19T12:15:00.000Z` | Par défaut, la date est celle du jour; si une date existe dans le message c'est celle-ci qui est utilisée. |
| `urlRenouvellement` | URL de renouvellement de la démarche | non | `https://etc...` | Règles iso sur le format d'une URL. |


Règles :

*   Une démarche ABANDONNEE  est uniquement la mise à jour d'une démarche existante, dont voici les cas possibles:
    *   La démarche existe (basé sur la PK de ces 3 champs : idPrestation, idUsager, idDemarcheSIMetier (sinon il y a erreur car la démarche n'existe pas)
    *   N'importe quel démarche peut être abandonnée, sauf si celle-ci était TERMINEE
*   Renvoi de données, contenu identique (idempotence): si tous les champs sont identiques à l'exception du timestamp et du CorrelationId, l'ENU ne fait rien (pas d'erreur ni d'écriture, uniquement un message de confirmation).
*   Renvoi de données, contenu différent:  il y a erreur car une démarche précédemment abandonnée est "frozen".
*   Statut suivant possible: aucun.
*   Statut précédent possible: tous sauf TERMINEE 

Règles UX:

*   Lors de l'abandon d'une démarche, effacer l'état actuel et les actions ("à faire") requises à l'usager, afficher "abandonnée" dans la section "mes démarches passées". 


---

### envoi d'une suggestion de démarche à l'usager : message JSON

Une partie des prestations de l'Etat étant obligatoires (par exemple les impôts), il est possible d'envoyer une suggestion de démarches à l'usager à travers son espace e-démarches. La suggestion contient un texte + un lien vers la démarche en question. La suggestion correspond à un flyer numérique.
A noter que l'usager peut effacer la suggestion s'il n'est pas intéressé.
A noter également qu'un usager qui utilise rarement son espace e-démarches ne verra probablement pas la suggestion. Il s'agira donc de communiquer à travers plusieurs canaux.

En-têtes nécessaires :

*   `ContentType` = `application/suggestion-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation; clé primaire (clé à 2 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation. Attention à utiliser le même identifiant qui sera ensuite utilisé par le BROUILLON et/ou la démarche DEPOSEE |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 2 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `libelleAction` | texte court pour le bouton de création de démarche | oui | `Déclarer mes impôts` | Taille maximale : 25 caractères. |
| `urlAction` | URL de création de démarche | oui | `https://etc...` | Règles iso sur le format d'une URL: https://datatracker.ietf.org/doc/html/rfc2616 |
| `dateEcheanceAction` | date avant laquelle l'usager est sensé créer la démarche | non | `2021-02-19` | La date uniquement, sans l'heure (format de date à la norme ISO). |
| `descriptionAction` | texte complet de la suggestion | oui | `Merci de déclarer vos impôts avant l'échéance du 31 mars` | Taille maximale : 150 caractères. |
| `urlPrestation` | URL du feuillet décrivant la prestation | oui | `https://www.ge.ch/...` | Ce lien pointe vers une page de ge.ch qui fournit une explication sur la prestation (qui est eligible, etc) |

Règles :

*   Une SUGGESTION peut être crée ou mise à jour. 
*   Si la SUGGESTION existe (basé sur la PK de ces 2 champs :  idPrestation, idUsager) c'est une mise à jour, sinon une création.
*   envoi de données, contenu identique (idempotence): si tous les champs sont identiques à l'exception du timestamp et du CorrelationId, l'ENU ne fait rien (pas d'erreur ni d'écriture, uniquement un message de confirmation).
*   Renvoi de données, contenu différent: mise à jour des informations de la SUGGESTION.
*   Statut suivant possible: BROUILLON, ou dépôt d'une démarche (DEPOSEE), ou abandon de la SUGGESTION.
*   Statut précédent possible: aucun ou SUGGESTION
*   Basé sur la PK (idPrestation, idUsager), si une démarche pour cette prestation est en BROUILLON, DEPOSEE, EN_TRAITEMENT, ou ACTION_REQUISE, la suggestion est ignorée (elle ne va pas s'afficher).

Règles UX:

*   Dans la section "A faire" de l'espace e-démarches: la suggestion pour cette prestation est effacée (si elle existe déjà), et remplacée par la nouvelle suggestion. 
*   La SUGGESTION est effacée juste après la date d'échéance (J+1)


---

### effacer une suggestion de démarche : message JSON

Permettre à l'usager et/ou l'administration d'abandonner une SUGGESTION.
Ce message peut à la fois venir de l'usager et venir du SI Métier.

Exemples de cas d'usage: 

* L'usager n'est pas intéressé.
* L'usager a entamé la procédure papier.
* L'usager a fait une demande de délais (et le SI Métier veut annuler la SUGGESTION).
* L'administration une SUGGESTION ou la remplace par une autre (exemple: TEST COVID par une autre prestation de VACCINATION COVID)

En-têtes nécessaires :

*   `ContentType` = `application/suggestion-abandon-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation; clé primaire (clé à 2 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation. |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 2 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |

Règles :

*   Une SUGGESTION peut être effacée si elle existe basé sur la PK de ces 2 champs :  idPrestation, idUsager  (sinon il y a erreur)
* Renvoi de données, contenu identique (idempotence): ce cas n'existe pas si la SUGGESTION est déjà effacée.
* Renvoi de données, contenu différent: ce cas n'existe pas: seule la PK est transmise.
* Statut suivant possible: aucun.
* Statut précédent possible: SUGGESTION

Règles UX:

*   Dans la section "A faire" de l'espace e-démarches: la suggestion pour cette prestation est effacée


---

### sauvegarde d'un brouillon de démarche : message JSON

Dans les formulaires standards (JWAY/Formsolution) ou les autres formulaires, il est parfoit possible de sauvegarder une demande qui n'a pas été soumise.
Dans ce cas, l'usager peut la restrouver dans son espace pour la compléter plus tard.

En-têtes nécessaires :

*   `ContentType` = `application/brouillon-de-demarche-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant unique de la prestation; clé primaire (clé à 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | numéro de la démarche dans le SI métier; clé primaire (clé à 3 champs) | oui | `AEL-100000` | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères. Attention, cet identifiant doit pouvoir être repris lorsque la démarche est déposée (pour effacer le brouillon qui sera remplacé par la démarche). |
| `urlAction` | URL pour ouvrir le brouillon | non | `https://etc...` | Règles iso sur le format d'une URL. |
| `dateEcheanceAction` | date avant laquelle l'usager est sensé avoir soumis la démarche | non | `2021-02-19` | La date uniquement, sans l'heure. |

Règles :

*   Une démarche en BROUILLON  peut être créée ou mise à jour.
*   Renvoi de données, contenu identique (idempotence): si tous les champs sont identiques à l'exception du timestamp et du CorrelationId, l'ENU ne fait rien (pas d'erreur ni d'écriture, uniquement un message de confirmation).
*   Renvoi de données, contenu différent: si les trois identifiants (idPrestation, idUsager, idDemarcheSIMetier) existent déjà et que les autres champs diffèrent (à part le timestamp et le CorrelationId), le brouillon est mis à jour.
*   Statut suivant possible: brouillon (pour une mise à jour) ou dépôt d'une démarche, ou abandon du brouillon.
*   Statut précédent possible: aucun (n'existe pas), ou BROUILLON

Règles UX:

*   Dans la section "A faire" de l'espace e-démarches: le brouillon actuel (basé sur la PK) est effacé s'il existe, et est remplacé par le nouveau. 

Autres remarques:

*   Nous n'avons pas besoin de la date pour le brouillon; en effet la date du jour convient.

---

### effacer un brouillon de démarche : message JSON

Cas d'usage : l'usager a une ou plusieurs suggestions de démarches à compléter. Il décide d'en détruire une plutôt que de la compléter.
Aucun historique des brouillon n'étant conservé, la destruction d'un brouillon est définitive.

En-têtes nécessaires :

*   `ContentType` = `application/brouillon-abandon-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant unique de la prestation; clé primaire (clé à 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche; clé primaire (clé à 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | numéro de la démarche dans le SI métier; clé primaire (clé à 3 champs) | oui | `AEL-100000` | Pour une prestation donnée et pour un usager donné, doit doit être unique. Maximum 50 caractères. |

Règles :

*   Il s'agit d'un message de l'ENU vers le SI Métier.
*   Une démarche en BROUILLON peut être détruite: se brouillon existe (basé sur la PK de ces 3 champs :  idPrestation, idUsager, idDemarcheSIMetier    (sinon il y a erreur).
*   Renvoi de données, contenu identique (idempotence): cette situation n'existe pas (nous n'envoyons que la clé primaire); en effet, la destruction d'un brouillon ayant effacé les données. Une erreur sera générée.
*   Renvoi de données, contenu différent: cette situation n'existe pas (nous n'envoyons que la clé primaire). Une erreur sera générée.
*   Statut suivant possible: aucun.
*   Statut précédent possible: BROUILLON

Règles back/BDD de l'ENU:

*   Le brouillon est effacé.
*   Le SI Métier doit envoyer un ACK pour que l'ENU n'envoie pas répétitivement ce message.
*   Si le SI Métier ne trouve pas la démarche, il est probable que le BROUILLON ait été déjà effacé.

Règles front: 

*  Dans la section "A faire" de l'espace e-démarches:  le brouillon est effacé. 

---

### Envoi d'un courrier par l'administration : message JSON

Il s'agit ici de créer un courrier, c'est-à-dire l'équivalent numérique d'un envoi postal à l'usager.  
Ce format est le format standard d'envoi d'un courrier (à préférer); les autres cas sont des variantes. 

Le courrier:
* est constitué d'un ou plusieurs documents, ainsi que d'un en-tête.  
* porte sur une démarche qui a été précédemment créée.
* son envoi se fait à travers la GED (le document n'est pas joint, seule la référence est passée). Voir le chapitre consacré à la GED, au bas de cette page.

En-têtes nécessaires :

*   `Content-Type` = `application/courrier-administration-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation (clé primaire sur 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation. A noter qu'à travers cet identifiant, le courrier va être catégorisé dans un thème (école, impôts, etc). |
| `idUsager` | identifiant de l'usager à qui le courrier est destiné  (clé primaire sur 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina. |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier. Il s'agit de la démarche à laquelle le courrier est rattaché.  (clé primaire sur 3 champs) | oui | `AEL-100000` | Maximum 50 caractères |
| `libelleCourrier` | titre du courrier | oui | `Votre demande de permis D` | Maximum 50 caractères. A noter que lorsque plusieurs fichiers sont envoyés à l'usager, ce titre est celui qui les groupe |
| `documents[i].libelleDocument` | titre du fichier (faisant partie du courrier) | oui | `Annexe 1: éléments déterminants la décision` | Maximum 50 caractères |
| `documents[i].idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée, la démarche donnée et pour l'usager donné. Maximum 50 caractères |
| `documents[i].mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, seuls les PDFs sont acceptés; donc la seule valeur possible est `application/pdf` |
| `documents[i].fournisseurGed` | Nom de la GED | oui | `DATA_CONTENT` | Actuellement la seule valeur possible est `DATA_CONTENT` |
| `documents[i].idDocumentGed` | identifiant du document dans la GED | oui | `123456` | Cette valeur est été fournie par la GED lorsque le document a été stocké dans la GED |
| `documents[i].algorithmeHash` | algorithme utilisé par la GED pour calculer l'empreinte du document | oui | `SHA-256` |   |
| `documents[i].hash` | empreinte du document dans la GED | oui | \- |   |
| `dateEnvoi` | date de courrier/document (à utiliser pour le chargement de l'historique). Si absent, la date de réception dans l'ENU est utilisée | non | `2021-02-19` | La date uniquement, sans l'heure. |

L'indice `i` ci-dessus commence à 0, pour le premier document du courrier.

Règles :

| Nom | Description |
| --- | --- |
| Nombre de documents maximums dans un courrier | Le courrier doit contenir au minimum 1 document et au maximum 20 documents. | 
| Chargement d'historique possible ?  | Oui (sous réserve que le status de la démarche le permette; donc si une démarche n'est pas terminée) |
| Est-ce possible de mettre à jour des courriers / documents | Non (à la place, envoyer un courrier rectificatif) |
| Courrier possible pour quel statut de démarche ? | N'importe quel statut d'une démarche qui n'est pas une suggestion ou un brouillon.
Si une démarche est terminée: l'administration a 7 jours pour envoyer les derniers documents/courrier sinon -> erreur |
| Action ENU front | Afficher dans les documents + dans la démarche concernée |
| Notifications | Oui si l'usager préfère la version numérique du courrier, sinon non. |
| Renvoi de documents | Si tous les champs sont identiques (idempotence) , rien ne se passe. Sinon les documents/courriers s'accumulent dans l'ENU |
| Taille de fichier max | Pas de limite |



---

### Envoi d'un courrier par l'administration, hors e-démarche : message JSON

Ce cas est particulier et rare: il s'agit d'un courrier envoyé à l'usager sans que celui-ci ait entamé une démarche.
L'usager ne verra donc **pas** son courrier dans sa liste de démarches, et ne pourra consulter le courrier que depuis le menu "mes documents".

Ce courrier:
* est constitué d'un ou plusieurs documents, ainsi que d'un en-tête.  
* **ne porte pas** sur une démarche qui a été précédemment créée (donc n'apparait pas dans le menu "mes démarches").
* son envoi se fait à travers la GED (le document n'est pas joint, seule la référence est passée). Voir le chapitre consacré à la GED, au bas de cette page.

En-têtes nécessaires :

*   `Content-Type` = `application/courrier-administration-hors-demarche-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation (clé primaire sur 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation. A noter qu'à travers cet identifiant, le courrier va être catégorisé dans un thème (école, impôts, etc). |
| `idUsager` | identifiant de l'usager à qui le courrier est destiné  (clé primaire sur 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina. |
| `libelleCourrier` | titre du courrier | oui | `Votre demande de permis D` | Maximum 50 caractères. A noter que lorsque plusieurs fichiers sont envoyés à l'usager, ce titre est celui qui les groupe |
| `documents[i].libelleDocument` | titre du fichier (faisant partie du courrier) | oui | `Annexe 1: éléments déterminants la décision` | Maximum 50 caractères |
| `documents[i].idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée, la démarche donnée et pour l'usager donné. Maximum 50 caractères |
| `documents[i].mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, seuls les PDFs sont acceptés; donc la seule valeur possible est `application/pdf` |
| `documents[i].fournisseurGed` | Nom de la GED | oui | `DATA_CONTENT` | Actuellement la seule valeur possible est `DATA_CONTENT` |
| `documents[i].idDocumentGed` | identifiant du document dans la GED | oui | `123456` | Cette valeur est été fournie par la GED lorsque le document a été stocké dans la GED |
| `documents[i].algorithmeHash` | algorithme utilisé par la GED pour calculer l'empreinte du document | oui | `SHA-256` |   |
| `documents[i].hash` | empreinte du document dans la GED | oui | \- |   |
| `dateEnvoi` | date de courrier/document (à utiliser pour le chargement de l'historique). Si absent, la date de réception dans l'ENU est utilisée | non | `2021-02-19` | La date uniquement, sans l'heure. |

L'indice `i` ci-dessus commence à 0, pour le premier document du courrier.

Règles :

| Nom | Description |
| --- | --- |
| Nombre de documents maximums dans un courrier | Le courrier doit contenir au minimum 1 document et au maximum 20 documents. | 
| Chargement d'historique possible ?  | Oui |
| Est-ce possible de mettre à jour des courriers / documents | Non (à la place, envoyer un courrier rectificatif) |
| Courrier possible pour quel statut de démarche ? | Ce point n'est pas applicable hors démarche |
| Action ENU front | Affichage uniquement dans "mes documents" |
| Notifications | Oui si l'usager préfère la version numérique du courrier, sinon non. |
| Renvoi de documents | Si tous les champs sont identiques (idempotence) , rien ne se passe. Sinon les documents/courriers s'accumulent dans l'ENU |
| Taille de fichier max | Pas de limite |


---

### Envoi d'un courrier par l'administration, en pièce jointe (binaire) : message JSON

Il s'agit ici d'envoyer un courrier à l'usager, c'est-à-dire l'équivalent numérique d'un envoi postal.  
Ce format est à éviter: au lieu d'envoyer les documents à l'ENU via la GED, cete variante permet un envoi binaire des documents. 

Le courrier:
* est constitué d'un ou plusieurs documents, ainsi que d'un en-tête.  
* porte sur une démarche qui a été précédemment créée.
* son envoi se fait en binaire.

En-têtes nécessaires :

*   `Content-Type` = `application/ourrier-administration-binaire-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation (clé primaire sur 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation. A noter qu'à travers cet identifiant, le courrier va être catégorisé dans un thème (école, impôts, etc). |
| `idUsager` | identifiant de l'usager à qui le courrier est destiné  (clé primaire sur 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina. |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier. Il s'agit de la démarche à laquelle le courrier est rattaché.  (clé primaire sur 3 champs) | oui | `AEL-100000` | Maximum 50 caractères |
| `libelleCourrier` | titre du courrier | oui | `Votre demande de permis D` | Maximum 50 caractères. A noter que lorsque plusieurs fichiers sont envoyés à l'usager, ce titre est celui qui les groupe |
| `documents[i].libelleDocument` | titre du fichier (faisant partie du courrier) | oui | `Annexe 1: éléments déterminants la décision` | Maximum 50 caractères |
| `documents[i].idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée, la démarche donnée et pour l'usager donné. Maximum 50 caractères |
| `documents[i].mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, seuls les PDFs sont acceptés; donc la seule valeur possible est `application/pdf` |
| `documents[i].contenu` | le document lui-même (binaire) | oui | \- | \- |
| `documents[i].algorithmeHash` | algorithme utilisé pour calculer l'empreinte du document | oui | `SHA-256` |   |
| `documents[i].hash` | empreinte du document | oui | \- |   |
| `dateEnvoi` | date de courrier/document (à utiliser pour le chargement de l'historique). Si absent, la date de réception dans l'ENU est utilisée | non | `2021-02-19` | La date uniquement, sans l'heure. |

L'indice `i` ci-dessus commence à 0, pour le premier document du courrier.

Règles :

| Nom | Description |
| --- | --- |
| Nombre de documents maximums dans un courrier | Le courrier doit contenir au minimum 1 document et au maximum 20 documents. | 
| Chargement d'historique possible ?  | Oui (sous réserve que le status de la démarche le permette; donc si une démarche n'est pas terminée) |
| Est-ce possible de mettre à jour des courriers / documents | Non (à la place, envoyer un courrier rectificatif) |
| Courrier possible pour quel statut de démarche ? | N'importe quel statut d'une démarche qui n'est pas une suggestion ou un brouillon.
Si une démarche est terminée: l'administration a 7 jours pour envoyer les derniers documents/courrier sinon -> erreur |
| Action ENU front | Afficher dans les documents + dans la démarche concernée |
| Notifications | Oui si l'usager préfère la version numérique du courrier, sinon non. |
| Renvoi de documents | Si tous les champs sont identiques (idempotence) , rien ne se passe. Sinon les documents/courriers s'accumulent dans l'ENU |
| Taille de fichier max | attention, le document ne doit pas dépasser les 10kb |




---

### Envoi d'un courrier par l'administration, hors e-démarche, en pièce jointe (binaire) : message JSON

Ce cas est particulier et très rare: il s'agit d'un courrier envoyé à l'usager sans que celui-ci ait entamé une démarche.
L'usager ne verra donc **pas** son courrier dans sa liste de démarches, et ne pourra consulter le courrier que depuis le menu "mes documents".
De plus, l'envoi se fait "en direct", sans passer par la GED. Attention, cela limite la taille des documents transmis.

Ce courrier:
* est constitué d'un ou plusieurs documents, ainsi que d'un en-tête.  
* **ne porte pas** sur une démarche qui a été précédemment créée (donc n'apparait pas dans le menu "mes démarches").
* son envoi se fait à travers la GED (le document n'est pas joint, seule la référence est passée). Voir le chapitre consacré à la GED, au bas de cette page.

En-têtes nécessaires :

*   `Content-Type` = `application/courrier-administration-hors-demarche-binaire-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation (clé primaire sur 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation. A noter qu'à travers cet identifiant, le courrier va être catégorisé dans un thème (école, impôts, etc). |
| `idUsager` | identifiant de l'usager à qui le courrier est destiné  (clé primaire sur 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina. |
| `libelleCourrier` | titre du courrier | oui | `Votre demande de permis D` | Maximum 50 caractères. A noter que lorsque plusieurs fichiers sont envoyés à l'usager, ce titre est celui qui les groupe |
| `documents[i].libelleDocument` | titre du fichier (faisant partie du courrier) | oui | `Annexe 1: éléments déterminants la décision` | Maximum 50 caractères |
| `documents[i].idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée, la démarche donnée et pour l'usager donné. Maximum 50 caractères |
| `documents[i].mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, seuls les PDFs sont acceptés; donc la seule valeur possible est `application/pdf` |
| `documents[i].contenu` | le document lui-même (binaire) | oui | \- | \- |
| `documents[i].algorithmeHash` | algorithme utilisé  pour calculer l'empreinte du document | oui | `SHA-256` |   |
| `documents[i].hash` | empreinte du document | oui | \- |   |
| `dateEnvoi` | date de courrier/document (à utiliser pour le chargement de l'historique). Si absent, la date de réception dans l'ENU est utilisée | non | `2021-02-19` | La date uniquement, sans l'heure. |

L'indice `i` ci-dessus commence à 0, pour le premier document du courrier.

Règles :

| Nom | Description |
| --- | --- |
| Nombre de documents maximums dans un courrier | Le courrier doit contenir au minimum 1 document et au maximum 20 documents. | 
| Chargement d'historique possible ?  | Oui |
| Est-ce possible de mettre à jour des courriers / documents | Non (à la place, envoyer un courrier rectificatif) |
| Courrier possible pour quel statut de démarche ? | Ce point n'est pas applicable hors démarche |
| Action ENU front | Affichage uniquement dans "mes documents" |
| Notifications | Oui si l'usager préfère la version numérique du courrier, sinon non. |
| Renvoi de documents | Si tous les champs sont identiques (idempotence) , rien ne se passe. Sinon les documents/courriers s'accumulent dans l'ENU |
| Taille de fichier max | attention, le document ne doit pas dépasser les 10kb |


---

### Ajout d'un document de l'usager : message JSON

Ce contrat permet d'ajouter à l'espace e-démarche les documents téléversés par l'usager, et le récapitulatif de sa demande.
Il pourra ainsi s'y référer.

Contrairement au courrier, l'envoi se fait document par document. De plus, ces documents vont apparaitre dans la démarche concernée (et non sous "mes documents").

Cet envoi se fait à travers la GED (le document n'est pas joint, seule la référence est passée). Voir le chapitre consacré à la GED, au bas de cette page.

En-têtes nécessaires :

*   `Content-Type` = `application/document-usager-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation (clé primaire sur 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation. A noter qu'à travers cet identifiant, le courrier va être catégorisé dans un thème (école, impôts, etc). |
| `idUsager` | identifiant de l'usager (clé primaire sur 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina. |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier. Il s'agit de la démarche à laquelle le document est rattaché.  (clé primaire sur 3 champs) | oui | `AEL-100000` | Maximum 50 caractères |
| `typeDocument` | type de document | oui | `RECAPITULATIF` | soit RECAPITULATIF (de la demande e-démarche), soit JUSTIFICATIF (pièces téléversées par l'usager) |
| `libelleDocument` | titre du document | oui | `Carte d'identité` | Maximum 50 caractères. |
| `idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée, la démarche donnée et pour l'usager donné. Maximum 50 caractères |
| `mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, seuls les PDFs sont acceptés pour les RECAPITULATIF; donc la seule valeur possible dans ce cas est `application/pdf`; pour les JUSTIFICATIF tout est accepté |
| `fournisseurGed` | Nom de la GED | oui | `DATA_CONTENT` | Actuellement la seule valeur possible est `DATA_CONTENT` |
| `idDocumentGed` | identifiant du document dans la GED | oui | `123456` | Cette valeur est été fournie par la GED lorsque le document a été stocké dans la GED |
| `algorithmeHash` | algorithme utilisé par la GED pour calculer l'empreinte du document | oui | `SHA-256` |   |
| `hash` | empreinte du document dans la GED | oui | \- |   |
| `dateEnvoi` | date de document (à utiliser pour le chargement de l'historique). Si absent, la date de réception dans l'ENU est utilisée | non | `2021-02-19` | La date uniquement, sans l'heure. |

Règles :

| Nom | Description |
| --- | --- |
| Nombre de documents maximums dans un message | Un seul. | 
| Chargement d'historique possible ?  | Oui (sous réserve que le status de la démarche le permette; donc si une démarche n'est pas terminée) |
| Est-ce possible de mettre à jour des courriers / documents | Non (à la place, envoyer un courrier rectificatif) |
| Courrier possible pour quel statut de démarche ? | N'importe quel statut d'une démarche qui n'est pas une suggestion ou un brouillon.
Si une démarche est terminée: l'administration a 7 jours pour envoyer les derniers documents/courrier sinon -> erreur |
| Action ENU front | Afficher dans la démarche concernée |
| Notifications | Non. |
| Renvoi de documents | Si tous les champs sont identiques (idempotence) , rien ne se passe. Sinon les documents s'accumulent dans l'ENU |
| Taille de fichier max | Pas de limite |


---

### Ajout d'un document de l'usager, en pièce jointe (binaire) : message JSON

Même chose que le contrat ci-dessus, mais en pièce jointe (binaire).
Point d'attention : pour les RECAPITULATIF cela peut convenir, mais pour les JUSTIFICATIF il y a de fortes chances que la taille max soit tout de suite atteinte.

En-têtes nécessaires :

*   `Content-Type` = `application/document-usager-binaire-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation (clé primaire sur 3 champs) | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation. A noter qu'à travers cet identifiant, le courrier va être catégorisé dans un thème (école, impôts, etc). |
| `idUsager` | identifiant de l'usager (clé primaire sur 3 champs) | oui | `CGE-1000000` | Cet usager doit être connu de Gina. |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier. Il s'agit de la démarche à laquelle le document est rattaché.  (clé primaire sur 3 champs) | oui | `AEL-100000` | Maximum 50 caractères |
| `typeDocument` | type de document | oui | `RECAPITULATIF` | soit RECAPITULATIF (de la demande e-démarche), soit JUSTIFICATIF (pièces téléversées par l'usager) |
| `libelleDocument` | titre du document | oui | `Carte d'identité` | Maximum 50 caractères. |
| `idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée, la démarche donnée et pour l'usager donné. Maximum 50 caractères |
| `mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, seuls les PDFs sont acceptés pour les RECAPITULATIF; donc la seule valeur possible dans ce cas est `application/pdf`; pour les JUSTIFICATIF tout est accepté |
| `contenu` | le document lui-même (binaire) | oui | \- | \- |
| `algorithmeHash` | algorithme utilisé pour calculer l'empreinte du document | oui | `SHA-256` |   |
| `hash` | empreinte du document | oui | \- |   |
| `dateEnvoi` | date de document (à utiliser pour le chargement de l'historique). Si absent, la date de réception dans l'ENU est utilisée | non | `2021-02-19` | La date uniquement, sans l'heure. |

Règles :

| Nom | Description |
| --- | --- |
| Nombre de documents maximums dans un message | Un seul. | 
| Chargement d'historique possible ?  | Oui (sous réserve que le status de la démarche le permette; donc si une démarche n'est pas terminée) |
| Est-ce possible de mettre à jour des courriers / documents | Non (à la place, envoyer un courrier rectificatif) |
| Courrier possible pour quel statut de démarche ? | N'importe quel statut d'une démarche qui n'est pas une suggestion ou un brouillon.
Si une démarche est terminée: l'administration a 7 jours pour envoyer les derniers documents/courrier sinon -> erreur |
| Action ENU front | Afficher dans la démarche concernée |
| Notifications | Non. |
| Renvoi de documents | Si tous les champs sont identiques (idempotence) , rien ne se passe. Sinon les documents s'accumulent dans l'ENU |
| Taille de fichier max | 10kb |


---











---

A REPRENDRE: A PARTIR DE CE POINT
---

A REPRENDRE: A PARTIR DE CE POINT
---

A REPRENDRE: A PARTIR DE CE POINT

---

A REPRENDRE: A PARTIR DE CE POINT

| `documents[i].contenu` | contenu du fichier en base64 | oui si `ged` est absent, pas applicable sinon | \- | Maximum 10'000'000 caractères |



`ENRICHISSEMENT_DE_DEMANDE` | Doit valoir soit `ENRICHISSEMENT_DE_DEMANDE`, soit `REPONSE_DEMANDE_RENSEIGNEMENT` |  
| `dateEcheanceAction` | date avant laquelle l'usager est sensé effectuer l'opération sur la démarche | oui si `urlAction` est fournie, pas applicable sinon | `2021-02-18` | La date uniquement, sans l'heure |




### Ajout d'un document à une démarche : message JSON

Il s'agit ici de compléter une démarche qui a été précédemment créée.

En-têtes nécessaires :

*   `Content-Type` = `application/new-document-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager propriétaire de la démarche | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier | oui | `AEL-100000` | Maximum 50 caractères |
| `typeDocument` | type de document | oui | `RECAPITULATIF` | Doit valoir soit `RECAPITULATIF`, soit `JUSTIFICATIF` |
| `libelleDocument` | titre du document, déterminant le nom du fichier | oui | `Décision administration 2020-02-19` | Maximum 50 caractères |
| `idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée et pour l'usager donné. Maximum 50 caractères |
| `mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, la seule valeur possible est `application/pdf` |
| `contenu` | contenu du fichier en base64 | oui si `ged` est absent, pas applicable sinon | \- | Maximum 10'000'000 caractères |
| `ged` | données GED du document | oui si `contenu` est absent, pas applicable sinon | \- | Voir le chapitre consacré à la GED, au bas de cette page |
| `ged.fournisseur` | identifiant d'une GED | oui | `DATA_CONTENT` | Actuellement la seule valeur possible est `DATA_CONTENT` |
| `ged.version` | version de l'interfaçage à la GED | oui | `1` | Actuellement la seule valeur possible est `1` |
| `ged.idDocument` | identifiant du document dans la GED | oui | `123456` | Cette valeur est été fournie par la GED lorsque le document a été stocké dans la GED |
| `ged.algorithmeHash` | algorithme utilisé par la GED pour calculer l'empreinte du document | oui | `SHA-256` |   |
| `ged.hash` | empreinte du document dans la GED | oui | \- |   |

### Création d'un courrier : message JSON

Il s'agit ici de créer un courrier, c'est-à-dire l'équivalent numérique d'un envoi postal à l'usager.  
Le courrier est constitué d'un ou plusieurs documents, ainsi que d'un en-tête.  
Le courrier peut soit porter sur une démarche qui a été précédemment créée, soit ne porter sur aucune démarche.

En-têtes nécessaires :

*   `Content-Type` = `application/new-courrier-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation, et donc de la catégorie du courrier | oui | `FL_SOCIAL_INDICATEL` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager à qui le courrier est destiné. Si `type` vaut `LIE`, l'usager doit être le propriétaire de la démarche | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier. Il s'agit de la démarche à laquelle le courrier est rattaché. Si ce champ n'est pas fourni, le courrier est un courrier hors démarche | non | `AEL-100000` | Maximum 50 caractères |
| `libelleCourrier` | titre du courrier | oui | `Notification de l'impôt` | Maximum 50 caractères |
| `documents[i].libelleDocument` | titre du document, déterminant le nom du fichier | oui | `Décision administration 2020-02-19` | Maximum 50 caractères |
| `documents[i].idDocumentSiMetier` | identifiant permettant au SI métier d'identifier son document | oui | `DOC-123456789` | Doit doit être unique, pour la prestation donnée et pour l'usager donné. Maximum 50 caractères |
| `documents[i].mime` | type MIME du fichier | oui | `application/pdf` | Actuellement, la seule valeur possible est `application/pdf` |
| `documents[i].contenu` | contenu du fichier en base64 | oui si `ged` est absent, pas applicable sinon | \- | Maximum 10'000'000 caractères |
| `documents[i].ged` | données GED du document | oui si `contenu` est absent, pas applicable sinon | \- | Voir le chapitre consacré à la GED, au bas de cette page |
| `documents[i].ged.fournisseur` | identifiant d'une GED | oui | `DATA_CONTENT` | Actuellement la seule valeur possible est `DATA_CONTENT` |
| `documents[i].ged.version` | version de l'interfaçage à la GED | oui | `1` | Actuellement la seule valeur possible est `1` |
| `documents[i].ged.idDocument` | identifiant du document dans la GED | oui | `123456` | Cette valeur est été fournie par la GED lorsque le document a été stocké dans la GED |
| `documents[i].ged.algorithmeHash` | algorithme utilisé par la GED pour calculer l'empreinte du document | oui | `SHA-256` |   |
| `documents[i].ged.hash` | empreinte du document dans la GED | oui | \- |   |

L'indice `i` ci-dessus commence à 0, pour le premier document du courrier.

Le courrier doit contenir au minimum 1 document et au maximum 20 documents.





## Messages de l'Espace numérique pour le SI métier

L'essentiel du trafic se fait dans le sens SI métier -> Espace numérique,  
cependant certains messages vont dans l'autre sens.

| Message | Producteur | Consommateur | Exchange RabbitMQ / queue RabbitMQ |
| --- | --- | --- | --- |
| réponse à un message | enu-mediation | SI métier | enu-to-si1 / enu-to-si1-main |
| destruction d'une démarche brouillon | enu-backend (1) | SI métier | enu-to-si1 / enu-to-si1-main |
| consultation d'un document par l'usager | enu-backend (1) | SI métier | enu-to-si1 / enu-to-si1-main |
| changement de mode de réception des documents par l'usager | enu-backend (1) | SI métier | enu-to-si1 / enu-to-si1-main |

(1) enu-backend est le producteur initial du message. En fait, le message est relayé, sans modification,  
par enu-mediation.



### Réponse à un message

Cas d'usage : le SI métier a envoyé un message à l'ENU, par exemple un message de création d'une démarche.  
L'ENU a traité ce message d'origine et en retour envoie au SI métier un message ; celui-ci est un message de  
réussite ou d'échec.

En-têtes garantis :

*   `ContentType` = `application/reply-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| resultat | résultat du traitement du message d'origine | oui | OK | Doit valoir soit `OK`, soit `KO` |
| description | description du résultat | oui si `resultat` vaut `KO`, pas applicable sinon | Le champ "idPrestation" est obligatoire | \- |

En l'occurrence, l'identifiant de corrélation n'est pas créé par le producteur `enu-mediation`, mais est  
identique à l'identifiant contenu dans le message d'origine.

### Destruction d'une démarche brouillon : message JSON

Cas d'usage : l'usager a une ou plusieurs suggestions de démarches à compléter.  
Il décide d'en détruire une plutôt que de la compléter.

En-têtes garantis :

*   `ContentType` = `application/brouillon-deletion-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation | oui | `FL_TER_PERMISPECHE` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDemarcheSiMetier` | identifiant de la démarche dans le SI métier | oui | `AEL-100000` | Taille maximale : 50 caractères |

### Consultation d'un document par l'usager : message JSON

Cas d'usage : l'usager a consulté un document, ou bien le délai qui lui était imparti pour consulter  
ce document est écoulé.

En-têtes garantis :

*   `ContentType` = `application/document-access-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
| `idPrestation` | identifiant de la prestation | oui | `FL_TER_PERMISPECHE` | Fourni par l'équipe médiation |
| `idUsager` | identifiant de l'usager | oui | `CGE-1000000` | Cet usager doit être connu de Gina |
| `idDocumentSiMetier` | identifiant du document dans le SI métier | oui | `DOC-123456789` | Taille maximale : 50 caractères |

### Changement de mode de réception des documents par l'usager : message JSON

Cas d'usage : l'usager a modifié son choix du mode de réception de ses documents.

En-têtes garantis :

*   `ContentType` = `application/document-reception-mode-v1.0+json`
*   `CorrelationId` (voir au bas de cette page)

Champs :

| Nom | Description | Obligatoire | Exemple | Commentaire |
| --- | --- | --- | --- | --- |
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
| --- | --- | --- |
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
