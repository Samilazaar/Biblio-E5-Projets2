# Bibliothèque Application

## Description

Cette application de bibliothèque permet aux utilisateurs de s'inscrire, se connecter et emprunter des livres. Elle comporte trois interfaces principales : une interface d'accueil, une interface de présentation des livres, et une interface d'administration.

## Fonctionnalités

- **Inscription et Connexion**
  - Inscription de nouveaux utilisateurs
  - Connexion des utilisateurs existants

- **Présentation des Livres**
  - Affichage des livres disponibles
  - Recherche de livres par titre ou catégorie
  - Emprunt de livres
  - Visualisation du panier d'emprunts

- **Administration**
  - Ajout de nouveaux livres
  - Suppression de livres existants

## Installation

### Prérequis

- Java Development Kit (JDK) 8 ou supérieur
- Serveur MySQL

### Configuration de la base de données

1. Ouvrez votre terminal MySQL et exécutez les commandes suivantes pour créer la base de données et les tables nécessaires :

    ```sql
    DROP DATABASE IF EXISTS MaBibliotheque;
    CREATE DATABASE IF NOT EXISTS MaBibliotheque;
    USE MaBibliotheque;

    CREATE TABLE IF NOT EXISTS livre (
        id_livre INT PRIMARY KEY AUTO_INCREMENT,
        titre VARCHAR(255) NOT NULL,
        description TEXT,
        categorie VARCHAR(50),
        prix DECIMAL(10, 2),
        auteur VARCHAR(100) NOT NULL
    );

    CREATE TABLE IF NOT EXISTS utilisateurs (
        id_utilisateur INT PRIMARY KEY AUTO_INCREMENT,
        username VARCHAR(50) NOT NULL,
        mdp VARCHAR(255) NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL
    );

    CREATE TABLE IF NOT EXISTS emprunts (
        id_emprunt INT PRIMARY KEY AUTO_INCREMENT,
        id_utilisateur INT NOT NULL,
        id_livre INT NOT NULL,
        date_emprunt DATE NOT NULL,
        date_retour_prevue DATE NOT NULL,
        FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id_utilisateur),
        FOREIGN KEY (id_livre) REFERENCES livre(id_livre)
    );
    ```

### Configuration de l'application

1. Clonez ce repository sur votre machine locale.

    ```bash
    git clone <url-du-repository>
    cd <nom-du-repository>
    ```

2. Ouvrez le projet dans votre IDE Java préféré (Eclipse, IntelliJ, NetBeans, etc.).

3. Assurez-vous que le fichier `biblio.java` contient les informations correctes de connexion à la base de données :

    ```java
    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/MaBibliotheque", "root", "root")) {
        // ...
    }
    ```

## Exécution

1. Compilez les fichiers Java dans votre IDE.
2. Exécutez le fichier principal `biblio.java`.

    ```bash
    javac biblio.java
    java biblio
    ```

## Structure des Fichiers

- `biblio.java` : Interface principale de l'application avec la page d'accueil.
- `livres.java` : Interface de présentation des livres avec la recherche et l'emprunt.
- `admin.java` : Interface d'administration pour ajouter et supprimer des livres.

## Utilisation

### Connexion

1. Inscrivez-vous en tant que nouvel utilisateur.
2. Connectez-vous avec vos informations d'identification.
3. Accédez aux différentes fonctionnalités de l'application selon vos besoins (emprunt de livres, administration, etc.).

