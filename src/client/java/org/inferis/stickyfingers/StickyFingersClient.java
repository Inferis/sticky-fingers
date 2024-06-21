package org.inferis.stickyfingers;

import org.inferis.stickyfingers.items.MagnetItem;
import org.inferis.stickyfingers.items.MagnetItem.Mode;
import org.inferis.stickyfingers.networking.ModeChangePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class StickyFingersClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sticky-fingers");
	private static KeyBinding magnetModeKeyBinding;

	@Override
	public void onInitializeClient() {
		registerKeyBinding();
	}

	private static void registerKeyBinding() {
		magnetModeKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.stickyfingers.mode",
			InputUtil.Type.KEYSYM,
			InputUtil.GLFW_KEY_M,
			"category.stickyfingers.keys"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
		    if (magnetModeKeyBinding.wasPressed()) {
				var mode = MagnetItem.mutateMagnetsOfPlayer(client.player, (stack, item) -> {
					item.setMode(stack, item.getMode(stack) == Mode.ACTIVE ? Mode.INACTIVE : Mode.ACTIVE);
				});
				ClientPlayNetworking.send(new ModeChangePayload(mode == Mode.ACTIVE));
			}
		});
	}
}