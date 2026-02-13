# 🧘 Yoga App – Projet Full‑Stack

Ce projet est une application full‑stack composée :

- d’un back‑end Spring Boot sécurisé par JWT,
- d’un front‑end Angular,
- de tests unitaires, de tests d’intégration et de tests end‑to‑end (Cypress),
- de rapports de couverture de code pour :
  - le front‑end,
  - le back‑end,
  - les tests end‑to‑end.

---

## Prérequis

- Java 8
- Maven
- Node.js (version LTS recommandée)
- npm
- MySQL (ou MariaDB)
- Git

---

## Installation de la base de données

Créer la base de données :

```sql
CREATE DATABASE yoga_app;
```

Configurer ensuite la connexion dans :

back/src/main/resources/application.properties

Exemple :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/yoga_app?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

Adapter les identifiants à votre environnement.

---

## Installation du projet

Cloner le projet :

```bash
git clone <url-du-repository>
cd <nom-du-repository>
```

---

## Lancer le back‑end

```bash
cd back
mvn clean install
mvn spring-boot:run
```

Le back‑end est disponible à l’adresse :

http://localhost:8080

---

## Lancer le front‑end

Dans un autre terminal :

```bash
cd front
npm install
npm start
```

Le front‑end est accessible à l’adresse :

http://localhost:4200

---

## Lancer les tests

### Tests back‑end

```bash
cd back
mvn test
```

---

### Tests front‑end

```bash
cd front
npm run test
```

---

### Tests end‑to‑end (Cypress)

Le front‑end doit être démarré avant de lancer Cypress.

```bash
cd front
npx cypress run
```

Ou en mode interface graphique :

```bash
npx cypress open
```

---

## Génération des rapports de couverture

### Couverture du back‑end (JaCoCo)

```bash
cd back
mvn clean test
```

Ouvrir ensuite :

back/target/site/jacoco/index.html

---

### Couverture du front‑end (Angular / Karma)

```bash
cd front
npm run test -- --coverage
```

Ouvrir ensuite :

front/coverage/jest/lcov-report/index.html

---

### Couverture des tests end‑to‑end (Cypress)

```bash
cd front
npm run e2e
```

Puis générer le rapport NYC (si configuré dans le projet) :

```bash
npm run e2e:coverage
```

Le rapport est disponible dans :

front/coverage/lcov-report/index.html

---
