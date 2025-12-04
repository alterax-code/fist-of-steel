package com.fistofsteel.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.utils.EntityConstants;
import com.fistofsteel.utils.PhysicsConstants;
import com.fistofsteel.utils.HealthBar;

/**
 * Classe abstraite représentant un ennemi dans le jeu.
 * Gère l'IA, les animations, les collisions et les combats.
 */
public abstract class Enemy {
    
    protected float x, y;
    protected float width = EntityConstants.ENEMY_WIDTH;
    protected float height = EntityConstants.ENEMY_HEIGHT;
    protected float velocityX = 0;
    protected float velocityY = 0;
    
    protected float spawnX, spawnY;
    protected float patrolCenterX;
    protected boolean onGroundLastFrame = false;
    
    protected Player targetPlayer;
    
    protected int health = 50;
    protected int maxHealth = 50;
    protected int damage = 10;
    
    /**
     * États possibles de l'ennemi.
     */
    protected enum State { 
        IDLE, PATROL, CHASE, ATTACK, HIT, DEAD 
    }
    protected State currentState = State.IDLE;
    
    protected float detectionRange = 400f;
    protected float losePlayerRange = 600f;
    protected float attackRange = 80f;
    
    protected float patrolRange = 200f;
    protected float edgeDetectionDistance = 20f;
    protected float patrolSpeed = 50f;
    protected float chaseSpeed = 75f;
    protected boolean facingRight = true;
    
    protected float attackTimer = 0;
    protected float attackCooldown = 2.0f;
    protected float hitTimer = 0;
    protected float hitDuration = 0.3f;
    protected float deadTimer = 0;
    protected float deadDuration = 2.0f;
    
    protected boolean isAttacking = false;
    protected boolean isHit = false;
    protected boolean isDead = false;
    protected boolean isOnGround = false;
    protected boolean hasDealtDamageThisAttack = false;
    
    protected float animationTimer = 0f;
    protected float walkFrameDuration = 0.15f;
    protected float attackFrameDuration = 0.2f;
    protected float deadFrameDuration = 0.3f;
    
    protected int walkFrame = 0;
    protected int attackFrame = 0;
    protected int deadFrame = 0;
    protected int hurtFrame = 0;
    protected int idleFrame = 0;
    
    protected static final float GRAVITY = -800f;
    protected static final float TERMINAL_VELOCITY = -500f;
    protected static final float GROUND_FRICTION = 0.9f;
    protected Rectangle hitbox;
    protected Array<Rectangle> collisionRects;
    
    protected Texture[][] textures;
    
    /**
     * Constructeur de l'ennemi.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param targetPlayer Le joueur ciblé
     */
    public Enemy(float x, float y, Player targetPlayer) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.spawnY = y;
        this.patrolCenterX = x;
        this.targetPlayer = targetPlayer;
        this.currentState = State.IDLE;
        
        initStats();
        loadTextures();
        
