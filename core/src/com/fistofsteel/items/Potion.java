package com.fistofsteel.items;

import com.fistofsteel.entities.player.Player;

public class Potion extends Item {

    private final int healAmount;

    public Potion(String id, String displayName, int healAmount) {
        super(id, displayName);
        this.healAmount = healAmount;
    }

    @Override
    public void applyEffect(Player player) {
        if (player == null) return;
        player.heal(healAmount);
        System.out.println("💊 Potion utilisée : +" + healAmount + " PV");
    }
}
