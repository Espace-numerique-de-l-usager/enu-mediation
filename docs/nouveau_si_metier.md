# Configuration d'un nouveau service métier

Cette page décrit la configuration à réaliser chaque fois qu'un nouveau service métier est ajouté
à l'ENU.

On commence par convenir d'un identifiant pour le nouveau service métier,
par exemple "afc" ou "ael-form".
Dans la suite, on considère que l'identifiant est "simetier".

## Travail à mener par l'équipe RabbitMQ

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

## Travail à mener par l'équipe médiation

## Travail à mener par l'équipe SI métier
