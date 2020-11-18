# Espace numérique de l'usager : médiation

Ce projet définit les médiations pour le système Espace numérique de l'usager :

RabbitMQ <---> médiation <---> FormServices (services REST)

## Exécution

Pour tester, une méthode simple est la suivante :
- créer un message via l'application rabbit-send (qui consiste en quelques lignes de Camel)
- consommer le message via la médiation

Marche à suivre :
- Récupérer les sources de l'application rabbit-send :
```
git clone https://argon.ceti.etat-ge.ch/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/faisabilite/rabbit-send.git
```
- Créer un projet IntelliJ rabbit-send
- Depuis IntelliJ, lancer l'application rabbit-send. Une invite "Enter something:" s'affiche
- Taper dans l'invite le message JSON d'une nouvelle démarche. Exemple :
```
{"idPrestation": "EDGSmartOne_afl", "idUtilisateur": "DUBOISPELERINY", "idClientDemande": "Dossier-pipo-1", "etat": "BROUILLON", "urlFormulaire": "http://www.tdg.ch", "libelleAction": "Prendre le tram", "echeanceAction": "2021-02-18"} 
```
- Depuis IntelliJ, lancer l'application enu-mediation. La console va montrer que le message ci-dessus est 
consommé, transformé et adressé à un service REST du backend enu. 

## Divers

### Console RabbitMQ
La console du RabbitMQ utilisé dans les sources est ici : http://lab-rh712stdc1133a:15672 (matheg/matheg).

### Keystore et Truststore TLS
Voir src/main/resources/application.yml: camel.ssl.config.key-managers.

La configuration actuelle attend un store JKS dans: /pki/lab.jks

Ajuster au besoin soit dans application.yml soit via une variable d'environnement système (par ex. CAMEL_SSL_CONFIG_KEY-MANAGERS_etc. ). Elle remplacera automatiquement la valeur du application.yml. (Doc complète: https://docs.spring.io/spring-boot/docs/2.3.5.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config)

### Doc Spring Boot + Camel:

* [Using Apache Camel with Spring Boot](https://camel.apache.org/camel-spring-boot/latest/spring-boot.html)
