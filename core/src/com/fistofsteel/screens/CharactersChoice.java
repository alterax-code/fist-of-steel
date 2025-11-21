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

/**
 * CharactersChoice - Hérite de BaseScreen
 * ✅ MODIFIÉ : Boutons de sélection centrés
 */
public class CharactersChoice extends BaseScreen {
    
    private SimpleButton hugoButton;
    private SimpleButton alexisButton;
    private SimpleButton backButton;
    
    private String selectedCharacter = null;
    
    public CharactersChoice(FistOfSteelGame game, AudioManager audioManager) {
        super(game, audioManager);
    }
    
    @Override
    protected String getBackgroundPath() {
        return "assets/menu/character_selection_background.png";
    }
    
    @Override
    protected void initializeFonts() {
        super.initializeFonts();
        titleFont.setColor(new Color(1f, 0.8f, 0.2f, 1f));
    }
    
    @Override
    public void show() {
        super.show();
        System.out.println("🎵 CharactersChoice : Musique menu continue");
    }
    
    @Override
    protected void createElements() {
        float centerX = screenWidth / 2f;
        float charBtnWidth = Math.min(360f, screenWidth * 0.32f);
        float charBtnHeight = 120f;
        float yRow = screenHeight * 0.5f; // ✅ Centré verticalement
        float gap = 50f;
        
        // ✅ Boutons centrés horizontalement
        hugoButton = new SimpleButton("HUGO", centerX - charBtnWidth/2f - gap/2f, yRow, charBtnWidth, charBtnHeight);
        alexisButton = new SimpleButton("ALEXIS", centerX + charBtnWidth/2f + gap/2f, yRow, charBtnWidth, charBtnHeight);
        backButton = new SimpleButton("BACK", centerX, screenHeight * 0.18f, 300f, 70f);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        int mouseX = Gdx.input.getX();
        int mouseY = (int)(screenHeight - Gdx.input.getY());
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game, audioManager));
            return;
        }
        
        if (Gdx.input.justTouched()) {
            if (hugoButton.isClicked(mouseX, mouseY)) {
                selectedCharacter = "Hugo";
                game.setScreen(new GameManager(game, "Hugo", audioManager));
                return;
            } else if (alexisButton.isClicked(mouseX, mouseY)) {
                selectedCharacter = "Alexis";
                game.setScreen(new GameManager(game, "Alexis", audioManager));
                return;
            } else if (backButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new MenuScreen(game, audioManager));
                return;
            }
        }
        
        hugoButton.update(mouseX, mouseY);
        alexisButton.update(mouseX, mouseY);
        backButton.update(mouseX, mouseY);
        
        batch.begin();
        renderBackground();
        titleFont.draw(batch, "CHOOSE YOUR FIGHTER", 0, screenHeight * 0.86f, screenWidth, Align.center, false);
        
        if (selectedCharacter == null) {
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Click a character to start", 0, screenHeight * 0.30f, screenWidth, Align.center, false);
        } else {
            font.setColor(Color.GREEN);
            font.draw(batch, "Selected: " + selectedCharacter, 0, screenHeight * 0.30f, screenWidth, Align.center, false);
        }
        font.setColor(Color.WHITE);
        batch.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderCharacterButton(shapeRenderer, hugoButton, "Hugo".equals(selectedCharacter));
        renderCharacterButton(shapeRenderer, alexisButton, "Alexis".equals(selectedCharacter));
        backButton.render(shapeRenderer);
        shapeRenderer.end();
        
        batch.begin();
        hugoButton.renderText(batch, font);
        alexisButton.renderText(batch, font);
        backButton.renderText(batch, font);
        batch.end();
    }
    
    private void renderCharacterButton(ShapeRenderer sr, SimpleButton btn, boolean selected) {
        Color bgColor;
        if (selected) {
            bgColor = new Color(0.15f, 0.45f, 0.18f, 1f);
        } else if (btn.isHovered()) {
            bgColor = new Color(0.8f, 0.1f, 0.1f, 0.9f);
        } else {
            bgColor = new Color(0.20f, 0.20f, 0.25f, 0.95f);
        }
        sr.setColor(bgColor);
        sr.rect(btn.getBounds().x, btn.getBounds().y, btn.getBounds().width, btn.getBounds().height);
    }
}