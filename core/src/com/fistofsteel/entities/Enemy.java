package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.utils.Constants;
import com.fistofsteel.utils.HealthBar;

/**
 * Classe abstraite représentant un ennemi
 * VERSION FINALE CORRIGÉE - Hitbox stable + One-hit-per-attack !
 */
public abstract class Enemy {
    
    // Position et dimensions
    protected float x, y;
    protected float width = Constants.ENEMY_WIDTH;
    protected float height = Constants.ENEMY_HEIGHT;
    protected float velocityX = 0;
    protected float velocityY = 0;
    
    // Point de spawn (référence pour la patrouille au sol)
    protected float spawnX, spawnY;
    
    // ⭐ NOUVELLE LOGIQUE : Position de patrouille (mise à jour quand l'ennemi perd le joueur)
    protected float patrolCenterX;
    protected boolean onGroundLastFrame = false;
    
    // Référence au joueur
    protected Player targetPlayer;
    
    // Stats
    protected int health = 50;
    protected int maxHealth = 50;
    protected int damage = 10;
    
    // IA et déplacements
    protected enum State { 
        IDLE, PATROL, CHASE, ATTACK, HIT, DEAD 
    }
    protected State currentState = State.IDLE;
    
    // ⭐ ZONES DE DÉTECTION ET DISTANCES (avec hysteresis)
    protected float detectionRange = 400f;      // Distance pour COMMENCER à poursuivre
    protected float losePlayerRange = 600f;     // Distance pour ARRÊTER de poursuivre (plus grande !)
    protected float attackRange = 80f;          // Distance pour attaquer
    
    // ⭐ PATROUILLE INTELLIGENTE
    protected float patrolRange = 200f;         // Distance de patrouille de chaque côté
    protected float edgeDetectionDistance = 20f; // Distance pour détecter un bord
    protected float patrolSpeed = 50f;
    protected float chaseSpeed = 75f;
    protected boolean facingRight = true;
    
    // Timers
    protected float attackTimer = 0;
    protected float attackCooldown = 1.5f;
    protected float hitTimer = 0;
    protected float hitDuration = 0.3f;
    protected float deadTimer = 0;
    protected float deadDuration = 2.0f;
    
    // États
    protected boolean isAttacking = false;
    protected boolean isHit = false;
    protected boolean isDead = false;
    protected boolean isOnGround = false;
    
    // ⭐ NOUVEAU : Flag pour éviter les multi-hits
    protected boolean hasDealtDamageThisAttack = false;
    
    // ⭐ Variables d'animation (pour Knight)
    protected float animationTimer = 0f;
    protected float walkFrameDuration = 0.15f;
    protected float attackFrameDuration = 0.15f;
    protected float deadFrameDuration = 0.3f;
    
    protected int walkFrame = 0;
    protected int attackFrame = 0;
    protected int deadFrame = 0;
    protected int hurtFrame = 0;
    protected int idleFrame = 0;
    
    // Physique (comme Player)
    protected static final float GRAVITY = -800f;
    protected static final float TERMINAL_VELOCITY = -500f;
    protected static final float GROUND_FRICTION = 0.9f;
    protected Rectangle hitbox;
    protected Array<Rectangle> collisionRects;
    
    public Enemy(float x, float y, Player targetPlayer) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.spawnY = y;
        this.patrolCenterX = x;  // ⭐ Initialement au spawn
        this.targetPlayer = targetPlayer;
        this.currentState = State.IDLE;
        
        // ⚠️ IMPORTANT : Initialiser les stats AVANT la hitbox
        initStats();
        loadTextures();
        
