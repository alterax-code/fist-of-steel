package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.fistofsteel.FistOfSteelGame;
import com.fistofsteel.audio.AudioManager;

/**
 * Écran Game Over - s'affiche quand le joueur meurt
 */
public class GameOverScreen implements Screen {
    
    private final FistOfSteelGame game;
    private final AudioManager audioManager;
    
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    
    private Texture backgroundTexture;
    
    private SimpleButton returnMenuButton;
    private SimpleButton quitButton;
    
    private float screenWidth;
    private float screenHeight;
    
    // Animation du titre
    private float animationTimer = 0f;
    private float titleOffsetY = 0f;
    
    public GameOverScreen(FistOfSteelGame game, AudioManager audioManager) {
        this.game = game;
        this.audioManager = audioManager;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Police pour le titre "GAME OVER"
        titleFont = new BitmapFont();
        titleFont.getData().setScale(5f);
        titleFont.setColor(new Color(0.9f, 0.1f, 0.1f, 1f)); // Rouge vif
        
        // Police pour les boutons
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(2f);
        buttonFont.setColor(Color.WHITE);
        
        // Charger le background
        try {
            backgroundTexture = new Texture(Gdx.files.internal("assets/menu/Gemini_Generated_Image_xrzy8qxrzy8qxrzy.png"));
            backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            System.out.println("✅ Background Game Over chargé");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement background Game Over : " + e.getMessage());
        }
        
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        createButtons();
        
        // ⭐ Arrêter la musique de level et lancer la musique de victoire/défaite
        audioManager.stopLevelMusic();
        audioManager.startVictoryMusic();
        System.out.println("💀 Game Over Screen");
    }
    
    private void createButtons() {
        float centerX = screenWidth / 2f;
        float buttonWidth = 400f;
        float buttonHeight = 80f;
        float buttonY = screenHeight * 0.3f;
        float spacing = 100f;
        
        returnMenuButton = new SimpleButton("RETURN MENU", centerX, buttonY, buttonWidth, buttonHeight);
        quitButton = new SimpleButton("QUIT", centerX, buttonY - spacing, buttonWidth, buttonHeight);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Mise à jour animation
        animationTimer += delta;
        titleOffsetY = (float) Math.sin(animationTimer * 2f) * 15f; // Oscillation de ±15 pixels
        
        int mouseX = Gdx.input.getX();
        int mouseY = (int) (screenHeight - Gdx.input.getY());
        
        // Mise à jour des boutons
        returnMenuButton.update(mouseX, mouseY);
        quitButton.update(mouseX, mouseY);
        
        // Gestion des clics
        if (Gdx.input.justTouched()) {
            if (returnMenuButton.isClicked(mouseX, mouseY)) {
                audioManager.stopVictoryMusic();
                audioManager.startMenuMusic();
                game.setScreen(new MenuScreen(game, audioManager));
                return;
            } else if (quitButton.isClicked(mouseX, mouseY)) {
                Gdx.app.exit();
                return;
            }
        }
        
        // Rendu
        batch.begin();
        
        // Background
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
        }
        
        // Titre "GAME OVER" avec animation
        float titleY = screenHeight * 0.75f + titleOffsetY;
        titleFont.draw(batch, "GAME OVER", 0, titleY, screenWidth, Align.center, false);
        
        batch.end();
        
        // Boutons (fonds)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderButton(shapeRenderer, returnMenuButton);
        renderButton(shapeRenderer, quitButton);
        shapeRenderer.end();
        
        // Textes des boutons
        batch.begin();
        returnMenuButton.renderText(batch, buttonFont);
        quitButton.renderText(batch, buttonFont);
        batch.end();
    }
    
    private void renderButton(ShapeRenderer sr, SimpleButton btn) {
        Color bgColor = btn.hovered 
            ? new Color(0.9f, 0.2f, 0.2f, 0.9f)  // Rouge hover
            : new Color(0.2f, 0.2f, 0.25f, 0.95f); // Gris normal
        
        sr.setColor(bgColor);
        sr.rect(btn.bounds.x, btn.bounds.y, btn.bounds.width, btn.bounds.height);
    }
    
    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        createButtons();
    }
    
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
    
    // Classe interne pour les boutons
    private static class SimpleButton {
        String text;
        Rectangle bounds;
        boolean hovered = false;
        
        public SimpleButton(String text, float centerX, float centerY, float width, float height) {
            this.text = text;
            this.bounds = new Rectangle(centerX - width/2, centerY - height/2, width, height);
        }
        
        public void update(int mouseX, int mouseY) {
            hovered = bounds.contains(mouseX, mouseY);
        }
        
        public void renderText(SpriteBatch batch, BitmapFont font) {
            font.draw(batch, text, bounds.x, bounds.y + bounds.height/2 + 15f, bounds.width, Align.center, false);
        }
        
        public boolean isClicked(int mouseX, int mouseY) {
            return bounds.contains(mouseX, mouseY);
        }
    }
}