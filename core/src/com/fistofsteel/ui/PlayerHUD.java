package com.fistofsteel.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.items.Armor;
import com.fistofsteel.items.Weapon;
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
    
    // Textures des icônes d'items
    private Texture armorLightIcon;
    private Texture armorHeavyIcon;
    private Texture sword1Icon;
    private Texture sword2Icon;
    private Texture sword3Icon;
    
    public PlayerHUD() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        
        // Charger les icônes d'items
        try {
            armorLightIcon = new Texture("assets/items/armor_light.png");
            armorHeavyIcon = new Texture("assets/items/armor_heavy.png");
            sword1Icon = new Texture("assets/items/sword_1.png");
            sword2Icon = new Texture("assets/items/sword_2.png");
            sword3Icon = new Texture("assets/items/sword_3.png");
            System.out.println("✅ Icônes d'items chargées pour le HUD");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement icônes items : " + e.getMessage());
        }
        
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
        renderInventory(batch, currentY, player);
        
        currentY -= SPACING + 30f;
        renderTimer(batch, currentY);
        
        currentY -= SPACING + 30f;
        renderKillCounter(batch, currentY, enemiesKilled, totalEnemies);
    }
    
    private void renderHealthBar(SpriteBatch batch, float y, int currentHealth, int maxHealth) {
        if (maxHealth <= 0) return;
        
        float healthPercent = Math.max(0f, Math.min(1f, (float) currentHealth / maxHealth));
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(MARGIN - 2, y - 2, HP_BAR_WIDTH + 4, HP_BAR_HEIGHT + 4);
        
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.9f));
        shapeRenderer.rect(MARGIN, y, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        
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
    
    private void renderInventory(SpriteBatch batch, float y, Player player) {
        // Dessiner les slots
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int i = 0; i < 2; i++) {
            float slotX = MARGIN + i * (INVENTORY_SLOT_SIZE + 10f);
            
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(slotX - 2, y - 2, INVENTORY_SLOT_SIZE + 4, INVENTORY_SLOT_SIZE + 4);
            
            shapeRenderer.setColor(new Color(0.15f, 0.15f, 0.2f, 0.9f));
            shapeRenderer.rect(slotX, y, INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE);
        }
        
        shapeRenderer.end();
        
        // Dessiner les icônes des items équipés
        batch.begin();
        
        // SLOT 0 : ARMURE
        Armor armor = player.getEquippedArmor();
        if (armor != null) {
            Texture icon = getArmorIcon(armor);
            if (icon != null) {
                float slotX = MARGIN;
                float centerX = slotX + INVENTORY_SLOT_SIZE / 2f;
                float centerY = y + INVENTORY_SLOT_SIZE / 2f;
                float size = INVENTORY_SLOT_SIZE - 8f;
                
                batch.draw(icon, centerX - size / 2f, centerY - size / 2f, size, size);
            }
        }
        
        // SLOT 1 : ARME
        Weapon weapon = player.getEquippedWeapon();
        if (weapon != null) {
            Texture icon = getWeaponIcon(weapon);
            if (icon != null) {
                float slotX = MARGIN + (INVENTORY_SLOT_SIZE + 10f);
                float centerX = slotX + INVENTORY_SLOT_SIZE / 2f;
                float centerY = y + INVENTORY_SLOT_SIZE / 2f;
                float size = INVENTORY_SLOT_SIZE - 8f;
                
                batch.draw(icon, centerX - size / 2f, centerY - size / 2f, size, size);
            }
        }
        
        batch.end();
    }
    
    /**
     * Retourne l'icône appropriée pour l'armure équipée
     */
    private Texture getArmorIcon(Armor armor) {
        if (armor == null) return null;
        
        String id = armor.getId();
        if ("armor_light".equals(id)) {
            return armorLightIcon;
        } else if ("armor_heavy".equals(id)) {
            return armorHeavyIcon;
        }
        
        return null;
    }
    
    /**
     * Retourne l'icône appropriée pour l'arme équipée
     */
    private Texture getWeaponIcon(Weapon weapon) {
        if (weapon == null) return null;
        
        String id = weapon.getId();
        if ("sword1".equals(id)) {
            return sword1Icon;
        } else if ("sword2".equals(id)) {
            return sword2Icon;
        } else if ("sword3".equals(id)) {
            return sword3Icon;
        }
        
        return null;
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
        
        if (armorLightIcon != null) armorLightIcon.dispose();
        if (armorHeavyIcon != null) armorHeavyIcon.dispose();
        if (sword1Icon != null) sword1Icon.dispose();
        if (sword2Icon != null) sword2Icon.dispose();
        if (sword3Icon != null) sword3Icon.dispose();
    }
}