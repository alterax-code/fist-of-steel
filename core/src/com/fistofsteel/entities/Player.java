package com.fistofsteel.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.audio.SoundManager;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;

public class Player {
    private float x, y;
    private float velocityX, velocityY;
    private boolean onGround = false;
    private boolean facingRight = true;

    private Rectangle hitbox;
    private InputHandler input;
    private SoundManager soundManager;
    
    private Array<Rectangle> collisionRects;


    private Texture idleTexture;
    private Texture jumpTexture;
    private Texture blockTexture;
    private Texture crouchTexture;
    private Texture hitTexture;
    

    private Texture[] walkTextures;
    private Texture[] attackTextures;
    private Texture[] deadTextures;

    private int walkFrame = 0;
    private int attackFrame = 0;
    private int deadFrame = 0;
    
    private float animationTimer = 0f;
    private float walkFrameDuration = 0.15f;
    private float attackFrameDuration = 0.15f;
    private float deadFrameDuration = 0.3f;

    private enum State { 
        IDLE, WALK, CROUCH, JUMP, FALL, 
        ATTACK, BLOCK, HIT, DEAD 
    }
    private State currentState = State.IDLE;
    

    private float attackTimer = 0f;
    private float attackDuration = 0.3f;
    private float hitTimer = 0f;
    private float hitDuration = 0.2f;
    private boolean isDead = false;
    private boolean isHit = false;
    private boolean isAttacking = false;
    
    private float fastFallCooldownTimer = 0f;
    private float fastFallCooldownDuration = 0.6f;
    private boolean isFastFalling = false;
    

    private float jumpProtectionTimer = 0f;
    private float jumpProtectionDuration = 0.15f;

    public Player(InputHandler input, SoundManager soundManager) {
        this.input = input;
        this.soundManager = soundManager;
        this.x = 100;
        this.y = 200;

        hitbox = new Rectangle(x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);

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
    
    public void setCollisionRects(Array<Rectangle> collisions) {
        this.collisionRects = collisions;
    }

    public void update(float delta) {
        handleInput(delta);
        updateTimers(delta);
        applyPhysics(delta);
        updateAnimation(delta);
        hitbox.setPosition(x, y);
    }

    private void handleInput(float delta) {
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
                if (onGround) {
                    currentState = State.IDLE;
                }
            }
        }
    }

    private void updateTimers(float delta) {
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

    private void applyPhysics(float delta) {
        if (isDead) return;
        

        if (!isFastFalling) {
            velocityY -= Constants.GRAVITY * delta;
        }
        

        float newY = y + velocityY * delta;
        

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

    private void updateAnimation(float delta) {
        animationTimer += delta;
        
        switch (currentState) {
            case WALK:
                if (animationTimer >= walkFrameDuration) {
                    walkFrame = (walkFrame + 1) % walkTextures.length;
                    animationTimer = 0f;
                }
                break;
                
            case ATTACK:
                if (animationTimer >= attackFrameDuration) {
                    attackFrame = (attackFrame + 1) % attackTextures.length;
                    animationTimer = 0f;
                }
                break;
                
            case DEAD:
                if (animationTimer >= deadFrameDuration && deadFrame < deadTextures.length - 1) {
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

    public void render(SpriteBatch batch) {
        Texture currentTexture = idleTexture;

        switch (currentState) {
            case WALK:
                currentTexture = walkTextures[walkFrame];
                break;
            case CROUCH:
                currentTexture = crouchTexture;
                break;
            case JUMP:
            case FALL:
                currentTexture = jumpTexture;
                break;
            case ATTACK:
                currentTexture = attackTextures[attackFrame];
                break;
            case BLOCK:
                currentTexture = blockTexture;
                break;
            case HIT:
                currentTexture = hitTexture;
                break;
            case DEAD:
                currentTexture = deadTextures[deadFrame];
                break;
            default:
                currentTexture = idleTexture;
                break;
        }

        batch.begin();
        
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
        
        batch.end();
    }

    public void dispose() {
        idleTexture.dispose();
        jumpTexture.dispose();
        blockTexture.dispose();
        crouchTexture.dispose();
        hitTexture.dispose();
        
        for (Texture t : walkTextures) t.dispose();
        for (Texture t : attackTextures) t.dispose();
        for (Texture t : deadTextures) t.dispose();
    }
    
    public State getCurrentState() { return currentState; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isDead() { return isDead; }
    public float getX() { return x; }
    public float getY() { return y; }
}