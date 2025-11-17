package com.fistofsteel.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.fistofsteel.audio.AudioManager;

/**
 * InputHandler ultra-simple
 * Détecte les touches et joue les sons IMMÉDIATEMENT
 */
public class InputHandler extends InputAdapter {
    
    // États des touches
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    private boolean attackPressed = false;
    private boolean blockPressed = false;
    private boolean crouchPressed = false;
    
    // Actions de test
    private boolean hitPressed = false;
    private boolean deadPressed = false;
    
    // Référence à l'AudioManager
    private AudioManager audioManager;
    
    /**
     * Constructeur - prend l'AudioManager en paramètre
     */
    public InputHandler(AudioManager audioManager) {
        this.audioManager = audioManager;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        
        // ===== MOUVEMENTS =====
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            leftPressed = true;
        }
        
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            rightPressed = true;
        }
        
        // ===== JUMP + SON INSTANTANÉ =====
        if (keycode == Input.Keys.SPACE && !jumpPressed) {
            jumpPressed = true;
            audioManager.playSound("jump");
        }
        
        // ===== ATTACK + SON INSTANTANÉ =====
        if (keycode == Input.Keys.Q && !attackPressed) {
            attackPressed = true;
            audioManager.playSound("attack");
        }
        
        // ===== BLOCK =====
        if (keycode == Input.Keys.E) {
            blockPressed = true;
        }
        
        // ===== CROUCH =====
        if (keycode == Input.Keys.S) {
            crouchPressed = true;
        }
        
        // ===== TESTS =====
        if (keycode == Input.Keys.F && !hitPressed) {
            hitPressed = true;
            audioManager.playSound("hit");
        }
        
        if (keycode == Input.Keys.R && !deadPressed) {
            deadPressed = true;
            audioManager.playSound("death");
        }
        
        return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            leftPressed = false;
        }
        
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            rightPressed = false;
        }
        
        if (keycode == Input.Keys.SPACE) {
            jumpPressed = false;
        }
        
        if (keycode == Input.Keys.Q) {
            attackPressed = false;
        }
        
        if (keycode == Input.Keys.E) {
            blockPressed = false;
        }
        
        if (keycode == Input.Keys.S) {
            crouchPressed = false;
        }
        
        if (keycode == Input.Keys.F) {
            hitPressed = false;
        }
        
        if (keycode == Input.Keys.R) {
            deadPressed = false;
        }
        
        return true;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Clic gauche = Attack + son instantané
        if (button == Input.Buttons.LEFT && !attackPressed) {
            attackPressed = true;
            audioManager.playSound("attack");
        }
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            attackPressed = false;
        }
        return true;
    }
    
    // ========================================
    // GETTERS SIMPLES
    // ========================================
    
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isJumpPressed() { return jumpPressed; }
    public boolean isAttackPressed() { return attackPressed; }
    public boolean isBlockPressed() { return blockPressed; }
    public boolean isCrouchPressed() { return crouchPressed; }
    public boolean isHitPressed() { return hitPressed; }
    public boolean isDeadPressed() { return deadPressed; }
}