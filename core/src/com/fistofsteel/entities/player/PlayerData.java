package com.fistofsteel.entities.player;

import com.fistofsteel.items.Armor;
import com.fistofsteel.items.Weapon;

/**
 * Classe pour sauvegarder les données du joueur entre les niveaux.
 * Utilisé lors du changement de niveau pour conserver :
 * - Les PV actuels
 * - L'équipement (armure et arme)
 * - Les bonus de stats
 */
public class PlayerData {
    
    private int health;
    private int maxHealth;
    private int armor;
    private int attackBonus;
    private Armor equippedArmor;
    private Weapon equippedWeapon;
    
    /**
     * Constructeur par défaut (nouvelle partie).
     * Toutes les valeurs sont à zéro/null.
     */
    public PlayerData() {
        this.health = 100;
        this.maxHealth = 100;
        this.armor = 0;
        this.attackBonus = 0;
        this.equippedArmor = null;
        this.equippedWeapon = null;
    }
    
    /**
     * Crée un PlayerData à partir d'un joueur existant.
     * Utilisé pour sauvegarder l'état avant de changer de niveau.
     * 
     * @param player Le joueur dont on sauvegarde les données
     */
    public PlayerData(Player player) {
        this.health = player.getHealth();
        this.maxHealth = player.getMaxHealth();
        this.armor = player.getArmor();
        this.attackBonus = player.getAttackBonus();
        this.equippedArmor = player.getEquippedArmor();
        this.equippedWeapon = player.getEquippedWeapon();
        
        System.out.println("PlayerData sauvegardé :");
        System.out.println("   HP: " + health + "/" + maxHealth);
        System.out.println("   Armure: " + (equippedArmor != null ? equippedArmor.getDisplayName() : "Aucune"));
        System.out.println("   Arme: " + (equippedWeapon != null ? equippedWeapon.getDisplayName() : "Aucune"));
        System.out.println("   DEF: " + armor + " | ATK bonus: " + attackBonus);
    }
    
    /**
     * Applique les données sauvegardées à un joueur.
     * Utilisé après la création du joueur dans le nouveau niveau.
     * 
     * @param player Le joueur à qui appliquer les données
     */
    public void applyToPlayer(Player player) {
        // Appliquer l'équipement d'abord (pour que les bonus soient corrects)
        if (equippedArmor != null) {
            player.equipArmor(equippedArmor);
        }
        if (equippedWeapon != null) {
            player.equipWeapon(equippedWeapon);
        }
        
        // Appliquer les PV (après l'équipement pour éviter les conflits)
        player.setHealth(health);
        
        System.out.println("PlayerData appliqué au joueur :");
        System.out.println("   HP: " + player.getHealth() + "/" + player.getMaxHealth());
        System.out.println("   DEF: " + player.getArmor());
        System.out.println("   ATK total: " + player.getTotalAttack());
    }
    
    // Getters
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getArmor() { return armor; }
    public int getAttackBonus() { return attackBonus; }
    public Armor getEquippedArmor() { return equippedArmor; }
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    
    /**
     * Vérifie si ce sont des données de nouvelle partie (rien d'équipé, PV max).
     * 
     * @return true si c'est une nouvelle partie
     */
    public boolean isNewGame() {
        return equippedArmor == null && equippedWeapon == null && health == maxHealth;
    }
}