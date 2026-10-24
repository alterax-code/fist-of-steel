package com.fistofsteel.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.utils.EntityConstants;

/**
 * Ennemi Knight - VERSION SIMPLIFIÉE
 * Utilise le système d'animation automatique
 */
public class Knight extends Enemy {

    private Texture[][] allTextures; // [idle, hurt, walk, attack, dead]

    public Knight(float x, float y, Player targetPlayer) {
        super(x, y, targetPlayer);
    }
    
    public Knight(float x, float y, Player targetPlayer, float patrolMin, float patrolMax) {
        super(x, y, targetPlayer);
        setPatrolZone(patrolMin, patrolMax);
    }

    @Override
    protected void initStats() {
        this.maxHealth = 50;
        this.health = maxHealth;
        this.damage = 10;
        this.detectionRange = 400f;
        this.attackRange = 80f;
    }

    @Override
    protected void loadTextures() {
        allTextures = EnemyAnimationHelper.loadEnemySprites("assets/sprites/sbires/", "Knight");
    }

    @Override
    protected void disposeTextures() {
        EnemyAnimationHelper.disposeTextures(allTextures);
    }

    @Override
    protected void updateAnimation(float delta) {
        animationTimer += delta;
        
        switch (currentState) {
            case IDLE:
                if (animationTimer >= 0.15f) {
                    idleFrame = (idleFrame + 1) % allTextures[0].length;
                    animationTimer = 0f;
                }
                break;
                
            case PATROL:
            case CHASE:
                if (animationTimer >= walkFrameDuration) {
                    walkFrame = (walkFrame + 1) % allTextures[2].length;
                    animationTimer = 0f;
                }
                break;
                
            case ATTACK:
                if (animationTimer >= attackFrameDuration) {
                    attackFrame = (attackFrame + 1) % allTextures[3].length;
                    animationTimer = 0f;
                }
                break;
                
            case HIT:
                if (animationTimer >= 0.1f) {
                    hurtFrame = (hurtFrame + 1) % allTextures[1].length;
                    animationTimer = 0f;
                }
                break;
                
            case DEAD:
                if (animationTimer >= deadFrameDuration && deadFrame < allTextures[4].length - 1) {
                    deadFrame++;
                    animationTimer = 0f;
                }
                break;
        }
    }

    private Texture getCurrentTexture() {
        if (allTextures == null) return null;
        
        switch (currentState) {
            case IDLE:
                return allTextures[0][idleFrame % allTextures[0].length];
            case HIT:
                return allTextures[1][hurtFrame % allTextures[1].length];
            case PATROL:
            case CHASE:
                return allTextures[2][walkFrame % allTextures[2].length];
            case ATTACK:
                return allTextures[3][attackFrame % allTextures[3].length];
            case DEAD:
                return allTextures[4][deadFrame % allTextures[4].length];
            default:
                return allTextures[0][0];
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Texture tex = getCurrentTexture();
        if (tex == null) return;
        
        if (facingRight) {
            batch.draw(tex, x + width, y, -width, height);
        } else {
            batch.draw(tex, x, y, width, height);
        }
    }

    @Override
    protected int getWalkFrameCount() {
        return allTextures != null ? allTextures[2].length : 6;
    }

    @Override
    protected int getAttackFrameCount() {
        return allTextures != null ? allTextures[3].length : 5;
    }

    @Override
    protected int getDeadFrameCount() {
        return allTextures != null ? allTextures[4].length : 10;
    }

    @Override
    protected float getHitboxWidth() {
        return EntityConstants.ENEMY_HITBOX_WIDTH;
    }

    @Override
    protected float getHitboxHeight() {
        return EntityConstants.ENEMY_HITBOX_HEIGHT;
    }

    @Override
    protected float getHitboxOffsetX() {
        return EntityConstants.ENEMY_HITBOX_OFFSET_X;
    }

    @Override
    protected float getHitboxOffsetY() {
        return EntityConstants.ENEMY_HITBOX_OFFSET_Y;
    }

    @Override
    protected boolean useDirectionalHitbox() {
        return true;
    }
    
    @Override
    protected float getDirectionalHitboxWidth() {
        return EntityConstants.ENEMY_HITBOX_WIDTH * 0.75f;
    }
    
    @Override
    protected float getDirectionalHitboxOffsetX() {
        float fullWidth = EntityConstants.ENEMY_HITBOX_WIDTH;
        float reducedWidth = getDirectionalHitboxWidth();
        float reduction = fullWidth - reducedWidth;
        
        if (facingRight) {
            return EntityConstants.ENEMY_HITBOX_OFFSET_X;
        } else {
            return EntityConstants.ENEMY_HITBOX_OFFSET_X + reduction;
        }
    }
}