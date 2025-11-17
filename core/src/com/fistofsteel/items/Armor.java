package com.fistofsteel.items;

import com.fistofsteel.entities.Player;

public class Armor extends Item {

    private final int armorBonus;

    public Armor(String id, String displayName, int armorBonus) {
        super(id, displayName);
        this.armorBonus = armorBonus;
    }

    @Override
    public void applyEffect(Player player) {
        if (player == null) return;

        int newArmor = player.getArmor() + armorBonus;
        player.setArmor(newArmor);

        System.out.println("🛡 Armure équipée : " + displayName + " (+" + armorBonus + " DEF)");
    }

    public int getArmorBonus() {
        return armorBonus;
    }
}
