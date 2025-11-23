package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fistofsteel.FistOfSteelGame;
import com.fistofsteel.audio.AudioManager;

/**
 * Classe de base pour tous les écrans du jeu.
 * Gère les ressources communes (batch, fonts, background).
 */
public abstract class BaseScreen implements Screen {
    
    protected final FistOfSteelGame game;
    protected final AudioManager audioManager;
    
    protected SpriteBatch batch;
    protected ShapeRenderer shapeRenderer;
    protected BitmapFont font;
    protected BitmapFont titleFont;
    
    protected Texture backgroundTexture;
    
    protected float screenWidth;
    protected float screenHeight;
    
    /**
     * Constructeur de l'écran de base.
     * 
     * @param game L'instance du jeu
     * @param audioManager Le gestionnaire audio
     */
    public BaseScreen(FistOfSteelGame game, AudioManager audioManager) {
        this.game = game;
        this.audioManager = audioManager;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        initializeFonts();
        loadBackground();
        createElements();
    }
    
    /**
     * Initialise les couleurs et tailles des fonts.
     * À surcharger si besoin de personnalisation.
     */
    protected void initializeFonts() {
        font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        titleFont.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    }
    
    /**
     * Charge le background de l'écran.
     */
    protected void loadBackground() {
        String backgroundPath = getBackgroundPath();
        if (backgroundPath != null) {
            try {
                backgroundTexture = new Texture(Gdx.files.internal(backgroundPath));
                backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                System.out.println("Background charge : " + backgroundPath);
            } catch (Exception e) {
                System.err.println("Erreur chargement background : " + e.getMessage());
            }
        }
    }
    
    /**
     * Dessine le background s'il existe.
     */
    protected void renderBackground() {
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
        }
    }
    
    /**
     * Retourne le chemin du background (null si pas de background).
     * 
     * @return Le chemin du fichier de background
     */
    protected abstract String getBackgroundPath();
    
    /**
     * Crée les éléments spécifiques à l'écran (boutons, etc.).
     */
    protected abstract void createElements();
    
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
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}