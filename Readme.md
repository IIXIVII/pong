# Pong multijoueur en Java

Ce projet est un jeu de Pong multijoueur en réseau reposant sur une architecture client-serveur en Java. Il intègre une gestion d'états partagés, des obstacles dynamiques et une interface graphique découpée en plusieurs vues (accueil, salon d'attente, partie et fin de match).


## Prérequis

* **Java Development Kit (JDK)** version 11 ou supérieure.
* Un terminal (Bash, PowerShell ou invite de commandes).



## Compilation et lancement

### 1. Compilation

Place-toi à la racine du projet, puis compile tous les fichiers sources vers un dossier `bin` :

**Sous Linux / macOS :**

```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")

```

**Sous Windows (PowerShell) :**

```powershell
New-Item -ItemType Directory -Force -Path bin
javac -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName

```

---

### 2. Démarrage du serveur

Lance le serveur dans un premier terminal. Il se mettra en écoute des connexions clientes :

```bash
java -cp bin Server.Server

```

---

### 3. Démarrage des clients

Ouvre un terminal distinct pour chaque joueur et exécute l'application cliente :

```bash
java -cp bin Game.PongClientApp

```

Pour lancer une partie complète à deux joueurs, exécute cette commande une seconde fois dans un troisième terminal.
