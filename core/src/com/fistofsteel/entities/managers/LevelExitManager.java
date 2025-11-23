package com.fistofsteel.entities.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.entities.world.LevelExit;

/**
 * Gestionnaire des portes de sortie de niveau.
 * Gère l'affichage, l'état et les interactions avec les portes.
 */
public class LevelExitManager {
    
    private Array<LevelExit> exits;
    private ShapeRenderer shapeRenderer;
    
    /**
     * Constructeur du gestionnaire de sorties.
     */
    public LevelExitManager() {
        this.exits = new Array<>();
        this.shapeRenderer = new ShapeRenderer();
    }
    
    /**
     * Ajoute une porte de sortie.
     * 
     * @param x Position X
     * @param y Position Y
     * @param targetLevel Le nom du niveau cible
     */
    public void addExit(float x, float y, String targetLevel) {
        LevelExit exit = new LevelExit(x, y, targetLevel);
        exits.add(exit);
        System.out.println("Porte de sortie ajoutee : " + targetLevel);
    }
    
    /**
     * Met à jour toutes les portes selon les ennemis tués.
     * 
     * @param enemiesKilled Le nombre d'ennemis tués
     * @param totalEnemies Le nombre total d'ennemis
     */
    public void update(int enemiesKilled, int totalEnemies) {
        for (LevelExit exit : exits) {
            exit.update(enemiesKilled, totalEnemies);
        }
    }
    
    /**
     * Affiche toutes les portes.
     * 
     * @param batch Le SpriteBatch pour le rendu
     * @param camera La caméra du jeu
     */
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        for (LevelExit exit : exits) {
            exit.render(batch, shapeRenderer, camera);
        }
    }
    
    /**
     * Vérifie si le joueur est sur une porte déverrouillée.
     * 
     * @param player Le joueur
     * @return Le nom du prochain niveau, ou null si aucune porte accessible
     */
    public String checkPlayerOnExit(Player player) {
        for (LevelExit exit : exits) {
            if (exit.isUnlocked() && exit.isPlayerInside(player)) {
                System.out.println("Joueur sur la porte -> " + exit.getTargetLevel());
                return exit.getTargetLevel();
            }
        }
        return null;
    }
    
    /**
     * Vérifie si une porte est déverrouillée.
     * 
     * @return true si au moins une porte est déverrouillée
     */
    public boolean hasUnlockedExit() {
        for (LevelExit exit : exits) {
            if (exit.isUnlocked()) {
                return true;
            }
        }
        return false;
    }
    
    public int getExitCount() {
        return exits.size;
    }
    
    /**
     * Libère les ressources.
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        exits.clear();
    }
}