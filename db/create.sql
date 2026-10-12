-- Cree la base de l'application. Les tables sont creees par Hibernate au demarrage.
CREATE DATABASE IF NOT EXISTS ras CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Utilisateur dedie a l'application (plutot que root).
-- Remplacer <mot_de_passe> avant d'executer : le vrai mot de passe n'est jamais versionne,
-- l'application le lit dans la variable d'environnement DB_PASSWORD.
CREATE USER IF NOT EXISTS 'ras'@'%' IDENTIFIED BY '<mot_de_passe>';
GRANT ALL PRIVILEGES ON ras.* TO 'ras'@'%';
FLUSH PRIVILEGES;
