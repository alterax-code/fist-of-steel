package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.utils.Constants;
import com.fistofsteel.utils.HitboxDebugger;

/**
 * Gestionnaire centralise de tous les ennemis du niveau
 */
public class EnemyManager {
    
    private Array<Enemy> enemies;
    private Player player;
    
    public EnemyManager(Player player) {
        this.enemies = new Array<>();
        this.player = player;
    }
    
    /**
     * Ajoute un ennemi Knight au niveau
     */
    public void addKnight(float x, float y) {
        Knight knight = new Knight(x, y, player);
        enemies.add(knight);
        System.out.println("⚔️ Knight ajoute a (" + (int)x + ", " + (int)y + ")");
    }
    
    /**
     * Ajoute un ennemi Knight avec zone de patrouille personnalisee
     */
    public void addKnight(float x, float y, float patrolMin, float patrolMax) {
        Knight knight = new Knight(x, y, player);
        knight.setPatrolZone(patrolMin, patrolMax);
        enemies.add(knight);
        System.out.println("⚔️ Knight ajoute a (" + (int)x + ", " + (int)y + ") | Patrol: [" + 
                          (int)patrolMin + " -> " + (int)patrolMax + "]");
    }
    
    /* ⭐ EXEMPLE : Comment ajouter un nouveau type d'ennemi
     * 
     * Pour ajouter un Goblin, copie ces méthodes et change "Knight" par "Goblin" :
     * 
     * public void addGoblin(float x, float y) {
     *     Goblin goblin = new Goblin(x, y, player);
     *     enemies.add(goblin);
     *     System.out.println("👹 Goblin ajoute a (" + (int)x + ", " + (int)y + ")");
     * }
     * 
     * public void addGoblin(float x, float y, float patrolMin, float patrolMax) {
     *     Goblin goblin = new Goblin(x, y, player);
     *     goblin.setPatrolZone(patrolMin, patrolMax);
     *     enemies.add(goblin);
     *     System.out.println("👹 Goblin ajoute a (" + (int)x + ", " + (int)y + ")");
     * }
     * 
     * Ensuite dans loadEnemiesFromTiled(), ajoute :
     * 
     * } else if ("Goblin".equalsIgnoreCase(enemyType)) {
     *     if (patrolMinObj != null && patrolMaxObj != null) {
     *         enemyManager.addGoblin(libgdxX, libgdxY, libgdxX + patrolMinObj, libgdxX + patrolMaxObj);
     *     } else {
     *         enemyManager.addGoblin(libgdxX, libgdxY);
     *     }
     *     enemyCount++;
     * }
     */
    
    /**
     * Definit les rectangles de collision pour tous les ennemis
     */
    public void setCollisionRects(Array<Rectangle> collisions) {
        for (Enemy enemy : enemies) {
            enemy.setCollisionRects(collisions);
        }
    }
    
    /**
     * Met a jour tous les ennemis
     */
    public void update(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }
    }
    
    /**
     * Affiche tous les ennemis
     */
    public void render(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
    }
    
    /**
     * Verifie si un ennemi touche le joueur avec son attaque
     */
    public void checkEnemyAttacks(Player player) {
        for (Enemy enemy : enemies) {
            if (enemy.canHitPlayer() && !player.isDead()) {
                System.out.println("💥 Le joueur est touche par " + enemy.getClass().getSimpleName() + 
                                 " (-" + enemy.getDamage() + " HP)");
            }
        }
    }
    
    /**
     * Verifie si le joueur touche un ennemi avec son attaque
     */
    public void checkPlayerAttack(Player player) {
        if (!player.isAttacking()) return;
        
        Rectangle playerHitbox = player.getHitbox();
        
        float attackRange = 80f;
        float attackWidth = attackRange;
        float attackHeight = playerHitbox.height;
        
        float attackX = player.getX();
        
        Rectangle attackBox = new Rectangle(attackX, player.getY(), attackWidth, attackHeight);
        
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) continue;
            
            if (attackBox.overlaps(enemy.getHitbox())) {
                enemy.takeDamage(10);
                System.out.println("💥 " + enemy.getClass().getSimpleName() + 
                                 " touche ! HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
            }
        }
    }
    
    /**
     * Supprime les ennemis morts
     */
    public void removeDeadEnemies() {
        Array<Enemy> toRemove = new Array<>();
        
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
            }
        }
        
        for (Enemy enemy : toRemove) {
            enemies.removeValue(enemy, true);
            enemy.dispose();
        }
    }
    
    /**
     * Nombre d'ennemis vivants
     */
    public int getAliveCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Nombre total d'ennemis
     */
    public int getTotalCount() {
        return enemies.size;
    }
    
    /**
     * DEBUG : Affiche les hitbox des ennemis (F3)
     * Rouge = hitbox, Jaune = sprite complet, Vert = centre
     */
    public void renderDebugHitboxes(OrthographicCamera camera) {
        for (Enemy enemy : enemies) {
            HitboxDebugger.renderEnemyHitbox(enemy, camera);
        }
    }
    
    /**
     * Nettoyage
     */
    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
    }
}