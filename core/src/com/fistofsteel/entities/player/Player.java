package com.fistofsteel.entities.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.utils.PhysicsConstants;
import com.fistofsteel.utils.EntityConstants;
import com.fistofsteel.items.Armor;
import com.fistofsteel.items.Weapon;

/**
 * Classe abstraite représentant le joueur.
 * Gère le mouvement, les combats, l'équipement et les animations.
 */
public abstract class Player {
    protected float x, y;
    protected float velocityX, velocityY;
    protected boolean onGround = false;
    protected boolean facingRight = true;

    protected Rectangle hitbox;
    protected InputHandler input;
    protected AudioManager audioManager;
    
    protected Array<Rectangle> collisionRects;

    /**
     * États possibles du joueur.
     */
    protected enum State { 
        IDLE, WALK, CROUCH, JUMP, FALL, 
        ATTACK, BLOCK, HIT, DEAD 
    }
    protected State currentState = State.IDLE;
    
    protected float attackTimer = 0f;
    protected float attackDuration = 0.4f;
    protected float attackCooldownTimer = 0f;
    protected float attackCooldown = 0.6f;
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
    protected float attackFrameDuration = 0.2f;
    protected float deadFrameDuration = 0.3f;
    
    protected int walkFrame = 0;
    protected int attackFrame = 0;
    protected int deadFrame = 0;
    
    private int debugFrameCounter = 0;

    protected int maxHealth = 100;
    protected int health = maxHealth;
    protected int armor = 0;
    protected int baseAttack = 10;
    protected int attackBonus = 0;
    
    protected Armor equippedArmor = null;
    protected Weapon equippedWeapon = null;

    /**
     * Constructeur du joueur.
     * 
     * @param input Le gestionnaire d'input
     * @param audioManager Le gestionnaire audio
     */
    public Player(InputHandler input, AudioManager audioManager) {
        this.input = input;
        this.audioManager = audioManager;
        loadTextures();
        hitbox = new Rectangle(x + getHitboxOffsetX(), y + getHitboxOffsetY(), getHitboxWidth(), getHitboxHeight());
        this.maxHealth = 100;
        this.health = maxHealth;
    }
    
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
    
    /**
     * Définit les rectangles de collision.
     * 
     * @param collisions Les rectangles de collision
     */
    public void setCollisionRects(Array<Rectangle> collisions) {
        this.collisionRects = collisions;
    }
    
    /**
     * Met à jour le joueur.
     * 
     * @param delta Le temps écoulé
     */
    public void update(float delta) {
        handleInput(delta);
        updateTimers(delta);
        applyPhysics(delta);
        updateAnimation(delta);
        hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
        resolveHitboxCollisions();
        
        debugFrameCounter++;
        if (debugFrameCounter >= 60) {
            System.out.println("Player: (" + (int)x + ", " + (int)y + ") | Ground: " + onGround + " | State: " + currentState);
            debugFrameCounter = 0;
        }
    }

