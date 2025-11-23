package com.fistofsteel.entities.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.entities.projectiles.Projectile;
import com.fistofsteel.entities.projectiles.HugoProjectile;
import com.fistofsteel.entities.projectiles.MageProjectile;
import com.fistofsteel.entities.enemies.Enemy;

/**
 * Gestionnaire de tous les projectiles du jeu.
 * Gère les projectiles du joueur et des ennemis.
 */
public class ProjectileManager {
    
    private Array<Projectile> projectiles;
    private float mapWidth;
    
    /**
     * Constructeur du gestionnaire de projectiles.
     * 
     * @param mapWidth La largeur de la map
     */
    public ProjectileManager(float mapWidth) {
        this.projectiles = new Array<>();
        this.mapWidth = mapWidth;
    }
    
    /**
     * Ajoute un projectile.
     * 
     * @param projectile Le projectile à ajouter
     */
    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }
    
    /**
     * Met à jour tous les projectiles.
     * 
     * @param delta Le temps écoulé
     */
    public void update(float delta) {
        for (Projectile projectile : projectiles) {
            if (projectile.isActive()) {
                projectile.update(delta);
                
                if (projectile.isOffScreen(mapWidth)) {
                    projectile.deactivate();
                }
            }
        }
    }
    
    /**
     * Affiche tous les projectiles actifs.
     * 
     * @param batch Le SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            if (projectile.isActive()) {
                projectile.render(batch);
            }
        }
    }
    
    /**
     * Vérifie les collisions avec le joueur (projectiles ennemis).
     * 
     * @param player Le joueur
     */
    public void checkPlayerCollisions(Player player) {
        if (player.isDead()) return;
        
        Rectangle playerHitbox = player.getHitbox();
        
        for (Projectile projectile : projectiles) {
            if (projectile instanceof MageProjectile && projectile.isActive() && 
                !projectile.hasDealtDamage() && projectile.getHitbox().overlaps(playerHitbox)) {
                
                player.applyDamage(projectile.getDamage());
                projectile.markDamageDealt();
                projectile.deactivate();
                
                System.out.println("Projectile ennemi touche le joueur ! -" + projectile.getDamage() + " HP");
            }
        }
    }
    
    /**
     * Vérifie les collisions avec les ennemis (projectiles joueur).
     * 
     * @param enemyManager Le gestionnaire d'ennemis
     */
    public void checkEnemyCollisions(EnemyManager enemyManager) {
        for (Projectile projectile : projectiles) {
            if (projectile instanceof HugoProjectile && projectile.isActive() && !projectile.hasDealtDamage()) {
                
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (!enemy.isDead() && projectile.getHitbox().overlaps(enemy.getHitbox())) {
                        
                        enemy.takeDamage(projectile.getDamage());
                        
                        projectile.markDamageDealt();
                        projectile.deactivate();
                        
                        System.out.println("Projectile Hugo touche " + enemy.getClass().getSimpleName() + " ! -" + 
                                         projectile.getDamage() + " HP | HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Nettoie les projectiles inactifs.
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
     * Compte les projectiles actifs.
     * 
     * @return Le nombre de projectiles actifs
     */
    public int getActiveCount() {
        int count = 0;
        for (Projectile projectile : projectiles) {
            if (projectile.isActive()) count++;
        }
        return count;
    }
    
    /**
     * Libère les ressources.
     */
    public void dispose() {
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
        projectiles.clear();
    }
}