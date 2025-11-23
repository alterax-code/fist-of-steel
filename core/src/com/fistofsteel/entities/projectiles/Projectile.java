package com.fistofsteel.entities.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Classe de base pour un projectile.
 * Gère le déplacement, l'animation et la détection de collision.
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
    
    protected float animationTimer = 0f;
    protected int currentFrame = 0;
    
    protected boolean hasDealtDamage = false;
    
    protected float distanceTraveled = 0f;
    protected float maxDistance = 600f;

    /**
     * Constructeur avec distance maximale par défaut.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param facingRight Direction du projectile
     * @param damage Les dégâts infligés
     */
    public Projectile(float x, float y, boolean facingRight, int damage) {
        this(x, y, facingRight, damage, 600f);
    }
    
    /**
     * Constructeur complet.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param facingRight Direction du projectile
     * @param damage Les dégâts infligés
     * @param maxDistance Distance maximale avant désactivation
     */
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
    
    /**
     * Met à jour le projectile.
     * 
     * @param delta Le temps écoulé
     */
    public void update(float delta) {
        if (!active) return;
        
        float deltaDistance = Math.abs(velocityX * delta);
        distanceTraveled += deltaDistance;
        
        if (distanceTraveled >= maxDistance) {
            deactivate();
            return;
        }
        
        x += velocityX * delta;
        y += velocityY * delta;
        
        hitbox.setPosition(x, y);
        
        updateAnimation(delta);
    }
    
    /**
     * Met à jour l'animation du projectile.
     * 
     * @param delta Le temps écoulé
     */
    protected abstract void updateAnimation(float delta);
    
    /**
     * Affiche le projectile.
     * 
     * @param batch Le SpriteBatch pour le rendu
     */
    public abstract void render(SpriteBatch batch);
    
    /**
     * Libère les ressources du projectile.
     */
    public abstract void dispose();
    
    /**
     * Désactive le projectile.
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Vérifie si le projectile est hors écran.
     * 
     * @param mapWidth La largeur de la map
     * @return true si hors écran
     */
    public boolean isOffScreen(float mapWidth) {
        return x < -100f || x > mapWidth + 100f || y < -100f;
    }
    
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