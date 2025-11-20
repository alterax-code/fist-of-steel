package com.fistofsteel.entities.managers;  // ✅ MODIFIÉ (était com.fistofsteel.entities)

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.entities.player.Player;  // ✅ AJOUT
import com.fistofsteel.entities.world.ItemPickup;  // ✅ AJOUT
import com.fistofsteel.items.Item;
import com.fistofsteel.items.Potion;

// ... reste du code inchangé

public class WorldItemManager {

    private Array<ItemPickup> pickups = new Array<>();

    public void update(float delta) {
        for (ItemPickup pickup : pickups) {
            pickup.update(delta);
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


    public void dispose() {
        for (ItemPickup pickup : pickups) {
            pickup.dispose();
        }
        pickups.clear();
    }
}
