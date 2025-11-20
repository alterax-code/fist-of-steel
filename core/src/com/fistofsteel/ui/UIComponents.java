package com.fistofsteel.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.Gdx;

/**
 * Composants UI réutilisables pour tous les écrans
 */
public class UIComponents {
    
    /**
     * Bouton simple avec hover effect
     */
    public static class SimpleButton {
        private String text;
        private Rectangle bounds;
        private boolean hovered = false;
        
        private Color normalColor = new Color(0.2f, 0.2f, 0.25f, 0.95f);
        private Color hoverColor = new Color(0.8f, 0.1f, 0.1f, 0.9f);
        
        public SimpleButton(String text, float centerX, float centerY, float width, float height) {
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
        }
        
        public void renderWithBorder(ShapeRenderer sr) {
            render(sr);
            
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
            font.draw(batch, text, bounds.x, bounds.y + bounds.height/2 + 10f, bounds.width, Align.center, false);
        }
        
        public boolean isClicked(int mouseX, int mouseY) {
            return bounds.contains(mouseX, mouseY);
        }
        
        public String getText() {
            return text;
        }
        
        public Rectangle getBounds() {
            return bounds;
        }
        
        public boolean isHovered() {
            return hovered;
        }
    }
    
    /**
     * Slider de volume avec curseur draggable
     */
    public static class VolumeSlider {
        private String label;
        private Rectangle bounds;
        private Rectangle cursor;
        private float value;
        private boolean isDragging = false;
        
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
            // Fond de la barre
            sr.setColor(0.3f, 0.3f, 0.35f, 1f);
            sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            
            // Partie remplie
            sr.setColor(0.8f, 0.1f, 0.1f, 1f);
            sr.rect(bounds.x, bounds.y, bounds.width * value, bounds.height);
            
            // Curseur
            sr.setColor(Color.WHITE);
            sr.rect(cursor.x, cursor.y, cursor.width, cursor.height);
        }
        
        public void renderText(SpriteBatch batch, BitmapFont font) {
            font.draw(batch, label, bounds.x, bounds.y + 50);
            font.draw(batch, (int)(value * 100) + "%", bounds.x + bounds.width + 20, bounds.y + 15);
        }
        
        public float getValue() {
            return value;
        }
        
        public boolean isDragging() {
            return isDragging;
        }
        
        public void setDragging(boolean dragging) {
            this.isDragging = dragging;
        }
        
        public Rectangle getBounds() {
            return bounds;
        }
    }
    
    /**
     * Bouton de configuration de touche
     */
    public static class KeyButton {
        private String action;
        private String keyName;
        private Rectangle bounds;
        
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
            float originalScale = font.getScaleX();
            font.getData().setScale(1.5f);
            font.draw(batch, action, bounds.x + 10, bounds.y + 45);
            font.setColor(Color.YELLOW);
            font.draw(batch, keyName, bounds.x + 10, bounds.y + 20);
            font.setColor(Color.WHITE);
            font.getData().setScale(originalScale);
        }
        
        public boolean isClicked(int mouseX, int mouseY) {
            return bounds.contains(mouseX, mouseY);
        }
        
        public String getAction() {
            return action;
        }
        
        public String getKeyName() {
            return keyName;
        }
        
        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }
    }
}