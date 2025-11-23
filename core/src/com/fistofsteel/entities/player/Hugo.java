package com.fistofsteel.entities.player;

import com.badlogic.gdx.graphics.Texture;
import com.fistofsteel.entities.managers.ProjectileManager;
import com.fistofsteel.entities.projectiles.HugoProjectile;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.utils.EntityConstants;

/**
 * Hugo - Personnage agile à distance.
 * Tire des projectiles au lieu d'attaquer au corps à corps.
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
    
    private ProjectileManager projectileManager;
    private boolean hasShot = false;

    /**
     * Constructeur d'Hugo.
     * 
     * @param input Le gestionnaire d'input
     * @param audioManager Le gestionnaire audio
     */
    public Hugo(InputHandler input, AudioManager audioManager) {
        super(input, audioManager);
    }
    
    /**
     * Définit le gestionnaire de projectiles.
     * 
     * @param manager Le ProjectileManager
     */
    public void setProjectileManager(ProjectileManager manager) {
        this.projectileManager = manager;
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

    @Override
    protected float getHitboxWidth() {
        return 60f;
    }

    @Override
    protected float getHitboxHeight() {
        return 100f;
    }

    @Override
    protected float getHitboxOffsetX() {
        return (EntityConstants.PLAYER_WIDTH - getHitboxWidth()) / 2f;
    }

    @Override
    protected float getHitboxOffsetY() {
        return 5f;
    }
    
    @Override
    public boolean isRangedAttacker() {
        return true;
    }
    
    @Override
    protected void handleInput(float delta) {
        if (!isAttacking && input.isAttackPressed()) {
            hasShot = false;
        }
        
        super.handleInput(delta);
    }
    
    @Override
    protected void updateAnimation(float delta) {
        animationTimer += delta;
        
        switch (currentState) {
            case WALK:
                if (animationTimer >= walkFrameDuration) {
                    walkFrame = (walkFrame + 1) % getWalkFrameCount();
                    animationTimer = 0f;
                }
                break;
                
            case ATTACK:
                if (animationTimer >= attackFrameDuration) {
                    attackFrame = (attackFrame + 1) % getAttackFrameCount();
                    animationTimer = 0f;
                    
                    if (attackFrame == 1 && !hasShot) {
                        shootProjectile();
                        hasShot = true;
                    }
                }
                break;
                
            case DEAD:
                if (animationTimer >= deadFrameDuration && deadFrame < getDeadFrameCount() - 1) {
                    deadFrame++;
                    animationTimer = 0f;
                }
                break;
                
            default:
                walkFrame = 0;
                attackFrame = 0;
                animationTimer = 0f;
                break;
        }
    }
    
    /**
     * Tire un projectile.
     */
    private void shootProjectile() {
        if (projectileManager == null) {
            System.err.println("Hugo ne peut pas tirer : ProjectileManager null !");
            return;
        }
        
        float projectileX = x + EntityConstants.PLAYER_WIDTH / 2f;
        float projectileY = y + EntityConstants.PLAYER_HEIGHT / 2f;
        
        HugoProjectile projectile = new HugoProjectile(projectileX, projectileY, facingRight, getTotalAttack());
        projectileManager.addProjectile(projectile);
        
        System.out.println("Hugo tire un projectile a (" + (int)projectileX + ", " + (int)projectileY + ")");
    }
}