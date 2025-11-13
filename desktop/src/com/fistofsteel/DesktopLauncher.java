package com.fistofsteel;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Fist of Steel: Marvin's Vengeance");
        
        // ===== OPTION 1 : FULLSCREEN (recommandé) =====
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        
        // ===== OPTION 2 : WINDOWED ADAPTÉ (alternative) =====
        // Décommenter ces lignes et commenter la ligne ci-dessus pour mode fenêtré
        // Lwjgl3ApplicationConfiguration.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
        // int windowWidth = (int)(displayMode.width * 0.8f);  // 80% de la largeur écran
        // int windowHeight = (int)(displayMode.height * 0.8f); // 80% de la hauteur écran
        // config.setWindowedMode(windowWidth, windowHeight);
        
        config.setForegroundFPS(60);
        config.useVsync(true);
        
        new Lwjgl3Application(new FistOfSteelGame(), config);
    }
}