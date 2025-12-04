# 🎮 Fist of Steel: Marvin's Vengeance

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![LibGDX](https://img.shields.io/badge/LibGDX-1.12+-red?style=for-the-badge)
![Gradle](https://img.shields.io/badge/Gradle-8.5-blue?style=for-the-badge&logo=gradle)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**Un beat'em up 2D rétro en pixel art développé avec LibGDX**

[ Télécharger](#-installation) • [Jouer](#-lancer-le-jeu) • [Documentation](#-structure-du-projet) • [Contribuer](#-contribution)

</div>

---

## À propos

**Fist of Steel: Marvin's Vengeance** est un jeu de combat 2D de type beat'em up développé en Java avec le framework LibGDX. Incarnez Alexis ou Hugo et affrontez des hordes d'ennemis à travers 4 niveaux pour vaincre le boss final : Marvin !

### Caractéristiques

- **2 personnages jouables** : Alexis (mêlée avec combos) et Hugo (attaques à distance)
- **Système de combat** : Combos, blocage, esquive et attaques spéciales
- **Système d'équipement** : Armures et armes avec bonus de stats
- **4 niveaux progressifs** avec un boss final
- **Bande sonore immersive** avec gestion séparée musique/effets
- **Progression** : Santé et équipement conservés entre les niveaux

---

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :

| Outil              | Version minimale                 | Vérification     |
| ------------------ | -------------------------------- | ----------------- |
| **Java JDK** | 21+                              | `java -version` |
| **Gradle**   | 8.0+ (optionnel, wrapper inclus) | `./gradlew -v`  |
| **Git**      | Dernière version                | `git --version` |

### Installation de Java 21

<details>
<summary>Windows</summary>

1. Téléchargez [Eclipse Temurin JDK 21](https://adoptium.net/)
2. Exécutez l'installateur
3. Ajoutez `JAVA_HOME` aux variables d'environnement

</details>

<details>
<summary>macOS</summary>

```bash
brew install openjdk@21
```

</details>

<details>
<summary>Linux (Ubuntu/Debian)</summary>

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

</details>

---

## Installation

### Option 1 : Cloner le repository

```bash
# Cloner le projet
git clone https://github.com/VOTRE_USERNAME/fist-of-steel.git

# Accéder au dossier
cd fist-of-steel

# Vérifier que tout fonctionne
./gradlew --version
```

### Option 2 : Télécharger le ZIP

1. Cliquez sur le bouton vert **Code** en haut de la page
2. Sélectionnez **Download ZIP**
3. Extrayez l'archive
4. Ouvrez un terminal dans le dossier extrait

---

## Lancer le jeu

### Méthode rapide (recommandée)

```bash
./gradlew desktop:run
```

> **Note Windows** : Utilisez `gradlew.bat desktop:run` au lieu de `./gradlew`

### Première exécution

La première fois, Gradle téléchargera les dépendances (~100 Mo). Cela peut prendre quelques minutes selon votre connexion.

```bash
# Linux/macOS
./gradlew desktop:run

# Windows
gradlew.bat desktop:run
```

### Créer un exécutable (.jar)

```bash
./gradlew desktop:dist
```

Le fichier JAR sera généré dans : `desktop/build/libs/desktop-1.0.jar`

Pour l'exécuter :

```bash
java -jar desktop/build/libs/desktop-1.0.jar
```

---

## Contrôles

| Action                  | Touche                   |
| ----------------------- | ------------------------ |
| Déplacement gauche     | `A` ou `←`          |
| Déplacement droite     | `D` ou `→`          |
| Sauter                  | `ESPACE`               |
| Attaquer                | `Q` ou `Clic gauche` |
| Bloquer                 | `E`                    |
| S'accroupir / Fast fall | `S`                    |
| Mode debug (hitboxes)   | `F3`                   |
| Retour menu             | `ÉCHAP`               |

---

## Structure du projet

```
fist-of-steel/
├── 📁 core/                    # Code source principal
│   └── src/com/fistofsteel/
│       ├── 📁 audio/           # Gestion audio
│       ├── 📁 entities/        # Joueurs, ennemis, projectiles
│       │   ├── enemies/        # Knight, Mage, Rogue, Boss
│       │   ├── managers/       # EnemyManager, ProjectileManager...
│       │   ├── player/         # Alexis, Hugo, Player
│       │   ├── projectiles/    # Projectiles du jeu
│       │   └── world/          # Items, sorties de niveau
│       ├── 📁 input/           # Gestion des entrées
│       ├── 📁 items/           # Armes, armures, potions
│       ├── 📁 screens/         # Écrans (menu, jeu, options...)
│       ├── 📁 ui/              # Interface utilisateur
│       └── 📁 utils/           # Utilitaires et constantes
├── 📁 desktop/                 # Launcher desktop
├── 📁 assets/                  # Ressources du jeu
│   ├── 📁 sprites/             # Textures et animations
│   ├── 📁 maps/                # Niveaux Tiled (.tmx)
│   ├── 📁 music/               # Musiques
│   ├── 📁 sounds/              # Effets sonores
│   └── 📁 items/               # Icônes d'objets
├── 📁 maps/                    # Fichiers de maps
├── 📄 build.gradle             # Configuration Gradle principale
├── 📄 settings.gradle          # Paramètres Gradle
└── 📄 README.md                # Ce fichier
```

---

## Tests

Exécuter les tests unitaires :

```bash
./gradlew test
```

Les tests couvrent :

- ✅ Système de combos (Alexis)
- ✅ Statistiques des armes
- ✅ Calculs de dégâts

---

## Développement

### Importer dans un IDE

<details>
<summary>IntelliJ IDEA (recommandé)</summary>

1. **File** → **Open**
2. Sélectionnez le dossier du projet
3. Choisissez "Import as Gradle project"
4. Attendez la synchronisation
5. Run → **Edit Configurations** → **+** → **Application**
   - Main class: `com.fistofsteel.DesktopLauncher`
   - Working directory: `$PROJECT_DIR$/assets`
   - Use classpath of module: `desktop.main`

</details>

<details>
<summary>Eclipse</summary>

```bash
./gradlew eclipse
```

Puis importez le projet via **File** → **Import** → **Existing Projects**

</details>

<details>
<summary>VS Code</summary>

1. Installez l'extension "Extension Pack for Java"
2. Ouvrez le dossier du projet
3. Attendez l'indexation
4. Utilisez le terminal intégré pour les commandes Gradle

</details>

### Commandes utiles

| Commande                   | Description             |
| -------------------------- | ----------------------- |
| `./gradlew desktop:run`  | Lancer le jeu           |
| `./gradlew desktop:dist` | Créer le JAR           |
| `./gradlew test`         | Exécuter les tests     |
| `./gradlew clean`        | Nettoyer le build       |
| `./gradlew tasks`        | Voir toutes les tâches |

---

## Assets et Crédits

- **Sprites personnages** : Créés avec génération IA + retouches
- **Maps** : Créées avec [Tiled Map Editor](https://www.mapeditor.org/)
- **Musiques** : Compositions originales
- **Framework** : [LibGDX](https://libgdx.com/)

---

## Troubleshooting

<details>
<summary>❌ "Could not find or load main class"</summary>

```bash
./gradlew clean desktop:run
```

</details>

<details>
<summary>❌ Erreur OpenGL / Écran noir</summary>

- Mettez à jour vos drivers graphiques
- Essayez de lancer avec : `java -jar -Dorg.lwjgl.opengl.Display.allowSoftwareOpenGL=true desktop.jar`

</details>

<details>
<summary>❌ Assets non trouvés</summary>

Vérifiez que le dossier `assets/` est bien présent et contient les sous-dossiers `sprites/`, `maps/`, `music/`, `sounds/`

</details>

<details>
<summary>❌ Permission denied (Linux/macOS)</summary>

```bash
chmod +x gradlew
./gradlew desktop:run
```

</details>

---

## Contribution

Les contributions sont les bienvenues ! Voici comment participer :

1. **Fork** le projet
2. Créez une branche (`git checkout -b feature/ma-fonctionnalite`)
3. Committez vos changements (`git commit -m 'Ajout de ma fonctionnalité'`)
4. Push sur la branche (`git push origin feature/ma-fonctionnalite`)
5. Ouvrez une **Pull Request**

### Guidelines

- Respectez le style de code existant
- Ajoutez des commentaires Javadoc pour les nouvelles classes
- Testez vos modifications avant de soumettre

---

## License

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## Auteurs

- **Alterax** - *Développeur principal*

</div>
