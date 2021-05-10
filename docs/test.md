# Test

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
