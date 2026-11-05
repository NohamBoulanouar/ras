# RAS · Rien À Signaler

Application de supervision des équipements d'un réseau de transport et de suivi des incidents. Projet final du module ALTN72 (Développement full stack avec Java), 2026-27.

- **Application en ligne :** https://ras-production-76dd.up.railway.app
- **Documentation complète** (installation, architecture, tests, choix techniques, lot alpha, bilan) : [`journal.md`](journal.md)

## Démarrage rapide

```bash
# 1. Créer la base et l'utilisateur dédié (remplacer <mot_de_passe> dans le script)
mariadb -u root -p < db/create.sql

# 2. Fournir le mot de passe sans l'écrire dans le code
export DB_PASSWORD=...        # ou un fichier application-local.properties (ignoré par Git)

# 3. Lancer l'application (base recréée et remplie de données de démonstration au démarrage)
./mvnw spring-boot:run        # Windows : mvnw.cmd spring-boot:run
```

Puis ouvrir http://localhost:8080.

| Profil | Identifiant | Mot de passe |
|---|---|---|
| Responsable d'exploitation | `c.martin` | `Resp2026!` |
| Agent de station | `t.durand` | `Agent2026!` |

## Tests

```bash
./mvnw verify      # tests unitaires, d'intégration et web sur H2 + couverture JaCoCo (seuil de 80 %)
```

## Pile technique

Java 21 · Spring Boot 4.1 (Web MVC, Data JPA, Security, Validation) · Thymeleaf · MariaDB · springdoc-openapi (Swagger UI sur `/swagger-ui/index.html`) · JUnit 5 · JaCoCo · GitHub Actions · Docker (déploiement sur Railway).
