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
     * @param shapeRenderer Le renderer (doit être en mode Filled)
     * @param x Position X du centre de la barre
     * @param y Position Y (base de la barre)
     * @param width Largeur totale de la barre
     * @param height Hauteur de la barre
     * @param currentHealth PV actuels
     * @param maxHealth PV maximum
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
        
        // Barre de vie colorée (interpolation vert→rouge)
        Color healthColor = getHealthColor(healthPercent);
        shapeRenderer.setColor(healthColor);
        shapeRenderer.rect(x - width/2, y, width * healthPercent, height);
    }
    
    /**
     * Calcule la couleur en fonction du pourcentage de vie
     * 100% = Vert (0, 1, 0)
     * 50% = Jaune (1, 1, 0)
     * 0% = Rouge (1, 0, 0)
     */
    private static Color getHealthColor(float percent) {
        if (percent > 0.5f) {
            // De 50% à 100% : Rouge augmente, Vert reste à 1
            float red = (1f - percent) * 2f; // 0.5→1.0 devient 1.0→0.0, inversé = 0.0→1.0
            return new Color(1f - (percent - 0.5f) * 2f, 1f, 0f, 1f);
        } else {
            // De 0% à 50% : Vert diminue, Rouge reste à 1
            float green = percent * 2f;
            return new Color(1f, green, 0f, 1f);
        }
    }
}