package com.fistofsteel.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.utils.EntityConstants;

public class Boss extends Enemy {

    private Texture[] idleTextures;
    private Texture[] walkTextures;
    private Texture[] attackTextures;

    private static final float BOSS_WIDTH  = EntityConstants.ENEMY_WIDTH * 2.5f;
    private static final float BOSS_HEIGHT = EntityConstants.ENEMY_HEIGHT * 2.5f;

    public Boss(float x, float y, Player targetPlayer) {
        super(x, y, targetPlayer);
        this.patrolSpeed = 0f; // Le boss ne patrouille pas
    }

    // -------- STATS --------
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

    // -------- LOAD TEXTURES --------
    @Override
    protected void loadTextures() {
        try {
            System.out.println("👑 Chargement des sprites du Boss...");

            idleTextures = new Texture[]{
                new Texture("assets/sprites/marvin/Idle/idle_1.png"),
                new Texture("assets/sprites/marvin/Idle/idle_2.png"),
                new Texture("assets/sprites/marvin/Idle/idle_3.png"),
                new Texture("assets/sprites/marvin/Idle/idle_4.png"),
                new Texture("assets/sprites/marvin/Idle/idle_5.png"),
                new Texture("assets/sprites/marvin/Idle/idle_6.png"),
                new Texture("assets/sprites/marvin/Idle/idle_7.png"),
                new Texture("assets/sprites/marvin/Idle/idle_8.png"),
                new Texture("assets/sprites/marvin/Idle/idle_9.png"),
                new Texture("assets/sprites/marvin/Idle/idle_10.png"),
                new Texture("assets/sprites/marvin/Idle/idle_11.png"),
                new Texture("assets/sprites/marvin/Idle/idle_12.png"),
                new Texture("assets/sprites/marvin/Idle/idle_13.png"),
                new Texture("assets/sprites/marvin/Idle/idle_14.png"),
                new Texture("assets/sprites/marvin/Idle/idle_15.png"),
                new Texture("assets/sprites/marvin/Idle/idle_16.png")
            };

            walkTextures = new Texture[]{
                new Texture("assets/sprites/marvin/Walk/walk_1.png"),
                new Texture("assets/sprites/marvin/Walk/walk_2.png"),
                new Texture("assets/sprites/marvin/Walk/walk_3.png"),
                new Texture("assets/sprites/marvin/Walk/walk_4.png"),
                new Texture("assets/sprites/marvin/Walk/walk_5.png"),
                new Texture("assets/sprites/marvin/Walk/walk_6.png"),
                new Texture("assets/sprites/marvin/Walk/walk_7.png"),
                new Texture("assets/sprites/marvin/Walk/walk_8.png"),
                new Texture("assets/sprites/marvin/Walk/walk_9.png"),
                new Texture("assets/sprites/marvin/Walk/walk_10.png"),
                new Texture("assets/sprites/marvin/Walk/walk_11.png"),
                new Texture("assets/sprites/marvin/Walk/walk_12.png")
            };

            attackTextures = new Texture[]{
                new Texture("assets/sprites/marvin/atk_1/atk_1_1.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_2.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_3.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_4.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_5.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_6.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_7.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_8.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_9.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_10.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_11.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_12.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_13.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_14.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_15.png"),
                new Texture("assets/sprites/marvin/atk_1/atk_1_16.png")
            };

        } catch (Exception e) {
            System.err.println("❌ Erreur sprites Boss, fallback Knight");

            try {
                idleTextures = new Texture[]{
                    new Texture("assets/sprites/sbires/Knight/Idle/idle1.png")
                };
                walkTextures = new Texture[]{
                    new Texture("assets/sprites/sbires/Knight/Walk/walk1.png")
                };
                attackTextures = walkTextures;
            } catch (Exception ignored) {}
        }
    }

    // -------- DISPOSE --------
    @Override
    protected void disposeTextures() {
        if (idleTextures != null) for (Texture t : idleTextures) t.dispose();
        if (walkTextures != null) for (Texture t : walkTextures) t.dispose();
        if (attackTextures != null && attackTextures != walkTextures)
            for (Texture t : attackTextures) t.dispose();
    }

    // -------- IA --------
    @Override
    protected void updateAI(float delta) {
        if (isHit || isAttacking) {
            velocityX = 0;
            return;
        }

        float dx = Math.abs(targetPlayer.getX() - x);
        float dy = Math.abs(targetPlayer.getY() - y);

        // Attaque
        if (dx <= attackRange && dy <= 100f && attackTimer <= 0) {
            attack();
            return;
        }

        // Chase
        if (dx <= detectionRange && dy <= 250f) {
            chase();
            return;
        }

        // Idle
        currentState = State.IDLE;
        velocityX = 0;
    }

    // -------- ANIMATION --------
    private Texture getCurrentTexture() {
        switch (currentState) {
            case IDLE:
                return idleTextures[idleFrame % idleTextures.length];

            case CHASE:
            case PATROL:
                return walkTextures[walkFrame % walkTextures.length];

            case ATTACK:
                return attackTextures[attackFrame % attackTextures.length];

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
                if (animationTimer >= 0.2f) {
                    idleFrame = (idleFrame + 1) % idleTextures.length;
                    animationTimer = 0f;
                }
                break;

            case CHASE:
                if (animationTimer >= 0.12f) {
                    walkFrame = (walkFrame + 1) % walkTextures.length;
                    animationTimer = 0f;
                }
                break;

            case ATTACK:
                if (animationTimer >= 0.15f) {
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

        float w = BOSS_WIDTH;
        float h = BOSS_HEIGHT;

        /*
         * ⚠️ IMPORTANT :
         * Tes sprites de Marvin regardent vers la GAUCHE par défaut.
         * Donc :
         * - facingRight → flip horizontal
         * - !facingRight → normal
         */

        if (facingRight) {
            // Flip horizontal = largeur négative
            batch.draw(
                tex,
                x + w,  // décalage pour compenser le flip
                y,
                -w,     // largeur négative = flip
                h
            );
        } else {
            // Sprite normal (vers la gauche)
            batch.draw(
                tex,
                x,
                y,
                w,
                h
            );
        }
    }

    // -------- COUNTS --------
    @Override
    protected int getWalkFrameCount() { return walkTextures.length; }
    
    @Override
    protected int getAttackFrameCount() { return attackTextures.length; }
    
    @Override
    protected int getDeadFrameCount() { return 1; }

    @Override
    protected float getHitboxWidth() { return EntityConstants.ENEMY_HITBOX_WIDTH * 1.4f; }
    
    @Override
    protected float getHitboxHeight() { return EntityConstants.ENEMY_HITBOX_HEIGHT * 1.3f; }
    
    @Override
    protected float getHitboxOffsetX() { return EntityConstants.ENEMY_HITBOX_OFFSET_X; }
    
    @Override
    protected float getHitboxOffsetY() { return EntityConstants.ENEMY_HITBOX_OFFSET_Y; }
}