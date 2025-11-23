package com.fistofsteel.items;

import com.fistofsteel.entities.player.Player;

/**
 * Classe de base pour les armes.
 * Augmente l'attaque du joueur.
 */
public class Weapon extends Item {

    private final int attackBonus;

    /**
     * Constructeur d'arme.
     * 
     * @param id L'identifiant de l'arme
     * @param displayName Le nom affiché
     * @param attackBonus Le bonus d'attaque
     */
    public Weapon(String id, String displayName, int attackBonus) {
        super(id, displayName);
        this.attackBonus = attackBonus;
    }

    @Override
    public void applyEffect(Player player) {
        if (player == null) return;
        
        player.equipWeapon(this);
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}