package com.fistofsteel.entities.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.entities.enemies.Enemy;
import com.fistofsteel.entities.enemies.Knight;
import com.fistofsteel.entities.enemies.Mage;
import com.fistofsteel.entities.enemies.Rogue;
import com.fistofsteel.entities.enemies.Boss;
import com.fistofsteel.utils.HitboxDebugger;

/**
 * Gestionnaire des ennemis du jeu.
 * Gère le spawn, l'update, le rendu et les combats de tous les ennemis.
 * 
 * MODIFIÉ : Les ennemis meurent maintenant s'ils touchent une zone de mort.
 */
public class EnemyManager {

    private Array<Enemy> enemies;
    private Player player;
    
    private int enemiesKilled = 0;
    private int totalEnemiesSpawned = 0;
    
    private ProjectileManager projectileManager;
    
    // ═══════════════════════════════════════════════════════════════════════════
    // AJOUTÉ : Zones de mort pour tuer les ennemis qui tombent sous la map
    // ═══════════════════════════════════════════════════════════════════════════
    private Array<Rectangle> deathRects;

    /**
     * Constructeur du gestionnaire d'ennemis.
     * 
     * @param player Le joueur cible
     */
    public EnemyManager(Player player) {
        this.enemies = new Array<>();
        this.player = player;
        this.deathRects = new Array<>();  // ← AJOUTÉ
    }
    
