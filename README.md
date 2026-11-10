# Auto-Battler

Application web de gestion d'une guilde de héros, réalisée dans le cadre du projet JEE (CY Tech, 2026).
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

| Membre        | Parties réalisées |
|---------------|-------------------|
| Louis ESCALLE | …                 |
| Léo VAYRAC    | …                 |
| Louis GRIGNON | …                 |

## Crédits

- A COMPLETER