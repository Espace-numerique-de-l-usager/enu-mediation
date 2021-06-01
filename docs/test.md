# Test

Pour tester enu-mediation, deux outils fonctionnellement à peu près équivalents sont proposés :
enu-mediation-client et le faux SI métier.

Ces deux outils fonctionnent par l'envoi de messages JSON dans une queue RabbitMQ.

## L'outil enu-mediation-client
L'outil enu-mediation-client est une simple application Java sans interface utilisateur.
Il est plutôt adapté pour un développeur de enu-mediation ou pour un développeur d'un SI métier
devant interfacer son système avec l'Espace numérique de l'usager.
Les messages JSON y sont explicites.
Il en existe deux versions équivalentes :
- `enu-mediation-client-camel` : la communication avec RabbitMQ se fait via Apache Camel.
  Cette version n'est plus maintenue à partir de mai 2021.
- `enu-mediation-client-spring` : la communication avec RabbitMQ se fait via Spring RabbitMQ.

## Le faux SI métier
Le faux SI métier (`enu-si-metier`) est une petite application HTML 5 qui permet,
via une interface utilisateur minimale, de produire des messages RabbitMQ.
Il est plutôt adapté pour un analyste métier ou un testeur fonctionnel.
Les messages JSON y sont cachés à l'utilisateur.
