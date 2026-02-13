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
mvn clean test
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
npm run test -- --code-coverage
```

Ouvrir ensuite :

front/coverage/index.html

---

### Couverture des tests end‑to‑end (Cypress)

```bash
cd front
npx cypress run
```

Puis générer le rapport NYC (si configuré dans le projet) :

```bash
npx nyc report --reporter=html
```

Le rapport est disponible dans :

front/coverage/

ou

front/coverage/lcov-report/index.html

(selon la configuration du projet).

---

## Captures d’écran à fournir

Les captures demandées pour le rendu sont :

- la page principale du rapport de couverture front‑end ;
- la page principale du rapport de couverture back‑end ;
- la page principale du rapport de couverture end‑to‑end.

Les captures doivent être fournies au format PNG ou JPEG.

---

## Vérification avant rendu

Il est recommandé de :

- cloner le dépôt dans un nouveau dossier,
- suivre toutes les étapes de ce README,
- vérifier que :
  - le back‑end démarre correctement,
  - le front‑end démarre correctement,
  - tous les tests passent,
  - les rapports de couverture sont accessibles.

---

## Remarques

- Le back‑end utilise Spring Security et JWT.
- Les contrôleurs sont testés avec MockMvc.
- Les packages DTO et Mapper peuvent être exclus du contrôle de couverture conformément aux consignes.
