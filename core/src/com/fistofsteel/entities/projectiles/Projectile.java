package com.fistofsteel.entities.projectiles;  // ✅ MODIFIÉ (était com.fistofsteel.entities)

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// ... reste du code inchangé

/**
 * Classe de base pour un projectile
 * VERSION AMÉLIORÉE avec système de distance max intégré
 */
public abstract class Projectile {
    
    protected float x, y;
    protected float velocityX, velocityY;
    protected float speed = 300f;
    protected int damage = 10;
    protected boolean active = true;
    protected boolean facingRight;
    
    protected Rectangle hitbox;
    protected float hitboxWidth = 30f;
    protected float hitboxHeight = 30f;
    
    // Animation
    protected float animationTimer = 0f;
    protected int currentFrame = 0;
    
    // Anti-oneshot
    protected boolean hasDealtDamage = false;
    
    // ⭐ NOUVEAU : Distance max tracking
    protected float distanceTraveled = 0f;
    protected float maxDistance = 600f; // Par défaut

    public Projectile(float x, float y, boolean facingRight, int damage) {
        this(x, y, facingRight, damage, 600f);
    }
    
    public Projectile(float x, float y, boolean facingRight, int damage, float maxDistance) {
        this.x = x;
        this.y = y;
        this.facingRight = facingRight;
        this.damage = damage;
        this.maxDistance = maxDistance;
        
        this.velocityX = facingRight ? speed : -speed;
        this.velocityY = 0f;
        
        this.hitbox = new Rectangle(x, y, hitboxWidth, hitboxHeight);
    }
    
    public void update(float delta) {
        if (!active) return;
        
        // ⭐ Tracking de la distance
        float deltaDistance = Math.abs(velocityX * delta);
        distanceTraveled += deltaDistance;
        
        if (distanceTraveled >= maxDistance) {
            deactivate();
            return;
        }
        
        // Déplacement
        x += velocityX * delta;
        y += velocityY * delta;
        
        // Mise à jour hitbox
        hitbox.setPosition(x, y);
        
        // Animation
        updateAnimation(delta);
    }
    
    protected abstract void updateAnimation(float delta);
    
    public abstract void render(SpriteBatch batch);
    
    public abstract void dispose();
    
    public void deactivate() {
        this.active = false;
    }
    
    public boolean isOffScreen(float mapWidth) {
        return x < -100f || x > mapWidth + 100f || y < -100f;
    }
    
    // Getters/Setters
    public boolean hasDealtDamage() {
        return hasDealtDamage;
    }
    
    public void markDamageDealt() {
        this.hasDealtDamage = true;
    }
    
    public boolean isActive() { return active; }
    public Rectangle getHitbox() { return hitbox; }
    public int getDamage() { return damage; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getDistanceTraveled() { return distanceTraveled; }
    public float getMaxDistance() { return maxDistance; }
}