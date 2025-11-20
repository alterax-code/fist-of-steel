package com.fistofsteel.entities.enemies;

import com.fistofsteel.entities.player.Player;  // ✅ AJOUT
import com.fistofsteel.entities.managers.ProjectileManager;  // ✅ AJOUT
import com.fistofsteel.entities.projectiles.MageProjectile;  // ✅ AJOUT
import com.fistofsteel.utils.EntityConstants;

// ... reste du code inchangé

/**
 * Ennemi Mage - VERSION SIMPLIFIÉE avec projectiles
 */
public class Mage extends Enemy {
    
    private ProjectileManager projectileManager;
    private boolean hasShot = false;

    public Mage(float x, float y, Player targetPlayer) {
        super(x, y, targetPlayer);
    }
    
    public Mage(float x, float y, Player targetPlayer, float patrolMin, float patrolMax) {
        super(x, y, targetPlayer);
        setPatrolZone(patrolMin, patrolMax);
    }
    
    public void setProjectileManager(ProjectileManager manager) {
        this.projectileManager = manager;
    }

    @Override
    protected String getEnemyName() {
        return "Mage";
    }

    @Override
    protected void initStats() {
        this.maxHealth = 40;
        this.health = maxHealth;
        this.damage = 15;
        this.detectionRange = 500f;
        this.attackRange = 400f;
        this.attackCooldown = 3.0f;
    }
    
    @Override
    protected void attack() {
        super.attack();
        hasShot = false;
    }
    
    /**
     * ⭐ HOOK appelé pendant l'animation d'attaque
     */
    @Override
    protected void onAttackFrame(int frame) {
        if (frame == 5 && !hasShot) {
            shootProjectile();
            hasShot = true;
        }
    }
    
    private void shootProjectile() {
        if (projectileManager == null) {
            System.err.println("⚠️ Mage ne peut pas tirer : ProjectileManager null !");
            return;
        }
        
        float projectileX = x + width / 2f;
        float projectileY = y + height / 2f;
        
        MageProjectile projectile = new MageProjectile(projectileX, projectileY, facingRight, damage);
        projectileManager.addProjectile(projectile);
        
        System.out.println("🔥 Mage tire un projectile à (" + (int)projectileX + ", " + (int)projectileY + ")");
    }
    
    @Override
    protected boolean canHitPlayer() {
        com.badlogic.gdx.math.Rectangle playerHitbox = targetPlayer.getHitbox();
        float distance = Math.abs(playerHitbox.x - hitbox.x);
        float verticalDistance = Math.abs(playerHitbox.y - hitbox.y);
        
        return distance <= attackRange && distance >= 100f && verticalDistance <= 80f;
    }
    
    @Override
    public void tryDealDamage() {
        // Le Mage ne fait pas de dégâts directs
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