package com.fistofsteel.utils;

import com.badlogic.gdx.graphics.Color;

/**
 * Utilitaires pour les calculs de couleur des barres de vie.
 */
public class ColorUtils {
    
    /**
     * Calcule la couleur en fonction du pourcentage de vie.
     * 100% = Vert (0, 1, 0)
     * 50% = Jaune (1, 1, 0)
     * 0% = Rouge (1, 0, 0)
     * 
     * @param healthPercent Pourcentage de vie (0.0 à 1.0)
     * @return La couleur correspondante
     */
    public static Color getHealthColor(float healthPercent) {
        if (healthPercent > 0.5f) {
            return new Color(1f - (healthPercent - 0.5f) * 2f, 1f, 0f, 1f);
        } else {
            float green = healthPercent * 2f;
            return new Color(1f, green, 0f, 1f);
        }
    }
}