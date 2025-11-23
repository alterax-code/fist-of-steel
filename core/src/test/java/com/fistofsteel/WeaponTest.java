package com.fistofsteel;

import com.fistofsteel.items.Weapon;
import com.fistofsteel.items.Sword1;
import com.fistofsteel.items.Sword2;
import com.fistofsteel.items.Sword3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test du système d'armes
 */
public class WeaponTest {
    
    @Test
    public void testSword1Stats() {
        // Sword1 doit donner +5 ATK
        Sword1 sword = new Sword1();
        
        assertEquals("sword1", sword.getId(), "L'ID doit être sword1");
        assertEquals(5, sword.getAttackBonus(), "Sword1 doit donner +5 ATK");
        assertEquals("Epée de base", sword.getDisplayName(), "Le nom doit correspondre");
    }
    
    @Test
    public void testSword2Stats() {
        // Sword2 doit donner +10 ATK
        Sword2 sword = new Sword2();
        
        assertEquals("sword2", sword.getId(), "L'ID doit être sword2");
        assertEquals(10, sword.getAttackBonus(), "Sword2 doit donner +10 ATK");
        assertEquals("Epée tranchante", sword.getDisplayName(), "Le nom doit correspondre");
    }
    
    @Test
    public void testSword3Stats() {
        // Sword3 doit donner +15 ATK (arme légendaire)
        Sword3 sword = new Sword3();
        
        assertEquals("sword3", sword.getId(), "L'ID doit être sword3");
        assertEquals(15, sword.getAttackBonus(), "Sword3 doit donner +15 ATK");
        assertEquals("Epée légendaire", sword.getDisplayName(), "Le nom doit correspondre");
    }
    
    @Test
    public void testWeaponProgression() {
        // Chaque arme doit être plus puissante que la précédente
        Sword1 sword1 = new Sword1();
        Sword2 sword2 = new Sword2();
        Sword3 sword3 = new Sword3();
        
        assertTrue(sword2.getAttackBonus() > sword1.getAttackBonus(),
                   "Sword2 doit être plus puissante que Sword1");
        assertTrue(sword3.getAttackBonus() > sword2.getAttackBonus(),
                   "Sword3 doit être plus puissante que Sword2");
    }
    
    @Test
    public void testTotalAttackCalculation() {
        // Test du calcul d'attaque totale : base (10) + arme
        int baseAttack = 10;
        Sword3 sword = new Sword3();
        
        int totalAttack = baseAttack + sword.getAttackBonus();
        
        assertEquals(25, totalAttack, "10 base + 15 sword3 = 25 ATK total");
    }
}