package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.fistofsteel.FistOfSteelGame;
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.ui.UIComponents.SimpleButton;

/**
 * MenuScreen - Hérite de BaseScreen
 */
public class MenuScreen extends BaseScreen {
    
    private SimpleButton newGameButton;
    private SimpleButton optionsButton;
    private SimpleButton quitButton;
    
    private static final String TITLE = "FIST OF STEEL";
    private static final String SUBTITLE = "Marvin's Vengeance";
    
    public MenuScreen(FistOfSteelGame game, AudioManager audioManager) {
        super(game, audioManager);
    }
    
    @Override
    protected String getBackgroundPath() {
        return "assets/menu/menu_background.png";
    }
    
    @Override
    protected void initializeFonts() {
        super.initializeFonts();
        titleFont.setColor(new Color(1f, 0.2f, 0.2f, 1f));
    }
    
    @Override
    public void show() {
        super.show();
        
        if (!audioManager.isMenuMusicPlaying()) {
            audioManager.startMenuMusic();
            System.out.println("🎵 MenuScreen : Musique menu démarrée");
        }
    }
    
    @Override
    protected void createElements() {
        float centerX = screenWidth / 2f;
        float startY = screenHeight * 0.55f;
        float buttonWidth = Math.max(300f, screenWidth * 0.2f);
        float buttonHeight = 80f;
        float buttonSpacing = screenHeight * 0.11f;
        
        newGameButton = new SimpleButton("NEW GAME", centerX, startY, buttonWidth, buttonHeight);
        optionsButton = new SimpleButton("OPTIONS", centerX, startY - buttonSpacing, buttonWidth, buttonHeight);
        quitButton = new SimpleButton("QUIT", centerX, startY - buttonSpacing * 2, buttonWidth, buttonHeight);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundTexture != null ? 0 : 0.1f, 
                           backgroundTexture != null ? 0 : 0.1f, 
                           backgroundTexture != null ? 0 : 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        renderBackground();
        
        float titleY = screenHeight * 0.88f;
        float subtitleY = screenHeight * 0.80f;
        titleFont.draw(batch, TITLE, 0, titleY, screenWidth, Align.center, false);
        font.draw(batch, SUBTITLE, 0, subtitleY, screenWidth, Align.center, false);
        batch.end();
        
        int mouseX = Gdx.input.getX();
        int mouseY = (int)screenHeight - Gdx.input.getY();
        
        newGameButton.update(mouseX, mouseY);
        optionsButton.update(mouseX, mouseY);
        quitButton.update(mouseX, mouseY);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        newGameButton.renderWithBorder(shapeRenderer);
        optionsButton.renderWithBorder(shapeRenderer);
        quitButton.renderWithBorder(shapeRenderer);
        shapeRenderer.end();
        
        batch.begin();
        newGameButton.renderText(batch, font);
        optionsButton.renderText(batch, font);
        quitButton.renderText(batch, font);
        batch.end();
        
        if (Gdx.input.justTouched()) {
            if (newGameButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new CharactersChoice(game, audioManager));
            } else if (optionsButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new OptionsScreen(game, audioManager));
            } else if (quitButton.isClicked(mouseX, mouseY)) {
                Gdx.app.exit();
            }
        }
    }
}