    /**
     * Gère les entrées du joueur.
     * 
     * @param delta Le temps écoulé
     */
    protected void handleInput(float delta) {
        if (isDead) return;
        if (isHit) return;
        if (isAttacking) return;
        
        if (input.isDeadPressed()) {
            die();
            return;
        }
        
        if (input.isHitPressed()) {
            triggerHitState();
            return;
        }
        
        if (input.isAttackPressed() && attackCooldownTimer <= 0) {
            isAttacking = true;
            attackTimer = attackDuration;
            attackCooldownTimer = attackCooldown;
            currentState = State.ATTACK;
            attackFrame = 0;
            animationTimer = 0f;
            velocityX = 0;
            hasDealtDamageThisAttack = false;
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

    /**
     * Met à jour les timers du joueur.
     * 
     * @param delta Le temps écoulé
     */
    protected void updateTimers(float delta) {
        if (isAttacking) {
            attackTimer -= delta;
            if (attackTimer <= 0) {
                isAttacking = false;
                currentState = State.IDLE;
                hasDealtDamageThisAttack = false;
            }
        }
        
        if (attackCooldownTimer > 0) {
            attackCooldownTimer -= delta;
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

    /**
     * Applique la physique au joueur.
     * 
     * @param delta Le temps écoulé
     */
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

    /**
     * Met à jour l'animation du joueur.
     * 
     * @param delta Le temps écoulé
     */
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

    /**
     * Affiche le joueur.
     * 
     * @param batch Le SpriteBatch pour le rendu
     */
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

    /**
     * Résout les collisions de la hitbox.
     */
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

    /**
     * Applique des dégâts au joueur.
     * 
     * @param rawDamage Les dégâts bruts avant réduction d'armure
     */
    public void applyDamage(int rawDamage) {
        if (isDead || isHit) return;
        
        int effectiveDamage = Math.max(0, rawDamage - armor);
        
        health -= effectiveDamage;
        if (health < 0) health = 0;
        
        System.out.println("Degats : " + rawDamage + " (armure -" + armor + ") = " + effectiveDamage + " | HP: " + health + "/" + maxHealth);
        
        if (health == 0) {
            die();
        } else {
            triggerHitState();
        }
    }

    /**
     * Déclenche l'état "touché".
     */
    protected void triggerHitState() {
        isHit = true;
        hitTimer = hitDuration;
        currentState = State.HIT;
        animationTimer = 0f;
        velocityX = 0;
        
        if (audioManager != null) {
            audioManager.playSound("hit");
        }
    }

    /**
     * Soigne le joueur.
     * 
     * @param amount Le nombre de PV à restaurer
     */
    public void heal(int amount) {
        if (amount <= 0) return;
        health += amount;
        if (health > maxHealth) health = maxHealth;
        System.out.println("Heal +" + amount + " -> " + health + "/" + maxHealth);
    }

    /**
     * Tue le joueur.
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
        
        if (audioManager != null) {
            audioManager.playSound("death");
        }
        
        System.out.println("Le joueur est mort");
    }

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

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = Math.max(0, armor);
        System.out.println("Armure : " + this.armor);
    }
    
    /**
     * Équipe une armure.
     * 
     * @param newArmor L'armure à équiper
     */
    public void equipArmor(Armor newArmor) {
        if (newArmor == null) return;
        
        if (equippedArmor != null) {
            armor -= equippedArmor.getArmorBonus();
            System.out.println("Armure retiree : " + equippedArmor.getDisplayName() + " (-" + equippedArmor.getArmorBonus() + " DEF)");
        }
        
        equippedArmor = newArmor;
        armor += newArmor.getArmorBonus();
        
        System.out.println("Armure equipee : " + newArmor.getDisplayName() + " (+" + newArmor.getArmorBonus() + " DEF) | Total DEF: " + armor);
    }
    
    /**
     * Équipe une arme.
     * 
     * @param newWeapon L'arme à équiper
     */
    public void equipWeapon(Weapon newWeapon) {
        if (newWeapon == null) return;
        
        if (equippedWeapon != null) {
            attackBonus -= equippedWeapon.getAttackBonus();
            System.out.println("Arme retiree : " + equippedWeapon.getDisplayName() + " (-" + equippedWeapon.getAttackBonus() + " ATK)");
        }
        
        equippedWeapon = newWeapon;
        attackBonus += newWeapon.getAttackBonus();
        
        System.out.println("Arme equipee : " + newWeapon.getDisplayName() + " (+" + newWeapon.getAttackBonus() + " ATK) | Total ATK: " + getTotalAttack());
    }
    
    public Armor getEquippedArmor() {
        return equippedArmor;
    }
    
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public State getCurrentState() { return currentState; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isDead() { return isDead; }
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getHitbox() { return hitbox; }

    /**
     * Définit la position du joueur.
     * 
     * @param x La nouvelle position X
     * @param y La nouvelle position Y
     */
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

    /**
     * Libère les ressources du joueur.
     */
    public void dispose() {
        disposeTextures();
    }
}