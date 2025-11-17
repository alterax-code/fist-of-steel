package com.fistofsteel.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Potion {
    private float x, y;
    
    private static final float POTION_WIDTH = 64f;
    private static final float POTION_HEIGHT = 64f;
    
    private Texture[] frames;
    private int currentFrame = 0;
    private float animationTimer = 0f;
    private static final float FRAME_DURATION = 0.15f;
    
    private boolean collected = false;
    private Rectangle bounds;
    
    private float totalTime = 0f;
    private static final float HOVER_SPEED = 2f;
    private static final float HOVER_AMPLITUDE = 5f;
    
    public Potion(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, POTION_WIDTH, POTION_HEIGHT);
        loadTextures();
    }
    
    private void loadTextures() {
        frames = new Texture[5];
        
        try {
            frames[0] = new Texture("assets/items/potion_frame_1.png");
            frames[1] = new Texture("assets/items/potion_frame_2.png");
            frames[2] = new Texture("assets/items/potion_frame_3.png");
            frames[3] = new Texture("assets/items/potion_frame_4.png");
            frames[4] = new Texture("assets/items/potion_frame_5.png");
            
            System.out.println("Potion textures chargees");
        } catch (Exception e) {
            System.err.println("Erreur chargement sprites potion : " + e.getMessage());
        }
    }
    
    public void update(float delta) {
        if (collected) return;
        
        animationTimer += delta;
        if (animationTimer >= FRAME_DURATION) {
            animationTimer = 0f;
            currentFrame = (currentFrame + 1) % frames.length;
        }
        
        totalTime += delta;
    }
    
    public void render(SpriteBatch batch) {
        if (collected || frames[currentFrame] == null) return;
        
        float hoverOffset = (float)Math.sin(totalTime * HOVER_SPEED) * HOVER_AMPLITUDE;
        
        batch.draw(frames[currentFrame], x, y + hoverOffset, POTION_WIDTH, POTION_HEIGHT);
    }
    
    public boolean checkCollision(Rectangle playerBounds) {
        if (collected) return false;
        return bounds.overlaps(playerBounds);
    }
    
    public void collect() {
        collected = true;
        System.out.println("Potion collectee a (" + x + ", " + y + ")");
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public float getX() { 
        return x; 
    }
    
    public float getY() { 
        return y; 
    }
    
    public void dispose() {
        if (frames != null) {
            for (Texture frame : frames) {
                if (frame != null) {
                    frame.dispose();
                }
            }
        }
    }
}