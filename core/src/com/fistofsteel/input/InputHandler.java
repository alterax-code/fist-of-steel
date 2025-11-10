package com.fistofsteel.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {
    // Déplacements
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    
    // Actions
    private boolean attackPressed = false;
    private boolean blockPressed = false;
    private boolean crouchPressed = false;
    
    // Actions provisoires pour tests
    private boolean hitPressed = false;
    private boolean deadPressed = false;
    
    @Override
    public boolean keyDown(int keycode) {
        // Déplacements - AZERTY corrigé
        // Sur AZERTY : physiquement A = Keys.Q, Q = Keys.A en LibGDX
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) leftPressed = true;  // A physique (à gauche)
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) rightPressed = true;
        if (keycode == Input.Keys.SPACE) jumpPressed = true;
        
        // Actions de combat - AZERTY corrigé
        if (keycode == Input.Keys.Q) attackPressed = true;  // Q physique = A sur AZERTY
        if (keycode == Input.Keys.E) blockPressed = true;
        if (keycode == Input.Keys.S) crouchPressed = true;
        
        // Provisoires pour tests
        if (keycode == Input.Keys.F) hitPressed = true;
        if (keycode == Input.Keys.R) deadPressed = true;
        
        return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        // Déplacements - AZERTY corrigé
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) leftPressed = false;  // A physique
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) rightPressed = false;
        if (keycode == Input.Keys.SPACE) jumpPressed = false;
        
        // Actions de combat - AZERTY corrigé
        if (keycode == Input.Keys.Q) attackPressed = false;  // Q physique = A sur AZERTY
        if (keycode == Input.Keys.E) blockPressed = false;
        if (keycode == Input.Keys.S) crouchPressed = false;
        
        // Provisoires pour tests
        if (keycode == Input.Keys.F) hitPressed = false;
        if (keycode == Input.Keys.R) deadPressed = false;
        
        return true;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Clic gauche = Attaque
        if (button == Input.Buttons.LEFT) {
            attackPressed = true;
        }
        return true;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Relâcher clic gauche
        if (button == Input.Buttons.LEFT) {
            attackPressed = false;
        }
        return true;
    }
    
    // Getters pour déplacements
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isJumpPressed() { return jumpPressed; }
    
    // Getters pour actions
    public boolean isAttackPressed() { return attackPressed; }
    public boolean isBlockPressed() { return blockPressed; }
    public boolean isCrouchPressed() { return crouchPressed; }
    
    // Getters provisoires pour tests
    public boolean isHitPressed() { return hitPressed; }
    public boolean isDeadPressed() { return deadPressed; }
}