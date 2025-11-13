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

public class CharactersChoice implements Screen {

    private final FistOfSteelGame game;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;

    private float screenWidth;
    private float screenHeight;

    private SimpleButton hugoButton;
    private SimpleButton alexisButton;
    private SimpleButton backButton;

    private String selectedCharacter = null; // "Hugo" ou "Alexis"

    public CharactersChoice(FistOfSteelGame game) {
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
        titleFont.setColor(new Color(1f, 0.8f, 0.2f, 1f));

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        createElements();
    }

    private void createElements() {
        float centerX = screenWidth / 2f;

        float charBtnWidth = Math.min(360f, screenWidth * 0.32f);
        float charBtnHeight = 120f;

        float yRow = screenHeight * 0.55f;
        float gap = 50f;

        // ✅ Correction : positionnement des boutons
        hugoButton   = new SimpleButton("HUGO",   centerX - charBtnWidth - gap/2f, yRow, charBtnWidth, charBtnHeight);
        alexisButton = new SimpleButton("ALEXIS", centerX + gap/2f, yRow, charBtnWidth, charBtnHeight);

        backButton = new SimpleButton("BACK", centerX, screenHeight * 0.18f, 300f, 70f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.10f, 0.10f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int mouseX = Gdx.input.getX();
        int mouseY = (int) (screenHeight - Gdx.input.getY());

        // Raccourci
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        // Clics
        if (Gdx.input.justTouched()) {
            if (hugoButton.isClicked(mouseX, mouseY)) {
                selectedCharacter = "Hugo";
                // ✅ Lancer le jeu avec Hugo
                game.setScreen(new GameManager(game, "Hugo"));
                return;
            } else if (alexisButton.isClicked(mouseX, mouseY)) {
                selectedCharacter = "Alexis";
                // ✅ Lancer le jeu avec Alexis
                game.setScreen(new GameManager(game, "Alexis"));
                return;
            } else if (backButton.isClicked(mouseX, mouseY)) {
                game.setScreen(new MenuScreen(game));
                return;
            }
        }

        // Mise à jour hover des boutons
        hugoButton.update(mouseX, mouseY);
        alexisButton.update(mouseX, mouseY);
        backButton.update(mouseX, mouseY);

        // Dessin UI - Titres
        batch.begin();
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

        // Boutons (fonds)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderButton(shapeRenderer, hugoButton, "Hugo".equals(selectedCharacter));
        renderButton(shapeRenderer, alexisButton, "Alexis".equals(selectedCharacter));
        renderButton(shapeRenderer, backButton, false);
        shapeRenderer.end();

        // Textes
        batch.begin();
        hugoButton.renderText(batch, font);
        alexisButton.renderText(batch, font);
        backButton.renderText(batch, font);
        batch.end();
    }

    private void renderButton(ShapeRenderer sr, SimpleButton btn, boolean selected) {
        Color bgColor;
        if (selected) {
            bgColor = new Color(0.15f, 0.45f, 0.18f, 1f); // vert sélection
        } else if (btn.hovered) {
            bgColor = new Color(0.8f, 0.1f, 0.1f, 0.9f); // rouge hover
        } else {
            bgColor = new Color(0.20f, 0.20f, 0.25f, 0.95f); // gris normal
        }
        sr.setColor(bgColor);
        sr.rect(btn.bounds.x, btn.bounds.y, btn.bounds.width, btn.bounds.height);
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

    // ====== Bouton simple ======
    private static class SimpleButton {
        String text;
        Rectangle bounds;
        boolean hovered = false;

        public SimpleButton(String text, float x, float y, float width, float height) {
            this.text = text;
            this.bounds = new Rectangle(x, y, width, height);
        }

        public void update(int mouseX, int mouseY) {
            hovered = bounds.contains(mouseX, mouseY);
        }

        public void renderText(SpriteBatch batch, BitmapFont font) {
            font.draw(batch, text, bounds.x, bounds.y + bounds.height / 2f + 10f, bounds.width, Align.center, false);
        }

        public boolean isClicked(int mouseX, int mouseY) {
            return bounds.contains(mouseX, mouseY);
        }
    }
}