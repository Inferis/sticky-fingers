package org.inferis.stickyfingers.mixin;

import org.inferis.stickyfingers.EntityVacuum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayNetworkHandler;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayerMoveMixin {

    @Inject(at = @At("RETURN"), method = "onPlayerMove(Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket;)V")
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo callbackInfo) {
        var player = ((ServerPlayNetworkHandler)(Object)this).player;
        var entities = player.getWorld().getEntitiesByClass(
            ItemEntity.class, 
            player.getBoundingBox().expand(EntityVacuum.MAX_DISTANCE), 
            EntityPredicates.VALID_ENTITY
        );
        for (var itemEntity: entities) {
            EntityVacuum.tryVacuum(itemEntity, player, true);
        }
    }
}
