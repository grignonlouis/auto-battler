# Auto-Battler

Application web Spring Boot de gestion de ressources pour un jeu fantasy.
Projet JEE, CY Tech, 2026.

L'implémentation actuelle couvre l'inscription, la connexion, et la gestion des donjons, monstres, équipements et compétences.
Une API REST expose le ping, les donjons, les monstres et les équipements.
Les entités de héros, d'équipes et de combats sont présentes ; le moteur de combat n'est pas implémenté.

## Technologies

* Java 21, Maven
* Spring Boot 4.1.2 (MVC, Spring Data JPA, Thymeleaf, Validation)
* H2 en fichier
* Bootstrap 5 (CDN)
* JUnit 5, Mockito, collection Bruno

## Prérequis

* JDK 21
* Maven fourni par le wrapper (`./mvnw`, Maven 3.10.0)

Les commandes ci-dessous se lancent dans le dossier `auto-battler/`.

## Installation

```bash
cd auto-battler
./mvnw -DskipTests package
```

## Lancement

```bash
./mvnw spring-boot:run
```

* Application : http://localhost:8080
* Console H2 : http://localhost:8080/h2-console

Profil `final` (base `./db/gamedb`, console H2 et logs SQL désactivés) :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=final
```

JAR :

```bash
java -jar target/auto-battler-0.0.1-SNAPSHOT.jar
```

## Tests

```bash
./mvnw test
```

## Documentation

Le guide est dans `auto-battler/docs/`.
Génération HTML, sans compiler ni tester l'application :

```bash
./mvnw -Pdocs asciidoctor:process-asciidoc
```

Ouvrir `target/docs/index.html`.

## Équipe

| Membre |
|---|
| Louis ESCALLE |
| Léo VAYRAC |
| Louis GRIGNON |
