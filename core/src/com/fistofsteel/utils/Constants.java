package com.fistofsteel.utils;

public class Constants {
    // ===== PHYSIQUE GAMEPLAY =====
    public static final float GRAVITY = 1200f;       
    public static final float WALK_SPEED = 400f;   
    public static final float JUMP_FORCE = 700f;
    public static final float MAX_FALL_SPEED = 1000f;

    // ===== PHYSIQUE MOTEUR (SUB-STEPPING) =====
    public static final int MAX_PHYSICS_STEPS = 8;      // Augmenté pour gravité élevée
    public static final float MAX_MOVE_PER_STEP = 30f;  // Réduit pour plus de précision

    // ===== DIMENSIONS DU JOUEUR =====
    public static final float PLAYER_WIDTH = 120;   
    public static final float PLAYER_HEIGHT = 128;  

    // ===== HITBOX DU JOUEUR (DÉFAUT) =====
    // Utilisées par les ennemis comme Knight
    public static final float PLAYER_HITBOX_WIDTH = 70f;
    public static final float PLAYER_HITBOX_HEIGHT = 105f;
    public static final float PLAYER_HITBOX_OFFSET_X = (PLAYER_WIDTH - PLAYER_HITBOX_WIDTH) / 2f;
    public static final float PLAYER_HITBOX_OFFSET_Y = 8f;

    // ===== DIMENSIONS DES ENNEMIS =====
    public static final float ENEMY_WIDTH = 160f;   // Plus grand que le joueur
    public static final float ENEMY_HEIGHT = 170f;  // Plus grand que le joueur
    
    // ===== HITBOX DES ENNEMIS =====
    public static final float ENEMY_HITBOX_WIDTH = 85f;
    public static final float ENEMY_HITBOX_HEIGHT = 130f;
    public static final float ENEMY_HITBOX_OFFSET_X = (ENEMY_WIDTH - ENEMY_HITBOX_WIDTH) / 2f;
    public static final float ENEMY_HITBOX_OFFSET_Y = 10f;

    // ===== FENÊTRE / ÉCRAN =====
    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;
}