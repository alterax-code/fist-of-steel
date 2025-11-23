package com.fistofsteel.items;

import com.fistofsteel.entities.player.Player;

/**
 * Classe de base pour tous les items du jeu.
 */
public abstract class Item {

    protected String id;
    protected String displayName;

    /**
     * Constructeur avec ID uniquement.
     * 
     * @param id L'identifiant de l'item
     */
    public Item(String id) {
        this(id, id);
    }

    /**
     * Constructeur complet.
     * 
     * @param id L'identifiant de l'item
     * @param displayName Le nom affiché
     */
    public Item(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Applique l'effet de l'item au joueur.
     * 
     * @param player Le joueur qui utilise l'item
     */
    public abstract void applyEffect(Player player);
}