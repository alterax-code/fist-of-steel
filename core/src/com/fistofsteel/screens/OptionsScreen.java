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

/**
 * Écran des options.
 * Permet de régler le volume de la musique et des effets sonores.
 * 
 * MODIFIÉ : Suppression de la configuration des touches (non fonctionnelle).
 */
public class OptionsScreen extends BaseScreen {
    
    private VolumeSlider musicSlider;
    private VolumeSlider sfxSlider;
    private SimpleButton backButton;
    
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
        
        // Sliders de volume - positionnés au centre de l'écran
        float sliderWidth = Math.min(800f, screenWidth * 0.42f);
        float sliderX = centerX - sliderWidth / 2f;
        float sliderY1 = screenHeight * 0.55f;
        float sliderY2 = screenHeight * 0.40f;
        
        musicSlider = new VolumeSlider("MUSIC VOLUME", sliderX, sliderY1, sliderWidth, audioManager.getMusicVolume());
        sfxSlider = new VolumeSlider("SFX VOLUME", sliderX, sliderY2, sliderWidth, audioManager.getSoundVolume());
        
        // Bouton retour
        float backY = screenHeight * 0.20f;
        backButton = new SimpleButton("BACK", centerX, backY, 300, 70);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        int mouseX = Gdx.input.getX();
        int mouseY = (int)screenHeight - Gdx.input.getY();
        
        // Retour au menu avec Échap
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game, audioManager));
            return;
        }
        
        // Gestion des sliders
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
        
        // Mise à jour hover du bouton
        backButton.update(mouseX, mouseY);
        
        // Gestion du clic sur le bouton
        if (Gdx.input.justTouched()) {
            if (backButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new MenuScreen(game, audioManager));
                return;
            }
        }
        
        // ═══════════════════════════════════════════════════════════════════════════
        // RENDU
        // ═══════════════════════════════════════════════════════════════════════════
        
        // Titre
        batch.begin();
        float titleY = screenHeight * 0.80f;
        titleFont.draw(batch, "OPTIONS", 0, titleY, screenWidth, Align.center, false);
        batch.end();
        
        // Sliders et bouton
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        musicSlider.render(shapeRenderer);
        sfxSlider.render(shapeRenderer);
        backButton.render(shapeRenderer);
        shapeRenderer.end();
        
        // Textes
        batch.begin();
        musicSlider.renderText(batch, font);
        sfxSlider.renderText(batch, font);
        backButton.renderText(batch, font);
        batch.end();
    }
}