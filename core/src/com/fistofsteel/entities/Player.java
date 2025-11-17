package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;

/**
 * Classe Player ultra-simplifiée
 * Gère uniquement la logique gameplay - PAS les sons
 */
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

    public Player(InputHandler input) {
        this.input = input;
        loadTextures();
        hitbox = new Rectangle(x + getHitboxOffsetX(), y + getHitboxOffsetY(), getHitboxWidth(), getHitboxHeight());
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
        
        // ⭐ NOUVEAU : Résoudre les collisions après mise à jour de la hitbox
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
            isDead = true;
            currentState = State.DEAD;
            deadFrame = 0;
            animationTimer = 0f;
            velocityX = 0;
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
            return;
        }
        
        // ===== SAUT =====
        if (input.isJumpPressed() && onGround) {
            velocityY = Constants.JUMP_FORCE;
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
                    velocityY = -Constants.MAX_FALL_SPEED;
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
                velocityX = -Constants.WALK_SPEED;
                facingRight = false;
                if (onGround) currentState = State.WALK;
            } else if (input.isRightPressed()) {
                velocityX = Constants.WALK_SPEED;
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
            float rotatedWidth = Constants.PLAYER_HEIGHT;
            float rotatedHeight = Constants.PLAYER_WIDTH;
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
                batch.draw(currentTexture, x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
            } else {
                batch.draw(currentTexture, x + Constants.PLAYER_WIDTH, y, -Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
            }
        }
    }

    /**
     * ⭐ NOUVEAU : Repousse le joueur si sa hitbox est coincée dans un bloc de collision
     * Calcule la distance minimale pour sortir dans chaque direction (haut, bas, gauche, droite)
     * et repousse le joueur dans la direction la plus courte
     */
    protected void resolveHitboxCollisions() {
        if (collisionRects == null || collisionRects.size == 0) return;
        
        // Vérifier si la hitbox est dans un bloc
        for (Rectangle collRect : collisionRects) {
            if (hitbox.overlaps(collRect)) {
                // Calculer les distances de sortie dans chaque direction
                float overlapLeft = (hitbox.x + hitbox.width) - collRect.x;
                float overlapRight = (collRect.x + collRect.width) - hitbox.x;
                float overlapBottom = (hitbox.y + hitbox.height) - collRect.y;
                float overlapTop = (collRect.y + collRect.height) - hitbox.y;
                
                // Trouver la plus petite distance de sortie
                float minOverlap = Math.min(
                    Math.min(overlapLeft, overlapRight),
                    Math.min(overlapBottom, overlapTop)
                );
                
                // Repousser dans la direction la plus proche
                if (minOverlap == overlapLeft) {
                    // Repousser vers la gauche
                    float pushDistance = overlapLeft + 0.1f; // +0.1f pour éviter les collisions multiples
                    x -= pushDistance;
                    velocityX = 0;
                    System.out.println("⬅️ Player repoussé vers la GAUCHE de " + (int)pushDistance + "px");
                } 
                else if (minOverlap == overlapRight) {
                    // Repousser vers la droite
                    float pushDistance = overlapRight + 0.1f;
                    x += pushDistance;
                    velocityX = 0;
                    System.out.println("➡️ Player repoussé vers la DROITE de " + (int)pushDistance + "px");
                } 
                else if (minOverlap == overlapBottom) {
                    // Repousser vers le bas
                    float pushDistance = overlapBottom + 0.1f;
                    y -= pushDistance;
                    velocityY = 0;
                    System.out.println("⬇️ Player repoussé vers le BAS de " + (int)pushDistance + "px");
                } 
                else if (minOverlap == overlapTop) {
                    // Repousser vers le haut
                    float pushDistance = overlapTop + 0.1f;
                    y += pushDistance;
                    velocityY = 0;
                    onGround = true; // Si on repousse vers le haut, c'est qu'on est sur le sol
                    System.out.println("⬆️ Player repoussé vers le HAUT de " + (int)pushDistance + "px");
                }
                
                // Mettre à jour la hitbox après le déplacement
                hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
                
                // Vérifier s'il y a encore des collisions après le premier déplacement
                // (nécessaire si l'entité est coincée entre plusieurs blocs)
                break; // On ne traite qu'une collision à la fois pour éviter les comportements étranges
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
}