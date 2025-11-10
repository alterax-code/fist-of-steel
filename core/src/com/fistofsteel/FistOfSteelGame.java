package com.fistofsteel;

import com.badlogic.gdx.Game;
import com.fistofsteel.screens.GameScreen;

public class FistOfSteelGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
