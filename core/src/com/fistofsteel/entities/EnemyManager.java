package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.utils.HitboxDebugger;

public class EnemyManager {

    private Array<Enemy> enemies;
    private Player player;

    public EnemyManager(Player player) {
        this.enemies = new Array<>();
        this.player = player;
    }

    // ----------- KNIGHT -----------

    public void addKnight(float x, float y) {
        Knight knight = new Knight(x, y, player);
        enemies.add(knight);
        System.out.println("⚔️ Knight ajouté à (" + (int)x + ", " + (int)y + ")");
    }

    public void addKnight(float x, float y, float patrolMin, float patrolMax) {
        Knight knight = new Knight(x, y, player);
        knight.setPatrolZone(patrolMin, patrolMax);
        enemies.add(knight);
        System.out.println("⚔️ Knight ajouté à (" + (int)x + ", " + (int)y + ") | Patrol: [" +
                (int)patrolMin + " -> " + (int)patrolMax + "]");
    }

    // ----------- MAGE -----------

    public void addMage(float x, float y) {
        Mage mage = new Mage(x, y, player);
        enemies.add(mage);
        System.out.println("👹 Mage ajouté à (" + (int)x + ", " + (int)y + ")");
    }

    public void addMage(float x, float y, float patrolMin, float patrolMax) {
        Mage mage = new Mage(x, y, player);
        mage.setPatrolZone(patrolMin, patrolMax);
        enemies.add(mage);
        System.out.println("👹 Mage ajouté à (" + (int)x + ", " + (int)y + 
                ") | Patrol: [" + patrolMin + " -> " + patrolMax + "]");
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

    // ----------- COMBATS -----------

    public void checkEnemyAttacks(Player player) {
        for (Enemy enemy : enemies) {
            if (enemy.canHitPlayer() && !player.isDead()) {
                System.out.println("💥 Le joueur est touché par " + enemy.getClass().getSimpleName() +
                        " (-" + enemy.getDamage() + " HP)");
            }
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
        for (Enemy enemy : enemies) if (enemy.isDead()) toRemove.add(enemy);

        for (Enemy enemy : toRemove) {
            enemies.removeValue(enemy, true);
            enemy.dispose();
        }
    }

    public int getAliveCount() {
        int count = 0;
        for (Enemy enemy : enemies) if (!enemy.isDead()) count++;
        return count;
    }

    public int getTotalCount() {
        return enemies.size;
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
