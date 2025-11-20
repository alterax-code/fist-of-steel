package com.fistofsteel.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.items.Item;
import com.fistofsteel.items.Potion;
import com.fistofsteel.items.Armor;
import com.fistofsteel.items.LightArmor;
import com.fistofsteel.items.HeavyArmor;
import com.fistofsteel.items.Weapon;
import com.fistofsteel.items.Sword1;
import com.fistofsteel.items.Sword2;
import com.fistofsteel.items.Sword3;


public class WorldItemManager {

    private Array<ItemPickup> pickups = new Array<>();

    public void update(float delta, Player player) {
        for (ItemPickup pickup : pickups) {
            pickup.update(delta);
        }

        // collisions
        Rectangle playerBounds = player.getHitbox();
        for (ItemPickup pickup : pickups) {
            if (!pickup.isCollected() && pickup.getBounds().overlaps(playerBounds)) {
                pickup.onPickup(player);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (ItemPickup pickup : pickups) {
            pickup.render(batch);
        }
    }

    // Collision joueur ↔ items
    public void checkPlayerCollisions(Player player) {
        Rectangle playerBounds = player.getHitbox();

        for (ItemPickup pickup : pickups) {
            if (!pickup.isCollected() && pickup.getBounds().overlaps(playerBounds)) {
                pickup.onPickup(player);
            }
        }

        // Nettoyage des items ramassés
        for (int i = pickups.size - 1; i >= 0; i--) {
            if (pickups.get(i).isCollected()) {
                pickups.removeIndex(i);
            }
        }
    }

    // Pour tester : spawn d’une potion de soin +30 PV
    public void spawnHealPotion(float x, float y) {
        Texture tex = new Texture("assets/items/potion_frame_1.png");
        Item item = new Potion("heal_small", "Potion de soin", 30);

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("💊 Potion spawn à (). Total pickups = " + pickups.size);
    }

    public void spawnArmorHeavy(float x, float y) {
        Texture tex = new Texture("assets/items/armor_heavy.png");
        Item item = new HeavyArmor();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("🛡 Armure lourde spawn à (" + x + ", " + y + "). Total pickups = " + pickups.size);
    }

    public void spawnArmorLight(float x, float y) {
        Texture tex = new Texture("assets/items/armor_light.png");
        Item item = new LightArmor();   // bonus défini dans LightArmor

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("🛡 Armure légère spawn à (" + x + ", " + y + "). Total pickups = " + pickups.size);
    }

    public void spawnSword1(float x, float y) {
        Texture tex = new Texture("assets/items/sword_1.png"); // ⚠ chemin à adapter
        Item item = new Sword1();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("🗡 Sword1 spawn à (" + x + ", " + y + "). Total pickups = " + pickups.size);
    }

    public void spawnSword2(float x, float y) {
        Texture tex = new Texture("assets/items/sword_2.png"); // ⚠ chemin à adapter
        Item item = new Sword2();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("🗡 Sword2 spawn à (" + x + ", " + y + "). Total pickups = " + pickups.size);
    }

    public void spawnSword3(float x, float y) {
        Texture tex = new Texture("assets/items/sword_3.png"); // ⚠ chemin à adapter
        Item item = new Sword3();

        ItemPickup pickup = new ItemPickup(x, y, tex, item);
        pickups.add(pickup);

        System.out.println("🗡 Sword3 spawn à (" + x + ", " + y + "). Total pickups = " + pickups.size);
    }



    public void dispose() {
        for (ItemPickup pickup : pickups) {
            pickup.dispose();
        }
        pickups.clear();
    }
}