        // ⭐ CORRECTION : Créer la hitbox avec les bonnes dimensions dès le départ
        this.hitbox = new Rectangle(
            x + getHitboxOffsetX(), 
            y + getHitboxOffsetY(), 
            getHitboxWidth(), 
            getHitboxHeight()
        );
    }
    
    protected abstract void initStats();
    protected abstract void loadTextures();
    protected abstract void disposeTextures();
    
    // ⭐ Méthodes abstraites pour Knight
    protected abstract int getWalkFrameCount();
    protected abstract int getAttackFrameCount();
    protected abstract int getDeadFrameCount();
    protected abstract float getHitboxWidth();
    protected abstract float getHitboxHeight();
    protected abstract float getHitboxOffsetX();
    protected abstract float getHitboxOffsetY();
    
    // ⭐ NOUVEAU : Méthodes pour hitbox directionnelle (optionnel)
    // Surcharger ces méthodes dans les sous-classes qui veulent une hitbox directionnelle
    protected boolean useDirectionalHitbox() {
        return false; // Par défaut, hitbox normale
    }
    
    protected float getDirectionalHitboxWidth() {
        return getHitboxWidth(); // Par défaut, même largeur
    }
    
    protected float getDirectionalHitboxOffsetX() {
        return getHitboxOffsetX(); // Par défaut, même offset
    }
    
    public void setCollisionRects(Array<Rectangle> collisionRects) {
        this.collisionRects = collisionRects;
    }
    
    public void setPatrolZone(float min, float max) {
        // ⭐ Nouvelle logique : définit la distance de patrouille
        this.patrolRange = Math.max(Math.abs(min), Math.abs(max));
        System.out.println("⚙️ Zone de patrouille : ±" + (int)patrolRange + " pixels");
    }
    
    /**
     * ⭐ IA AMÉLIORÉE : Patrouille intelligente sur plateformes
     */
    protected void updateAI(float delta) {
        // Si hit ou en attaque, ne pas bouger
        if (isHit || isAttacking) {
            velocityX = 0;
            return;
        }
        
        // Calculer distances au joueur
        float distanceToPlayerX = Math.abs(targetPlayer.getX() - x);
        float distanceToPlayerY = Math.abs(targetPlayer.getY() - y);
        
        // ⭐ RÈGLE 1 : Si DÉJÀ en CHASE, continuer jusqu'à perdre le joueur
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
                // ⭐ JOUEUR PERDU : Mettre à jour le centre de patrouille ICI
                System.out.println("🔄 Joueur perdu ! Nouvelle zone de patrouille à x=" + (int)x);
                patrolCenterX = x;
                currentState = State.IDLE;
                velocityX = 0;
            }
        }
        
        // ⭐ RÈGLE 2 : Détecter le joueur
        boolean playerDetected = distanceToPlayerX <= detectionRange && distanceToPlayerY <= 200f;
        
        if (playerDetected) {
            if (canHitPlayer() && attackTimer <= 0) {
                attack();
            } else {
                chase();
            }
            return;
        }
        
        // ⭐ RÈGLE 3 : Patrouiller intelligemment
        patrolWithEdgeDetection();
    }
    
    /**
     * ⭐ NOUVELLE MÉTHODE : Patrouille avec détection de bord de plateforme
     */
    protected void patrolWithEdgeDetection() {
        currentState = State.PATROL;
        
        // Vérifier si on est au bord d'une plateforme ou d'un mur
        boolean edgeAhead = isEdgeAhead();
        boolean wallAhead = isWallAhead();
        
        // Calculer la distance depuis le centre de patrouille
        float distanceFromCenter = x - patrolCenterX;
        
        // ⭐ Faire demi-tour si :
        // 1. On détecte un bord devant
        // 2. On détecte un mur devant
        // 3. On est trop loin du centre de patrouille
        if (edgeAhead || wallAhead || Math.abs(distanceFromCenter) > patrolRange) {
            facingRight = !facingRight;
            velocityX = facingRight ? patrolSpeed : -patrolSpeed;
        } else {
            // Continuer dans la direction actuelle
            velocityX = facingRight ? patrolSpeed : -patrolSpeed;
        }
    }
    
    /**
     * ⭐ Détecte si il y a un bord (pas de sol) devant l'ennemi
     */
    protected boolean isEdgeAhead() {
        if (collisionRects == null || !isOnGround) return false;
        
        // Point de test devant l'ennemi (au niveau des pieds)
        float testX = facingRight 
            ? x + getHitboxWidth() + edgeDetectionDistance 
            : x - edgeDetectionDistance;
        
        float testY = y + getHitboxOffsetY() - 5f; // Juste sous les pieds
        
        // Créer un petit rectangle de test
        Rectangle testRect = new Rectangle(testX - 5f, testY - 10f, 10f, 10f);
        
        // Vérifier si ce point touche une plateforme
        for (Rectangle rect : collisionRects) {
            if (testRect.overlaps(rect)) {
                return false; // Il y a du sol, pas de bord
            }
        }
        
        return true; // Pas de sol = bord détecté
    }
    
    /**
     * ⭐ Détecte si il y a un mur devant l'ennemi
     */
    protected boolean isWallAhead() {
        if (collisionRects == null) return false;
        
        // Point de test devant l'ennemi (à hauteur du corps)
        float testX = facingRight 
            ? x + getHitboxWidth() + edgeDetectionDistance 
            : x - edgeDetectionDistance;
        
        float testY = y + getHitboxHeight() / 2f;
        
        // Créer un petit rectangle de test vertical
        Rectangle testRect = new Rectangle(testX - 5f, testY - 20f, 10f, 40f);
        
        // Vérifier si on touche un mur
        for (Rectangle rect : collisionRects) {
            if (testRect.overlaps(rect)) {
                return true; // Mur détecté
            }
        }
        
        return false;
    }
    
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
    
    protected void attack() {
        currentState = State.ATTACK;
        isAttacking = true;
        velocityX = 0;
        
        // ⭐ NOUVEAU : Réinitialiser le flag de dégâts pour cette nouvelle attaque
        hasDealtDamageThisAttack = false;
        
        attackTimer = attackCooldown;
        System.out.println("⚔️ " + getClass().getSimpleName() + " commence une attaque !");
    }
    
    protected boolean canHitPlayer() {
        Rectangle playerHitbox = targetPlayer.getHitbox();
        float distance = Math.abs(playerHitbox.x - hitbox.x);
        float verticalDistance = Math.abs(playerHitbox.y - hitbox.y);
        
        return distance <= attackRange && verticalDistance <= 80f;
    }
    
    /**
     * ⭐ NOUVEAU : Inflige les dégâts au joueur UNE SEULE FOIS par attaque
     * Appelé par EnemyManager à chaque frame
     */
    public void tryDealDamage() {
        // Conditions pour infliger des dégâts :
        // 1. L'ennemi est en train d'attaquer
        // 2. Le joueur est à portée
        // 3. Les dégâts n'ont pas encore été infligés pour cette attaque
        if (isAttacking && canHitPlayer() && !hasDealtDamageThisAttack && !targetPlayer.isDead()) {
            targetPlayer.applyDamage(damage);
            hasDealtDamageThisAttack = true; // ⭐ Marquer les dégâts comme infligés
            System.out.println("💥 " + getClass().getSimpleName() + " touche le joueur ! (-" + damage + " HP)");
        }
    }
    
    public void takeDamage(int damage) {
        if (isDead || isHit) return;
        
        health -= damage;
        System.out.println("💥 " + getClass().getSimpleName() + " touché ! HP: " + health + "/" + maxHealth);
        
        if (health <= 0) {
            die();
        } else {
            isHit = true;
            hitTimer = hitDuration;
            currentState = State.HIT;
            velocityX = 0;
        }
    }
    
    protected void die() {
        isDead = true;
        currentState = State.DEAD;
        velocityX = 0;
        deadTimer = deadDuration;
        System.out.println("💀 " + getClass().getSimpleName() + " mort !");
    }
    
    public void update(float delta) {
        if (isDead) {
            deadTimer -= delta;
            updateAnimation(delta);
            return;
        }
        
        // Timers
        if (attackTimer > 0) {
            attackTimer -= delta;
            if (attackTimer <= 0) {
                isAttacking = false;
                hasDealtDamageThisAttack = false; // ⭐ Réinitialiser pour la prochaine attaque
            }
        }
        
        if (isHit) {
            hitTimer -= delta;
            if (hitTimer <= 0) {
                isHit = false;
            }
        }
        
        // IA
        updateAI(delta);
        
        // Physique
        applyPhysics(delta);
        
        // Animation
        updateAnimation(delta);
        
        // ⭐ Hitbox suit la position
        updateHitbox();
    }
    
    /**
     * ⭐ PHYSIQUE CORRIGÉE - Gestion propre de la hitbox
     */
    protected void applyPhysics(float delta) {
        if (collisionRects == null) return;
        
        // Gravité
        if (!isOnGround) {
            velocityY += GRAVITY * delta;
            if (velocityY < TERMINAL_VELOCITY) {
                velocityY = TERMINAL_VELOCITY;
            }
        }
        
        // Friction au sol
        if (isOnGround && currentState != State.CHASE && currentState != State.PATROL) {
            velocityX *= GROUND_FRICTION;
            if (Math.abs(velocityX) < 1f) {
                velocityX = 0;
            }
        }
        
        // ⭐ Subdivision du mouvement
        int subdivisions = 4;
        float subDelta = delta / subdivisions;
        
        for (int i = 0; i < subdivisions; i++) {
            // ⭐ MOUVEMENT HORIZONTAL
            float nextX = x + velocityX * subDelta;
            
            // Créer une hitbox temporaire pour tester
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
                    
                    // ⭐ En patrouille, faire demi-tour au mur
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
            
            // ⭐ MOUVEMENT VERTICAL
            float nextY = y + velocityY * subDelta;
            
            // Créer une hitbox temporaire pour tester
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
                        // Atterrissage
                        isOnGround = true;
                        y = rect.y + rect.height - getHitboxOffsetY();
                    } else {
                        // Tête qui cogne
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
    
    protected abstract void updateAnimation(float delta);
    
    /**
     * ⭐ Mise à jour de la hitbox - position ET taille (pour hitbox directionnelle)
     */
    protected void updateHitbox() {
        if (useDirectionalHitbox()) {
            // Hitbox directionnelle : largeur et offset changent selon la direction
            hitbox.set(
                x + getDirectionalHitboxOffsetX(),
                y + getHitboxOffsetY(),
                getDirectionalHitboxWidth(),
                getHitboxHeight()
            );
        } else {
            // Hitbox normale : seulement la position change
            hitbox.setPosition(x + getHitboxOffsetX(), y + getHitboxOffsetY());
        }
        
        // ⭐ NOUVEAU : Résoudre les collisions après mise à jour de la hitbox
        resolveHitboxCollisions();
    }
    
    /**
     * ⭐ NOUVEAU : Repousse l'entité si sa hitbox est coincée dans un bloc de collision
     * Calcule la distance minimale pour sortir dans chaque direction (haut, bas, gauche, droite)
     * et repousse l'entité dans la direction la plus courte
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
                } 
                else if (minOverlap == overlapRight) {
                    // Repousser vers la droite
                    float pushDistance = overlapRight + 0.1f;
                    x += pushDistance;
                    velocityX = 0;
                } 
                else if (minOverlap == overlapBottom) {
                    // Repousser vers le bas
                    float pushDistance = overlapBottom + 0.1f;
                    y -= pushDistance;
                    velocityY = 0;
                } 
                else if (minOverlap == overlapTop) {
                    // Repousser vers le haut
                    float pushDistance = overlapTop + 0.1f;
                    y += pushDistance;
                    velocityY = 0;
                    isOnGround = true; // Si on repousse vers le haut, c'est qu'on est sur le sol
                }
                
                // Mettre à jour la hitbox après le déplacement
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
                
                // Vérifier s'il y a encore des collisions après le premier déplacement
                // (nécessaire si l'entité est coincée entre plusieurs blocs)
                break; // On ne traite qu'une collision à la fois pour éviter les comportements étranges
            }
        }
    }
    
    public abstract void render(SpriteBatch batch);
    
    /**
     * ⭐ NOUVEAU : Rendu de la barre de vie au-dessus de l'ennemi
     */
    public void renderHealthBar(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        if (isDead) return; // Ne pas afficher la barre si l'ennemi est mort
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Position au-dessus de la tête de l'ennemi
        float barX = x + width / 2f;
        float barY = y + height + 10f; // 10 pixels au-dessus
        float barWidth = 80f;
        float barHeight = 8f;
        
        HealthBar.render(shapeRenderer, barX, barY, barWidth, barHeight, health, maxHealth);
        
        shapeRenderer.end();
    }
    
    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        
        // Zone de détection
        shapeRenderer.setColor(new Color(1, 1, 0, 0.3f));
        shapeRenderer.circle(x + width/2, y + height/2, detectionRange);
        
        // Zone de perte du joueur
        shapeRenderer.setColor(new Color(1, 0.5f, 0, 0.2f));
        shapeRenderer.circle(x + width/2, y + height/2, losePlayerRange);
    }
    
    public void dispose() {
        disposeTextures();
    }
    
    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getHitbox() { return hitbox; }
    public boolean isDead() { return isDead && deadTimer <= 0; }
    public State getCurrentState() { return currentState; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    
    // ⭐ NOUVELLES MÉTHODES pour stabilisation au sol
    public boolean getIsOnGround() {  // Renommé pour éviter conflit avec variable isOnGround
        return isOnGround;
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateHitbox();
    }
}