package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.fistofsteel.FistOfSteelGame;

public class OptionsScreen implements Screen {
    private FistOfSteelGame game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    private VolumeSlider musicSlider;
    private VolumeSlider sfxSlider;
    
    private SimpleButton backButton;
    
    private boolean waitingForKey = false;
    private String keyToRemap = "";
    private KeyButton[] keyButtons;
    
    private float screenWidth;
    private float screenHeight;
    
    public OptionsScreen(FistOfSteelGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        font.setColor(Color.WHITE);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        titleFont.setColor(new Color(1f, 0.2f, 0.2f, 1f));
        
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        createElements();
    }
    
    private void createElements() {
        float centerX = screenWidth / 2f;
        
        float sliderWidth = screenWidth * 0.42f;
        if (sliderWidth > 800) sliderWidth = 800;
        float sliderX = centerX - sliderWidth / 2f;
        float sliderY1 = screenHeight * 0.65f;
        float sliderY2 = screenHeight * 0.51f;
        
        musicSlider = new VolumeSlider("MUSIC VOLUME", sliderX, sliderY1, sliderWidth, 0.5f);
        sfxSlider = new VolumeSlider("SFX VOLUME", sliderX, sliderY2, sliderWidth, 1.0f);
        
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
                        if (kb.action.equals(keyToRemap)) {
                            kb.keyName = Input.Keys.toString(keycode);
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
            game.setScreen(new MenuScreen(game));
            return;
        }
        
        if (Gdx.input.isTouched()) {
            if (musicSlider.isDragging || musicSlider.bounds.contains(mouseX, mouseY)) {
                musicSlider.updateValue(mouseX);
            }
            if (sfxSlider.isDragging || sfxSlider.bounds.contains(mouseX, mouseY)) {
                sfxSlider.updateValue(mouseX);
            }
        } else {
            musicSlider.isDragging = false;
            sfxSlider.isDragging = false;
        }
        
        if (Gdx.input.justTouched() && !waitingForKey) {
            if (backButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new MenuScreen(game));
                return;
            }
            
            for (KeyButton kb : keyButtons) {
                if (kb.isClicked(mouseX, mouseY)) {
                    waitingForKey = true;
                    keyToRemap = kb.action;
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
    
    @Override 
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        createElements();
    }
    
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        titleFont.dispose();
    }
    
    private static class VolumeSlider {
        String label;
        Rectangle bounds;
        Rectangle cursor;
        float value;
        boolean isDragging = false;
        
        public VolumeSlider(String label, float x, float y, float width, float initialValue) {
            this.label = label;
            this.bounds = new Rectangle(x, y, width, 20);
            this.value = initialValue;
            this.cursor = new Rectangle(x + width * value - 10, y - 10, 20, 40);
        }
        
        public void updateValue(int mouseX) {
            isDragging = true;
            value = Math.max(0f, Math.min(1f, (mouseX - bounds.x) / bounds.width));
            cursor.x = bounds.x + bounds.width * value - 10;
        }
        
        public void render(ShapeRenderer sr) {
            sr.setColor(0.3f, 0.3f, 0.35f, 1f);
            sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            
            sr.setColor(0.8f, 0.1f, 0.1f, 1f);
            sr.rect(bounds.x, bounds.y, bounds.width * value, bounds.height);
            
            sr.setColor(Color.WHITE);
            sr.rect(cursor.x, cursor.y, cursor.width, cursor.height);
        }
        
        public void renderText(SpriteBatch batch, BitmapFont font) {
            font.draw(batch, label, bounds.x, bounds.y + 50);
            font.draw(batch, (int)(value * 100) + "%", bounds.x + bounds.width + 20, bounds.y + 15);
        }
    }
    
    private static class SimpleButton {
        String text;
        Rectangle bounds;
        
        public SimpleButton(String text, float centerX, float centerY, float width, float height) {
            this.text = text;
            this.bounds = new Rectangle(centerX - width/2, centerY - height/2, width, height);
        }
        
        public void render(ShapeRenderer sr) {
            sr.setColor(0.2f, 0.2f, 0.25f, 0.9f);
            sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        public void renderText(SpriteBatch batch, BitmapFont font) {
            font.draw(batch, text, bounds.x, bounds.y + bounds.height/2 + 10, bounds.width, Align.center, false);
        }
        
        public boolean isClicked(int mouseX, int mouseY) {
            return bounds.contains(mouseX, mouseY);
        }
    }
    
    private static class KeyButton {
        String action;
        String keyName;
        Rectangle bounds;
        
        public KeyButton(String action, String keyName, float x, float y, float width) {
            this.action = action;
            this.keyName = keyName;
            this.bounds = new Rectangle(x - width/2, y, width, 60);
        }
        
        public void render(ShapeRenderer sr) {
            sr.setColor(0.25f, 0.25f, 0.3f, 1f);
            sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        public void renderText(SpriteBatch batch, BitmapFont font) {
            font.getData().setScale(1.5f);
            font.draw(batch, action, bounds.x + 10, bounds.y + 45);
            font.setColor(Color.YELLOW);
            font.draw(batch, keyName, bounds.x + 10, bounds.y + 20);
            font.setColor(Color.WHITE);
            font.getData().setScale(1.8f);
        }
        
        public boolean isClicked(int mouseX, int mouseY) {
            return bounds.contains(mouseX, mouseY);
        }
    }
}