package com.fistofsteel.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.utils.ColorUtils;

/**
 * HUD du joueur affiché en haut à gauche de l'écran
 */
public class PlayerHUD {
    
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera hudCamera;
    
    private float levelTime = 0f;
    
    private static final float MARGIN = 20f;
    private static final float HP_BAR_WIDTH = 250f;
    private static final float HP_BAR_HEIGHT = 25f;
    private static final float INVENTORY_SLOT_SIZE = 50f;
    private static final float SPACING = 15f;
    
    public PlayerHUD() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, screenWidth, screenHeight);
        hudCamera.update();
    }
    
    public void update(float delta) {
        levelTime += delta;
    }
    
    public void render(SpriteBatch batch, Player player, int enemiesKilled, int totalEnemies) {
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        batch.setProjectionMatrix(hudCamera.combined);
        
        float screenHeight = Gdx.graphics.getHeight();
        float currentY = screenHeight - MARGIN;
        
        currentY -= HP_BAR_HEIGHT;
        renderHealthBar(batch, currentY, player.getHealth(), player.getMaxHealth());
        
        currentY -= SPACING + INVENTORY_SLOT_SIZE;
        renderInventory(currentY);
        
        currentY -= SPACING + 30f;
        renderTimer(batch, currentY);
        
        currentY -= SPACING + 30f;
        renderKillCounter(batch, currentY, enemiesKilled, totalEnemies);
    }
    
    private void renderHealthBar(SpriteBatch batch, float y, int currentHealth, int maxHealth) {
        float healthPercent = Math.max(0f, Math.min(1f, (float) currentHealth / maxHealth));
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(MARGIN - 2, y - 2, HP_BAR_WIDTH + 4, HP_BAR_HEIGHT + 4);
        
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.9f));
        shapeRenderer.rect(MARGIN, y, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        
        // Utilise ColorUtils pour la couleur
        Color healthColor = ColorUtils.getHealthColor(healthPercent);
        shapeRenderer.setColor(healthColor);
        shapeRenderer.rect(MARGIN, y, HP_BAR_WIDTH * healthPercent, HP_BAR_HEIGHT);
        
        shapeRenderer.end();
        
        batch.begin();
        font.setColor(Color.WHITE);
        String hpText = currentHealth + " / " + maxHealth + " HP";
        font.draw(batch, hpText, MARGIN + 5, y + HP_BAR_HEIGHT - 5);
        batch.end();
    }
    
    private void renderInventory(float y) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int i = 0; i < 2; i++) {
            float slotX = MARGIN + i * (INVENTORY_SLOT_SIZE + 10f);
            
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(slotX - 2, y - 2, INVENTORY_SLOT_SIZE + 4, INVENTORY_SLOT_SIZE + 4);
            
            shapeRenderer.setColor(new Color(0.15f, 0.15f, 0.2f, 0.9f));
            shapeRenderer.rect(slotX, y, INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE);
        }
        
        shapeRenderer.end();
    }
    
    private void renderTimer(SpriteBatch batch, float y) {
        batch.begin();
        font.setColor(Color.CYAN);
        int seconds = (int) levelTime;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);
        font.draw(batch, timeText, MARGIN, y);
        batch.end();
    }
    
    private void renderKillCounter(SpriteBatch batch, float y, int killed, int total) {
        batch.begin();
        font.setColor(Color.YELLOW);
        String killText = "Kills: " + killed + " / " + total;
        font.draw(batch, killText, MARGIN, y);
        batch.end();
    }
    
    public void resetTimer() {
        levelTime = 0f;
    }
    
    public float getLevelTime() {
        return levelTime;
    }
    
    public void resize(int width, int height) {
        hudCamera.setToOrtho(false, width, height);
        hudCamera.update();
    }
    
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}