package com.fistofsteel;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Point d'entrée desktop du jeu.
 */
public class DesktopLauncher {
    
    /**
     * Méthode principale.
     * 
     * @param arg Arguments de ligne de commande
     */
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Fist of Steel: Marvin's Vengeance");
        
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        
        config.setForegroundFPS(60);
        config.useVsync(true);
        
        new Lwjgl3Application(new FistOfSteelGame(), config);
    }
}