package com.fistofsteel.entities.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.entities.world.ItemPickup;
import com.fistofsteel.items.Item;
import com.fistofsteel.items.Potion;
import com.fistofsteel.items.LightArmor;
import com.fistofsteel.items.HeavyArmor;
import com.fistofsteel.items.Sword1;
import com.fistofsteel.items.Sword2;
import com.fistofsteel.items.Sword3;

/**
 * Gestionnaire des items ramassables dans le monde.
 * Gère le spawn, l'affichage et la collecte des items.
 */
public class WorldItemManager {

    private Array<ItemPickup> pickups = new Array<>();

    /**
     * Met à jour tous les items.
     * 
     * @param delta Le temps écoulé
     */
    public void update(float delta) {
        for (ItemPickup pickup : pickups) {
            pickup.update(delta);
        }
    }

    /**
     * Affiche tous les items.
     * 
     * @param batch Le SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        for (ItemPickup pickup : pickups) {
            pickup.render(batch);
        }
    }

    /**
     * Vérifie les collisions entre le joueur et les items.
     * 
     * @param player Le joueur
     */
    public void checkPlayerCollisions(Player player) {
        Rectangle playerBounds = player.getHitbox();

        for (ItemPickup pickup : pickups) {
            if (!pickup.isCollected() && pickup.getBounds().overlaps(playerBounds)) {
                pickup.onPickup(player);
            }
        }

        for (int i = pickups.size - 1; i >= 0; i--) {
            if (pickups.get(i).isCollected()) {
                pickups.removeIndex(i);
            }
        }
    }

    /**
     * Fait apparaître une potion de soin.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void spawnHealPotion(float x, float y) {
        Texture tex = new Texture("assets/items/potion_frame_1.png");
        Item item = new Potion("heal_small", "Potion de soin", 30);

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("Potion spawn a (" + (int)x + ", " + (int)y + "). Total pickups = " + pickups.size);
    }

    /**
     * Fait apparaître une armure légère.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void spawnArmorLight(float x, float y) {
        Texture tex = new Texture("assets/items/armor_light.png");
        Item item = new LightArmor();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("Armure legere spawn a (" + (int)x + ", " + (int)y + "). Total pickups = " + pickups.size);
    }

    /**
     * Fait apparaître une armure lourde.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void spawnArmorHeavy(float x, float y) {
        Texture tex = new Texture("assets/items/armor_heavy.png");
        Item item = new HeavyArmor();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("Armure lourde spawn a (" + (int)x + ", " + (int)y + "). Total pickups = " + pickups.size);
    }

    /**
     * Fait apparaître une épée de base.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void spawnSword1(float x, float y) {
        Texture tex = new Texture("assets/items/sword_1.png");
        Item item = new Sword1();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("Epee de base spawn a (" + (int)x + ", " + (int)y + "). Total pickups = " + pickups.size);
    }

    /**
     * Fait apparaître une épée tranchante.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void spawnSword2(float x, float y) {
        Texture tex = new Texture("assets/items/sword_2.png");
        Item item = new Sword2();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("Epee tranchante spawn a (" + (int)x + ", " + (int)y + "). Total pickups = " + pickups.size);
    }

    /**
     * Fait apparaître une épée légendaire.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void spawnSword3(float x, float y) {
        Texture tex = new Texture("assets/items/sword_3.png");
        Item item = new Sword3();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("Epee legendaire spawn a (" + (int)x + ", " + (int)y + "). Total pickups = " + pickups.size);
    }

    /**
     * Libère les ressources.
     */
    public void dispose() {
        for (ItemPickup pickup : pickups) {
            pickup.dispose();
        }
        pickups.clear();
    }
}