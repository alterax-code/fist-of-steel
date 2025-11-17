package com.fistofsteel.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Gestionnaire centralisé de toutes les potions du niveau
 */
public class PotionManager {
    
    private Array<Potion> potions;
    
    public PotionManager() {
        potions = new Array<>();
    }
    
    public void addPotion(float x, float y) {
        Potion potion = new Potion(x, y);
        potions.add(potion);
        System.out.println("💊 Potion ajoutée à (" + x + ", " + y + ")");
    }
    
    public void update(float delta) {
        for (Potion potion : potions) {
            potion.update(delta);
        }
    }
    
    public void render(SpriteBatch batch) {
        for (Potion potion : potions) {
            potion.render(batch);
        }
    }
    
    public int checkCollisions(Rectangle playerBounds) {
        int collected = 0;
        
        for (Potion potion : potions) {
            if (potion.checkCollision(playerBounds)) {
                potion.collect();
                collected++;
            }
        }
        
        return collected;
    }
    
    public void removeCollectedPotions() {
        Array<Potion> toRemove = new Array<>();
        
        for (Potion potion : potions) {
            if (potion.isCollected()) {
                toRemove.add(potion);
            }
        }
        
        for (Potion potion : toRemove) {
            potions.removeValue(potion, true);
            potion.dispose();
        }
    }
    
    public int getRemainingPotions() {
        int count = 0;
        for (Potion potion : potions) {
            if (!potion.isCollected()) {
                count++;
            }
        }
        return count;
    }
    
    public void dispose() {
        for (Potion potion : potions) {
            potion.dispose();
        }
        potions.clear();
    }
}