        this.hitbox = new Rectangle(
            x + getHitboxOffsetX(), 
            y + getHitboxOffsetY(), 
            getHitboxWidth(), 
            getHitboxHeight()
        );
    }
    
    /**
     * Initialise les statistiques de l'ennemi.
     */
    protected abstract void initStats();
    
    /**
     * Retourne le nom de l'ennemi pour charger ses sprites.
     * 
     * @return Le nom de l'ennemi
     */
    protected abstract String getEnemyName();
    
    /**
     * Charge les textures de l'ennemi.
     * Peut être surchargé pour des cas spéciaux (Boss).
     */
    protected void loadTextures() {
        textures = EnemyAnimationHelper.loadEnemySprites("assets/sprites/sbires/", getEnemyName());
    }
    
    /**
     * Libère les textures de l'ennemi.
     */
    protected void disposeTextures() {
        EnemyAnimationHelper.disposeTextures(textures);
    }
    
    /**
     * Récupère la texture courante selon l'état.
     * 
     * @return La texture à afficher
     */
    protected Texture getCurrentTexture() {
        if (textures == null || textures.length == 0) {
            return null;
        }
        
        Texture[] idle = textures[0];
        Texture[] hurt = textures[1];
        Texture[] walk = textures[2];
        Texture[] attack = textures[3];
        Texture[] dead = textures[4];
        
        if (idle == null || idle.length == 0) {
            return null;
        }
        
        switch (currentState) {
            case IDLE:
                return idle[idleFrame % idle.length];
                
            case PATROL:
            case CHASE:
                return walk != null && walk.length > 0 
                    ? walk[walkFrame % walk.length] 
                    : idle[0];
                
            case ATTACK:
                return attack != null && attack.length > 0 
                    ? attack[attackFrame % attack.length] 
                    : idle[0];
                
            case HIT:
                return hurt != null && hurt.length > 0 
                    ? hurt[hurtFrame % hurt.length] 
                    : idle[0];
                
            case DEAD:
                return dead != null && dead.length > 0 
                    ? dead[Math.min(deadFrame, dead.length - 1)] 
                    : idle[0];
                
            default:
                return idle[0];
        }
    }
    
    /**
     * Met à jour l'animation selon l'état courant.
     * 
     * @param delta Le temps écoulé depuis la dernière frame
     */
    protected void updateAnimation(float delta) {
        animationTimer += delta;
        
        switch (currentState) {
            case IDLE:
                if (animationTimer >= 0.1f && textures[0] != null) {
                    idleFrame = (idleFrame + 1) % textures[0].length;
                    animationTimer = 0f;
                }
                break;
                
            case PATROL:
            case CHASE:
                if (animationTimer >= walkFrameDuration && textures[2] != null) {
                    walkFrame = (walkFrame + 1) % textures[2].length;
                    animationTimer = 0f;
                }
                break;
                
            case ATTACK:
                if (animationTimer >= attackFrameDuration && textures[3] != null) {
                    attackFrame = (attackFrame + 1) % textures[3].length;
                    animationTimer = 0f;
                    
                    onAttackFrame(attackFrame);
                }
                break;
                
            case HIT:
                if (animationTimer >= 0.1f && textures[1] != null) {
                    hurtFrame = (hurtFrame + 1) % textures[1].length;
                    animationTimer = 0f;
                }
                break;
                
            case DEAD:
                if (animationTimer >= deadFrameDuration && textures[4] != null && deadFrame < textures[4].length - 1) {
                    deadFrame++;
                    animationTimer = 0f;
                }
                break;
                
            default:
                idleFrame = 0;
                walkFrame = 0;
                attackFrame = 0;
                hurtFrame = 0;
                animationTimer = 0f;
                break;
        }
    }
    
    /**
     * Hook pour comportement spécifique pendant une frame d'attaque.
     * Utilisé par Mage pour tirer un projectile.
     * 
     * @param frame La frame d'attaque courante
     */
    protected void onAttackFrame(int frame) {
    }
    
    protected abstract float getHitboxWidth();
    protected abstract float getHitboxHeight();
    protected abstract float getHitboxOffsetX();
    protected abstract float getHitboxOffsetY();
    
    protected boolean useDirectionalHitbox() {
        return false;
    }
    
    protected float getDirectionalHitboxWidth() {
        return getHitboxWidth();
    }
    
    protected float getDirectionalHitboxOffsetX() {
        return getHitboxOffsetX();
    }
    
    /**
     * Définit les rectangles de collision pour l'ennemi.
     * 
     * @param collisionRects Les rectangles de collision de la map
     */
    public void setCollisionRects(Array<Rectangle> collisionRects) {
        this.collisionRects = collisionRects;
    }
    
    /**
     * Définit la zone de patrouille de l'ennemi.
     * 
     * @param min Distance minimale de patrouille
     * @param max Distance maximale de patrouille
     */
    public void setPatrolZone(float min, float max) {
        this.patrolRange = Math.max(Math.abs(min), Math.abs(max));
        System.out.println("Zone de patrouille : " + (int)patrolRange + " pixels");
    }
    
    /**
     * Met à jour l'IA de l'ennemi.
     * 
     * @param delta Le temps écoulé
     */
    protected void updateAI(float delta) {
        if (isHit || isAttacking) {
            velocityX = 0;
            return;
        }
        
        float distanceToPlayerX = Math.abs(targetPlayer.getX() - x);
        float distanceToPlayerY = Math.abs(targetPlayer.getY() - y);
        
        if (currentState == State.CHASE) {
            boolean stillInRange = distanceToPlayerX <= losePlayerRange && distanceToPlayerY <= 300f;
            
            if (stillInRange) {
                if (canHitPlayer() && attackTimer <= 0) {
                    attack();
                } else {
                    chase();
                }
                return;
            } else {
                System.out.println("Joueur perdu ! Nouvelle zone de patrouille a x=" + (int)x);
                patrolCenterX = x;
                currentState = State.IDLE;
                velocityX = 0;
            }
        }
        
        boolean playerDetected = distanceToPlayerX <= detectionRange && distanceToPlayerY <= 200f;
        
        if (playerDetected) {
            if (canHitPlayer() && attackTimer <= 0) {
                attack();
            } else {
                chase();
            }
            return;
        }
        
        patrolWithEdgeDetection();
    }
    
    /**
     * Fait patrouiller l'ennemi avec détection des bords.
     */
    protected void patrolWithEdgeDetection() {
        currentState = State.PATROL;
        
        boolean edgeAhead = isEdgeAhead();
        boolean wallAhead = isWallAhead();
        
        float distanceFromCenter = x - patrolCenterX;
        
        if (edgeAhead || wallAhead || Math.abs(distanceFromCenter) > patrolRange) {
            facingRight = !facingRight;
            velocityX = facingRight ? patrolSpeed : -patrolSpeed;
        } else {
            velocityX = facingRight ? patrolSpeed : -patrolSpeed;
        }
    }
    
    /**
     * Vérifie s'il y a un bord devant l'ennemi.
     * 
     * @return true s'il y a un bord
     */
    protected boolean isEdgeAhead() {
        if (collisionRects == null || !isOnGround) return false;
        
        float testX = facingRight 
            ? x + getHitboxWidth() + edgeDetectionDistance 
            : x - edgeDetectionDistance;
        
        float testY = y + getHitboxOffsetY() - 5f;
        
        Rectangle testRect = new Rectangle(testX - 5f, testY - 10f, 10f, 10f);
        
        for (Rectangle rect : collisionRects) {
            if (testRect.overlaps(rect)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Vérifie s'il y a un mur devant l'ennemi.
     * 
     * @return true s'il y a un mur
     */
    protected boolean isWallAhead() {
        if (collisionRects == null) return false;
        
        float testX = facingRight 
            ? x + getHitboxWidth() + edgeDetectionDistance 
            : x - edgeDetectionDistance;
        
        float testY = y + getHitboxHeight() / 2f;
        
        Rectangle testRect = new Rectangle(testX - 5f, testY - 20f, 10f, 40f);
        
        for (Rectangle rect : collisionRects) {
            if (testRect.overlaps(rect)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Fait poursuivre le joueur par l'ennemi.
     */
    protected void chase() {
        currentState = State.CHASE;
        
        float directionX = targetPlayer.getX() - x;
        
        if (directionX > 0) {
            velocityX = chaseSpeed;
            facingRight = true;
        } else {
            velocityX = -chaseSpeed;
            facingRight = false;
        }
    }
    
    /**
     * Lance une attaque.
     */
    protected void attack() {
        currentState = State.ATTACK;
        isAttacking = true;
        velocityX = 0;
        
        hasDealtDamageThisAttack = false;
        
        attackTimer = attackCooldown;
        System.out.println(getClass().getSimpleName() + " commence une attaque !");
    }
    
    /**
     * Vérifie si l'ennemi peut toucher le joueur.
     * 
     * @return true si le joueur est à portée
     */
    protected boolean canHitPlayer() {
        Rectangle playerHitbox = targetPlayer.getHitbox();
        float distance = Math.abs(playerHitbox.x - hitbox.x);
        float verticalDistance = Math.abs(playerHitbox.y - hitbox.y);
        
        return distance <= attackRange && verticalDistance <= 80f;
    }
    
    /**
     * Tente d'infliger des dégâts au joueur.
     */
    public void tryDealDamage() {
        if (isAttacking && canHitPlayer() && !hasDealtDamageThisAttack && !targetPlayer.isDead()) {
            targetPlayer.applyDamage(damage);
            hasDealtDamageThisAttack = true;
            System.out.println(getClass().getSimpleName() + " touche le joueur ! (-" + damage + " HP)");
        }
    }
    
    /**
     * Inflige des dégâts à l'ennemi.
     * 
     * @param damage Les dégâts à infliger
     */
    public void takeDamage(int damage) {
        if (isDead || isHit) return;
        
        health -= damage;
        System.out.println(getClass().getSimpleName() + " touche ! HP: " + health + "/" + maxHealth);
        
        if (health <= 0) {
            die();
        } else {
            isHit = true;
            hitTimer = hitDuration;
            currentState = State.HIT;
            velocityX = 0;
        }
    }
    
    /**
     * Tue l'ennemi.
     */
    protected void die() {
        isDead = true;
        currentState = State.DEAD;
        velocityX = 0;
        deadTimer = deadDuration;
        System.out.println(getClass().getSimpleName() + " mort !");
    }
    
    /**
 * Tue l'ennemi instantanément (sans animation de mort).
 * Utilisé quand l'ennemi tombe dans une zone de mort.
 */
public void killInstantly() {
    isDead = true;
    currentState = State.DEAD;
    velocityX = 0;
    velocityY = 0;
    deadTimer = 0;
    health = 0;
    System.out.println(getClass().getSimpleName() + " mort instantanément (zone de mort) !");
}
    /**
     * Met à jour l'ennemi.
     * 
     * @param delta Le temps écoulé
     */
    public void update(float delta) {
        if (isDead) {
            deadTimer -= delta;
            updateAnimation(delta);
            return;
        }
        
        if (attackTimer > 0) {
            attackTimer -= delta;
            if (attackTimer <= 0) {
                isAttacking = false;
                hasDealtDamageThisAttack = false;
            }
        }
        
        if (isHit) {
            hitTimer -= delta;
            if (hitTimer <= 0) {
                isHit = false;
            }
        }
        
        updateAI(delta);
        applyPhysics(delta);
        updateAnimation(delta);
        updateHitbox();
    }
    
    /**
     * Applique la physique à l'ennemi.
     * 
     * @param delta Le temps écoulé
     */
    protected void applyPhysics(float delta) {
        if (collisionRects == null) return;
        
        if (!isOnGround) {
            velocityY += GRAVITY * delta;
            if (velocityY < TERMINAL_VELOCITY) {
                velocityY = TERMINAL_VELOCITY;
            }
        }
        
        if (isOnGround && currentState != State.CHASE && currentState != State.PATROL) {
            velocityX *= GROUND_FRICTION;
            if (Math.abs(velocityX) < 1f) {
                velocityX = 0;
            }
        }
        
        int subdivisions = 4;
        float subDelta = delta / subdivisions;
        
        for (int i = 0; i < subdivisions; i++) {
            float nextX = x + velocityX * subDelta;
            
            Rectangle testHitbox;
            if (useDirectionalHitbox()) {
                testHitbox = new Rectangle(
                    nextX + getDirectionalHitboxOffsetX(),
                    y + getHitboxOffsetY(),
                    getDirectionalHitboxWidth(),
                    getHitboxHeight()
                );
            } else {
                testHitbox = new Rectangle(
                    nextX + getHitboxOffsetX(),
                    y + getHitboxOffsetY(),
                    getHitboxWidth(),
                    getHitboxHeight()
                );
            }
            
            boolean collisionX = false;
            for (Rectangle rect : collisionRects) {
                if (testHitbox.overlaps(rect)) {
                    collisionX = true;
                    velocityX = 0;
                    
                    if (currentState == State.PATROL) {
                        facingRight = !facingRight;
                        velocityX = facingRight ? patrolSpeed : -patrolSpeed;
                    }
                    break;
                }
            }
            
            if (!collisionX) {
                x = nextX;
            }
            
            float nextY = y + velocityY * subDelta;
            
            if (useDirectionalHitbox()) {
                testHitbox.set(
                    x + getDirectionalHitboxOffsetX(),
                    nextY + getHitboxOffsetY(),
                    getDirectionalHitboxWidth(),
                    getHitboxHeight()
                );
            } else {
                testHitbox.set(
                    x + getHitboxOffsetX(),
                    nextY + getHitboxOffsetY(),
                    getHitboxWidth(),
                    getHitboxHeight()
                );
            }
            
            isOnGround = false;
            for (Rectangle rect : collisionRects) {
                if (testHitbox.overlaps(rect)) {
                    if (velocityY < 0) {
                        isOnGround = true;
                        y = rect.y + rect.height - getHitboxOffsetY();
                    } else {
                        y = rect.y - getHitboxHeight() - getHitboxOffsetY();
                    }
                    velocityY = 0;
                    break;
                }
            }
            
            if (!isOnGround) {
                y = nextY;
            }
        }
    }
    
    /**
     * Met à jour la hitbox de l'ennemi.
     */
    protected void updateHitbox() {
        if (useDirectionalHitbox()) {
            hitbox.set(
                x + getDirectionalHitboxOffsetX(),
                y + getHitboxOffsetY(),
                getDirectionalHitboxWidth(),
                getHitboxHeight()
            );
        } else {
            hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
        }
        
        resolveHitboxCollisions();
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
                    isOnGround = true;
                }
                
                if (useDirectionalHitbox()) {
                    hitbox.set(
                        x + getDirectionalHitboxOffsetX(),
                        y + getHitboxOffsetY(),
                        getDirectionalHitboxWidth(),
                        getHitboxHeight()
                    );
                } else {
                    hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
                }
                
                break;
            }
        }
    }
    
    /**
     * Affiche l'ennemi.
     * 
     * @param batch Le SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        Texture currentTexture = getCurrentTexture();
        
        if (currentTexture == null) {
            return;
        }
        
        if (currentState == State.DEAD) {
            float rotatedWidth = EntityConstants.ENEMY_WIDTH;
            float rotatedHeight = EntityConstants.ENEMY_HEIGHT;
            float originX = rotatedWidth / 2f;
            float originY = rotatedHeight / 2f;
            float rotation = -90f;
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
                batch.draw(currentTexture, x, y, EntityConstants.ENEMY_WIDTH, EntityConstants.ENEMY_HEIGHT);
            } else {
                batch.draw(currentTexture, x + EntityConstants.ENEMY_WIDTH, y, -EntityConstants.ENEMY_WIDTH, EntityConstants.ENEMY_HEIGHT);
            }
        }
    }
    
    /**
     * Affiche la barre de vie de l'ennemi.
     * 
     * @param shapeRenderer Le ShapeRenderer pour le rendu
     * @param camera La caméra du jeu
     */
    public void renderHealthBar(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        if (isDead) return;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        float barX = x + width / 2f;
        float barY = y + height + 10f;
        float barWidth = 80f;
        float barHeight = 8f;
        
        HealthBar.render(shapeRenderer, barX, barY, barWidth, barHeight, health, maxHealth);
        
        shapeRenderer.end();
    }
    
    /**
     * Affiche le debug de l'ennemi (hitbox et portées).
     * 
     * @param shapeRenderer Le ShapeRenderer pour le rendu
     */
    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        
        shapeRenderer.setColor(new Color(1, 1, 0, 0.3f));
        shapeRenderer.circle(x + width/2, y + height/2, detectionRange);
        
        shapeRenderer.setColor(new Color(1, 0.5f, 0, 0.2f));
        shapeRenderer.circle(x + width/2, y + height/2, losePlayerRange);
    }
    
    /**
     * Libère les ressources de l'ennemi.
     */
    public void dispose() {
        disposeTextures();
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getHitbox() { return hitbox; }
    public boolean isDead() { return isDead && deadTimer <= 0; }
    public State getCurrentState() { return currentState; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    public boolean getIsOnGround() { return isOnGround; }
    
    /**
     * Définit la position de l'ennemi.
     * 
     * @param x La nouvelle position X
     * @param y La nouvelle position Y
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateHitbox();
    }
}