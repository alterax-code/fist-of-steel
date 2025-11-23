package com.fistofsteel.entities.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Projectile du Mage (boule de feu).
 * Utilise le système de distance maximale pour se désactiver automatiquement.
 */
public class MageProjectile extends Projectile {
    
    private Texture[] fireTextures;
    private float frameDuration = 0.08f;
    
    private static final float PROJECTILE_WIDTH = 60f;
    private static final float PROJECTILE_HEIGHT = 60f;
    
    /**
     * Constructeur du projectile du Mage.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param facingRight Direction du projectile
     * @param damage Les dégâts infligés
     */
    public MageProjectile(float x, float y, boolean facingRight, int damage) {
        super(x, y, facingRight, damage, 600f);
        
        this.hitboxWidth = 40f;
        this.hitboxHeight = 40f;
        this.hitbox.setSize(hitboxWidth, hitboxHeight);
        
        System.out.println("MageProjectile cree a (" + (int)x + ", " + (int)y + ")");
        
        loadTextures();
    }
    
    /**
     * Charge les textures du feu.
     */
    private void loadTextures() {
        try {
            fireTextures = new Texture[]{
                new Texture("assets/sprites/sbires/Mage/Fire/fire1.png"),
                new Texture("assets/sprites/sbires/Mage/Fire/fire2.png"),
                new Texture("assets/sprites/sbires/Mage/Fire/fire3.png"),
                new Texture("assets/sprites/sbires/Mage/Fire/fire4.png"),
                new Texture("assets/sprites/sbires/Mage/Fire/fire5.png"),
                new Texture("assets/sprites/sbires/Mage/Fire/fire6.png"),
                new Texture("assets/sprites/sbires/Mage/Fire/fire7.png"),
                new Texture("assets/sprites/sbires/Mage/Fire/fire8.png"),
                new Texture("assets/sprites/sbires/Mage/Fire/fire9.png")
            };
            System.out.println("Projectile Mage : " + fireTextures.length + " frames chargees");
        } catch (Exception e) {
            System.err.println("Erreur chargement sprites projectile Mage : " + e.getMessage());
            fireTextures = new Texture[0];
        }
    }
    
    @Override
    protected void updateAnimation(float delta) {
        animationTimer += delta;
        if (animationTimer >= frameDuration && fireTextures.length > 0) {
            currentFrame = (currentFrame + 1) % fireTextures.length;
            animationTimer = 0f;
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (!active || fireTextures == null || fireTextures.length == 0) return;
        
        Texture currentTexture = fireTextures[currentFrame];
        if (currentTexture == null) return;
        
        if (facingRight) {
            batch.draw(currentTexture, x, y, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
        } else {
            batch.draw(currentTexture, x + PROJECTILE_WIDTH, y, -PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
        }
    }
    
    @Override
    public void dispose() {
        if (fireTextures != null) {
            for (Texture t : fireTextures) {
                if (t != null) t.dispose();
            }
        }
    }
}