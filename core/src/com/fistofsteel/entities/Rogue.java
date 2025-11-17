package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fistofsteel.utils.Constants;

/**
 * Ennemi Rogue - Utilise son set complet de sprites
 */
public class Rogue extends Enemy {
    private Texture[] idleTextures;   // 12 frames !
    private Texture[] hurtTextures;   // 4 frames
    private Texture[] walkTextures;   // 6 frames
    private Texture[] attackTextures; // 5 frames (attack0-4)
    private Texture[] deadTextures;   // 10 frames

    public Rogue(float x, float y, Player targetPlayer) {
        super(x, y, targetPlayer);
    }
    
    public Rogue(float x, float y, Player targetPlayer, float patrolMin, float patrolMax) {
        super(x, y, targetPlayer);
        setPatrolZone(patrolMin, patrolMax);
    }

    @Override
    protected void loadTextures() {
        try {
            System.out.println("🗡️ Chargement complet des sprites Rogue...");
            
            // ⭐ IDLE - 18 FRAMES !
            idleTextures = new Texture[]{
                new Texture("assets/sprites/sbires/Rogue/Idle/idle1.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle2.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle3.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle4.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle5.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle6.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle7.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle8.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle9.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle10.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle12.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle13.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle14.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle15.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle16.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle17.png"),
                new Texture("assets/sprites/sbires/Rogue/Idle/idle18.png")
            };
            
            // ⭐ HURT - 4 FRAMES
            hurtTextures = new Texture[]{
                new Texture("assets/sprites/sbires/Rogue/Hurt/hurt1.png"),
                new Texture("assets/sprites/sbires/Rogue/Hurt/hurt2.png"),
                new Texture("assets/sprites/sbires/Rogue/Hurt/hurt3.png"),
                new Texture("assets/sprites/sbires/Rogue/Hurt/hurt4.png")
            };
            
            // ⭐ WALK - 6 FRAMES
            walkTextures = new Texture[]{
                new Texture("assets/sprites/sbires/Rogue/Walk/walk1.png"),
                new Texture("assets/sprites/sbires/Rogue/Walk/walk2.png"),
                new Texture("assets/sprites/sbires/Rogue/Walk/walk3.png"),
                new Texture("assets/sprites/sbires/Rogue/Walk/walk4.png"),
                new Texture("assets/sprites/sbires/Rogue/Walk/walk5.png"),
                new Texture("assets/sprites/sbires/Rogue/Walk/walk6.png")
            };
            
            attackTextures = new Texture[]{
                new Texture("assets/sprites/sbires/Rogue/Attack/Attack1.png"),
                new Texture("assets/sprites/sbires/Rogue/Attack/Attack2.png"),
                new Texture("assets/sprites/sbires/Rogue/Attack/Attack3.png"),
                new Texture("assets/sprites/sbires/Rogue/Attack/Attack4.png"),
                new Texture("assets/sprites/sbires/Rogue/Attack/Attack5.png"),
                new Texture("assets/sprites/sbires/Rogue/Attack/Attack6.png"),
                new Texture("assets/sprites/sbires/Rogue/Attack/Attack7.png")
            };

            
            // ⭐ DEATH - 10 FRAMES
            deadTextures = new Texture[]{
                new Texture("assets/sprites/sbires/Rogue/Death/death1.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death2.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death3.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death4.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death5.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death6.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death7.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death8.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death9.png"),
                new Texture("assets/sprites/sbires/Rogue/Death/death10.png")
            };
            
            System.out.println("✅ Tous les sprites Rogue chargés avec succès !");
            System.out.println("   🎭 Idle: " + idleTextures.length + " frames");
            System.out.println("   💢 Hurt: " + hurtTextures.length + " frames");
            System.out.println("   🚶 Walk: " + walkTextures.length + " frames");
            System.out.println("   ⚔️  Attack: " + attackTextures.length + " frames");
            System.out.println("   💀 Death: " + deadTextures.length + " frames");
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR chargement sprites Rogue: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback sur Hugo en cas d'erreur
            System.err.println("⚠️ Utilisation du fallback Hugo...");
            try {
                idleTextures = new Texture[]{ 
                    new Texture("assets/sprites/Hugo/Idle/idle1.png") 
                };
                hurtTextures = new Texture[]{ 
                    new Texture("assets/sprites/Hugo/Hurt/hurt1.png") 
                };
                walkTextures = new Texture[]{
                    new Texture("assets/sprites/Hugo/Walk/walk1.png"),
                    new Texture("assets/sprites/Hugo/Walk/walk2.png"),
                    new Texture("assets/sprites/Hugo/Walk/walk3.png"),
                    new Texture("assets/sprites/Hugo/Walk/walk4.png"),
                    new Texture("assets/sprites/Hugo/Walk/walk5.png"),
                    new Texture("assets/sprites/Hugo/Walk/walk6.png")
                };
                attackTextures = walkTextures;
                deadTextures = idleTextures;
                System.err.println("✅ Fallback Hugo chargé");
            } catch (Exception e2) {
                System.err.println("❌❌ Échec total du chargement !");
                e2.printStackTrace();
            }
        }
    }

