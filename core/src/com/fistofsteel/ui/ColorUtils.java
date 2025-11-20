package com.fistofsteel.utils;

import com.badlogic.gdx.graphics.Color;

/**
 * Utilitaires pour les calculs de couleur
 */
public class ColorUtils {
    
    /**
     * Calcule la couleur en fonction du pourcentage de vie
     * 100% = Vert (0, 1, 0)
     * 50% = Jaune (1, 1, 0)
     * 0% = Rouge (1, 0, 0)
     */
    public static Color getHealthColor(float healthPercent) {
        if (healthPercent > 0.5f) {
            // De 50% à 100% : Rouge augmente, Vert reste à 1
            return new Color(1f - (healthPercent - 0.5f) * 2f, 1f, 0f, 1f);
        } else {
            // De 0% à 50% : Vert diminue, Rouge reste à 1
            float green = healthPercent * 2f;
            return new Color(1f, green, 0f, 1f);
        }
    }
}