package com.fistofsteel.entities.enemies;

import com.fistofsteel.entities.player.Player;
import com.fistofsteel.utils.EntityConstants;

/**
 * Ennemi Rogue - Combattant de mêlée agile.
 */
public class Rogue extends Enemy {

    /**
     * Constructeur du Rogue.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param targetPlayer Le joueur ciblé
     */
    public Rogue(float x, float y, Player targetPlayer) {
        super(x, y, targetPlayer);
    }
    
    /**
     * Constructeur avec zone de patrouille.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param targetPlayer Le joueur ciblé
     * @param patrolMin Distance minimale de patrouille
     * @param patrolMax Distance maximale de patrouille
     */
    public Rogue(float x, float y, Player targetPlayer, float patrolMin, float patrolMax) {
        super(x, y, targetPlayer);
        setPatrolZone(patrolMin, patrolMax);
    }

    @Override
    protected String getEnemyName() {
        return "Rogue";
    }

    @Override
    protected void initStats() {
        this.maxHealth = 50;
        this.health = maxHealth;
        this.damage = 10;
        this.detectionRange = 400f;
        this.attackRange = 80f;
    }

    @Override
    protected float getHitboxWidth() {
        return EntityConstants.ENEMY_HITBOX_WIDTH;
    }

    @Override
    protected float getHitboxHeight() {
        return EntityConstants.ENEMY_HITBOX_HEIGHT;
    }

    @Override
    protected float getHitboxOffsetX() {
        return EntityConstants.ENEMY_HITBOX_OFFSET_X;
    }

    @Override
    protected float getHitboxOffsetY() {
        return EntityConstants.ENEMY_HITBOX_OFFSET_Y;
    }

    @Override
    protected boolean useDirectionalHitbox() {
        return true;
    }
    
    @Override
    protected float getDirectionalHitboxWidth() {
        return EntityConstants.ENEMY_HITBOX_WIDTH * 0.75f;
    }
    
    @Override
    protected float getDirectionalHitboxOffsetX() {
        float fullWidth = EntityConstants.ENEMY_HITBOX_WIDTH;
        float reducedWidth = getDirectionalHitboxWidth();
        float reduction = fullWidth - reducedWidth;
        
        if (facingRight) {
            return EntityConstants.ENEMY_HITBOX_OFFSET_X;
        } else {
            return EntityConstants.ENEMY_HITBOX_OFFSET_X + reduction;
        }
    }
}