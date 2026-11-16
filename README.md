# Auto-Battler

<p align="center">
  Application web de gestion d'une guilde de héros, avec combats automatiques simulés côté serveur.<br>
  Projet JEE · CY Tech · 2026
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-2563EB?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.2-0F766E?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Data-JPA-0F766E?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA">
  <img src="https://img.shields.io/badge/Thymeleaf-Templates-0F766E?style=for-the-badge&logo=thymeleaf&logoColor=white" alt="Thymeleaf">
  <img src="https://img.shields.io/badge/H2-Database-0891B2?style=for-the-badge" alt="H2 Database">
  <img src="https://img.shields.io/badge/Bootstrap-5-6366F1?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap 5">
  <img src="https://img.shields.io/badge/Maven-Build-2563EB?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/JUnit-5-0891B2?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5">
  <img src="https://img.shields.io/badge/API-REST-0891B2?style=for-the-badge" alt="API REST">
  <img src="https://img.shields.io/badge/Bruno-API%20Tests-6366F1?style=for-the-badge" alt="Bruno">
  <img src="https://img.shields.io/badge/Status-In%20Progress-475569?style=for-the-badge" alt="Status">
</p>

## Description

Le joueur recrute des héros, compose une équipe de 4 héros maximum, les équipe, puis lance des combats automatiques contre des monstres. Les combats sont simulés côté serveur, en Java, et affichés sous forme de journal.

## Stack technique

- Java 21, Maven
- Spring Boot 4.1.2 (Spring MVC, Spring Data JPA, Thymeleaf, Validation)
- H2 embarquée (fichier)
- Bootstrap 5 (CDN)
- Tests : JUnit 5, Mockito, MockMvc
- Collection d'API : Bruno (dossier `bruno/`)

## Prérequis

- JDK 21
- Maven

## Installation et lancement

### Développement (base locale, non versionnée)

```bash
mvn spring-boot:run
```

- Application : [http://localhost:8080](http://localhost:8080)
- Console H2 : [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL : `jdbc:h2:file:./data/devdb`
- Utilisateur : `sa`
- Mot de passe : vide

### Version livrée (base `db/gamedb.mv.db` incluse dans le dépôt)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=final
```

Utilise la base du dossier `db/`. La console H2 et les logs SQL sont désactivés.

## Commandes Maven

### Compilation

```bash
mvn clean
mvn compile
mvn clean compile
```

### Tests

```bash
mvn test
mvn clean test
```

Les rapports des tests sont disponibles dans `target/surefire-reports/`.

### Génération de la Javadoc

```bash
mvn javadoc:javadoc
```

La documentation HTML générée est disponible dans `target/reports/apidocs/index.html`.

Pour consulter la documentation, ouvrir ce fichier dans un navigateur.

### Packaging et exécution

```bash
mvn package
mvn package -DskipTests
mvn install
mvn clean package
```

Le fichier JAR généré se trouve dans le dossier `target/`.

Pour exécuter le JAR :

```bash
java -jar target/auto-battler.jar
```

## Vérification rapide

- `GET http://localhost:8080/` : page d'accueil
- `GET http://localhost:8080/api/v1/ping` : `{"status":"ok"}`

## Structure du projet

```text
src/main/java/.../
├── entity/       entités JPA et enums
├── repository/   interfaces Spring Data
├── service/      logique métier, transactions, mapping DTO (+ dto/)
├── combat/       moteur de combat (Java pur, sans Spring)
├── controller/   contrôleurs Thymeleaf (vues)
├── rest/         API REST (JSON, DTO uniquement)
└── config/       configuration, DataSeeder

bruno/            collection Bruno
db/               base H2 finale (versionnée à la livraison)
data/             base H2 de développement (ignorée par Git)
docs/             diagramme de classes, captures
```

## API REST

Préfixe `/api/v1`.

La collection Bruno du dossier `bruno/` contient une requête par endpoint, avec ses assertions (cas nominaux et cas d'erreur : 400, 404, 409).

Pour l'utiliser :

1. Ouvrir Bruno.
2. Sélectionner « Open Collection ».
3. Choisir le dossier `bruno/`.
4. Sélectionner l'environnement `local`.

## Équipe

| Membre |
|---|
| Louis ESCALLE |
| Léo VAYRAC |
| Louis GRIGNON |