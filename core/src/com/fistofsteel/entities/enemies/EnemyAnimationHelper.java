package com.fistofsteel.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.Gdx;

/**
 * Helper pour charger automatiquement les sprites des ennemis.
 * Version robuste avec fallback et détection automatique de casse.
 */
public class EnemyAnimationHelper {
    
    private static Texture fallbackTexture = null;
    
    /**
     * Crée une texture de fallback rouge si le chargement échoue.
     * 
     * @return La texture de fallback
     */
    private static Texture getFallbackTexture() {
        if (fallbackTexture == null) {
            Pixmap pixmap = new Pixmap(64, 64, Format.RGBA8888);
            pixmap.setColor(1, 0, 0, 0.5f);
            pixmap.fill();
            fallbackTexture = new Texture(pixmap);
            pixmap.dispose();
        }
        return fallbackTexture;
    }
    
    /**
     * Charge les sprites d'un ennemi.
     * 
     * @param basePath Le chemin de base vers les sprites
     * @param enemyName Le nom de l'ennemi
     * @return Un tableau 2D contenant toutes les animations
     */
    public static Texture[][] loadEnemySprites(String basePath, String enemyName) {
        System.out.println("Chargement sprites " + enemyName + "...");
        
        Texture[][] allTextures = new Texture[5][];
        
        boolean isRogue = enemyName.equals("Rogue");
        
        int idleCount = getIdleFrameCount(enemyName);
        if (isRogue) {
            allTextures[0] = loadRogueIdleAnimation(basePath, enemyName);
        } else {
            allTextures[0] = loadAnimation(basePath, enemyName, "Idle/idle", idleCount, 1);
        }
        
        if (isRogue) {
            allTextures[1] = loadAnimationRobust(basePath, enemyName, "Hurt", "hurt", 4, 1);
        } else {
            allTextures[1] = loadAnimation(basePath, enemyName, "Hurt/hurt", 4, 1);
        }
        
        if (isRogue) {
            allTextures[2] = loadAnimationRobust(basePath, enemyName, "Walk", "walk", 6, 1);
        } else {
            allTextures[2] = loadAnimation(basePath, enemyName, "Walk/walk", 6, 1);
        }
        
        int attackCount = getAttackFrameCount(enemyName);
        int attackStartIndex = enemyName.equals("Knight") ? 0 : 1;
        if (isRogue) {
            allTextures[3] = loadAnimationRobust(basePath, enemyName, "Attack", "Attack", attackCount, attackStartIndex);
        } else {
            allTextures[3] = loadAnimation(basePath, enemyName, "Attack/attack", attackCount, attackStartIndex);
        }
        
        if (isRogue) {
            allTextures[4] = loadAnimationRobust(basePath, enemyName, "Death", "death", 10, 1);
        } else {
            allTextures[4] = loadAnimation(basePath, enemyName, "Death/death", 10, 1);
        }
        
        System.out.println("Sprites " + enemyName + " charges !");
        System.out.println("   Idle: " + allTextures[0].length + " frames");
        System.out.println("   Hurt: " + allTextures[1].length + " frames");
        System.out.println("   Walk: " + allTextures[2].length + " frames");
        System.out.println("   Attack: " + allTextures[3].length + " frames");
        System.out.println("   Death: " + allTextures[4].length + " frames");
        
        return allTextures;
    }
    
    /**
     * Charge l'animation idle du Rogue (saute idle11).
     */
    private static Texture[] loadRogueIdleAnimation(String basePath, String enemyName) {
        Texture[] textures = new Texture[17];
        int loadedCount = 0;
        int textureIndex = 0;
        
        for (int frameNumber = 1; frameNumber <= 18; frameNumber++) {
            if (frameNumber == 11) continue;
            
            String path = basePath + enemyName + "/Idle/idle" + frameNumber + ".png";
            
            try {
                if (Gdx.files.internal(path).exists()) {
                    textures[textureIndex] = new Texture(Gdx.files.internal(path));
                    loadedCount++;
                } else {
                    System.err.println("Fichier non trouve : " + path);
                    textures[textureIndex] = getFallbackTexture();
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement " + path + ": " + e.getMessage());
                textures[textureIndex] = getFallbackTexture();
            }
            textureIndex++;
        }
        
        System.out.println("   Idle (Rogue) : " + loadedCount + "/17 frames chargees (idle11 sautee)");
        return textures;
    }
    
    /**
     * Version robuste pour Rogue : essaie les deux casses et gère les erreurs.
     */
    private static Texture[] loadAnimationRobust(String basePath, String enemyName, 
                                                  String folder, String prefix, 
                                                  int frameCount, int startIndex) {
        Texture[] textures = new Texture[frameCount];
        int loadedCount = 0;
        
        for (int i = 0; i < frameCount; i++) {
            int frameNumber = startIndex + i;
            String path = basePath + enemyName + "/" + folder + "/" + prefix + frameNumber + ".png";
            
            try {
                if (Gdx.files.internal(path).exists()) {
                    textures[i] = new Texture(Gdx.files.internal(path));
                    loadedCount++;
                } else {
                    System.err.println("Fichier non trouve : " + path);
                    textures[i] = getFallbackTexture();
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement " + path + ": " + e.getMessage());
                textures[i] = getFallbackTexture();
            }
        }
        
        System.out.println("   " + folder + " : " + loadedCount + "/" + frameCount + " frames chargees");
        return textures;
    }
    
    /**
     * Charge une séquence d'animation (version normale pour Knight/Mage).
     */
    private static Texture[] loadAnimation(String basePath, String enemyName, 
                                          String animationPath, int frameCount, int startIndex) {
        Texture[] textures = new Texture[frameCount];
        
        for (int i = 0; i < frameCount; i++) {
            String path = basePath + enemyName + "/" + animationPath + (startIndex + i) + ".png";
            textures[i] = new Texture(path);
        }
        
        return textures;
    }
    
    private static int getIdleFrameCount(String enemyName) {
        switch (enemyName) {
            case "Knight": return 12;
            case "Mage": return 14;
            case "Rogue": return 17;
            default: return 12;
        }
    }
    
    private static int getAttackFrameCount(String enemyName) {
        switch (enemyName) {
            case "Knight": return 5;
            case "Mage": return 7;
            case "Rogue": return 7;
            default: return 5;
        }
    }
    
    /**
     * Libère toutes les textures chargées.
     * 
     * @param allTextures Le tableau de textures à libérer
     */
    public static void disposeTextures(Texture[][] allTextures) {
        if (allTextures == null) return;
        
        for (Texture[] textures : allTextures) {
            if (textures != null) {
                for (Texture t : textures) {
                    if (t != null && t != fallbackTexture) {
                        t.dispose();
                    }
                }
            }
        }
        
        if (fallbackTexture != null) {
            fallbackTexture.dispose();
            fallbackTexture = null;
        }
    }
}