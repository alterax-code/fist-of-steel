package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.utils.Constants;

/**
 * Classe abstraite Enemy - Base pour tous les ennemis
 * Inspirée de Player mais avec IA au lieu d'InputHandler
 */
public abstract class Enemy {
    protected float x, y;
    protected float velocityX, velocityY;
    protected boolean onGround = false;
    protected boolean facingRight = true;

    protected Rectangle hitbox;
    protected Array<Rectangle> collisionRects;
    
    // Stats de l'ennemi
    protected int health;
    protected int maxHealth;
    protected int damage;
    protected float detectionRange;
    protected float attackRange;
    
    // Référence au joueur pour l'IA
    protected Player targetPlayer;

    protected enum State { 
        IDLE, PATROL, CHASE, ATTACK, HIT, DEAD 
    }
    protected State currentState = State.IDLE;
    
    // Timers
    protected float attackTimer = 0f;
    protected float attackCooldown = 1.5f;
    protected float hitTimer = 0f;
    protected float hitDuration = 0.3f;
    protected boolean isDead = false;
    protected boolean isHit = false;
    protected boolean isAttacking = false;
    
    // Animation
    protected float animationTimer = 0f;
    protected float walkFrameDuration = 0.15f;
    protected float attackFrameDuration = 0.15f;
    protected float deadFrameDuration = 0.3f;
    
    protected int walkFrame = 0;
    protected int attackFrame = 0;
    protected int deadFrame = 0;
    
    // IA - Patrouille
    protected float patrolMinX;
    protected float patrolMaxX;
    protected float patrolSpeed = 80f;
    protected boolean patrolGoingRight = true;

    public Enemy(float x, float y, Player targetPlayer) {
        this.x = x;
        this.y = y;
        this.targetPlayer = targetPlayer;
        loadTextures();
        initStats();
        hitbox = new Rectangle(
            x + getHitboxOffsetX(), 
            y + getHitboxOffsetY(), 
            getHitboxWidth(), 
            getHitboxHeight()
        );
        
        // Zone de patrouille par défaut (300px autour du spawn)
        patrolMinX = x - 300f;
        patrolMaxX = x + 300f;
    }
    
    // ===== MÉTHODES ABSTRAITES =====
    protected abstract void loadTextures();
    protected abstract void disposeTextures();
    protected abstract Texture getCurrentTexture();
    protected abstract int getWalkFrameCount();
    protected abstract int getAttackFrameCount();
    protected abstract int getDeadFrameCount();
    
    /**
     * Initialise les stats de l'ennemi (HP, dégâts, portées)
     */
    protected abstract void initStats();
    
    // ===== HITBOX ABSTRAITES =====
    protected abstract float getHitboxWidth();
    protected abstract float getHitboxHeight();
    protected abstract float getHitboxOffsetX();
    protected abstract float getHitboxOffsetY();
    
    public void setCollisionRects(Array<Rectangle> collisions) {
        this.collisionRects = collisions;
    }
    
    public void setPatrolZone(float minX, float maxX) {
        this.patrolMinX = minX;
        this.patrolMaxX = maxX;
    }
    
    public void update(float delta) {
        if (isDead) {
            updateDeadAnimation(delta);
            return;
        }
        
        updateTimers(delta);
        updateAI(delta);
        applyPhysics(delta);
        updateAnimation(delta);
        
        hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
    }
    
    /**
     * IA de l'ennemi - À surcharger pour des comportements spécifiques
     */
    protected void updateAI(float delta) {
        if (isHit || isAttacking) {
            velocityX = 0;
            return;
        }
        
        float distanceToPlayer = Math.abs(targetPlayer.getX() - x);
        
        // Détection du joueur
        if (distanceToPlayer <= detectionRange) {
            // En portée d'attaque ?
            if (distanceToPlayer <= attackRange && attackTimer <= 0) {
                attack();
            } else {
                // Poursuite
                chase();
            }
        } else {
            // Patrouille
            patrol();
        }
    }
    
    /**
     * Comportement de patrouille
     */
    protected void patrol() {
        currentState = State.PATROL;
        
        if (patrolGoingRight) {
            velocityX = patrolSpeed;
            facingRight = true;
            
            if (x >= patrolMaxX) {
                patrolGoingRight = false;
            }
        } else {
            velocityX = -patrolSpeed;
            facingRight = false;
            
            if (x <= patrolMinX) {
                patrolGoingRight = true;
            }
        }
    }
    