    @Override
    protected void disposeTextures() {
        if (idleTextures != null) {
            for (Texture t : idleTextures) if (t != null) t.dispose();
        }
        if (hurtTextures != null) {
            for (Texture t : hurtTextures) if (t != null) t.dispose();
        }
        if (walkTextures != null) {
            for (Texture t : walkTextures) if (t != null) t.dispose();
        }
        if (attackTextures != null && attackTextures != walkTextures) {
            for (Texture t : attackTextures) if (t != null) t.dispose();
        }
        if (deadTextures != null && deadTextures != idleTextures) {
            for (Texture t : deadTextures) if (t != null) t.dispose();
        }
    }

    protected Texture getCurrentTexture() {
        if (idleTextures == null || idleTextures.length == 0) {
            return null;
        }
        
        switch (currentState) {
            case IDLE:
                // Animation idle avec 12 frames
                return idleTextures[idleFrame % idleTextures.length];
                
            case PATROL:
            case CHASE:
                return walkTextures != null && walkTextures.length > 0 
                    ? walkTextures[walkFrame % walkTextures.length] 
                    : idleTextures[0];
                
            case ATTACK:
                return attackTextures != null && attackTextures.length > 0 
                    ? attackTextures[attackFrame % attackTextures.length] 
                    : idleTextures[0];
                
            case HIT:
                return hurtTextures != null && hurtTextures.length > 0 
                    ? hurtTextures[hurtFrame % hurtTextures.length] 
                    : idleTextures[0];
                
            case DEAD:
                return deadTextures != null && deadTextures.length > 0 
                    ? deadTextures[Math.min(deadFrame, deadTextures.length - 1)] 
                    : idleTextures[0];
                
            default:
                return idleTextures[0];
        }
    }
    
    @Override
    protected void updateAnimation(float delta) {
        animationTimer += delta;
        
        switch (currentState) {
            case IDLE:
                // Animation idle (plus lente)
                if (animationTimer >= 0.1f) {
                    idleFrame = (idleFrame + 1) % getIdleFrameCount();
                    animationTimer = 0f;
                }
                break;
                
            case PATROL:
            case CHASE:
                if (animationTimer >= walkFrameDuration) {
                    walkFrame = (walkFrame + 1) % getWalkFrameCount();
                    animationTimer = 0f;
                }
                break;
                
            case ATTACK:
                if (animationTimer >= attackFrameDuration) {
                    attackFrame = (attackFrame + 1) % getAttackFrameCount();
                    animationTimer = 0f;
                }
                break;
                
            case HIT:
                // Animation hurt
                if (animationTimer >= 0.1f) {
                    hurtFrame = (hurtFrame + 1) % getHurtFrameCount();
                    animationTimer = 0f;
                }
                break;
                
            case DEAD:
                if (animationTimer >= deadFrameDuration && deadFrame < getDeadFrameCount() - 1) {
                    deadFrame++;
                    animationTimer = 0f;
                }
                break;
                
            default:
                idleFrame = 0;
                walkFrame = 0;
                attackFrame = 0;
                hurtFrame = 0;
                animationTimer = 0f;
                break;
        }
    }