    /**
     * Définit le gestionnaire de projectiles.
     * 
     * @param manager Le ProjectileManager
     */
    public void setProjectileManager(ProjectileManager manager) {
        this.projectileManager = manager;
        
        for (Enemy enemy : enemies) {
            if (enemy instanceof Mage) {
                ((Mage) enemy).setProjectileManager(manager);
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // AJOUTÉ : Méthode pour définir les zones de mort
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * Définit les zones de mort pour les ennemis.
     * Les ennemis qui touchent ces zones meurent instantanément.
     * 
     * @param deathRects Les rectangles des zones de mort
     */
    public void setDeathRects(Array<Rectangle> deathRects) {
        this.deathRects = deathRects;
        if (deathRects != null) {
            System.out.println("Zones de mort configurées pour les ennemis (" + deathRects.size + " zones)");
        }
    }

    /**
     * Ajoute un Knight à la position donnée.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void addKnight(float x, float y) {
        Knight knight = new Knight(x, y, player);
        enemies.add(knight);
        totalEnemiesSpawned++;
        System.out.println("Knight ajoute a (" + (int)x + ", " + (int)y + ")");
    }

    /**
     * Ajoute un Knight avec une zone de patrouille.
     * 
     * @param x Position X
     * @param y Position Y
     * @param patrolMin Distance minimale de patrouille
     * @param patrolMax Distance maximale de patrouille
     */
    public void addKnight(float x, float y, float patrolMin, float patrolMax) {
        Knight knight = new Knight(x, y, player);
        knight.setPatrolZone(patrolMin, patrolMax);
        enemies.add(knight);
        totalEnemiesSpawned++;
        System.out.println("Knight ajoute a (" + (int)x + ", " + (int)y + ") | Patrol: [" +
                (int)patrolMin + " -> " + (int)patrolMax + "]");
    }

    /**
     * Ajoute un Mage à la position donnée.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void addMage(float x, float y) {
        Mage mage = new Mage(x, y, player);
        if (projectileManager != null) {
            mage.setProjectileManager(projectileManager);
        }
        enemies.add(mage);
        totalEnemiesSpawned++;
        System.out.println("Mage ajoute a (" + (int)x + ", " + (int)y + ")");
    }

    /**
     * Ajoute un Mage avec une zone de patrouille.
     * 
     * @param x Position X
     * @param y Position Y
     * @param patrolMin Distance minimale de patrouille
     * @param patrolMax Distance maximale de patrouille
     */
    public void addMage(float x, float y, float patrolMin, float patrolMax) {
        Mage mage = new Mage(x, y, player);
        mage.setPatrolZone(patrolMin, patrolMax);
        if (projectileManager != null) {
            mage.setProjectileManager(projectileManager);
        }
        enemies.add(mage);
        totalEnemiesSpawned++;
        System.out.println("Mage ajoute a (" + (int)x + ", " + (int)y + 
                ") | Patrol: [" + (int)patrolMin + " -> " + (int)patrolMax + "]");
    }

    /**
     * Ajoute un Rogue à la position donnée.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void addRogue(float x, float y) {
        Rogue rogue = new Rogue(x, y, player);
        enemies.add(rogue);
        totalEnemiesSpawned++;
        System.out.println("Rogue ajoute a (" + (int)x + ", " + (int)y + ")");
    }

    /**
     * Ajoute un Rogue avec une zone de patrouille.
     * 
     * @param x Position X
     * @param y Position Y
     * @param patrolMin Distance minimale de patrouille
     * @param patrolMax Distance maximale de patrouille
     */
    public void addRogue(float x, float y, float patrolMin, float patrolMax) {
        Rogue rogue = new Rogue(x, y, player);
        rogue.setPatrolZone(patrolMin, patrolMax);
        enemies.add(rogue);
        totalEnemiesSpawned++;
        System.out.println("Rogue ajoute a (" + (int)x + ", " + (int)y + 
                ") | Patrol: [" + (int)patrolMin + " -> " + (int)patrolMax + "]");
    }

    /**
     * Ajoute un Boss à la position donnée.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void addBoss(float x, float y) {
        Boss boss = new Boss(x, y, player);
        enemies.add(boss);
        totalEnemiesSpawned++;
        System.out.println("BOSS ajoute a (" + (int)x + ", " + (int)y + ")");
    }

    /**
     * Définit les rectangles de collision pour tous les ennemis.
     * 
     * @param collisions Les rectangles de collision
     */
    public void setCollisionRects(Array<Rectangle> collisions) {
        for (Enemy enemy : enemies) {
            enemy.setCollisionRects(collisions);
        }
    }

    /**
     * Met à jour tous les ennemis.
     * Vérifie également si un ennemi touche une zone de mort.
     * 
     * @param delta Le temps écoulé
     */
    public void update(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }
        
        // ═══════════════════════════════════════════════════════════════════════════
        // AJOUTÉ : Vérifier si un ennemi touche une zone de mort
        // ═══════════════════════════════════════════════════════════════════════════
        checkEnemyDeathZones();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // AJOUTÉ : Méthode pour vérifier les zones de mort
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * Vérifie si des ennemis touchent une zone de mort et les tue.
     */
    private void checkEnemyDeathZones() {
        if (deathRects == null || deathRects.size == 0) return;
        
        for (Enemy enemy : enemies) {
            // Ne pas vérifier les ennemis déjà morts
            if (enemy.isDead()) continue;
            
            Rectangle enemyHitbox = enemy.getHitbox();
            
            for (Rectangle deathRect : deathRects) {
                if (enemyHitbox.overlaps(deathRect)) {
                    // Tuer l'ennemi instantanément
                    enemy.killInstantly();
                    System.out.println(enemy.getClass().getSimpleName() + 
                            " est tombé dans une zone de mort ! Position: (" + 
                            (int)enemy.getX() + ", " + (int)enemy.getY() + ")");
                    break;
                }
            }
        }
    }

    /**
     * Affiche tous les ennemis.
     * 
     * @param batch Le SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
    }
    
    /**
     * Affiche les barres de vie de tous les ennemis.
     * 
     * @param shapeRenderer Le ShapeRenderer pour le rendu
     * @param camera La caméra du jeu
     */
    public void renderHealthBars(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        for (Enemy enemy : enemies) {
            enemy.renderHealthBar(shapeRenderer, camera);
        }
    }

    /**
     * Vérifie et applique les attaques des ennemis sur le joueur.
     * 
     * @param player Le joueur
     */
    public void checkEnemyAttacks(Player player) {
        for (Enemy enemy : enemies) {
            enemy.tryDealDamage();
        }
    }

    /**
     * Vérifie et applique l'attaque du joueur sur les ennemis.
     * 
     * @param player Le joueur
     */
    public void checkPlayerAttack(Player player) {
        if (player.isRangedAttacker()) {
            return;
        }
        
        if (!player.isAttacking()) return;
        if (player.hasDealtDamageThisAttack()) return;

        Rectangle playerHitbox = player.getHitbox();
        Rectangle attackBox = new Rectangle(player.getX(), player.getY(), 80f, playerHitbox.height);

        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && attackBox.overlaps(enemy.getHitbox())) {
                int damage = player.getTotalAttack();
                enemy.takeDamage(damage);
                player.markDamageDealt();
                System.out.println(enemy.getClass().getSimpleName() +
                        " touche ! (-" + damage + " HP) | HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
                break;
            }
        }
    }

    /**
     * Retire les ennemis morts de la liste.
     */
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

    /**
     * Compte le nombre d'ennemis vivants.
     * 
     * @return Le nombre d'ennemis vivants
     */
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
    
    public Array<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Affiche les hitboxes de debug pour tous les ennemis.
     * 
     * @param camera La caméra du jeu
     */
    public void renderDebugHitboxes(OrthographicCamera camera) {
        for (Enemy enemy : enemies) {
            HitboxDebugger.renderEnemyHitbox(enemy, camera);
        }
    }

    /**
     * Libère les ressources de tous les ennemis.
     */
    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
    }
}