    /**
     * Comportement de poursuite
     */
    protected void chase() {
        currentState = State.CHASE;
        
        float playerX = targetPlayer.getX();
        
        if (playerX > x) {
            velocityX = patrolSpeed * 1.5f; // 50% plus rapide en chasse
            facingRight = true;
        } else {
            velocityX = -patrolSpeed * 1.5f;
            facingRight = false;
        }
    }
    
    /**
     * Déclenche une attaque
     */
    protected void attack() {
        isAttacking = true;
        attackTimer = attackCooldown;
        currentState = State.ATTACK;
        attackFrame = 0;
        animationTimer = 0f;
        velocityX = 0;
    }
    
    /**
     * L'ennemi prend des dégâts
     */
    public void takeDamage(int damage) {
        if (isDead || isHit) return;
        
        health -= damage;
        
        if (health <= 0) {
            health = 0;
            die();
        } else {
            isHit = true;
            hitTimer = hitDuration;
            currentState = State.HIT;
            velocityX = 0;
        }
    }
    
    /**
     * L'ennemi meurt
     */
    protected void die() {
        isDead = true;
        currentState = State.DEAD;
        deadFrame = 0;
        animationTimer = 0f;
        velocityX = 0;
        velocityY = 0;
    }
    
    protected void updateTimers(float delta) {
        if (attackTimer > 0) {
            attackTimer -= delta;
            if (attackTimer <= 0 && isAttacking) {
                isAttacking = false;
                currentState = State.IDLE;
            }
        }
        
        if (hitTimer > 0) {
            hitTimer -= delta;
            if (hitTimer <= 0 && isHit) {
                isHit = false;
                currentState = State.IDLE;
            }
        }
    }
    
    protected void applyPhysics(float delta) {
        if (isDead) return;
        
        // Gravité
        velocityY -= Constants.GRAVITY * delta;
        
        if (velocityY < -Constants.MAX_FALL_SPEED) {
            velocityY = -Constants.MAX_FALL_SPEED;
        }
        
        // Mouvement vertical
        float newY = y + velocityY * delta;
        hitbox.setPosition(x + getHitboxOffsetX(), newY + getHitboxOffsetY());
        
        boolean collidedVertically = false;
        if (collisionRects != null) {
            for (Rectangle collRect : collisionRects) {
                if (hitbox.overlaps(collRect)) {
                    collidedVertically = true;
                    
                    if (velocityY < 0) {
                        // Sol
                        y = collRect.y + collRect.height - getHitboxOffsetY();
                        velocityY = 0;
                        onGround = true;
                    } else if (velocityY > 0) {
                        // Plafond
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
        }
        
        // Mouvement horizontal
        float newX = x + velocityX * delta;
        hitbox.setPosition(newX + getHitboxOffsetX(), y + getHitboxOffsetY());
        
        boolean collidedHorizontally = false;
        if (collisionRects != null) {
            for (Rectangle collRect : collisionRects) {
                if (hitbox.overlaps(collRect)) {
                    collidedHorizontally = true;
                    
                    // Inverse la direction de patrouille si collision
                    if (currentState == State.PATROL) {
                        patrolGoingRight = !patrolGoingRight;
                    }
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
            case PATROL:
            case CHASE:
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
    
    protected void updateDeadAnimation(float delta) {
        animationTimer += delta;
        if (animationTimer >= deadFrameDuration && deadFrame < getDeadFrameCount() - 1) {
            deadFrame++;
            animationTimer = 0f;
        }
    }
    
    public void render(SpriteBatch batch) {
        Texture currentTexture = getCurrentTexture();
        
        if (currentTexture == null) return;
        
        if (currentState == State.DEAD) {
            // Rotation pour la mort (comme Player)
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
    
    public void dispose() {
        disposeTextures();
    }
    
    // ===== GETTERS =====
    public State getCurrentState() { return currentState; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isDead() { return isDead; }
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getHitbox() { return hitbox; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    
    /**
     * Vérifie si l'attaque de l'ennemi touche le joueur
     */
    public boolean canHitPlayer() {
        if (!isAttacking) return false;
        
        // Zone d'attaque devant l'ennemi
        float attackWidth = attackRange;
        float attackHeight = getHitboxHeight();
        float attackX = facingRight ? x + getHitboxWidth() : x - attackWidth;
        float attackY = y;
        
        Rectangle attackBox = new Rectangle(attackX, attackY, attackWidth, attackHeight);
        return attackBox.overlaps(targetPlayer.getHitbox());
    }
}