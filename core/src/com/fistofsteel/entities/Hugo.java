package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Texture;
import com.fistofsteel.audio.SoundManager;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;

/**
 * Hugo - Personnage agile à distance
 * Hitbox plus petite pour faciliter l'esquive
 */
public class Hugo extends Player {
    private Texture idleTexture;
    private Texture jumpTexture;
    private Texture blockTexture;
    private Texture crouchTexture;
    private Texture hitTexture;
    
    private Texture[] walkTextures;
    private Texture[] attackTextures;
    private Texture[] deadTextures;

    public Hugo(InputHandler input, SoundManager soundManager) {
        super(input, soundManager);
    }

    @Override
    protected void loadTextures() {
        idleTexture = new Texture("assets/sprites/hugo/hugo_idle.png");
        jumpTexture = new Texture("assets/sprites/hugo/hugo_jump.png");
        blockTexture = new Texture("assets/sprites/hugo/hugo_block.png");
        crouchTexture = new Texture("assets/sprites/hugo/hugo_crouch.png");
        hitTexture = new Texture("assets/sprites/hugo/hugo_hit.png");
        
        walkTextures = new Texture[]{
            new Texture("assets/sprites/hugo/hugo_walk_1.png"),
            new Texture("assets/sprites/hugo/hugo_walk_2.png"),
            new Texture("assets/sprites/hugo/hugo_walk_3.png")
        };
        
        attackTextures = new Texture[]{
            new Texture("assets/sprites/hugo/hugo_attack_1.png"),
            new Texture("assets/sprites/hugo/hugo_attack_2.png")
        };
        
        deadTextures = new Texture[]{
            new Texture("assets/sprites/hugo/hugo_dead_1.png"),
            new Texture("assets/sprites/hugo/hugo_dead_2.png")
        };
    }

    @Override
    protected void disposeTextures() {
        idleTexture.dispose();
        jumpTexture.dispose();
        blockTexture.dispose();
        crouchTexture.dispose();
        hitTexture.dispose();
        
        for (Texture t : walkTextures) t.dispose();
        for (Texture t : attackTextures) t.dispose();
        for (Texture t : deadTextures) t.dispose();
    }

    @Override
    protected Texture getCurrentTexture() {
        switch (currentState) {
            case WALK:
                return walkTextures[walkFrame];
            case CROUCH:
                return crouchTexture;
            case JUMP:
            case FALL:
                return jumpTexture;
            case ATTACK:
                return attackTextures[attackFrame];
            case BLOCK:
                return blockTexture;
            case HIT:
                return hitTexture;
            case DEAD:
                return deadTextures[deadFrame];
            default:
                return idleTexture;
        }
    }

    @Override
    protected int getWalkFrameCount() {
        return walkTextures.length;
    }

    @Override
    protected int getAttackFrameCount() {
        return attackTextures.length;
    }

    @Override
    protected int getDeadFrameCount() {
        return deadTextures.length;
    }

    // ===== HITBOX PERSONNALISÉE DE HUGO =====
    
    @Override
    protected float getHitboxWidth() {
        // Hugo a une hitbox étroite pour un personnage agile
        // 50% de la largeur du sprite (120px)
        return 60f;
    }

    @Override
    protected float getHitboxHeight() {
        // Hauteur réduite pour correspondre au corps réel
        // ~78% de la hauteur du sprite (128px)
        return 100f;
    }

    @Override
    protected float getHitboxOffsetX() {
        // Centre la hitbox horizontalement sur le sprite
        // (120 - 60) / 2 = 30 pixels de chaque côté
        return (Constants.PLAYER_WIDTH - getHitboxWidth()) / 2f;
    }

    @Override
    protected float getHitboxOffsetY() {
        // Décalage vertical léger pour éviter les collisions trop sensibles au sol
        // La hitbox commence 10 pixels au-dessus du bas du sprite
        return 10f;
    }
}