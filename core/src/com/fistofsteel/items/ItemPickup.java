package com.fistofsteel.entities.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.items.Item;

/**
 * Item ramassable dans le monde du jeu.
 * Gère l'affichage et la collecte de l'item.
 */
public class ItemPickup {

    private float x, y;
    private Texture texture;
    private Item item;
    private boolean collected = false;
    private Rectangle bounds;

    private float drawWidth;
    private float drawHeight;

    /**
     * Constructeur d'un item ramassable.
     * 
     * @param x Position X
     * @param y Position Y
     * @param texture La texture de l'item
     * @param item L'item contenu
     */
    public ItemPickup(float x, float y, Texture texture, Item item) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.item = item;

        if (texture != null) {
            float texW = texture.getWidth();
            float texH = texture.getHeight();
            float maxSize = 64f;

            if (texH >= texW) {
                float scale = maxSize / texH;
                drawHeight = maxSize;
                drawWidth = texW * scale;
            } else {
                float scale = maxSize / texW;
                drawWidth = maxSize;
                drawHeight = texH * scale;
            }
        } else {
            drawWidth = drawHeight = 64f;
        }

        bounds = new Rectangle(x, y, drawWidth, drawHeight);
    }

    /**
     * Met à jour l'item.
     * 
     * @param delta Le temps écoulé
     */
    public void update(float delta) {
    }

    /**
     * Affiche l'item.
     * 
     * @param batch Le SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        if (collected || texture == null) return;
        batch.draw(texture, x, y, drawWidth, drawHeight);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isCollected() {
        return collected;
    }

    /**
     * Appelé quand le joueur ramasse l'item.
     * 
     * @param player Le joueur
     */
    public void onPickup(Player player) {
        if (collected) return;
        collected = true;

        if (item != null) {
            item.applyEffect(player);
        }

        System.out.println("Item ramasse : " + (item != null ? item.getDisplayName() : "???"));
    }

    /**
     * Libère les ressources.
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}