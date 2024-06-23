package org.inferis.stickyfingers;

import org.inferis.stickyfingers.items.MagnetItem;
import org.inferis.stickyfingers.items.MagnetItem.Mode;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
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

        if (entity.isRemoved()) {
            return;
        }

        // we can only process item and xp orb entities
        var isItemEntity = entity instanceof ItemEntity;
        var isXpEntity = entity instanceof ExperienceOrbEntity;
        if (!isItemEntity && !isXpEntity) {
            return;
        }

        for (var player: world.getPlayers()) {
            if (tryVacuum(entity, (PlayerEntity)player, true)) {
                return;
            }
        }
    }

    public static void tryVacuumAround(PlayerEntity player) {
        // find item entities around the player first
        var itemEntities = player.getWorld().getEntitiesByClass(
            ItemEntity.class, 
            player.getBoundingBox().expand(EntityVacuum.MAX_DISTANCE), 
            EntityPredicates.VALID_ENTITY
        );
        for (var itemEntity: itemEntities) {
            EntityVacuum.tryVacuum(itemEntity, player, false);
        }

        // then xp orbs
        var xpEntities = player.getWorld().getEntitiesByClass(
            ExperienceOrbEntity.class, 
            player.getBoundingBox().expand(EntityVacuum.MAX_DISTANCE), 
            EntityPredicates.VALID_ENTITY
        );
        for (var xpEntity: xpEntities) {
            EntityVacuum.tryVacuum(xpEntity, player, false);
        }
    }

    // Try to vacuum/process one entity for a player.
    public static boolean tryVacuum(Entity entity, PlayerEntity player, boolean resetDelay) {
        var mode = MagnetItem.magnetModeOfPlayer(player);
        if (!player.isSpectator() && mode == Mode.ACTIVE) {
            var maxDistance = MAX_DISTANCE;
            // if the player is in range of the entity, pretend they ran
            // into it so they pick it up. For freshly spawned items, we also
            // need to reset the 
            if (player.isInRange(entity, maxDistance)) {
                if (entity instanceof ItemEntity itemEntity) { 
                    itemEntity.setPickupDelay(0);
                }
                entity.onPlayerCollision(player);
                return true;
            }
        }
        return false;
    }
}
