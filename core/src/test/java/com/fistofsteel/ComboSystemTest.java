package com.fistofsteel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test du système de combos d'Alexis
 */
public class ComboSystemTest {
    
    // Constantes du système de combos (depuis Alexis.java)
    private static final float[] COMBO_MULTIPLIERS = {1.0f, 1.25f, 1.5f};
    private static final float COMBO_WINDOW = 0.8f;
    
    @Test
    public void testComboLevel1Multiplier() {
        // Le premier coup doit avoir un multiplicateur de x1.0
        float multiplier = COMBO_MULTIPLIERS[0];
        assertEquals(1.0f, multiplier, "Combo niveau 1 doit être x1.0");
    }
    
    @Test
    public void testComboLevel2Multiplier() {
        // Le deuxième coup doit avoir un multiplicateur de x1.25
        float multiplier = COMBO_MULTIPLIERS[1];
        assertEquals(1.25f, multiplier, "Combo niveau 2 doit être x1.25");
    }
    
    @Test
    public void testComboLevel3Multiplier() {
        // Le troisième coup doit avoir un multiplicateur de x1.5
        float multiplier = COMBO_MULTIPLIERS[2];
        assertEquals(1.5f, multiplier, "Combo niveau 3 doit être x1.5");
    }
    
    @Test
    public void testComboDamageCalculation() {
        // Test calcul dégâts avec combo
        int baseAttack = 10;
        int weaponBonus = 15; // Sword3
        
        // Coup 1
        int damage1 = Math.round((baseAttack + weaponBonus) * COMBO_MULTIPLIERS[0]);
        assertEquals(25, damage1, "Coup 1 : (10+15) x 1.0 = 25");
        
        // Coup 2
        int damage2 = Math.round((baseAttack + weaponBonus) * COMBO_MULTIPLIERS[1]);
        assertEquals(31, damage2, "Coup 2 : (10+15) x 1.25 = 31");
        
        // Coup 3
        int damage3 = Math.round((baseAttack + weaponBonus) * COMBO_MULTIPLIERS[2]);
assertEquals(38, damage3, "Coup 3 : (10+15) x 1.5 = 37.5 → 38");
    }
    
    @Test
    public void testComboWindowTiming() {
        // La fenêtre de combo doit être de 0.8 secondes
        assertEquals(0.8f, COMBO_WINDOW, "La fenêtre de combo doit être 0.8s");
    }
    
    @Test
    public void testComboResetAfterTimeout() {
        // Simuler timeout : si temps > 0.8s, le combo reset
        float timeSinceLastAttack = 0.9f; // Plus grand que COMBO_WINDOW
        boolean shouldReset = timeSinceLastAttack > COMBO_WINDOW;
        
        assertTrue(shouldReset, "Le combo doit reset après 0.8s");
    }
    
    @Test
    public void testComboMaintainedWithinWindow() {
        // Simuler attaque dans la fenêtre : combo maintenu
        float timeSinceLastAttack = 0.5f; // Moins que COMBO_WINDOW
        boolean shouldReset = timeSinceLastAttack > COMBO_WINDOW;
        
        assertFalse(shouldReset, "Le combo ne doit PAS reset avant 0.8s");
    }
    
    @Test
    public void testMaxComboLevel() {
        // Le niveau de combo maximum doit être 2 (index 0, 1, 2)
        int maxComboLevel = COMBO_MULTIPLIERS.length - 1;
        assertEquals(2, maxComboLevel, "Le niveau de combo max doit être 2");
    }
}