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

        // tu adapteras ces méthodes si leurs noms sont un peu différents
        int newBonus = player.getAttackBonus() + attackBonus;
        player.setAttackBonus(newBonus);

        System.out.println("🗡 Arme équipée : " + displayName + " (+" + attackBonus + " ATK)");
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}
