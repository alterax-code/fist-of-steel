package com.fistofsteel.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.entities.enemies.Enemy;

/**
 * Utilitaire pour visualiser les hitbox pendant le développement
 * À activer/désactiver avec F3
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
     * Rouge = hitbox, Bleu = sprite complet, Vert = centre
     */
    public static void renderPlayerHitbox(Player player, OrthographicCamera camera) {
        if (!DEBUG_ENABLED || shapeRenderer == null) return;
        
        Rectangle hitbox = player.getHitbox();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        // Hitbox en rouge
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        
        // Rectangle complet du sprite en bleu
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(player.getX(), player.getY(), 
                          EntityConstants.PLAYER_WIDTH, EntityConstants.PLAYER_HEIGHT);
        
        // Point central en vert
        shapeRenderer.setColor(Color.GREEN);
        float centerX = hitbox.x + hitbox.width / 2f;
        float centerY = hitbox.y + hitbox.height / 2f;
        drawCross(centerX, centerY, 5);
        
        shapeRenderer.end();
    }
    
    /**
     * Affiche la hitbox d'un ennemi avec la caméra du jeu
     * Rouge = hitbox, Jaune = sprite complet, Vert = centre
     */
    public static void renderEnemyHitbox(Enemy enemy, OrthographicCamera camera) {
        if (!DEBUG_ENABLED || shapeRenderer == null) return;
        
        Rectangle hitbox = enemy.getHitbox();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        // Hitbox en rouge
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        
        // Rectangle complet du sprite en jaune (différent du joueur)
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(enemy.getX(), enemy.getY(), 
                          EntityConstants.ENEMY_WIDTH, EntityConstants.ENEMY_HEIGHT);
        
        // Point central en vert
        shapeRenderer.setColor(Color.GREEN);
        float centerX = hitbox.x + hitbox.width / 2f;
        float centerY = hitbox.y + hitbox.height / 2f;
        drawCross(centerX, centerY, 5);
        
        shapeRenderer.end();
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