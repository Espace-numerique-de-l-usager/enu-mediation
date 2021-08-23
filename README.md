# Espace numérique de l'usager : médiation

Build GitHub :

[![Build with GitHub](https://github.com/Espace-numerique-de-l-usager/enu-mediation/actions/workflows/maven.yml/badge.svg)](https://github.com/Espace-numerique-de-l-usager/enu-mediation/actions/workflows/maven.yml)

Analyse SonarCloud :

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Espace-numerique-de-l-usager_enu-mediation&metric=bugs)](https://sonarcloud.io/dashboard?id=Espace-numerique-de-l-usager_enu-mediation)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Espace-numerique-de-l-usager_enu-mediation&metric=code_smells)](https://sonarcloud.io/dashboard?id=Espace-numerique-de-l-usager_enu-mediation)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Espace-numerique-de-l-usager_enu-mediation&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=Espace-numerique-de-l-usager_enu-mediation)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Espace-numerique-de-l-usager_enu-mediation&metric=coverage)](https://sonarcloud.io/dashboard?id=Espace-numerique-de-l-usager_enu-mediation)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Espace-numerique-de-l-usager_enu-mediation&metric=alert_status)](https://sonarcloud.io/dashboard?id=Espace-numerique-de-l-usager_enu-mediation)

Licence :

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/why-affero-gpl.html)

Ce projet définit les médiations pour le système Espace numérique de l'usager :

SI métier <---> RabbitMQ <---> médiation <---> FormServices

où "SI métier" signifie "un système informatique de l'État", par exemple un système
de l'Administration fiscale cantonale.

Une vue d'ensemble du système Espace numérique de l'usager est disponible sur
[GitHub](https://github.com/Espace-numerique-de-l-usager/enu-geneve).
Le rôle de l'application de médiation y est clairement présenté.

## Installation et exécution

Voir [ici](docs/installation.md).

## Organisation des queues RabbitMQ

Voir [ici](docs/queues_rabbitmq.md).

## Ajout d'un nouveau SI métier

Voir [ici](docs/nouveau_si_metier.md).

## Messages JSON à RabbitMQ

Voir [ici](docs/messages.md).

## Test

Voir [ici](docs/test.md).

## Divers

Voir [ici](docs/divers.md).
