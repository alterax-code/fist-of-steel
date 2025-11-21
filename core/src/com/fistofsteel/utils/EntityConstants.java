package com.fistofsteel.utils;

/**
 * Constantes des dimensions et hitboxes des entités
 */
public class EntityConstants {
    // ===== JOUEUR =====
    public static final float PLAYER_WIDTH = 120;
    public static final float PLAYER_HEIGHT = 128;
    
    public static final float PLAYER_HITBOX_WIDTH = 70f;
    public static final float PLAYER_HITBOX_HEIGHT = 105f;
    public static final float PLAYER_HITBOX_OFFSET_X = (PLAYER_WIDTH - PLAYER_HITBOX_WIDTH) / 2f;
    public static final float PLAYER_HITBOX_OFFSET_Y = 5f;
    
    // ===== ENNEMIS =====
    public static final float ENEMY_WIDTH = 160f;
    public static final float ENEMY_HEIGHT = 170f;
    
    public static final float ENEMY_HITBOX_WIDTH = 85f;
    public static final float ENEMY_HITBOX_HEIGHT = 95f;
    public static final float ENEMY_HITBOX_OFFSET_X = (ENEMY_WIDTH - ENEMY_HITBOX_WIDTH) / 2f;
    public static final float ENEMY_HITBOX_OFFSET_Y = 22f;
}