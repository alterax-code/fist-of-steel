package com.fistofsteel.entities.managers;  // ✅ MODIFIÉ (était com.fistofsteel.entities)

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;  // ✅ AJOUT
import com.fistofsteel.entities.projectiles.Projectile;  // ✅ AJOUT
import com.fistofsteel.entities.projectiles.HugoProjectile;  // ✅ AJOUT
import com.fistofsteel.entities.projectiles.MageProjectile;  // ✅ AJOUT
import com.fistofsteel.entities.enemies.Enemy;  // ✅ AJOUT

// ... reste du code inchangé

/**
 * Gestionnaire de tous les projectiles du jeu
 */
public class ProjectileManager {
    
    private Array<Projectile> projectiles;
    private float mapWidth;
    
    public ProjectileManager(float mapWidth) {
        this.projectiles = new Array<>();
        this.mapWidth = mapWidth;
    }
    
    /**
     * Ajoute un projectile
     */
    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }
    
    /**
     * Met à jour tous les projectiles
     */
    public void update(float delta) {
        for (Projectile projectile : projectiles) {
            if (projectile.isActive()) {
                projectile.update(delta);
                
                // Désactiver si hors écran
                if (projectile.isOffScreen(mapWidth)) {
                    projectile.deactivate();
                }
            }
        }
    }
    
    /**
     * Dessine tous les projectiles
     */
    public void render(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            if (projectile.isActive()) {
                projectile.render(batch);
            }
        }
    }
    
    /**
     * ⭐ Vérifie les collisions avec le joueur (projectiles ennemis → joueur)
     */
    public void checkPlayerCollisions(Player player) {
        if (player.isDead()) return;
        
        Rectangle playerHitbox = player.getHitbox();
        
        for (Projectile projectile : projectiles) {
            // Ne vérifier que les projectiles ennemis (MageProjectile)
            if (projectile instanceof MageProjectile && projectile.isActive() && 
                !projectile.hasDealtDamage() && projectile.getHitbox().overlaps(playerHitbox)) {
                
                player.applyDamage(projectile.getDamage());
                projectile.markDamageDealt();
                projectile.deactivate();
                
                System.out.println("💥 Projectile ennemi touche le joueur ! -" + projectile.getDamage() + " HP");
            }
        }
    }
    
    /**
     * ⭐ NOUVEAU : Vérifie les collisions avec les ennemis (projectiles joueur → ennemis)
     */
    public void checkEnemyCollisions(EnemyManager enemyManager) {
        for (Projectile projectile : projectiles) {
            // Ne vérifier que les projectiles du joueur (HugoProjectile)
            if (projectile instanceof HugoProjectile && projectile.isActive() && !projectile.hasDealtDamage()) {
                
                // Tester collision avec chaque ennemi
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (!enemy.isDead() && projectile.getHitbox().overlaps(enemy.getHitbox())) {
                        
                        // Appliquer les dégâts
                        enemy.takeDamage(projectile.getDamage());
                        
                        // Marquer le projectile comme ayant infligé des dégâts
                        projectile.markDamageDealt();
                        projectile.deactivate();
                        
                        System.out.println("💥 Projectile Hugo touche " + enemy.getClass().getSimpleName() + " ! -" + 
                                         projectile.getDamage() + " HP | HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Nettoie les projectiles inactifs
     */
    public void removeInactiveProjectiles() {
        Array<Projectile> toRemove = new Array<>();
        
        for (Projectile projectile : projectiles) {
            if (!projectile.isActive()) {
                toRemove.add(projectile);
            }
        }
        
        for (Projectile projectile : toRemove) {
            projectile.dispose();
            projectiles.removeValue(projectile, true);
        }
    }
    
    /**
     * Compte les projectiles actifs
     */
    public int getActiveCount() {
        int count = 0;
        for (Projectile projectile : projectiles) {
            if (projectile.isActive()) count++;
        }
        return count;
    }
    
    /**
     * Nettoyage
     */
    public void dispose() {
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
        projectiles.clear();
    }
}