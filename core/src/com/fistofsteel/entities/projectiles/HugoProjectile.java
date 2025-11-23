package com.fistofsteel.entities.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Projectile d'Hugo (boule de feu).
 * Utilise le système de distance maximale pour se désactiver automatiquement.
 */
public class HugoProjectile extends Projectile {
    
    private Texture[] fireballTextures;
    private float frameDuration = 0.1f;
    
    private static final float PROJECTILE_WIDTH = 50f;
    private static final float PROJECTILE_HEIGHT = 50f;
    
    /**
     * Constructeur du projectile d'Hugo.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param facingRight Direction du projectile
     * @param damage Les dégâts infligés
     */
    public HugoProjectile(float x, float y, boolean facingRight, int damage) {
        super(x, y, facingRight, damage, 700f);
        
        this.hitboxWidth = 35f;
        this.hitboxHeight = 35f;
        this.hitbox.setSize(hitboxWidth, hitboxHeight);
        this.speed = 400f;
        this.velocityX = facingRight ? speed : -speed;
        
        System.out.println("HugoProjectile cree a (" + (int)x + ", " + (int)y + ")");
        
        loadTextures();
    }
    
    /**
     * Charge les textures de la boule de feu.
     */
    private void loadTextures() {
        try {
            fireballTextures = new Texture[]{
                new Texture("assets/sprites/hugo/fireball_frame_1.png"),
                new Texture("assets/sprites/hugo/fireball_frame_2.png"),
                new Texture("assets/sprites/hugo/fireball_frame_3.png"),
                new Texture("assets/sprites/hugo/fireball_frame_4.png")
            };
            System.out.println("Projectile Hugo : " + fireballTextures.length + " frames chargees");
        } catch (Exception e) {
            System.err.println("Erreur chargement sprites projectile Hugo : " + e.getMessage());
            fireballTextures = new Texture[0];
        }
    }
    
    @Override
    protected void updateAnimation(float delta) {
        animationTimer += delta;
        if (animationTimer >= frameDuration && fireballTextures.length > 0) {
            currentFrame = (currentFrame + 1) % fireballTextures.length;
            animationTimer = 0f;
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (!active || fireballTextures == null || fireballTextures.length == 0) return;
        
        Texture currentTexture = fireballTextures[currentFrame];
        if (currentTexture == null) return;
        
        if (facingRight) {
            batch.draw(currentTexture, x, y, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
        } else {
            batch.draw(currentTexture, x + PROJECTILE_WIDTH, y, -PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
        }
    }
    
    @Override
    public void dispose() {
        if (fireballTextures != null) {
            for (Texture t : fireballTextures) {
                if (t != null) t.dispose();
            }
        }
    }
}