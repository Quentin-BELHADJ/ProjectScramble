## ProjectScramble

**Description**
Programme Java en ligne de commande (CLI) permettant le chiffrement (brouillage visuel) et le déchiffrement de fichiers vidéo. L'objectif est de sécuriser le contenu visuel d'une vidéo via un traitement algorithmique `((r + (2s+1)idLigne)%size)`, tout en conservant la possibilité de restaurer le flux original.

**Technologies utilisées**
* **Langage :** Java
* **Traitement d'image/vidéo :** OpenCV

**Fonctionnalités principales**
* Importation de fichiers vidéo.
* Chiffrement vidéo : Application d'un algorithme de "scrambling" pour rendre la vidéo illisible.
* Déchiffrement vidéo : Restauration de la vidéo originale à partir du fichier chiffré.
* Interface en ligne de commande pour piloter les traitements.

**Démonstration**
Le projet en action :
---
Vidéo chiffrée :  
![Encryption Demo](https://github.com/Quentin-BELHADJ/ProjectScramble/raw/main/medias/scrambled_test.gif)

---
Vidéo déchiffrée :  
![Decryption Demo](https://github.com/Quentin-BELHADJ/ProjectScramble/raw/main/medias/unscrambled_test.gif)
---

### Détails techniques & Retour d'expérience

**Défis rencontrés**
* **Performance :** Gestion des problèmes de qualité d'image liés à l'utilisation du codec MJPG lors du traitement frame par frame.

**Évolutions possibles**
* Implémentation d'un déchiffrement sans clé.
* Développement d'une interface graphique utilisateur (GUI) avec JavaFX.
