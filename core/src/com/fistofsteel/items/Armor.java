package com.fistofsteel.items;

import com.fistofsteel.entities.player.Player;

/**
 * Classe de base pour les armures.
 * Augmente la défense du joueur.
 */
public class Armor extends Item {

    private final int armorBonus;

    /**
     * Constructeur d'armure.
     * 
     * @param id L'identifiant de l'armure
     * @param displayName Le nom affiché
     * @param armorBonus Le bonus de défense
     */
    public Armor(String id, String displayName, int armorBonus) {
        super(id, displayName);
        this.armorBonus = armorBonus;
    }

    @Override
    public void applyEffect(Player player) {
        if (player == null) return;
        
        player.equipArmor(this);
    }

    public int getArmorBonus() {
        return armorBonus;
    }
}