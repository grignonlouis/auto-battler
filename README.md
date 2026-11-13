<h1 align="center">Auto-Battler</h1>

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

Le joueur recrute des héros, compose une équipe de 4 héros maximum, les équipe, puis lance des combats
automatiques contre des monstres. Les combats sont simulés côté serveur, en Java, et affichés sous forme de journal.

## Stack

- Java 21, Maven
- Spring Boot 4.1.2 (Spring MVC, Spring Data JPA, Thymeleaf, Validation)
- H2 embarquée (fichier)
- Bootstrap 5 (CDN)
- Tests : JUnit 5, Mockito, MockMvc
- Collection d'API : Bruno (dossier `bruno/`)

## Prérequis

- JDK 21
- Maven (ou le wrapper `./mvnw` fourni)

## Lancement

### Développement (base locale, non versionnée)

```bash
mvn spring-boot:run
```

- Application : http://localhost:8080
- Console H2 : http://localhost:8080/h2-console
  (JDBC URL `jdbc:h2:file:./data/devdb`, utilisateur `sa`, mot de passe vide)

### Version livrée (base `db/gamedb.mv.db` incluse dans le dépôt)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=final
```

Utilise la base du dossier `db/`. La console H2 et les logs SQL sont désactivés.

## Vérification rapide

- `GET http://localhost:8080/` : page d'accueil
- `GET http://localhost:8080/api/v1/ping` : `{"status":"ok"}`

## Structure du projet

```
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

Préfixe `/api/v1`. La collection Bruno du dossier `bruno/` contient une requête par endpoint,
avec ses assertions (cas nominaux et cas d'erreur : 400, 404, 409).
Pour l'utiliser : ouvrir Bruno, « Open Collection », choisir le dossier `bruno/`,
puis sélectionner l'environnement `local`.

## Organisation du travail

- `main` est protégée : pas de push direct, tout passe par une pull request.
- Une branche par fonctionnalité : `feature/hero-crud`, `feature/combat-engine`…
- Commits courts et préfixés : `feat:`, `fix:`, `test:`, `chore:`, `docs:`.
- Une seule personne modifie `pom.xml` et `db/`.

## Équipe

| Membre        | 
|---------------|
| Louis ESCALLE | 
| Léo VAYRAC    |
| Louis GRIGNON |
