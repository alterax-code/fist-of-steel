package com.fistofsteel.items;

import com.fistofsteel.entities.player.Player;

public class Armor extends Item {

    private final int armorBonus;

    public Armor(String id, String displayName, int armorBonus) {
        super(id, displayName);
        this.armorBonus = armorBonus;
    }

    @Override
    public void applyEffect(Player player) {
        if (player == null) return;
        
        // Utilise le système d'équipement du joueur
        player.equipArmor(this);
    }

    public int getArmorBonus() {
        return armorBonus;
    }
}