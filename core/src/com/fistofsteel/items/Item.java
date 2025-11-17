package com.fistofsteel.items;

import com.fistofsteel.entities.Player;

public abstract class Item {

    protected String id;
    protected String displayName;

    // Si tu veux juste un id
    public Item(String id) {
        this(id, id);
    }

    // Si tu veux un joli nom à afficher
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

    // Chaque item applique un effet différent au joueur
    public abstract void applyEffect(Player player);
}
