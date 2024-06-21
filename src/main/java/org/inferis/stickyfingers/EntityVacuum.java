package org.inferis.stickyfingers;

import org.inferis.stickyfingers.items.MagnetItem;
import org.inferis.stickyfingers.items.MagnetItem.Mode;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class EntityVacuum {
    public static int MAX_DISTANCE = 8;

    public static void registerEventListeners() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			onEntitySpawn(entity, world);
		});
    }

    private static void onEntitySpawn(Entity entity, World world) {
        if (world.isClient) {
            return;
        }

        if (!(entity instanceof ItemEntity itemEntity) || entity.isRemoved()) {
            return;
        }

        for (var player: world.getPlayers()) {
            if (tryVacuum(itemEntity, (PlayerEntity)player, false)) {
                return;
            }
        }
    }

    public static boolean tryVacuum(ItemEntity itemEntity, PlayerEntity player, boolean resetDelay) {
        var mode = MagnetItem.magnetModeOfPlayer(player);
        if (!player.isSpectator() && mode == Mode.ACTIVE) {
            var maxDistance = MAX_DISTANCE;
            if (player.isInRange(itemEntity, maxDistance)) {
                if (resetDelay) { 
                    itemEntity.setPickupDelay(0);
                }
                itemEntity.onPlayerCollision(player);
                return true;
            }
        }
        return false;
    }
}
