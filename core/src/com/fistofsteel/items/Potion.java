package com.fistofsteel.items;

import com.fistofsteel.entities.player.Player;

/**
 * Potion de soin qui restaure les points de vie.
 */
public class Potion extends Item {

    private final int healAmount;

    /**
     * Constructeur de potion.
     * 
     * @param id L'identifiant de la potion
     * @param displayName Le nom affiché
     * @param healAmount Le nombre de PV restaurés
     */
    public Potion(String id, String displayName, int healAmount) {
        super(id, displayName);
        this.healAmount = healAmount;
    }

    @Override
    public void applyEffect(Player player) {
        if (player == null) return;
        player.heal(healAmount);
        System.out.println("Potion utilisee : +" + healAmount + " PV");
    }
}