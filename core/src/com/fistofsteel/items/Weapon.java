package com.fistofsteel.items;

import com.fistofsteel.entities.player.Player;

public class Weapon extends Item {

    private final int attackBonus;

    public Weapon(String id, String displayName, int attackBonus) {
        super(id, displayName);
        this.attackBonus = attackBonus;
    }

    @Override
    public void applyEffect(Player player) {
        if (player == null) return;
        
        // Utilise le système d'équipement du joueur
        player.equipWeapon(this);
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}