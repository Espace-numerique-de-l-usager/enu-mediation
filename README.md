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
git clone https://argon.***REMOVED***/gitlab/ACCES_RESTREINT/3417_espace_numerique_usager/faisabilite/rabbit-send.git
```
- Créer un projet IntelliJ rabbit-send
- Depuis IntelliJ, lancer l'application rabbit-send. Une invite "Enter something:" s'affiche
- Taper dans l'invite le message JSON d'une nouvelle démarche. Exemple :
```
{"idPrestation": "EDGSmartOne_afl", "idUsager": "DUBOISPELERINY", "idClientDemande": "Dossier-pipo-20", "etat": "BROUILLON", "urlFormulaire": "http://www.tdg.ch", "libelleAction": "Prendre le tram", "echeanceAction": "2021-02-18"} 
```
(pour un autre exemple, voir ci-dessous)

- Depuis IntelliJ, lancer l'application enu-mediation. La console va montrer que le message ci-dessus est 
consommé, transformé et adressé à un service REST du backend ENU.

#### Autres exemples
L'exemple ci-dessus était le plus simple : celui de la création d'une nouvelle demande.
Un autre cas exemple est celui du changement d'état d'une demande existante.
Pour ce cas-là, il y a une étape préalable : dans l'application rabbit-send, classe MessageSender, veiller à
configurer ainsi le Content-Type :
```
   .setHeader("Content-Type", simple("application/json-status-change"))
```
Ensuite, lancer comme précédemment l'aplication rabbit-send.
Taper dans l'invite le message JSON d'un changement d'état. Exemple :
```
{"idPrestation": "EDGSmartOne_afl", "idUsager": "DUBOISPELERINY", "idClientDemande": "Dossier-pipo-20", "nouvelEtat": "EN_COURS", "dateNouvelEtat": "2022-02-18", "libelleSousEtat": "Un état second", "typeAction": "ENRICHISSEMENT_DE_DEMANDE", "urlAction": "https://www.humanite.fr", "libelleAction": "Lire des trucs", "echeanceAction": "2021-02-19" } 
```
Le dossier (idClientDemande) doit au préalable exister.


## Divers

### Console RabbitMQ
La console du RabbitMQ utilisé dans les sources est ici : http://lab-rh712stdc1133a:15672 (matheg/matheg).

### Keystore et Truststore TLS
Voir src/main/resources/application.yml: camel.ssl.config.key-managers.

La configuration actuelle attend un store JKS dans: /pki/lab.jks

Ajuster au besoin soit dans application.yml soit via une variable d'environnement système (par ex. CAMEL_SSL_CONFIG_KEY-MANAGERS_etc. ). Elle remplacera automatiquement la valeur du application.yml. (Doc complète: https://docs.spring.io/spring-boot/docs/2.3.5.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config)

### Doc Spring Boot + Camel:

* [Using Apache Camel with Spring Boot](https://camel.apache.org/camel-spring-boot/latest/spring-boot.html)
