package com.fistofsteel;

import com.badlogic.gdx.Game;
import com.fistofsteel.screens.MenuScreen;

/**
 * Classe principale du jeu
 * Démarre sur le menu au lieu du jeu directement
 */
public class FistOfSteelGame extends Game {
    @Override
    public void create() {
        // Démarrer sur le menu principal
        setScreen(new MenuScreen(this));
    }
}