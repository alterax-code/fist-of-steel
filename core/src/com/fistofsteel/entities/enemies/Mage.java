package com.fistofsteel.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.entities.managers.ProjectileManager;
import com.fistofsteel.entities.projectiles.MageProjectile;
import com.fistofsteel.utils.EntityConstants;

/**
 * Ennemi Mage - VERSION SIMPLIFIÉE avec projectiles
 */
public class Mage extends Enemy {
    
    private Texture[][] allTextures; // [idle, hurt, walk, attack, dead]
    private ProjectileManager projectileManager;
    private boolean hasShot = false;

    public Mage(float x, float y, Player targetPlayer) {
        super(x, y, targetPlayer);
    }
    
    public Mage(float x, float y, Player targetPlayer, float patrolMin, float patrolMax) {
        super(x, y, targetPlayer);
        setPatrolZone(patrolMin, patrolMax);
    }
    
    public void setProjectileManager(ProjectileManager manager) {
        this.projectileManager = manager;
    }

    @Override
    protected void initStats() {
        this.maxHealth = 40;
        this.health = maxHealth;
        this.damage = 15;
        this.detectionRange = 500f;
        this.attackRange = 400f;
        this.attackCooldown = 3.0f;
    }

    @Override
    protected void loadTextures() {
        allTextures = EnemyAnimationHelper.loadEnemySprites("assets/sprites/sbires/", "Mage");
    }

    @Override
    protected void disposeTextures() {
        EnemyAnimationHelper.disposeTextures(allTextures);
    }
    
    @Override
    protected void attack() {
        super.attack();
        hasShot = false;
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
                    
                    // Tirer le projectile à la frame 5
                    if (attackFrame == 5 && !hasShot) {
                        shootProjectile();
                        hasShot = true;
                    }
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
    
    private void shootProjectile() {
        if (projectileManager == null) {
            System.err.println("⚠️ Mage ne peut pas tirer : ProjectileManager null !");
            return;
        }
        
        float projectileX = x + width / 2f;
        float projectileY = y + height / 2f;
        
        MageProjectile projectile = new MageProjectile(projectileX, projectileY, facingRight, damage);
        projectileManager.addProjectile(projectile);
        
        System.out.println("🔥 Mage tire un projectile à (" + (int)projectileX + ", " + (int)projectileY + ")");
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
    protected boolean canHitPlayer() {
        com.badlogic.gdx.math.Rectangle playerHitbox = targetPlayer.getHitbox();
        float distance = Math.abs(playerHitbox.x - hitbox.x);
        float verticalDistance = Math.abs(playerHitbox.y - hitbox.y);
        
        return distance <= attackRange && distance >= 100f && verticalDistance <= 80f;
    }
    
    @Override
    public void tryDealDamage() {
        // Le Mage ne fait pas de dégâts directs, uniquement via projectiles
    }

    @Override
    protected int getWalkFrameCount() {
        return allTextures != null ? allTextures[2].length : 6;
    }

    @Override
    protected int getAttackFrameCount() {
        return allTextures != null ? allTextures[3].length : 7;
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