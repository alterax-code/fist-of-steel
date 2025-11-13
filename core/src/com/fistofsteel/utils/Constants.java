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

    // ===== FENÊTRE / ÉCRAN =====
    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;
}