package com.fistofsteel.entities.projectiles;  // ✅ MODIFIÉ (était com.fistofsteel.entities)

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// ... reste du code inchangé

/**
 * Projectile d'Hugo (boule de feu)
 * VERSION SIMPLIFIÉE - utilise le système de distance de Projectile
 */
public class HugoProjectile extends Projectile {
    
    private Texture[] fireballTextures;
    private float frameDuration = 0.1f;
    
    private static final float PROJECTILE_WIDTH = 50f;
    private static final float PROJECTILE_HEIGHT = 50f;
    
    public HugoProjectile(float x, float y, boolean facingRight, int damage) {
        super(x, y, facingRight, damage, 700f); // Distance max de 700px
        
        this.hitboxWidth = 35f;
        this.hitboxHeight = 35f;
        this.hitbox.setSize(hitboxWidth, hitboxHeight);
        this.speed = 400f;
        this.velocityX = facingRight ? speed : -speed;
        
        System.out.println("🔥 HugoProjectile créé à (" + (int)x + ", " + (int)y + ")");
        
        loadTextures();
    }
    
    private void loadTextures() {
        try {
            fireballTextures = new Texture[]{
                new Texture("assets/sprites/hugo/fireball_frame_1.png"),
                new Texture("assets/sprites/hugo/fireball_frame_2.png"),
                new Texture("assets/sprites/hugo/fireball_frame_3.png"),
                new Texture("assets/sprites/hugo/fireball_frame_4.png")
            };
            System.out.println("✅ Projectile Hugo : " + fireballTextures.length + " frames chargées");
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement sprites projectile Hugo : " + e.getMessage());
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