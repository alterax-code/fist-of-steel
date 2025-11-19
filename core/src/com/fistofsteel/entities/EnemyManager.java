package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.utils.HitboxDebugger;

public class EnemyManager {

    private Array<Enemy> enemies;
    private Player player;
    
    // ⭐ NOUVEAU : Compteur de kills
    private int enemiesKilled = 0;
    private int totalEnemiesSpawned = 0;

    public EnemyManager(Player player) {
        this.enemies = new Array<>();
        this.player = player;
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
        enemies.add(mage);
        totalEnemiesSpawned++;
        System.out.println("🧙 Mage ajouté à (" + (int)x + ", " + (int)y + ")");
    }

    public void addMage(float x, float y, float patrolMin, float patrolMax) {
        Mage mage = new Mage(x, y, player);
        mage.setPatrolZone(patrolMin, patrolMax);
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
    
    /**
     * ⭐ NOUVEAU : Rendu des barres de vie des ennemis
     */
    public void renderHealthBars(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        for (Enemy enemy : enemies) {
            enemy.renderHealthBar(shapeRenderer, camera);
        }
    }

    // ----------- COMBATS -----------

    /**
     * ⭐ CORRIGÉ : Vérifie si les ennemis peuvent infliger des dégâts
     * Chaque ennemi gère son propre flag "hasDealtDamageThisAttack"
     */
    public void checkEnemyAttacks(Player player) {
        for (Enemy enemy : enemies) {
            enemy.tryDealDamage(); // ⭐ Nouvelle méthode qui gère le one-hit-per-attack
        }
    }

    public void checkPlayerAttack(Player player) {
        if (!player.isAttacking()) return;

        Rectangle playerHitbox = player.getHitbox();
        Rectangle attackBox = new Rectangle(player.getX(), player.getY(), 80f, playerHitbox.height);

        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && attackBox.overlaps(enemy.getHitbox())) {
                enemy.takeDamage(10);
                System.out.println("💥 " + enemy.getClass().getSimpleName() +
                        " touché ! HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
            }
        }
    }

    // ----------- CLEANUP -----------

    public void removeDeadEnemies() {
        Array<Enemy> toRemove = new Array<>();
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
                enemiesKilled++; // ⭐ Incrémenter le compteur de kills
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
    
    /**
     * ⭐ NOUVEAU : Récupère le nombre d'ennemis tués
     */
    public int getEnemiesKilled() {
        return enemiesKilled;
    }
    
    /**
     * ⭐ NOUVEAU : Récupère le nombre total d'ennemis spawnés
     */
    public int getTotalEnemiesSpawned() {
        return totalEnemiesSpawned;
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