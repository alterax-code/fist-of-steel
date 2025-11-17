package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Texture;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;

/**
 * Alexis - Combattant de mêlée robuste
 * Hitbox plus large et plus haute que Hugo
 */
public class Alexis extends Player {
    private Texture idleTexture;
    private Texture jumpTexture;
    private Texture blockTexture;
    private Texture crouchTexture;
    private Texture hitTexture;
    
    private Texture[] walkTextures;
    private Texture[] attackTextures;
    private Texture[] deadTextures;

    public Alexis(InputHandler input) {
        super(input);
    }

    @Override
    protected void loadTextures() {
        idleTexture = new Texture("assets/sprites/alexis/alexis_idle.png");
        jumpTexture = new Texture("assets/sprites/alexis/alexis_jump.png");
        blockTexture = new Texture("assets/sprites/alexis/alexis_block.png");
        crouchTexture = new Texture("assets/sprites/alexis/alexis_crouch.png");
        hitTexture = new Texture("assets/sprites/alexis/alexis_hit.png");
        
        walkTextures = new Texture[]{
            new Texture("assets/sprites/alexis/alexis_walk_1.png"),
            new Texture("assets/sprites/alexis/alexis_walk_2.png"),
            new Texture("assets/sprites/alexis/alexis_walk_3.png")
        };
        
        attackTextures = new Texture[]{
            new Texture("assets/sprites/alexis/alexis_attack_1.png"),
            new Texture("assets/sprites/alexis/alexis_attack_2.png"),
            new Texture("assets/sprites/alexis/alexis_attack_3.png"),
            new Texture("assets/sprites/alexis/alexis_attack_4.png")
        };
        
        deadTextures = new Texture[]{
            new Texture("assets/sprites/alexis/alexis_dead_1.png"),
            new Texture("assets/sprites/alexis/alexis_dead_2.png")
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

    // ===== HITBOX PERSONNALISÉE D'ALEXIS =====
    
    @Override
    protected float getHitboxWidth() {
        return 75f;
    }

    @Override
    protected float getHitboxHeight() {
        return 110f;
    }

    @Override
    protected float getHitboxOffsetX() {
        return (Constants.PLAYER_WIDTH - getHitboxWidth()) / 2f;
    }

    @Override
    protected float getHitboxOffsetY() {
        return 5f;
    }
}