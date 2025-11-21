package com.fistofsteel.entities.world; 

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.fistofsteel.entities.player.Player;  
import com.fistofsteel.items.Item;


public class ItemPickup {

    private float x, y;
    private Texture texture;
    private Item item;
    private boolean collected = false;
    private Rectangle bounds;

    private float drawWidth;
    private float drawHeight;

    public ItemPickup(float x, float y, Texture texture, Item item) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.item = item;

        
        if (texture != null) {
            float texW = texture.getWidth();
            float texH = texture.getHeight();
            float maxSize = 64f;

            if (texH >= texW) {
                float scale = maxSize / texH;
                drawHeight = maxSize;
                drawWidth = texW * scale;
            } else {
                float scale = maxSize / texW;
                drawWidth = maxSize;
                drawHeight = texH * scale;
            }
        } else {
            drawWidth = drawHeight = 64f;
        }

        bounds = new Rectangle(x, y, drawWidth, drawHeight);
    }

    public void update(float delta) {
        
    }

    public void render(SpriteBatch batch) {
        if (collected || texture == null) return;
        batch.draw(texture, x, y, drawWidth, drawHeight);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isCollected() {
        return collected;
    }

    public void onPickup(Player player) {
        if (collected) return;
        collected = true;

        if (item != null) {
            item.applyEffect(player);
        }

        System.out.println("✅ Item ramassé : " + (item != null ? item.getDisplayName() : "???"));
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
