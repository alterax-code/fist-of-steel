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

public class MenuScreen implements Screen {
    private FistOfSteelGame game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    private Texture backgroundTexture;
    
    private MenuButton newGameButton;
    private MenuButton optionsButton;
    private MenuButton quitButton;
    
    private float screenWidth;
    private float screenHeight;
    
    private static final String TITLE = "FIST OF STEEL";
    private static final String SUBTITLE = "Marvin's Vengeance";
    
    public MenuScreen(FistOfSteelGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);
        titleFont.setColor(new Color(1f, 0.2f, 0.2f, 1f));
        
        try {
            backgroundTexture = new Texture(Gdx.files.internal("assets/menu/menu_background.png"));
        } catch (Exception e) {
            System.out.println("⚠️ Pas d'image de fond, utilisation d'un fond uni");
        }
        
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        createButtons();
    }
    
    private void createButtons() {
        float centerX = screenWidth / 2f;
        float startY = screenHeight * 0.55f;
        float buttonWidth = screenWidth * 0.2f;
        if (buttonWidth < 300) buttonWidth = 300;
        float buttonHeight = 80f;
        float buttonSpacing = screenHeight * 0.11f;
        
        newGameButton = new MenuButton("NEW GAME", centerX, startY, buttonWidth, buttonHeight);
        optionsButton = new MenuButton("OPTIONS", centerX, startY - buttonSpacing, buttonWidth, buttonHeight);
        quitButton = new MenuButton("QUIT", centerX, startY - buttonSpacing * 2, buttonWidth, buttonHeight);
    }
    
    @Override
    public void render(float delta) {
        if (backgroundTexture != null) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
        } else {
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
        }
        
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
        newGameButton.render(shapeRenderer);
        optionsButton.render(shapeRenderer);
        quitButton.render(shapeRenderer);
        shapeRenderer.end();
        
        batch.begin();
        newGameButton.renderText(batch, font);
        optionsButton.renderText(batch, font);
        quitButton.renderText(batch, font);
        batch.end();
        
        if (Gdx.input.justTouched()) {
            if (newGameButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new GameManager(game));
            } else if (optionsButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new OptionsScreen(game));
            } else if (quitButton.isClicked(mouseX, mouseY)) {
                Gdx.app.exit();
            }
        }
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
        font.dispose();
        titleFont.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
    
    private static class MenuButton {
        private String text;
        private Rectangle bounds;
        private boolean hovered = false;
        
        private Color normalColor = new Color(0.2f, 0.2f, 0.25f, 0.9f);
        private Color hoverColor = new Color(0.8f, 0.1f, 0.1f, 0.9f);
        
        public MenuButton(String text, float centerX, float centerY, float width, float height) {
            this.text = text;
            this.bounds = new Rectangle(centerX - width/2, centerY - height/2, width, height);
        }
        
        public void update(int mouseX, int mouseY) {
            hovered = bounds.contains(mouseX, mouseY);
        }
        
        public void render(ShapeRenderer sr) {
            Color color = hovered ? hoverColor : normalColor;
            
            sr.setColor(color);
            sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            
            if (hovered) {
                sr.end();
                sr.begin(ShapeRenderer.ShapeType.Line);
                Gdx.gl.glLineWidth(3);
                sr.setColor(Color.WHITE);
                sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
                sr.end();
                sr.begin(ShapeRenderer.ShapeType.Filled);
            }
        }
        
        public void renderText(SpriteBatch batch, BitmapFont font) {
            font.setColor(Color.WHITE);
            font.draw(batch, text, bounds.x, bounds.y + bounds.height/2 + 10, bounds.width, Align.center, false);
        }
        
        public boolean isClicked(int mouseX, int mouseY) {
            return bounds.contains(mouseX, mouseY);
        }
    }
}