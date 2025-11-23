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
 * Écran de Game Over.
 * Affiché quand le joueur meurt.
 */
public class GameOverScreen extends BaseScreen {
    
    private SimpleButton returnMenuButton;
    private SimpleButton quitButton;
    
    private float animationTimer = 0f;
    private float titleOffsetY = 0f;
    
    public GameOverScreen(FistOfSteelGame game, AudioManager audioManager) {
        super(game, audioManager);
    }
    
    @Override
    protected String getBackgroundPath() {
        return "assets/menu/Gemini_Generated_Image_xrzy8qxrzy8qxrzy.png";
    }
    
    @Override
    protected void initializeFonts() {
        super.initializeFonts();
        titleFont.getData().setScale(5f);
        titleFont.setColor(new Color(0.9f, 0.1f, 0.1f, 1f));
        font.getData().setScale(2f);
    }
    
    @Override
    public void show() {
        super.show();
        audioManager.stopLevelMusic();
        audioManager.startVictoryMusic();
        System.out.println("Game Over Screen");
    }
    
    @Override
    protected void createElements() {
        float centerX = screenWidth / 2f;
        float buttonWidth = 300f;
        float buttonHeight = 80f;
        float buttonY = screenHeight * 0.3f;
        float spacing = 50f;
        
        returnMenuButton = new SimpleButton("RETURN MENU", centerX - buttonWidth/2f - spacing/2f, buttonY, buttonWidth, buttonHeight);
        quitButton = new SimpleButton("QUIT", centerX + buttonWidth/2f + spacing/2f, buttonY, buttonWidth, buttonHeight);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        animationTimer += delta;
        titleOffsetY = (float) Math.sin(animationTimer * 2f) * 15f;
        
        int mouseX = Gdx.input.getX();
        int mouseY = (int)(screenHeight - Gdx.input.getY());
        
        returnMenuButton.update(mouseX, mouseY);
        quitButton.update(mouseX, mouseY);
        
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
        
        batch.begin();
        renderBackground();
        float titleY = screenHeight * 0.75f + titleOffsetY;
        titleFont.draw(batch, "GAME OVER", 0, titleY, screenWidth, Align.center, false);
        batch.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderButton(shapeRenderer, returnMenuButton);
        renderButton(shapeRenderer, quitButton);
        shapeRenderer.end();
        
        batch.begin();
        returnMenuButton.renderText(batch, font);
        quitButton.renderText(batch, font);
        batch.end();
    }
    
    private void renderButton(ShapeRenderer sr, SimpleButton btn) {
        Color bgColor = btn.isHovered() 
            ? new Color(0.9f, 0.2f, 0.2f, 0.9f)
            : new Color(0.2f, 0.2f, 0.25f, 0.95f);
        
        sr.setColor(bgColor);
        sr.rect(btn.getBounds().x, btn.getBounds().y, btn.getBounds().width, btn.getBounds().height);
    }
}