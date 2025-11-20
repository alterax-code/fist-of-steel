package com.fistofsteel.entities.managers;  // ✅ MODIFIÉ (était com.fistofsteel.entities)

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;  // ✅ AJOUT
import com.fistofsteel.entities.enemies.Enemy;  // ✅ AJOUT
import com.fistofsteel.entities.enemies.Knight;  // ✅ AJOUT
import com.fistofsteel.entities.enemies.Mage;  // ✅ AJOUT
import com.fistofsteel.entities.enemies.Rogue;  // ✅ AJOUT
import com.fistofsteel.utils.HitboxDebugger;

// ... reste du code inchangé

public class EnemyManager {

    private Array<Enemy> enemies;
    private Player player;
    
    private int enemiesKilled = 0;
    private int totalEnemiesSpawned = 0;
    
    private ProjectileManager projectileManager;

    public EnemyManager(Player player) {
        this.enemies = new Array<>();
        this.player = player;
    }
    
    /**
     * Définit le gestionnaire de projectiles
     */
    public void setProjectileManager(ProjectileManager manager) {
        this.projectileManager = manager;
        
        // Donner la référence à tous les Mages existants
        for (Enemy enemy : enemies) {
            if (enemy instanceof Mage) {
                ((Mage) enemy).setProjectileManager(manager);
            }
        }
    }

    // ----------- KNIGHT -----------

    public void addKnight(float x, float y) {
        Knight knight = new Knight(x, y, player);
        enemies.add(knight);
        totalEnemiesSpawned++;
        System.out.println("⚔️ Knight ajouté à (" + (int)x + ", " + (int)y + ")");
    }

    public void addKnight(float x, float y, float patrolMin, float patrolMax) {
        Knight knight = new Knight(x, y, player);
        knight.setPatrolZone(patrolMin, patrolMax);
        enemies.add(knight);
        totalEnemiesSpawned++;
        System.out.println("⚔️ Knight ajouté à (" + (int)x + ", " + (int)y + ") | Patrol: [" +
                (int)patrolMin + " -> " + (int)patrolMax + "]");
    }

    // ----------- MAGE -----------

    public void addMage(float x, float y) {
        Mage mage = new Mage(x, y, player);
        if (projectileManager != null) {
            mage.setProjectileManager(projectileManager);
        }
        enemies.add(mage);
        totalEnemiesSpawned++;
        System.out.println("🧙 Mage ajouté à (" + (int)x + ", " + (int)y + ")");
    }

    public void addMage(float x, float y, float patrolMin, float patrolMax) {
        Mage mage = new Mage(x, y, player);
        mage.setPatrolZone(patrolMin, patrolMax);
        if (projectileManager != null) {
            mage.setProjectileManager(projectileManager);
        }
        enemies.add(mage);
        totalEnemiesSpawned++;
        System.out.println("🧙 Mage ajouté à (" + (int)x + ", " + (int)y + 
                ") | Patrol: [" + (int)patrolMin + " -> " + (int)patrolMax + "]");
    }

    // ----------- ROGUE -----------

    public void addRogue(float x, float y) {
        Rogue rogue = new Rogue(x, y, player);
        enemies.add(rogue);
        totalEnemiesSpawned++;
        System.out.println("🗡️ Rogue ajouté à (" + (int)x + ", " + (int)y + ")");
    }

    public void addRogue(float x, float y, float patrolMin, float patrolMax) {
        Rogue rogue = new Rogue(x, y, player);
        rogue.setPatrolZone(patrolMin, patrolMax);
        enemies.add(rogue);
        totalEnemiesSpawned++;
        System.out.println("🗡️ Rogue ajouté à (" + (int)x + ", " + (int)y + 
                ") | Patrol: [" + (int)patrolMin + " -> " + (int)patrolMax + "]");
    }

    // ----------- COLLISIONS & UPDATE -----------

    public void setCollisionRects(Array<Rectangle> collisions) {
        for (Enemy enemy : enemies) {
            enemy.setCollisionRects(collisions);
        }
    }

    public void update(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
    }
    
    public void renderHealthBars(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        for (Enemy enemy : enemies) {
            enemy.renderHealthBar(shapeRenderer, camera);
        }
    }

    // ----------- COMBATS -----------

    /**
     * Vérifie les attaques des ennemis sur le joueur
     */
    public void checkEnemyAttacks(Player player) {
        for (Enemy enemy : enemies) {
            enemy.tryDealDamage();
        }
    }

    /**
     * ⭐ MODIFIÉ : Vérifie les attaques du joueur (corps-à-corps uniquement)
     * Les attaques à distance sont gérées par ProjectileManager.checkEnemyCollisions()
     */
    public void checkPlayerAttack(Player player) {
        // ⭐ Si le joueur attaque à distance, ignorer le corps-à-corps
        if (player.isRangedAttacker()) {
            return; // Hugo n'inflige pas de dégâts directs
        }
        
        // Corps-à-corps pour Alexis (ou autres personnages mêlée)
        if (!player.isAttacking()) return;
        if (player.hasDealtDamageThisAttack()) return;

        Rectangle playerHitbox = player.getHitbox();
        Rectangle attackBox = new Rectangle(player.getX(), player.getY(), 80f, playerHitbox.height);

        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && attackBox.overlaps(enemy.getHitbox())) {
                int damage = player.getTotalAttack();
                enemy.takeDamage(damage);
                player.markDamageDealt();
                System.out.println("💥 " + enemy.getClass().getSimpleName() +
                        " touché ! (-" + damage + " HP) | HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
                break;
            }
        }
    }

    // ----------- CLEANUP -----------

    public void removeDeadEnemies() {
        Array<Enemy> toRemove = new Array<>();
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
                enemiesKilled++;
            }
        }

        for (Enemy enemy : toRemove) {
            enemies.removeValue(enemy, true);
            enemy.dispose();
        }
    }

    public int getAliveCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) count++;
        }
        return count;
    }

    public int getTotalCount() {
        return enemies.size;
    }
    
    public int getEnemiesKilled() {
        return enemiesKilled;
    }
    
    public int getTotalEnemiesSpawned() {
        return totalEnemiesSpawned;
    }
    
    /**
     * ⭐ NOUVEAU : Getter pour accéder à la liste des ennemis
     * (nécessaire pour ProjectileManager.checkEnemyCollisions)
     */
    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public void renderDebugHitboxes(OrthographicCamera camera) {
        for (Enemy enemy : enemies) {
            HitboxDebugger.renderEnemyHitbox(enemy, camera);
        }
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
    }
}