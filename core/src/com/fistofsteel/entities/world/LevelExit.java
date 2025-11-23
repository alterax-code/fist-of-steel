package com.fistofsteel.entities.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.fistofsteel.entities.player.Player;

/**
 * Porte de sortie de niveau.
 * Rouge = verrouillée (ennemis restants), Vert = déverrouillée (tous les ennemis tués).
 */
public class LevelExit {
    
    private float x, y;
    private float width = 80f;
    private float height = 120f;
    
    private Rectangle bounds;
    private String targetLevel;
    
    private boolean unlocked = false;
    
    /**
     * Constructeur de la porte de sortie.
     * 
     * @param x Position X
     * @param y Position Y
     * @param targetLevel Le nom du niveau cible
     */
    public LevelExit(float x, float y, String targetLevel) {
        this.x = x;
        this.y = y;
        this.targetLevel = targetLevel;
        this.bounds = new Rectangle(x, y, width, height);
        
        System.out.println("Porte de sortie creee a (" + (int)x + ", " + (int)y + ") -> " + targetLevel);
    }
    
    /**
     * Met à jour l'état de la porte (verrouillée/déverrouillée).
     * 
     * @param enemiesKilled Le nombre d'ennemis tués
     * @param totalEnemies Le nombre total d'ennemis
     */
    public void update(int enemiesKilled, int totalEnemies) {
        boolean wasUnlocked = unlocked;
        unlocked = (enemiesKilled >= totalEnemies);
        
        if (!wasUnlocked && unlocked) {
            System.out.println("Porte deverrouillee ! Tous les ennemis ont ete vaincus");
        }
    }
    
    /**
     * Vérifie si le joueur touche la porte.
     * 
     * @param player Le joueur
     * @return true si le joueur est dans la porte
     */
    public boolean isPlayerInside(Player player) {
        return bounds.overlaps(player.getHitbox());
    }
    
    /**
     * Dessine la porte (rectangle coloré).
     * 
     * @param batch Le SpriteBatch pour le rendu
     * @param shapeRenderer Le ShapeRenderer pour dessiner
     * @param camera La caméra du jeu
     */
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        batch.end();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        if (unlocked) {
            shapeRenderer.setColor(new Color(0f, 0.8f, 0f, 0.8f));
        } else {
            shapeRenderer.setColor(new Color(0.8f, 0f, 0f, 0.8f));
        }
        
        shapeRenderer.rect(x, y, width, height);
        
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x, y, width, height);
        
        shapeRenderer.end();
        
        batch.begin();
    }
    
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