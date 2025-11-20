package com.fistofsteel.entities.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.PhysicsConstants;
import com.fistofsteel.utils.EntityConstants;

public abstract class Player {
    protected float x, y;
    protected float velocityX, velocityY;
    protected boolean onGround = false;
    protected boolean facingRight = true;

    protected Rectangle hitbox;
    protected InputHandler input;
    
    protected Array<Rectangle> collisionRects;

    protected enum State { 
        IDLE, WALK, CROUCH, JUMP, FALL, 
        ATTACK, BLOCK, HIT, DEAD 
    }
    protected State currentState = State.IDLE;
    
    protected float attackTimer = 0f;
    protected float attackDuration = 0.3f;
    protected float hitTimer = 0f;
    protected float hitDuration = 0.2f;
    protected boolean isDead = false;
    protected boolean isHit = false;
    protected boolean isAttacking = false;
    
    protected boolean hasDealtDamageThisAttack = false;
    
    protected float fastFallCooldownTimer = 0f;
    protected float fastFallCooldownDuration = 0.6f;
    protected boolean isFastFalling = false;
    
    protected float jumpProtectionTimer = 0f;
    protected float jumpProtectionDuration = 0.15f;
    
    protected float animationTimer = 0f;
    protected float walkFrameDuration = 0.15f;
    protected float attackFrameDuration = 0.15f;
    protected float deadFrameDuration = 0.3f;
    
    protected int walkFrame = 0;
    protected int attackFrame = 0;
    protected int deadFrame = 0;
    
    private int debugFrameCounter = 0;

    // ===== STATS =====
    protected int maxHealth = 100;
    protected int health = maxHealth;
    protected int armor = 0;
    protected int baseAttack = 10;
    protected int attackBonus = 0;

    public Player(InputHandler input) {
        this.input = input;
        loadTextures();
        hitbox = new Rectangle(x + getHitboxOffsetX(), y + getHitboxOffsetY(), getHitboxWidth(), getHitboxHeight());
        this.maxHealth = 100;
        this.health = maxHealth;
    }
    
    // ===== MÉTHODES ABSTRAITES =====
    protected abstract void loadTextures();
    protected abstract void disposeTextures();
    protected abstract Texture getCurrentTexture();
    protected abstract int getWalkFrameCount();
    protected abstract int getAttackFrameCount();
    protected abstract int getDeadFrameCount();
    protected abstract float getHitboxWidth();
    protected abstract float getHitboxHeight();
    protected abstract float getHitboxOffsetX();
    protected abstract float getHitboxOffsetY();
    
    public void setCollisionRects(Array<Rectangle> collisions) {
        this.collisionRects = collisions;
    }
    
    public void update(float delta) {
        handleInput(delta);
        updateTimers(delta);
        applyPhysics(delta);
        updateAnimation(delta);
        hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
        resolveHitboxCollisions();
        
        debugFrameCounter++;
        if (debugFrameCounter >= 60) {
            System.out.println("🔍 Player: (" + (int)x + ", " + (int)y + ") | Ground: " + onGround + " | State: " + currentState);
            debugFrameCounter = 0;
        }
    }

