package com.fistofsteel;

import com.badlogic.gdx.Game;
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.screens.MenuScreen;

/**
 * Classe principale du jeu.
 * Démarre sur le menu principal et gère l'instance unique d'AudioManager.
 */
public class FistOfSteelGame extends Game {
    
    private AudioManager audioManager;
    
    @Override
    public void create() {
        audioManager = new AudioManager();
        setScreen(new MenuScreen(this, audioManager));
    }
    
    /**
     * Récupère l'AudioManager du jeu.
     * 
     * @return L'instance d'AudioManager
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}