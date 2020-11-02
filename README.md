# Espace numérique de l'usager : médiation

Ce projet est définit les médiations pour le système Espace numérique de l'usager :

RabbitMQ <---> médiation <---> FormServices (services REST)

## Modules

### enu-mediation

L'application war destinée à être déployée sur un serveur Tomcat. Elle contient les médiations.

### enu-mediation-client-example

Contient des exemples de clients :
- produisant des messages dans une queue RabbitMQ, qui seront consommés par la médiation
- consommant des messages d'une queue RabbitMQ, produits par la médiation

Ces exemples sont destinés :
- aux développeurs de systèmes clients, tels l'AFC ou les Formulaires en ligne
- aux développeurs de la médiation et des services REST de FormServices

### Doc Spring Boot + Camel:

* [Using Apache Camel with Spring Boot](https://camel.apache.org/camel-spring-boot/latest/spring-boot.html)
