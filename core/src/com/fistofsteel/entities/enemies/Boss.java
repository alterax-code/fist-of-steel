package com.fistofsteel.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.utils.EntityConstants;

/**
 * Boss "Marvin" - Ennemi spécial avec système d'animation personnalisé.
 * Sprites: assets/sprites/marvin/ (Idle, Walk, atk_1).
 */
public class Boss extends Enemy {

    private Texture[] idleTextures;
    private Texture[] walkTextures;
    private Texture[] attackTextures;

    private static final float BOSS_WIDTH  = EntityConstants.ENEMY_WIDTH * 2.5f;
    private static final float BOSS_HEIGHT = EntityConstants.ENEMY_HEIGHT * 2.5f;

    /**
     * Constructeur du Boss.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param targetPlayer Le joueur ciblé
     */
    public Boss(float x, float y, Player targetPlayer) {
        super(x, y, targetPlayer);
        this.patrolSpeed = 0f;
    }

    @Override
    protected String getEnemyName() {
        return "Boss";
    }

    @Override
    protected void initStats() {
        this.maxHealth = 400;
        this.health = maxHealth;
        this.damage = 30;

        this.detectionRange = 600f;
        this.losePlayerRange = 800f;
        this.attackRange = 110f;

        this.chaseSpeed = 60f;
    }

    @Override
    protected void loadTextures() {
        try {
            System.out.println("Chargement des sprites du Boss (Marvin)...");

            idleTextures = new Texture[16];
            for (int i = 0; i < 16; i++) {
                String path = "assets/sprites/marvin/Idle/idle_" + (i + 1) + ".png";
                if (Gdx.files.internal(path).exists()) {
                    idleTextures[i] = new Texture(path);
                } else {
                    System.err.println("Fichier manquant : " + path);
                    idleTextures[i] = idleTextures[0];
                }
            }

            walkTextures = new Texture[12];
            for (int i = 0; i < 12; i++) {
                String path = "assets/sprites/marvin/Walk/walk_" + (i + 1) + ".png";
                if (Gdx.files.internal(path).exists()) {
                    walkTextures[i] = new Texture(path);
                } else {
                    System.err.println("Fichier manquant : " + path);
                    walkTextures[i] = walkTextures[0];
                }
            }

            attackTextures = new Texture[16];
            for (int i = 0; i < 16; i++) {
                String path = "assets/sprites/marvin/atk_1/atk_1_" + (i + 1) + ".png";
                if (Gdx.files.internal(path).exists()) {
                    attackTextures[i] = new Texture(path);
                } else {
                    System.err.println("Fichier manquant : " + path);
                    attackTextures[i] = attackTextures[0];
                }
            }

            System.out.println("Boss : " + idleTextures.length + " idle, " + 
                             walkTextures.length + " walk, " + 
                             attackTextures.length + " attack frames chargees");

        } catch (Exception e) {
            System.err.println("Erreur critique chargement sprites Boss : " + e.getMessage());
            e.printStackTrace();
            
            if (idleTextures == null) idleTextures = new Texture[1];
            if (walkTextures == null) walkTextures = new Texture[1];
            if (attackTextures == null) attackTextures = new Texture[1];
        }
    }

    @Override
    protected void disposeTextures() {
        if (idleTextures != null) {
            for (Texture t : idleTextures) {
                if (t != null) t.dispose();
            }
        }
        if (walkTextures != null) {
            for (Texture t : walkTextures) {
                if (t != null) t.dispose();
            }
        }
        if (attackTextures != null && attackTextures != walkTextures) {
            for (Texture t : attackTextures) {
                if (t != null) t.dispose();
            }
        }
    }

    @Override
    protected void updateAI(float delta) {
        if (isHit || isAttacking) {
            velocityX = 0;
            return;
        }

        float dx = Math.abs(targetPlayer.getX() - x);
        float dy = Math.abs(targetPlayer.getY() - y);

        if (dx <= attackRange && dy <= 100f && attackTimer <= 0) {
            attack();
            return;
        }

        if (dx <= detectionRange && dy <= 250f) {
            chase();
            return;
        }

        currentState = State.IDLE;
        velocityX = 0;
    }

    @Override
    protected Texture getCurrentTexture() {
        if (idleTextures == null || idleTextures.length == 0 || idleTextures[0] == null) {
            return null;
        }
        
        switch (currentState) {
            case IDLE:
                return idleTextures[idleFrame % idleTextures.length];

            case CHASE:
            case PATROL:
                if (walkTextures != null && walkTextures.length > 0 && walkTextures[0] != null) {
                    return walkTextures[walkFrame % walkTextures.length];
                }
                return idleTextures[0];

            case ATTACK:
                if (attackTextures != null && attackTextures.length > 0 && attackTextures[0] != null) {
                    return attackTextures[attackFrame % attackTextures.length];
                }
                return idleTextures[0];

            case HIT:
                return idleTextures[0];

            case DEAD:
                return idleTextures[0];

            default:
                return idleTextures[0];
        }
    }

    @Override
    protected void updateAnimation(float delta) {
        animationTimer += delta;

        switch (currentState) {
            case IDLE:
                if (animationTimer >= 0.1f && idleTextures != null) {
                    idleFrame = (idleFrame + 1) % idleTextures.length;
                    animationTimer = 0f;
                }
                break;

            case CHASE:
                if (animationTimer >= 0.12f && walkTextures != null) {
                    walkFrame = (walkFrame + 1) % walkTextures.length;
                    animationTimer = 0f;
                }
                break;

            case ATTACK:
                if (animationTimer >= 0.15f && attackTextures != null) {
                    attackFrame = (attackFrame + 1) % attackTextures.length;
                    animationTimer = 0f;
                }
                break;

            case DEAD:
                break;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Texture tex = getCurrentTexture();
        if (tex == null) return;

        if (facingRight) {
            batch.draw(tex, x + BOSS_WIDTH, y, -BOSS_WIDTH, BOSS_HEIGHT);
        } else {
            batch.draw(tex, x, y, BOSS_WIDTH, BOSS_HEIGHT);
        }
    }

    @Override
    protected float getHitboxWidth() {
        return EntityConstants.ENEMY_HITBOX_WIDTH * 2.0f;
    }

    @Override
    protected float getHitboxHeight() {
        return EntityConstants.ENEMY_HITBOX_HEIGHT * 2.0f;
    }

    @Override
    protected float getHitboxOffsetX() {
        return (BOSS_WIDTH - getHitboxWidth()) / 2f;
    }

    @Override
    protected float getHitboxOffsetY() {
        return EntityConstants.ENEMY_HITBOX_OFFSET_Y * 1.5f;
    }
}