# Installation

## Préliminaires

### Pour un développeur de l'État de Genève

Faire un clone du projet `enu-mediation-secrets` et copier le fichier `application-dev.properties` dans le
répertoire `src/main/resources`.

### Pour un développeur de la communauté open source

Dans le répertoire `src/main/resources`, copier le fichier `application-dev.template.properties` en
`application-dev.properties` et adapter les valeurs manquantes.

## Construction

L'application `enu-mediation` se construit avec Maven 3 et Java 8+, via la commande
```
mvn clean install
```
## Déploiement

Une fois que l'application est lancée, elle consomme les messages qu'elle trouve dans
les queues RabbitMQ.
Elle ne s'arrête que lorsqu'on l'arrête explicitement (par exemple, par un "contrôle-C").

### Exécution en développement

Dans un environnement de développement comme IntelliJ, il suffit d'exécuter la classe `MediationApplication`,
avec cependant l'option JVM `-Dspring.profiles.active=dev`.

### Exécution sur un serveur JEE

L'application est un "main" Java. Elle n'est en rien une application Web.
Cependant, pour se conformer aux usages de l'équipe de production de l'État de Genève,
il a été préféré de déployer l'application comme un fichier WAR sur un serveur Tomcat.

Toute propriété définie dans le fichier `src/main/resources/enu-mediation-default.properties`
peut être redéfinie. Il suffit pour cela de la définir dans un fichier appelé (obligatoirement)
`enu-mediation.properties`, à placer (obligatoirement) dans le sous-répertoire `conf` de Tomcat.

## Propriétés

TODO: mettre ici les infos fournies dans le
[wiki](https://prod.etat-ge.ch/wikiadm/pages/viewpage.action?pageId=1812824302)
et dans [ENU-704](***REMOVED***/browse/ENU-704). 
