package com.fistofsteel.items;

import com.fistofsteel.entities.Player;

public class Weapon extends Item {

    protected final int attackBonus;

    public Weapon(String id, String displayName, int attackBonus) {
        super(id, displayName);
        this.attackBonus = attackBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    @Override
    public void applyEffect(Player player) {
        if (player == null) return;
        player.equipWeapon(this);   // 👈 IMPORTANT
    }
}