    protected void handleInput(float delta) {
        if (isDead) return;
        if (isHit) return;
        if (isAttacking) return;
        
        // ===== MORT =====
        if (input.isDeadPressed()) {
            die();
            return;
        }
        
        // ===== HIT =====
        if (input.isHitPressed()) {
            isHit = true;
            hitTimer = hitDuration;
            currentState = State.HIT;
            animationTimer = 0f;
            velocityX = 0;
            return;
        }
        
        // ===== ATTAQUE =====
        if (input.isAttackPressed()) {
            isAttacking = true;
            attackTimer = attackDuration;
            currentState = State.ATTACK;
            attackFrame = 0;
            animationTimer = 0f;
            velocityX = 0;
            hasDealtDamageThisAttack = false;
            return;
        }
        
        // ===== SAUT =====
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
        
        // ===== BLOCK =====
        if (blockActive) {
            currentState = State.BLOCK;
            velocityX = 0;
        }
        // ===== CROUCH / FAST FALL =====
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
        // ===== MOUVEMENT NORMAL =====
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

    protected void updateTimers(float delta) {
        if (isAttacking) {
            attackTimer -= delta;
            if (attackTimer <= 0) {
                isAttacking = false;
                currentState = State.IDLE;
                hasDealtDamageThisAttack = false;
            }
        }
        
        if (isHit) {
            hitTimer -= delta;
            if (hitTimer <= 0) {
                isHit = false;
                currentState = State.IDLE;
            }
        }
        
        if (fastFallCooldownTimer > 0) {
            fastFallCooldownTimer -= delta;
        }
        
        if (jumpProtectionTimer > 0) {
            jumpProtectionTimer -= delta;
        }
    }

    protected void applyPhysics(float delta) {
        if (isDead) return;
        
        if (!isFastFalling) {
            velocityY -= PhysicsConstants.GRAVITY * delta;
        }
        
        if (velocityY < -PhysicsConstants.MAX_FALL_SPEED) {
            velocityY = -PhysicsConstants.MAX_FALL_SPEED;
        }
        
        float totalMoveY = velocityY * delta;
        
        int steps = 1;
        if (Math.abs(totalMoveY) > PhysicsConstants.MAX_MOVE_PER_STEP) {
            steps = (int) Math.ceil(Math.abs(totalMoveY) / PhysicsConstants.MAX_MOVE_PER_STEP);
            if (steps > PhysicsConstants.MAX_PHYSICS_STEPS) steps = PhysicsConstants.MAX_PHYSICS_STEPS;
        }
        
        float movePerStep = totalMoveY / steps;
        
        for (int i = 0; i < steps; i++) {
            float newY = y + movePerStep;
            hitbox.setPosition(x + getHitboxOffsetX(), newY + getHitboxOffsetY());
            boolean collidedVertically = false;
            
            if (collisionRects != null) {
                for (Rectangle collRect : collisionRects) {
                    if (hitbox.overlaps(collRect)) {
                        collidedVertically = true;
                        
                        if (velocityY < 0) {
                            y = collRect.y + collRect.height - getHitboxOffsetY();
                            velocityY = 0;
                            onGround = true;
                            isFastFalling = false;
                            jumpProtectionTimer = 0;
                            
                            if (currentState != State.CROUCH && currentState != State.BLOCK 
                                && currentState != State.ATTACK && currentState != State.HIT) {
                                currentState = velocityX == 0 ? State.IDLE : State.WALK;
                            }
                        }
                        else if (velocityY > 0) {
                            y = collRect.y - getHitboxHeight() - getHitboxOffsetY();
                            velocityY = 0;
                        }
                        break;
                    }
                }
            }
            
            if (!collidedVertically) {
                y = newY;
                onGround = false;
                
                if (currentState != State.CROUCH && currentState != State.BLOCK 
                    && currentState != State.ATTACK && currentState != State.HIT) {
                    currentState = velocityY > 0 ? State.JUMP : State.FALL;
                }
            } else {
                break;
            }
        }
        
        // Collision horizontale
        float newX = x + velocityX * delta;
        hitbox.setPosition(newX + getHitboxOffsetX(), y + getHitboxOffsetY());
        
        boolean collidedHorizontally = false;
        if (collisionRects != null) {
            for (Rectangle collRect : collisionRects) {
                if (hitbox.overlaps(collRect)) {
                    collidedHorizontally = true;
                    break;
                }
            }
        }
        
        if (!collidedHorizontally) {
            x = newX;
        }
    }

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

    public void render(SpriteBatch batch) {
        Texture currentTexture = getCurrentTexture();

        if (currentState == State.DEAD) {
            float rotatedWidth = EntityConstants.PLAYER_HEIGHT;
            float rotatedHeight = EntityConstants.PLAYER_WIDTH;
            float originX = rotatedWidth / 2f;
            float originY = rotatedHeight / 2f;
            float rotation = facingRight ? -90f : 90f;
            float scaleX = facingRight ? 1f : -1f;
            
            batch.draw(
                currentTexture,
                x, y,
                originX, originY,
                rotatedWidth, rotatedHeight,
                scaleX, 1f,
                rotation,
                0, 0,
                currentTexture.getWidth(), currentTexture.getHeight(),
                false, false
            );
        } else {
            if (facingRight) {
                batch.draw(currentTexture, x, y, EntityConstants.PLAYER_WIDTH, EntityConstants.PLAYER_HEIGHT);
            } else {
                batch.draw(currentTexture, x + EntityConstants.PLAYER_WIDTH, y, -EntityConstants.PLAYER_WIDTH, EntityConstants.PLAYER_HEIGHT);
            }
        }
    }

    protected void resolveHitboxCollisions() {
        if (collisionRects == null || collisionRects.size == 0) return;
        
        for (Rectangle collRect : collisionRects) {
            if (hitbox.overlaps(collRect)) {
                float overlapLeft = (hitbox.x + hitbox.width) - collRect.x;
                float overlapRight = (collRect.x + collRect.width) - hitbox.x;
                float overlapBottom = (hitbox.y + hitbox.height) - collRect.y;
                float overlapTop = (collRect.y + collRect.height) - hitbox.y;
                
                float minOverlap = Math.min(
                    Math.min(overlapLeft, overlapRight),
                    Math.min(overlapBottom, overlapTop)
                );
                
                if (minOverlap == overlapLeft) {
                    float pushDistance = overlapLeft + 0.1f;
                    x -= pushDistance;
                    velocityX = 0;
                } 
                else if (minOverlap == overlapRight) {
                    float pushDistance = overlapRight + 0.1f;
                    x += pushDistance;
                    velocityX = 0;
                } 
                else if (minOverlap == overlapBottom) {
                    float pushDistance = overlapBottom + 0.1f;
                    y -= pushDistance;
                    velocityY = 0;
                } 
                else if (minOverlap == overlapTop) {
                    float pushDistance = overlapTop + 0.1f;
                    y += pushDistance;
                    velocityY = 0;
                    onGround = true;
                }
                
                hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
                break;
            }
        }
    }

    // ===== SYSTÈME DE SANTÉ =====

    public void applyDamage(int rawDamage) {
        if (isDead) return;
        
        int effectiveDamage = Math.max(0, rawDamage - armor);
        
        health -= effectiveDamage;
        if (health < 0) health = 0;
        
        System.out.println("💥 Dégâts : " + rawDamage + " (armure -" + armor + ") = " + effectiveDamage + " | HP: " + health + "/" + maxHealth);
        
        if (health == 0) {
            die();
        }
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        health += amount;
        if (health > maxHealth) health = maxHealth;
        System.out.println("💊 Heal +" + amount + " -> " + health + "/" + maxHealth);
    }

    private void die() {
        if (isDead) return;
        isDead = true;
        currentState = State.DEAD;
        deadFrame = 0;
        animationTimer = 0f;
        velocityX = 0;
        velocityY = 0;
        health = 0;
        System.out.println("☠️ Le joueur est mort");
    }

    // ===== SYSTÈME D'ATTAQUE =====

    public int getTotalAttack() {
        return baseAttack + attackBonus;
    }

    public void setAttackBonus(int bonus) {
        this.attackBonus = bonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }
    
    public boolean hasDealtDamageThisAttack() {
        return hasDealtDamageThisAttack;
    }
    
    public void markDamageDealt() {
        hasDealtDamageThisAttack = true;
    }
    
    public boolean isRangedAttacker() {
        return false;
    }

    // ===== SYSTÈME D'ARMURE =====

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = Math.max(0, armor);
        System.out.println("🛡️ Armure : " + this.armor);
    }

    // ===== GETTERS =====

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public State getCurrentState() { return currentState; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isDead() { return isDead; }
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getHitbox() { return hitbox; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
        
        if (collisionRects != null) {
            Rectangle testHitbox = new Rectangle(
                x + getHitboxOffsetX(), 
                y + getHitboxOffsetY() - 2, 
                getHitboxWidth(), 
                getHitboxHeight()
            );
            
            for (Rectangle collRect : collisionRects) {
                if (testHitbox.overlaps(collRect)) {
                    onGround = true;
                    velocityY = 0;
                    currentState = State.IDLE;
                    break;
                }
            }
        }
    }

    public void dispose() {
        disposeTextures();
    }
}