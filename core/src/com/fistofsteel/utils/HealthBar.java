package com.fistofsteel.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Utilitaire pour dessiner des barres de vie
 * Interpolation de couleur : Vert (100% HP) → Rouge (0% HP)
 */
public class HealthBar {
    
    /**
     * Dessine une barre de vie au-dessus d'une entité
     */
    public static void render(ShapeRenderer shapeRenderer, float x, float y, float width, float height, int currentHealth, int maxHealth) {
        if (maxHealth <= 0) return;
        
        float healthPercent = Math.max(0f, Math.min(1f, (float) currentHealth / maxHealth));
        
        // Fond noir (contour)
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x - width/2 - 1, y - 1, width + 2, height + 2);
        
        // Fond gris foncé (barre vide)
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        shapeRenderer.rect(x - width/2, y, width, height);
        
        // Barre de vie colorée (utilise ColorUtils)
        Color healthColor = ColorUtils.getHealthColor(healthPercent);
        shapeRenderer.setColor(healthColor);
        shapeRenderer.rect(x - width/2, y, width * healthPercent, height);
    }
}