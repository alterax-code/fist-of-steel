package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.fistofsteel.FistOfSteelGame;
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.ui.UIComponents.SimpleButton;
import com.fistofsteel.ui.UIComponents.VolumeSlider;

/**
 * Écran de pause du jeu.
 * Affiche les options de volume et permet de reprendre ou quitter.
 */
public class PauseScreen implements Screen {
    
    private FistOfSteelGame game;
    private AudioManager audioManager;
    private GameManager gameManager;  // Référence au jeu en cours
    
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    private float screenWidth;
    private float screenHeight;
    
    private VolumeSlider musicSlider;
    private VolumeSlider sfxSlider;
    private SimpleButton resumeButton;
    private SimpleButton menuButton;
    
    /**
     * Constructeur du menu pause.
     * 
     * @param game L'instance du jeu
     * @param audioManager Le gestionnaire audio
     * @param gameManager Le GameManager en cours (pour reprendre)
     */
    public PauseScreen(FistOfSteelGame game, AudioManager audioManager, GameManager gameManager) {
        this.game = game;
        this.audioManager = audioManager;
        this.gameManager = gameManager;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        font.setColor(Color.WHITE);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);
        titleFont.setColor(new Color(1f, 0.8f, 0.2f, 1f));  // Jaune doré
        
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        createElements();
        
        // Mettre la musique en pause
        audioManager.pauseLevelMusic();
        
        System.out.println("Jeu en pause");
    }
    
    /**
     * Crée les éléments UI.
     */
    private void createElements() {
        float centerX = screenWidth / 2f;
        
        // Sliders de volume
        float sliderWidth = Math.min(600f, screenWidth * 0.35f);
        float sliderX = centerX - sliderWidth / 2f;
        float sliderY1 = screenHeight * 0.58f;
        float sliderY2 = screenHeight * 0.45f;
        
        musicSlider = new VolumeSlider("MUSIC VOLUME", sliderX, sliderY1, sliderWidth, audioManager.getMusicVolume());
        sfxSlider = new VolumeSlider("SFX VOLUME", sliderX, sliderY2, sliderWidth, audioManager.getSoundVolume());
        
        // Boutons
        float buttonWidth = 280f;
        float buttonHeight = 70f;
        float buttonY = screenHeight * 0.25f;
        float spacing = 40f;
        
        resumeButton = new SimpleButton("RESUME", centerX - buttonWidth/2f - spacing/2f, buttonY, buttonWidth, buttonHeight);
        menuButton = new SimpleButton("MAIN MENU", centerX + buttonWidth/2f + spacing/2f, buttonY, buttonWidth, buttonHeight);
    }
    
    @Override
    public void render(float delta) {
        // Fond semi-transparent noir
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        int mouseX = Gdx.input.getX();
        int mouseY = (int)(screenHeight - Gdx.input.getY());
        
        // Appuyer sur Échap = reprendre
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
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
        
        // Mise à jour hover des boutons
        resumeButton.update(mouseX, mouseY);
        menuButton.update(mouseX, mouseY);
        
        // Gestion des clics
        if (Gdx.input.justTouched()) {
            if (resumeButton.isClicked(mouseX, mouseY)) {
                resumeGame();
                return;
            } else if (menuButton.isClicked(mouseX, mouseY)) {
                returnToMenu();
                return;
            }
        }
        
        // ═══════════════════════════════════════════════════════════════════════════
        // RENDU
        // ═══════════════════════════════════════════════════════════════════════════
        
        // Dessiner le fond semi-transparent
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.7f);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        shapeRenderer.end();
        
        // Titre
        batch.begin();
        float titleY = screenHeight * 0.80f;
        titleFont.draw(batch, "PAUSED", 0, titleY, screenWidth, Align.center, false);
        batch.end();
        
        // Sliders et boutons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        musicSlider.render(shapeRenderer);
        sfxSlider.render(shapeRenderer);
        renderButton(shapeRenderer, resumeButton, new Color(0.1f, 0.6f, 0.1f, 0.9f));  // Vert
        renderButton(shapeRenderer, menuButton, new Color(0.6f, 0.1f, 0.1f, 0.9f));   // Rouge
        shapeRenderer.end();
        
        // Textes
        batch.begin();
        musicSlider.renderText(batch, font);
        sfxSlider.renderText(batch, font);
        resumeButton.renderText(batch, font);
        menuButton.renderText(batch, font);
        
        // Instructions
        font.setColor(Color.GRAY);
        font.draw(batch, "Press ESC to resume", 0, screenHeight * 0.12f, screenWidth, Align.center, false);
        font.setColor(Color.WHITE);
        batch.end();
    }
    
    /**
     * Dessine un bouton avec couleur personnalisée.
     */
    private void renderButton(ShapeRenderer sr, SimpleButton btn, Color hoverColor) {
        Color bgColor = btn.isHovered() ? hoverColor : new Color(0.2f, 0.2f, 0.25f, 0.95f);
        sr.setColor(bgColor);
        sr.rect(btn.getBounds().x, btn.getBounds().y, btn.getBounds().width, btn.getBounds().height);
    }
    
    /**
     * Reprend le jeu.
     */
    private void resumeGame() {
        System.out.println("Reprise du jeu");
        audioManager.resumeLevelMusic();
        game.setScreen(gameManager);  // Retourner au GameManager
    }
    
    /**
     * Retourne au menu principal.
     */
    private void returnToMenu() {
        System.out.println("Retour au menu principal");
        audioManager.stopLevelMusic();
        audioManager.startMenuMusic();
        
        // Disposer le GameManager
        if (gameManager != null) {
            gameManager.dispose();
        }
        
        game.setScreen(new MenuScreen(game, audioManager));
    }
    
    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        createElements();
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
    }
}