    @Override
    protected void initStats() {
        this.maxHealth = 50;
        this.health = maxHealth;
        this.damage = 10;
        this.detectionRange = 400f;
        this.attackRange = 80f;
    }

    protected int getIdleFrameCount() {
        return idleTextures != null ? idleTextures.length : 1;
    }
    
    protected int getHurtFrameCount() {
        return hurtTextures != null ? hurtTextures.length : 1;
    }

    @Override
    protected int getWalkFrameCount() {
        return walkTextures != null ? walkTextures.length : 1;
    }

    @Override
    protected int getAttackFrameCount() {
        return attackTextures != null ? attackTextures.length : 1;
    }

    @Override
    protected int getDeadFrameCount() {
        return deadTextures != null ? deadTextures.length : 1;
    }

    @Override
    protected float getHitboxWidth() {
        return Constants.ENEMY_HITBOX_WIDTH;
    }

    @Override
    protected float getHitboxHeight() {
        return Constants.ENEMY_HITBOX_HEIGHT;
    }

    @Override
    protected float getHitboxOffsetX() {
        return Constants.ENEMY_HITBOX_OFFSET_X;
    }

    @Override
    protected float getHitboxOffsetY() {
        return Constants.ENEMY_HITBOX_OFFSET_Y;
    }

    // ⭐ HITBOX DIRECTIONNELLE : Réduite de 25% du côté où le Rogue regarde
    @Override
    protected boolean useDirectionalHitbox() {
        return true; // Rogue utilise une hitbox directionnelle
    }
    
    @Override
    protected float getDirectionalHitboxWidth() {
        // Largeur réduite de 25%
        return Constants.ENEMY_HITBOX_WIDTH * 0.75f; // 85 * 0.75 = 63.75f
    }
    
    @Override
    protected float getDirectionalHitboxOffsetX() {
        float fullWidth = Constants.ENEMY_HITBOX_WIDTH;
        float reducedWidth = getDirectionalHitboxWidth();
        float reduction = fullWidth - reducedWidth; // 21.25f
        
        if (facingRight) {
            // Regarde à droite : réduction sur le bord droit
            // Garde le bord gauche fixe, donc même offset
            return Constants.ENEMY_HITBOX_OFFSET_X;
        } else {
            // Regarde à gauche : réduction sur le bord gauche
            // Décale vers la droite pour garder le bord droit fixe
            return Constants.ENEMY_HITBOX_OFFSET_X + reduction;
        }
    }

    // ⭐ MÉTHODE RENDER MANQUANTE !
    @Override
    public void render(SpriteBatch batch) {
        Texture currentTexture = getCurrentTexture();
        
        if (currentTexture == null) {
            return;
        }
        
        // Rendu similaire au Player mais adapté aux ennemis
        if (currentState == State.DEAD) {
            // Mort : sprite couché au sol
            float rotatedWidth = Constants.ENEMY_HEIGHT;
            float rotatedHeight = Constants.ENEMY_WIDTH;
            float originX = rotatedWidth / 2f;
            float originY = rotatedHeight / 2f;
            float rotation = facingRight ? -90f : 90f;
            float scaleX = facingRight ? 1f : -1f;
            
            batch.draw(
                currentTexture,
                x, y,
                originX, originY,
                rotatedWidth, rotatedHeight,
                scaleX, 1f,
                rotation,
                0, 0,
                currentTexture.getWidth(), currentTexture.getHeight(),
                false, false
            );
        } else {
            // Vivant : sprite normal avec flip selon direction
            if (facingRight) {
                batch.draw(currentTexture, x, y, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT);
            } else {
                // Flip horizontal quand regarde à gauche
                batch.draw(currentTexture, x + Constants.ENEMY_WIDTH, y, -Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT);
            }
        }
    }
}