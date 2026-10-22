# ⛏️ Minecraft Classic / Pré-Classique (WASM & Code Source Décompilé)

Ce dépôt contient la décompilation complète de la première version de Minecraft (`client.jar` - **Minecraft 0.0.11a** / **rd-132211**) ainsi qu'un port exécutable en **WebAssembly (WASM)** autonome, jouable instantanément dans n'importe quel navigateur web sur PC et prêt à être hébergé sur **GitHub Pages**.

---

## 🎮 Jouer en ligne (GitHub Pages)

### Comment héberger ce projet sur GitHub Pages en 30 secondes :
1. Poussez ce dossier sur un dépôt GitHub (par exemple `mon-minecraft-classic`).
2. Allez dans les **Settings** (Paramètres) de votre dépôt sur GitHub.
3. Cliquez sur l'onglet **Pages** (dans le menu de gauche).
4. Sous **Build and deployment > Branch**, sélectionnez la branche `main` (ou `master`) et le dossier `/ (root)`, puis cliquez sur **Save**.
5. Votre jeu sera accessible publiquement à l'adresse :  
   `https://<votre-pseudo>.github.io/<nom-du-depot>/`

---

## 🕹️ Contrôles du Jeu sur PC

Le jeu est optimisé pour les claviers français (AZERTY) et internationaux (QWERTY), ainsi que pour les flèches directionnelles :

| Action | Clavier / Souris |
| :--- | :--- |
| **Avancer** | <kbd>Z</kbd> (AZERTY) ou <kbd>W</kbd> (QWERTY) ou <kbd>↑</kbd> |
| **Reculer** | <kbd>S</kbd> ou <kbd>↓</kbd> |
| **Gauche** | <kbd>Q</kbd> (AZERTY) ou <kbd>A</kbd> (QWERTY) ou <kbd>←</kbd> |
| **Droite** | <kbd>D</kbd> ou <kbd>→</kbd> |
| **Sauter** | <kbd>Espace</kbd> |
| **Regarder** | Déplacement de la souris (avec verrouillage du pointeur) |
| **Creuser / Détruire** | <kbd>Clic Gauche</kbd> |
| **Poser un bloc** | <kbd>Clic Droit</kbd> |
| **Sélectionner un bloc** | Touches <kbd>1</kbd> à <kbd>7</kbd> ou Molette de la souris |
| **Réapparaître (Respawn)** | <kbd>R</kbd> |
| **Plein écran** | Bouton `⛶` dans l'interface ou <kbd>F11</kbd> |
| **Libérer la souris / Menu** | <kbd>Échap</kbd> |

### Blocs Disponibles :
1. **Pierre** (*Stone*)
2. **Herbe** (*Grass*)
3. **Terre** (*Dirt*)
4. **Pavé** (*Cobblestone*)
5. **Bois / Planches** (*Wood*)
6. **Bûche** (*Log*)
7. **Feuilles** (*Leaves*)

---

## 📂 Structure du Répertoire

- **`index.html`** : L'interface web complète et le moteur de jeu, intégrant le HUD, les contrôles ZQSD/WASD, le sélecteur de blocs, les effets sonores synthétisés Web Audio et le chargement du module WebAssembly (avec repli automatique).
- **`minecraft.wasm`** : Le binaire WebAssembly autonome (20.5 Ko), compilé sans libc ni dépendance externe pour des performances maximales à 60 FPS.
- **`assets/`** : Les textures et polices originales extraites de `client.jar` (`char.png`, `terrain.png`, `default.gif`).
- **`src/`** : Le code source Java décompilé et structuré en packages :
  - `com.mojang.minecraft.Minecraft` : Boucle principale de jeu, initialisation d'OpenGL/LWJGL, gestion du framerate, brouillard, rendu de l'interface GUI.
  - `com.mojang.minecraft.Player` : Entité du joueur, mouvements, caméra à la première personne, calculs de rotation.
  - `com.mojang.minecraft.character.*` : Modèles 3D (`Cube`, `Polygon`, `Vertex`, `ZombieModel`) et entité `Zombie`.
  - `com.mojang.minecraft.level.*` : Gestion du monde en voxels (`Level`), générateur de terrain (`LevelGen`, `NoiseMap`), découpage en chunks (`Chunk`, `LevelRenderer`).
  - `com.mojang.minecraft.level.tile.*` : Définition des blocs (`Tile`, `GrassTile`, `DirtTile`, `Bush`).
  - `com.mojang.minecraft.particle.*` : Moteur de particules lors de la destruction des blocs (`ParticleEngine`, `Particle`).
  - `com.mojang.minecraft.phys.AABB` : Boîtes englobantes alignées sur les axes (AABB) pour la détection de collision physique.
  - `com.mojang.minecraft.renderer.*` : Pipeline graphique (`Tesselator`, `Frustum` culling, gestionnaire de textures `Textures`).
  - `com.mojang.minecraft.comm.*` & `server.*` : Premiers essais de sockets réseau et de serveur multijoueur créés par Notch.
- **`client.jar`** : Le fichier JAR original de Minecraft 0.0.11a.

---

## 🛠️ Test Local

Pour tester le jeu localement sur votre ordinateur sans installer de serveur web tiers :
```bash
python -m http.server 8000
```
Puis ouvrez votre navigateur sur [http://localhost:8000](http://localhost:8000).

*(Note : Grâce au système de secours intégré, `index.html` fonctionne également si vous double-cliquez directement dessus dans l'explorateur de fichiers).*
