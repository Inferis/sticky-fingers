package org.inferis.stickyfingers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import org.inferis.stickyfingers.items.MagnetItem;
import org.inferis.stickyfingers.items.ModItems;
import org.inferis.stickyfingers.items.MagnetItem.Mode;
import org.inferis.stickyfingers.networking.ModeChangePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StickyFingers implements ModInitializer {
	public static final String MODID = "stickyfingers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		ModItems.registerItems();
		EntityVacuum.registerEventListeners();

		PayloadTypeRegistry.playC2S().register(ModeChangePayload.ID, ModeChangePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ModeChangePayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				StickyFingers.LOGGER.info("mode change received");
				MagnetItem.mutateMagnetsOfPlayer(context.player(), (stack, item) -> {
					item.setMode(stack, payload.active() ? Mode.ACTIVE : Mode.INACTIVE);
				});
			});
		});
	}
}