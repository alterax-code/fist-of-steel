package com.fistofsteel.entities.player;

import com.badlogic.gdx.graphics.Texture;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.utils.EntityConstants;
import com.fistofsteel.utils.PhysicsConstants;

/**
 * Alexis - Combattant de mêlée avec système de combo
 * VERSION MISE À JOUR avec AudioManager
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
    
    // Système de combo
    private int comboLevel = 0;
    private float comboTimer = 0f;
    private float comboWindow = 0.8f;
    private float[] comboMultipliers = {1.0f, 1.25f, 1.5f};

    public Alexis(InputHandler input, AudioManager audioManager) {
        super(input, audioManager);
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
                int textureIndex = getComboTextureIndex(attackFrame);
                return attackTextures[textureIndex];
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
    
    private int getComboTextureIndex(int frame) {
        if (frame == 0) {
            return 0;
        }
        
        switch (comboLevel) {
            case 0: return 1;
            case 1: return 3;
            case 2: return 2;
            default: return 1;
        }
    }

    @Override
    protected int getWalkFrameCount() {
        return walkTextures.length;
    }

    @Override
    protected int getAttackFrameCount() {
        return 2;
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
    protected void handleInput(float delta) {
        if (isDead) return;
        if (isHit) return;
        if (isAttacking) return;
        
        comboTimer += delta;
        
        if (comboTimer > comboWindow) {
            if (comboLevel != 0) {
                System.out.println("⏱️ Combo reset (délai dépassé)");
            }
            comboLevel = 0;
        }
        
        if (input.isDeadPressed()) {
            die();
            return;
        }
        
        if (input.isHitPressed()) {
            triggerHitState();
            return;
        }
        
        if (input.isAttackPressed()) {
            if (comboTimer <= comboWindow) {
                comboLevel = (comboLevel + 1) % 3;
            } else {
                comboLevel = 0;
            }
            
            String comboName = "";
            float multiplier = comboMultipliers[comboLevel];
            switch (comboLevel) {
                case 0: comboName = "ATTAQUE DE BASE"; break;
                case 1: comboName = "COMBO 2"; break;
                case 2: comboName = "COMBO 3"; break;
            }
            System.out.println("⚔️ Alexis : " + comboName + " (x" + multiplier + " dégâts)");
            
            isAttacking = true;
            attackTimer = attackDuration;
            currentState = State.ATTACK;
            attackFrame = 0;
            animationTimer = 0f;
            velocityX = 0;
            hasDealtDamageThisAttack = false;
            
            comboTimer = 0f;
            
            return;
        }
        
        if (input.isJumpPressed() && onGround) {
            velocityY = PhysicsConstants.JUMP_FORCE;
            onGround = false;
            currentState = State.JUMP;
            isFastFalling = false;
            jumpProtectionTimer = jumpProtectionDuration;
            return;
        }
        
        boolean blockActive = input.isBlockPressed();
        boolean crouchPressed = input.isCrouchPressed();
        
        if (blockActive) {
            currentState = State.BLOCK;
            velocityX = 0;
        }
        else if (crouchPressed) {
            if (onGround) {
                currentState = State.CROUCH;
                velocityX = 0;
                isFastFalling = false;
            } else {
                boolean canFastFall = 
                    fastFallCooldownTimer <= 0 &&      
                    !isFastFalling &&                   
                    jumpProtectionTimer <= 0 &&       
                    velocityY <= 0;                     
                
                if (canFastFall) {
                    isFastFalling = true;
                    velocityY = -PhysicsConstants.MAX_FALL_SPEED;
                    fastFallCooldownTimer = fastFallCooldownDuration;
                    currentState = State.CROUCH;
                    velocityX = 0;
                } else if (isFastFalling) {
                    currentState = State.CROUCH;
                    velocityX = 0;
                } else {
                    currentState = velocityY > 0 ? State.JUMP : State.FALL;
                }
            }
        }
        else {
            if (isFastFalling) {
                isFastFalling = false;
            }
            
            if (!onGround) {
                currentState = velocityY > 0 ? State.JUMP : State.FALL;
            }
            
            if (input.isLeftPressed()) {
                velocityX = -PhysicsConstants.WALK_SPEED;
                facingRight = false;
                if (onGround) currentState = State.WALK;
            } else if (input.isRightPressed()) {
                velocityX = PhysicsConstants.WALK_SPEED;
                facingRight = true;
                if (onGround) currentState = State.WALK;
            } else {
                velocityX = 0;
                if (onGround) currentState = State.IDLE;
            }
        }
    }
    
    @Override
    public int getTotalAttack() {
        int baseDamage = baseAttack + attackBonus;
        float multiplier = comboMultipliers[comboLevel];
        return Math.round(baseDamage * multiplier);
    }
    
    /**
     * ✅ MÉTHODE MODIFIÉE : Utilise die() de Player qui joue le son
     */
    private void die() {
        if (isDead) return;
        isDead = true;
        currentState = State.DEAD;
        deadFrame = 0;
        animationTimer = 0f;
        velocityX = 0;
        velocityY = 0;
        health = 0;
        
        // ✅ Jouer le son "death"
        if (audioManager != null) {
            audioManager.playSound("death");
        }
        
        System.out.println("☠️ Le joueur est mort");
    }
    
    public int getComboLevel() {
        return comboLevel;
    }
    
    public float getComboTimer() {
        return comboTimer;
    }
}