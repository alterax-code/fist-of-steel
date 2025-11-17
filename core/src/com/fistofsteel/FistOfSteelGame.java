package com.fistofsteel;

import com.badlogic.gdx.Game;
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.screens.MenuScreen;

/**
 * Classe principale du jeu
 * Démarre sur le menu au lieu du jeu directement
 * Gère l'instance unique d'AudioManager
 */
public class FistOfSteelGame extends Game {
    
    // Instance unique d'AudioManager partagée par tous les écrans
    private AudioManager audioManager;
    
    @Override
    public void create() {
        // Créer l'AudioManager une seule fois
        audioManager = new AudioManager();
        
        // Démarrer sur le menu principal avec l'AudioManager
        setScreen(new MenuScreen(this, audioManager));
    }
    
    /**
     * Récupère l'AudioManager du jeu
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        // Nettoyer l'AudioManager quand le jeu se ferme
        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}