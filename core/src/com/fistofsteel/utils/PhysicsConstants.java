package com.fistofsteel.utils;

/**
 * Constantes physiques du jeu
 */
public class PhysicsConstants {
    // Gravité et vitesses
    public static final float GRAVITY = 1200f;
    public static final float WALK_SPEED = 400f;
    public static final float JUMP_FORCE = 700f;
    public static final float MAX_FALL_SPEED = 1000f;
    
    // Sub-stepping pour physique précise
    public static final int MAX_PHYSICS_STEPS = 8;
    public static final float MAX_MOVE_PER_STEP = 30f;
}