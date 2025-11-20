package com.fistofsteel.entities.world;  // ✅ MODIFIÉ (était com.fistofsteel.entities)

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.fistofsteel.entities.player.Player;  // ✅ AJOUT

// ... reste du code inchangé

/**
 * Porte de sortie de niveau
 * Rouge = verrouillée (ennemis restants)
 * Vert = déverrouillée (tous les ennemis tués)
 */
public class LevelExit {
    
    private float x, y;
    private float width = 80f;
    private float height = 120f;
    
    private Rectangle bounds;
    private String targetLevel; // Nom du prochain niveau
    
    private boolean unlocked = false;
    
    public LevelExit(float x, float y, String targetLevel) {
        this.x = x;
        this.y = y;
        this.targetLevel = targetLevel;
        this.bounds = new Rectangle(x, y, width, height);
        
        System.out.println("🚪 Porte de sortie créée à (" + (int)x + ", " + (int)y + ") -> " + targetLevel);
    }
    
    /**
     * Met à jour l'état de la porte (verrouillée/déverrouillée)
     */
    public void update(int enemiesKilled, int totalEnemies) {
        boolean wasUnlocked = unlocked;
        unlocked = (enemiesKilled >= totalEnemies);
        
        if (!wasUnlocked && unlocked) {
            System.out.println("🔓 Porte déverrouillée ! Tous les ennemis ont été vaincus");
        }
    }
    
    /**
     * Vérifie si le joueur touche la porte
     */
    public boolean isPlayerInside(Player player) {
        return bounds.overlaps(player.getHitbox());
    }
    
    /**
     * Dessine la porte (rectangle coloré)
     */
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        // Utiliser ShapeRenderer pour dessiner un rectangle
        batch.end(); // Arrêter le batch pour dessiner des formes
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Couleur selon l'état
        if (unlocked) {
            shapeRenderer.setColor(new Color(0f, 0.8f, 0f, 0.8f)); // Vert transparent
        } else {
            shapeRenderer.setColor(new Color(0.8f, 0f, 0f, 0.8f)); // Rouge transparent
        }
        
        shapeRenderer.rect(x, y, width, height);
        
        // Contour noir
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x, y, width, height);
        
        shapeRenderer.end();
        
        batch.begin(); // Reprendre le batch
    }
    
    // Getters
    public boolean isUnlocked() {
        return unlocked;
    }
    
    public String getTargetLevel() {
        return targetLevel;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
}