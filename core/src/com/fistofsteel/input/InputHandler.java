package com.fistofsteel.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.fistofsteel.audio.AudioManager;

/**
 * Gestionnaire des entrées du joueur.
 * Détecte les touches et joue les sons immédiatement.
 */
public class InputHandler extends InputAdapter {
    
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    private boolean attackPressed = false;
    private boolean blockPressed = false;
    private boolean crouchPressed = false;
    
    private boolean hitPressed = false;
    private boolean deadPressed = false;
    
    private AudioManager audioManager;
    
    /**
     * Constructeur de l'InputHandler.
     * 
     * @param audioManager Le gestionnaire audio
     */
    public InputHandler(AudioManager audioManager) {
        this.audioManager = audioManager;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            leftPressed = true;
        }
        
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            rightPressed = true;
        }
        
        if (keycode == Input.Keys.SPACE && !jumpPressed) {
            jumpPressed = true;
            audioManager.playSound("jump");
        }
        
        if (keycode == Input.Keys.Q && !attackPressed) {
            attackPressed = true;
            audioManager.playSound("attack");
        }
        
        if (keycode == Input.Keys.E) {
            blockPressed = true;
        }
        
        if (keycode == Input.Keys.S) {
            crouchPressed = true;
        }
        
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
    
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isJumpPressed() { return jumpPressed; }
    public boolean isAttackPressed() { return attackPressed; }
    public boolean isBlockPressed() { return blockPressed; }
    public boolean isCrouchPressed() { return crouchPressed; }
    public boolean isHitPressed() { return hitPressed; }
    public boolean isDeadPressed() { return deadPressed; }
}