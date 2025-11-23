package com.fistofsteel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleTest {
    
    @Test
    public void testAddition() {
        int result = 2 + 2;
        assertEquals(4, result, "2 + 2 doit égaler 4");
    }
    
    @Test
    public void testStringNotEmpty() {
        String name = "Fist of Steel";
        assertFalse(name.isEmpty(), "Le nom ne doit pas être vide");
    }
}