# 2D-Game

Création d'un jeux vidéo en 2D

arboresence :

2D-GAME/
├── .gradle/
│
├── assets/
│ ├── items/
│ │ ├── potion*frame_1.png
│ │ ├── potion_frame_2.png
│ │ ├── potion_frame_3.png
│ │ ├── potion_frame_4.png
│ │ └── potion_frame_5.png
│ │
│ ├── maps/
│ │ ├── background_double.png
│ │ ├── fdhgsjhfshgf.tsx
│ │ ├── gemini_walls_perfect_256x192_transparent.png
│ │ ├── gemini_walls_ZERO_MAGENTA_FINAL.png
│ │ ├── ground_tileset.png
│ │ ├── ground_tileset.tsx
│ │ └── level1_example.tmx
│ │
│ ├── menu/
│ │ ├── character_selection_background.png
│ │ └── menu_background.png
│ │
│ ├── music/
│ │ ├── background_music.mp3
│ │ ├── background_music.ogg
│ │ ├── Untitled-\_1*.ogg
│ │ └── Untitled.ogg
│ │
│ ├── sounds/
│ │ ├── attack.mp3
│ │ ├── attack.ogg
│ │ ├── death.ogg
│ │ ├── death.wav
│ │ ├── hit.mp3
│ │ ├── hit.ogg
│ │ ├── jump.flac
│ │ └── jump.ogg
│ │
│ └── sprites/
│ ├── alexis/
│ │ ├── alexis_attack_1.png
│ │ ├── alexis_attack_2.png
│ │ ├── alexis_attack_3.png
│ │ ├── alexis_attack_4.png
│ │ ├── alexis_block.png
│ │ ├── alexis_crouch.png
│ │ ├── alexis_dead_1.png
│ │ ├── alexis_dead_2.png
│ │ ├── alexis_hit.png
│ │ ├── alexis_idle.png
│ │ ├── alexis_jump.png
│ │ ├── alexis_walk_1.png
│ │ ├── alexis_walk_2.png
│ │ └── alexis_walk_3.png
│ │
│ ├── hugo/
│ │ ├── hugo_attack_1.png
│ │ ├── hugo_attack_2.png
│ │ ├── hugo_block.png
│ │ ├── hugo_crouch.png
│ │ ├── hugo_dead_1.png
│ │ ├── hugo_dead_2.png
│ │ ├── hugo_hit.png
│ │ ├── hugo_idle.png
│ │ ├── hugo_jump.png
│ │ ├── hugo_walk_1.png
│ │ ├── hugo_walk_2.png
│ │ └── hugo_walk_3.png
│ │
│ └── sbires/
│ ├── Knight/
│ │ ├── Attack/
│ │ │ ├── attack0.png
│ │ │ ├── attack1.png
│ │ │ ├── attack2.png
│ │ │ ├── attack3.png
│ │ │ └── attack4.png
│ │ │
│ │ ├── Climb/
│ │ │ ├── climb1.png
│ │ │ ├── climb2.png
│ │ │ ├── climb3.png
│ │ │ └── climb4.png
│ │ │
│ │ ├── Death/
│ │ │ ├── death1.png
│ │ │ ├── death2.png
│ │ │ ├── death3.png
│ │ │ ├── death4.png
│ │ │ ├── death5.png
│ │ │ ├── death6.png
│ │ │ ├── death7.png
│ │ │ ├── death8.png
│ │ │ ├── death9.png
│ │ │ └── death10.png
│ │ │
│ │ ├── Hurt/
│ │ │ ├── hurt1.png
│ │ │ ├── hurt2.png
│ │ │ ├── hurt3.png
│ │ │ └── hurt4.png
│ │ │
│ │ ├── Idle/
│ │ │ ├── idle1.png
│ │ │ ├── idle2.png
│ │ │ ├── idle3.png
│ │ │ ├── idle4.png
│ │ │ ├── idle5.png
│ │ │ ├── idle6.png
│ │ │ ├── idle7.png
│ │ │ ├── idle8.png
│ │ │ ├── idle9.png
│ │ │ ├── idle10.png
│ │ │ ├── idle11.png
│ │ │ └── idle12.png
│ │ │
│ │ ├── Walk/
│ │ │ └── knight.png
│ │
│ ├── Mage/
│ │ ├── Attack/
│ │ │ ├── attack1.png
│ │ │ ├── attack2.png
│ │ │ ├── attack3.png
│ │ │ ├── attack4.png
│ │ │ ├── attack5.png
│ │ │ ├── attack6.png
│ │ │ └── attack7.png
│ │ │
│ │ ├── Death/
│ │ │ ├── death1.png
│ │ │ ├── death2.png
│ │ │ ├── death3.png
│ │ │ ├── death4.png
│ │ │ ├── death5.png
│ │ │ ├── death6.png
│ │ │ ├── death7.png
│ │ │ ├── death8.png
│ │ │ ├── death9.png
│ │ │ └── death10.png
│ │ │
│ │ ├── Fire/
│ │ │ ├── fire1.png
│ │ │ ├── fire2.png
│ │ │ ├── fire3.png
│ │ │ ├── fire4.png
│ │ │ ├── fire5.png
│ │ │ ├── fire6.png
│ │ │ ├── fire7.png
│ │ │ ├── fire8.png
│ │ │ └── fire9.png
│ │ │
│ │ ├── Hurt/
│ │ │ ├── hurt1.png
│ │ │ ├── hurt2.png
│ │ │ ├── hurt3.png
│ │ │ └── hurt4.png
│ │ │
│ │ ├── Idle/
│ │ │ ├── idle1.png
│ │ │ ├── idle2.png
│ │ │ ├── idle3.png
│ │ │ ├── idle4.png
│ │ │ ├── idle5.png
│ │ │ ├── idle6.png
│ │ │ ├── idle7.png
│ │ │ ├── idle8.png
│ │ │ ├── idle9.png
│ │ │ ├── idle10.png
│ │ │ ├── idle11.png
│ │ │ ├── idle12.png
│ │ │ ├── idle13.png
│ │ │ └── idle14.png
│ │ │
│ │ ├── Walk/
│ │ │ ├── walk1.png
│ │ │ ├── walk2.png
│ │ │ ├── walk3.png
│ │ │ ├── walk4.png
│ │ │ ├── walk5.png
│ │ │ └── walk6.png
│ │ │
│ │ └── mage.png
│ │
│ └── Rogue/
│ ├── Attack/
│ │ ├── Attack1.png
│ │ ├── Attack2.png
│ │ ├── Attack3.png
│ │ ├── Attack4.png
│ │ ├── Attack5.png
│ │ ├── Attack6.png
│ │ └── Attack7.png
│ │
│ ├── Climb/
│ │ ├── climb1.png
│ │ ├── climb2.png
│ │ ├── climb3.png
│ │ └── climb4.png
│ │
│ ├── Death/
│ │ ├── death1.png
│ │ ├── death2.png
│ │ ├── death3.png
│ │ ├── death4.png
│ │ ├── death5.png
│ │ ├── death6.png
│ │ ├── death7.png
│ │ ├── death8.png
│ │ ├── death9.png
│ │ └── death10.png
│ │
│ ├── Hurt/
│ │ ├── hurt1.png
│ │ ├── hurt2.png
│ │ ├── hurt3.png
│ │ └── hurt4.png
│ │
│ ├── Idle/
│ │ ├── idle1.png
│ │ ├── idle2.png
│ │ ├── idle3.png
│ │ ├── idle4.png
│ │ ├── idle5.png
│ │ ├── idle6.png
│ │ ├── idle7.png
│ │ ├── idle8.png
│ │ ├── idle9.png
│ │ ├── idle10.png
│ │ ├── idle12.png
│ │ ├── idle13.png
│ │ ├── idle14.png
│ │ ├── idle15.png
│ │ ├── idle16.png
│ │ ├── idle17.png
│ │ └── idle18.png
│ │
│ ├── Jump/
│ │ ├── jump1.png
│ │ ├── jump2.png
│ │ ├── jump3.png
│ │ ├── jump4.png
│ │ ├── jump5.png
│ │ ├── jump6.png
│ │ └── jump7.png
│ │
│ ├── Run/
│ │ ├── run1.png
│ │ ├── run2.png
│ │ ├── run3.png
│ │ ├── run4.png
│ │ ├── run5.png
│ │ ├── run6.png
│ │ ├── run7.png
│ │ └── run8.png
│ │
│ ├── Walk/
│ │ ├── walk1.png
│ │ ├── walk2.png
│ │ ├── walk3.png
│ │ ├── walk4.png
│ │ ├── walk5.png
│ │ └── walk6.png
│ │
│ ├── Walk_Attack/
│ │ ├── walk_attack1.png
│ │ ├── walk_attack2.png
│ │ ├── walk_attack3.png
│ │ ├── walk_attack4.png
│ │ ├── walk_attack5.png
│ │ └── walk_attack6.png
│ │
│ └── rogue.png
│
├── core/
│ ├── build/
│ ├── src/com/fistofsteel/
│ │ ├── audio/
│ │ │ └── AudioManager.java
│ │ │
│ │ ├── entities/
│ │ │ ├── Alexis.java
│ │ │ ├── Enemy.java
│ │ │ ├── EnemyManager.java
│ │ │ ├── Hugo.java
│ │ │ ├── Knight.java
│ │ │ ├── Mage.java
│ │ │ ├── Player.java
│ │ │ ├── Rogue.java
│ │ │ └── WorldItemManager.java
│ │ │
│ │ ├── input/
│ │ │ └── InputHandler.java
│ │ │
│ │ ├── items/
│ │ │ ├── Armor.java
│ │ │ ├── Item.java
│ │ │ ├── ItemPickup.java
│ │ │ ├── Potion.java
│ │ │ └── Weapon.java
│ │ │
│ │ ├── screens/
│ │ │ ├── CharactersChoice.java
│ │ │ ├── GameManager.java
│ │ │ ├── MenuScreen.java
│ │ │ └── OptionsScreen.java
│ │ │
│ │ └── utils/
│ │ └── FistOfSteelGame.java
│ │
│ ├── build.gradle
│ └── build.gradle.backup
│
├── desktop/
│ ├── build/
│ ├── src/com/fistofsteel/
│ │ └── DesktopLauncher.java
│ │
│ ├── build.gradle
│ └── build.gradle.backup
│
├── gradle/
│
├── build.gradle
├── create_project.sh
├── gradle.properties
├── gradle.properties.backup
├── gradlew
├── README.md
└── settings.gradle
