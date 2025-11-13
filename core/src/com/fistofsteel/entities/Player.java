package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.audio.SoundManager;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;

public abstract class Player {
    protected float x, y;
    protected float velocityX, velocityY;
    protected boolean onGround = false;
    protected boolean facingRight = true;

    protected Rectangle hitbox;
    protected InputHandler input;
    protected SoundManager soundManager;
    
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

    public Player(InputHandler input, SoundManager soundManager) {
        this.input = input;
        this.soundManager = soundManager;
        hitbox = new Rectangle(x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        loadTextures();
    }
    
    protected abstract void loadTextures();
    protected abstract void disposeTextures();
    protected abstract Texture getCurrentTexture();
    
    public void setCollisionRects(Array<Rectangle> collisions) {
        this.collisionRects = collisions;
    }
    
    public void update(float delta) {
        handleInput(delta);
        updateTimers(delta);
        applyPhysics(delta);
        updateAnimation(delta);
        hitbox.setPosition(x, y);
        
        debugFrameCounter++;
        if (debugFrameCounter >= 60) {
            System.out.println("📍 Player position: (" + (int)x + ", " + (int)y + ") | onGround: " + onGround + " | State: " + currentState);
            debugFrameCounter = 0;
        }
    }

    protected void handleInput(float delta) {
        if (isDead) return;
        if (isHit) return;
        if (isAttacking) return;
        
        if (input.isDeadPressed()) {
            isDead = true;
            currentState = State.DEAD;
            deadFrame = 0;
            animationTimer = 0f;
            velocityX = 0;
            
            if (soundManager != null) {
                soundManager.play("death");
            }
            
            return;
        }
        
        if (input.isHitPressed()) {
            isHit = true;
            hitTimer = hitDuration;
            currentState = State.HIT;
            animationTimer = 0f;
            velocityX = 0;
            
            if (soundManager != null) {
                soundManager.play("hit");
            }
            
            return;
        }
        
        if (input.isAttackPressed()) {
            isAttacking = true;
            attackTimer = attackDuration;
            currentState = State.ATTACK;
            attackFrame = 0;
            animationTimer = 0f;
            velocityX = 0;
            
            if (soundManager != null) {
                soundManager.play("attack");
            }
            
            return;
        }
        
        if (input.isJumpPressed() && onGround) {
            velocityY = Constants.JUMP_FORCE;
            onGround = false;
            currentState = State.JUMP;
            
            isFastFalling = false;
            jumpProtectionTimer = jumpProtectionDuration;
            
            if (soundManager != null) {
                soundManager.play("jump");
            }
            
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
                    velocityY = -Constants.MAX_FALL_SPEED;
                    fastFallCooldownTimer = fastFallCooldownDuration;
                    currentState = State.CROUCH;
                    velocityX = 0;
                } else if (isFastFalling) {
                    currentState = State.CROUCH;
                    velocityX = 0;
                } else {
                    if (velocityY > 0) {
                        currentState = State.JUMP;
                    } else {
                        currentState = State.FALL;
                    }
                }
            }
        }
        else {
            if (isFastFalling) {
                isFastFalling = false;
            }
            
            if (!onGround) {
                if (velocityY > 0) {
                    currentState = State.JUMP;
                } else {
                    currentState = State.FALL;
                }
            }
            
            if (input.isLeftPressed()) {
                velocityX = -Constants.WALK_SPEED;
                facingRight = false;
                if (onGround) currentState = State.WALK;
            } else if (input.isRightPressed()) {
                velocityX = Constants.WALK_SPEED;
                facingRight = true;
                if (onGround) currentState = State.WALK;
            } else {
                velocityX = 0;
                if (onGround && currentState == State.WALK) {
                    currentState = State.IDLE;
                }
            }
        }
    }
    
    protected void updateTimers(float delta) {
        if (isAttacking) {
            attackTimer -= delta;
            if (attackTimer <= 0) {
                isAttacking = false;
                currentState = State.IDLE;
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
            velocityY -= Constants.GRAVITY * delta;
        }
        
        if (velocityY < -Constants.MAX_FALL_SPEED) {
            velocityY = -Constants.MAX_FALL_SPEED;
        }
        
        float totalMoveY = velocityY * delta;
        
        int steps = 1;
        if (Math.abs(totalMoveY) > Constants.MAX_MOVE_PER_STEP) {
            steps = (int) Math.ceil(Math.abs(totalMoveY) / Constants.MAX_MOVE_PER_STEP);
            if (steps > Constants.MAX_PHYSICS_STEPS) steps = Constants.MAX_PHYSICS_STEPS;
        }
        
        float movePerStep = totalMoveY / steps;
        
        for (int i = 0; i < steps; i++) {
            float newY = y + movePerStep;
            
            hitbox.setPosition(x, newY);
            boolean collidedVertically = false;
            
            if (collisionRects != null) {
                for (Rectangle collRect : collisionRects) {
                    if (hitbox.overlaps(collRect)) {
                        collidedVertically = true;
                        
                        if (velocityY < 0) {
                            y = collRect.y + collRect.height;
                            velocityY = 0;
                            onGround = true;
                            
                            isFastFalling = false;
                            jumpProtectionTimer = 0;
                            
                            if (currentState != State.CROUCH && currentState != State.BLOCK 
                                && currentState != State.ATTACK && currentState != State.HIT) {
                                if (velocityX == 0) {
                                    currentState = State.IDLE;
                                } else {
                                    currentState = State.WALK;
                                }
                            }
                        }
                        else if (velocityY > 0) {
                            y = collRect.y - Constants.PLAYER_HEIGHT;
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
                    if (velocityY > 0) {
                        currentState = State.JUMP;
                    } else if (velocityY < 0) {
                        currentState = State.FALL;
                    }
                }
            } else {
                break;
            }
        }
        
        float newX = x + velocityX * delta;
        hitbox.setPosition(newX, y);
        
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
                
            case CROUCH:
            case BLOCK:
                walkFrame = 0;
                attackFrame = 0;
                animationTimer = 0f;
                break;
                
            default:
                walkFrame = 0;
                attackFrame = 0;
                animationTimer = 0f;
                break;
        }
    }
    
    protected abstract int getWalkFrameCount();
    protected abstract int getAttackFrameCount();
    protected abstract int getDeadFrameCount();

    public void render(SpriteBatch batch) {
        Texture currentTexture = getCurrentTexture();
        
        if (debugFrameCounter == 0) {
            System.out.println("🎨 Player.render() appelé - Position: (" + (int)x + ", " + (int)y + ") - Texture: " + (currentTexture != null ? "OK" : "NULL"));
        }

        if (currentState == State.DEAD) {
            float rotatedWidth = Constants.PLAYER_HEIGHT;
            float rotatedHeight = Constants.PLAYER_WIDTH;
            float originX = rotatedWidth / 2f;
            float originY = rotatedHeight / 2f;
            float drawX = x;
            float drawY = y;
            float rotation = facingRight ? -90f : 90f;
            float scaleX = facingRight ? 1f : -1f;
            
            batch.draw(
                currentTexture,
                drawX, drawY,
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
                batch.draw(currentTexture, x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
            } else {
                batch.draw(currentTexture, x + Constants.PLAYER_WIDTH, y, -Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
            }
        }
    }

    public void dispose() {
        disposeTextures();
    }
    
    public State getCurrentState() { return currentState; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isDead() { return isDead; }
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getHitbox() { return hitbox; }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.hitbox.setPosition(x, y);
        
        if (collisionRects != null) {
            Rectangle testHitbox = new Rectangle(x, y - 2, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
            
            for (Rectangle collRect : collisionRects) {
                if (testHitbox.overlaps(collRect)) {
                    onGround = true;
                    velocityY = 0;
                    currentState = State.IDLE;
                    System.out.println("✅ Spawn détecté sur collision - onGround = true");
                    break;
                }
            }
        }
        
        System.out.println("✅ Position du joueur définie : (" + x + ", " + y + ") | onGround: " + onGround);
    }
}