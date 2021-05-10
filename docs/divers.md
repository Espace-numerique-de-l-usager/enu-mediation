# Divers

## Keystore et Truststore TLS
Voir src/main/resources/application.yml: camel.ssl.config.key-managers.

La configuration actuelle attend un store JKS dans: /pki/lab.jks

Ajuster au besoin soit dans application.yml soit via une variable d'environnement système (par ex. CAMEL_SSL_CONFIG_KEY-MANAGERS_etc. ). Elle remplacera automatiquement la valeur du application.yml. (Doc complète: https://docs.spring.io/spring-boot/docs/2.3.5.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config)

## Doc Spring Boot + Camel:

* [Using Apache Camel with Spring Boot](https://camel.apache.org/camel-spring-boot/latest/spring-boot.html)
