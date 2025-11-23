package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.fistofsteel.FistOfSteelGame;
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.ui.UIComponents.SimpleButton;
import com.fistofsteel.ui.UIComponents.VolumeSlider;
import com.fistofsteel.ui.UIComponents.KeyButton;

/**
 * Écran des options.
 * Permet de régler le volume et de configurer les touches.
 */
public class OptionsScreen extends BaseScreen {
    
    private VolumeSlider musicSlider;
    private VolumeSlider sfxSlider;
    private SimpleButton backButton;
    
    private boolean waitingForKey = false;
    private String keyToRemap = "";
    private KeyButton[] keyButtons;
    
    public OptionsScreen(FistOfSteelGame game, AudioManager audioManager) {
        super(game, audioManager);
    }
    
    @Override
    protected String getBackgroundPath() {
        return null;
    }
    
    @Override
    protected void initializeFonts() {
        super.initializeFonts();
        titleFont.setColor(new Color(1f, 0.2f, 0.2f, 1f));
    }
    
    @Override
    public void show() {
        super.show();
        System.out.println("OptionsScreen : Musique menu continue");
    }
    
    @Override
    protected void createElements() {
        float centerX = screenWidth / 2f;
        
        float sliderWidth = Math.min(800f, screenWidth * 0.42f);
        float sliderX = centerX - sliderWidth / 2f;
        float sliderY1 = screenHeight * 0.65f;
        float sliderY2 = screenHeight * 0.51f;
        
        musicSlider = new VolumeSlider("MUSIC VOLUME", sliderX, sliderY1, sliderWidth, audioManager.getMusicVolume());
        sfxSlider = new VolumeSlider("SFX VOLUME", sliderX, sliderY2, sliderWidth, audioManager.getSoundVolume());
        
        float keyStartY = screenHeight * 0.37f;
        float keySpacing = screenHeight * 0.074f;
        float keyWidth = 250f;
        
        float col1X = centerX - keyWidth - 50;
        float col2X = centerX;
        float col3X = centerX + keyWidth + 50;
        
        keyButtons = new KeyButton[] {
            new KeyButton("Left", "A", col1X, keyStartY, keyWidth),
            new KeyButton("Right", "D", col2X, keyStartY, keyWidth),
            new KeyButton("Jump", "SPACE", col3X, keyStartY, keyWidth),
            new KeyButton("Attack", "Q", col1X, keyStartY - keySpacing, keyWidth),
            new KeyButton("Block", "E", col2X, keyStartY - keySpacing, keyWidth),
            new KeyButton("Crouch", "S", col3X, keyStartY - keySpacing, keyWidth)
        };
        
        float backY = screenHeight * 0.14f;
        backButton = new SimpleButton("BACK", centerX, backY, 300, 70);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        int mouseX = Gdx.input.getX();
        int mouseY = (int)screenHeight - Gdx.input.getY();
        
        if (waitingForKey) {
            for (int keycode = 0; keycode < 256; keycode++) {
                if (Gdx.input.isKeyJustPressed(keycode)) {
                    for (KeyButton kb : keyButtons) {
                        if (kb.getAction().equals(keyToRemap)) {
                            kb.setKeyName(Input.Keys.toString(keycode));
                            break;
                        }
                    }
                    waitingForKey = false;
                    keyToRemap = "";
                    break;
                }
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game, audioManager));
            return;
        }
        
        if (Gdx.input.isTouched()) {
            if (musicSlider.isDragging() || musicSlider.getBounds().contains(mouseX, mouseY)) {
                musicSlider.updateValue(mouseX);
                audioManager.setMusicVolume(musicSlider.getValue());
            }
            if (sfxSlider.isDragging() || sfxSlider.getBounds().contains(mouseX, mouseY)) {
                sfxSlider.updateValue(mouseX);
                audioManager.setSoundVolume(sfxSlider.getValue());
            }
        } else {
            musicSlider.setDragging(false);
            sfxSlider.setDragging(false);
        }
        
        if (Gdx.input.justTouched() && !waitingForKey) {
            if (backButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new MenuScreen(game, audioManager));
                return;
            }
            
            for (KeyButton kb : keyButtons) {
                if (kb.isClicked(mouseX, mouseY)) {
                    waitingForKey = true;
                    keyToRemap = kb.getAction();
                }
            }
        }
        
        batch.begin();
        float titleY = screenHeight * 0.88f;
        titleFont.draw(batch, "OPTIONS", 0, titleY, screenWidth, Align.center, false);
        batch.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        musicSlider.render(shapeRenderer);
        sfxSlider.render(shapeRenderer);
        backButton.render(shapeRenderer);
        for (KeyButton kb : keyButtons) {
            kb.render(shapeRenderer);
        }
        shapeRenderer.end();
        
        batch.begin();
        musicSlider.renderText(batch, font);
        sfxSlider.renderText(batch, font);
        backButton.renderText(batch, font);
        for (KeyButton kb : keyButtons) {
            kb.renderText(batch, font);
        }
        
        if (waitingForKey) {
            font.setColor(Color.YELLOW);
            float msgY = screenHeight * 0.185f;
            font.draw(batch, "Press a key for: " + keyToRemap, 0, msgY, screenWidth, Align.center, false);
            font.setColor(Color.WHITE);
        }
        batch.end();
    }
}