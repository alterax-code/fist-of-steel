package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Texture;
import com.fistofsteel.utils.Constants;

/**
 * Knight - Sbire basique de mêlée
 * Faible HP, attaque rapprochée, patrouille simple
 */
public class Knight extends Enemy {
    private Texture idleTexture;
    private Texture hitTexture;
    
    private Texture[] walkTextures;
    private Texture[] attackTextures;
    private Texture[] deadTextures;
    private Texture[] hurtTextures;

    public Knight(float x, float y, Player targetPlayer) {
        super(x, y, targetPlayer);
    }

    @Override
    protected void initStats() {
        this.maxHealth = 50;
        this.health = maxHealth;
        this.damage = 10;
        this.detectionRange = 400f;  // Détecte le joueur à 400px
        this.attackRange = 80f;      // Attaque à 80px (mêlée)
        this.attackCooldown = 1.2f;  // 1.2s entre chaque attaque
        this.patrolSpeed = 60f;      // Vitesse de patrouille
    }

    @Override
    protected void loadTextures() {
        // Pour l'instant, on réutilise les textures d'Hugo
        // À remplacer par vos propres sprites de Knight
        idleTexture = new Texture("assets/sprites/sbires/Knight/Idle/idle1.png");
        
        deadTextures = new Texture[]{
            new Texture("assets/sprites/sbires/Knight/Death/death1.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death2.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death3.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death4.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death5.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death6.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death7.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death8.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death9.png"),
            new Texture("assets/sprites/sbires/Knight/Death/death10.png"),
        };
        
        attackTextures = new Texture[]{
            new Texture("assets/sprites/sbires/Knight/Attack/attack0.png"),
            new Texture("assets/sprites/sbires/Knight/Attack/attack1.png"),
            new Texture("assets/sprites/sbires/Knight/Attack/attack2.png"),
            new Texture("assets/sprites/sbires/Knight/Attack/attack3.png"),
            new Texture("assets/sprites/sbires/Knight/Attack/attack4.png"),
        };
        
        walkTextures = new Texture[]{
            new Texture("assets/sprites/sbires/Knight/Walk/walk1.png"),
            new Texture("assets/sprites/sbires/Knight/Walk/walk2.png"),
            new Texture("assets/sprites/sbires/Knight/Walk/walk3.png"),
            new Texture("assets/sprites/sbires/Knight/Walk/walk4.png"),
            new Texture("assets/sprites/sbires/Knight/Walk/walk5.png"),
            new Texture("assets/sprites/sbires/Knight/Walk/walk6.png"),
        };

        hurtTextures = new Texture[]{
            new Texture("assets/sprites/sbires/Knight/Hurt/hurt1.png"),
            new Texture("assets/sprites/sbires/Knight/Hurt/hurt2.png"),
            new Texture("assets/sprites/sbires/Knight/Hurt/hurt3.png"),
            new Texture("assets/sprites/sbires/Knight/Hurt/hurt4.png"),
        };
    }

    @Override
    protected void disposeTextures() {
        idleTexture.dispose();
        hitTexture.dispose();
        
        for (Texture t : walkTextures) t.dispose();
        for (Texture t : attackTextures) t.dispose();
        for (Texture t : deadTextures) t.dispose();
        for (Texture t : hurtTextures) t.dispose();
    }

    @Override
    protected Texture getCurrentTexture() {
        switch (currentState) {
            case PATROL:
            case CHASE:
                return walkTextures[walkFrame];
            case ATTACK:
                return attackTextures[attackFrame];
            case HIT:
                return hitTexture;
            case DEAD:
                return deadTextures[deadFrame];
            default:
                return idleTexture;
        }
    }

    @Override
    protected int getWalkFrameCount() {
        return walkTextures.length;
    }

    @Override
    protected int getAttackFrameCount() {
        return attackTextures.length;
    }

    @Override
    protected int getDeadFrameCount() {
        return deadTextures.length;
    }

    // ===== HITBOX DU Knight =====
    @Override
    protected float getHitboxWidth() {
        return 60f;  // Similaire à Hugo
    }

    @Override
    protected float getHitboxHeight() {
        return 100f;
    }

    @Override
    protected float getHitboxOffsetX() {
        return (Constants.PLAYER_WIDTH - getHitboxWidth()) / 2f;
    }

    @Override
    protected float getHitboxOffsetY() {
        return 10f;
    }
}