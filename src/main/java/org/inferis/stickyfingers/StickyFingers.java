package org.inferis.stickyfingers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import org.inferis.stickyfingers.items.MagnetItem;
import org.inferis.stickyfingers.items.ModItems;
import org.inferis.stickyfingers.items.MagnetItem.Mode;
import org.inferis.stickyfingers.logic.EntityAttractor;
import org.inferis.stickyfingers.networking.ModeChangePayload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StickyFingers implements ModInitializer {
	public static final String MODID = "stickyfingers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		ModItems.registerItems();
		EntityAttractor.registerEventListeners();
		registerPayloadAndReceiver();
	}

    public void registerPayloadAndReceiver() {
        // Register Payload
		PayloadTypeRegistry.playC2S().register(ModeChangePayload.ID, ModeChangePayload.CODEC);

        // Register payload receiver. The client will send the payload to the server, which
        // will then in turn set the right mode on the magnet items.
		ServerPlayNetworking.registerGlobalReceiver(ModeChangePayload.ID, (payload, context) -> {
			context.server().execute(() -> {
                var newMode = payload.isActive() ? Mode.ACTIVE : Mode.INACTIVE;
				MagnetItem.mutateMagnetsOfPlayer(context.player(), (stack, item, currentMode) -> {
					item.setMode(stack, newMode);
				});
				EntityAttractor.tryAttractAround(context.player());
			});
		});
    }
}