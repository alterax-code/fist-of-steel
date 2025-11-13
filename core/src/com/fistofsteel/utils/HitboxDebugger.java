package com.fistofsteel.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.fistofsteel.entities.Player;

/**
 * Utilitaire pour visualiser les hitbox pendant le développement
 * À activer/désactiver avec une constante de debug
 */
public class HitboxDebugger {
    
    private static boolean DEBUG_ENABLED = false;
    private static ShapeRenderer shapeRenderer;
    
    /**
     * Active ou désactive l'affichage des hitbox
     */
    public static void setDebugEnabled(boolean enabled) {
        DEBUG_ENABLED = enabled;
        if (enabled && shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }
    }
    
    /**
     * Affiche la hitbox d'un joueur avec la caméra du jeu
     * À appeler après batch.end() et avant batch.begin()
     * 
     * @param player Le joueur dont on veut afficher la hitbox
     * @param camera La caméra orthographique du jeu
     */
    public static void renderPlayerHitbox(Player player, OrthographicCamera camera) {
        if (!DEBUG_ENABLED || shapeRenderer == null) return;
        
        Rectangle hitbox = player.getHitbox();
        
        // IMPORTANT : Utiliser la matrice de projection de la caméra
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        // Hitbox en rouge
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        
        // Rectangle complet du sprite en bleu (pour comparaison)
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(player.getX(), player.getY(), 
                          Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        
        // Point central en vert
        shapeRenderer.setColor(Color.GREEN);
        float centerX = hitbox.x + hitbox.width / 2f;
        float centerY = hitbox.y + hitbox.height / 2f;
        drawCross(centerX, centerY, 5);
        
        shapeRenderer.end();
    }
    
    /**
     * Affiche une hitbox quelconque avec la caméra
     */
    public static void renderHitbox(Rectangle hitbox, Color color, OrthographicCamera camera) {
        if (!DEBUG_ENABLED || shapeRenderer == null) return;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        shapeRenderer.end();
    }
    
    /**
     * Affiche des informations textuelles sur la hitbox
     */
    public static String getHitboxInfo(Player player) {
        if (!DEBUG_ENABLED) return "";
        
        Rectangle hitbox = player.getHitbox();
        return String.format(
            "Hitbox: [%.0f x %.0f] at (%.0f, %.0f)\n" +
            "Sprite: [%.0f x %.0f] at (%.0f, %.0f)",
            hitbox.width, hitbox.height, hitbox.x, hitbox.y,
            Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT, 
            player.getX(), player.getY()
        );
    }
    
    /**
     * Dessine une croix pour marquer un point
     */
    private static void drawCross(float x, float y, float size) {
        shapeRenderer.line(x - size, y, x + size, y);
        shapeRenderer.line(x, y - size, x, y + size);
    }
    
    /**
     * Nettoyage des ressources
     */
    public static void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
}