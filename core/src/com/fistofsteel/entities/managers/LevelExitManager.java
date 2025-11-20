package com.fistofsteel.entities.managers;  // ✅ MODIFIÉ (était com.fistofsteel.entities)

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;  // ✅ AJOUT
import com.fistofsteel.entities.world.LevelExit;  // ✅ AJOUT

// ... reste du code inchangé

/**
 * Gestionnaire des portes de sortie de niveau
 */
public class LevelExitManager {
    
    private Array<LevelExit> exits;
    private ShapeRenderer shapeRenderer;
    
    public LevelExitManager() {
        this.exits = new Array<>();
        this.shapeRenderer = new ShapeRenderer();
    }
    
    /**
     * Ajoute une porte de sortie
     */
    public void addExit(float x, float y, String targetLevel) {
        LevelExit exit = new LevelExit(x, y, targetLevel);
        exits.add(exit);
        System.out.println("✅ Porte de sortie ajoutée : " + targetLevel);
    }
    
    /**
     * Met à jour toutes les portes
     */
    public void update(int enemiesKilled, int totalEnemies) {
        for (LevelExit exit : exits) {
            exit.update(enemiesKilled, totalEnemies);
        }
    }
    
    /**
     * Dessine toutes les portes
     */
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        for (LevelExit exit : exits) {
            exit.render(batch, shapeRenderer, camera);
        }
    }
    
    /**
     * Vérifie si le joueur est sur une porte déverrouillée
     * @return Le nom du prochain niveau, ou null si aucune porte accessible
     */
    public String checkPlayerOnExit(Player player) {
        for (LevelExit exit : exits) {
            if (exit.isUnlocked() && exit.isPlayerInside(player)) {
                System.out.println("🚪 Joueur sur la porte -> " + exit.getTargetLevel());
                return exit.getTargetLevel();
            }
        }
        return null;
    }
    
    /**
     * Vérifie si une porte est déverrouillée
     */
    public boolean hasUnlockedExit() {
        for (LevelExit exit : exits) {
            if (exit.isUnlocked()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Compte le nombre de portes
     */
    public int getExitCount() {
        return exits.size;
    }
    
    /**
     * Nettoyage
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        exits.clear();
    }
}