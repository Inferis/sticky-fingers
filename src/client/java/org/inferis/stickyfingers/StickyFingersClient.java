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
				// Toggle the mode for all magnets
				var mode = MagnetItem.magnetModeOfPlayer(client.player);
				if (mode.isPresent()) {
					var newMode = mode.get() == Mode.ACTIVE ? Mode.INACTIVE : Mode.ACTIVE;
					MagnetItem.mutateMagnetsOfPlayer(client.player, (stack, item, currentMode) -> {
						item.setMode(stack, newMode);
					});
	
					// Since the keypress happens client side, we have to get the mode
					// change over to the server. So if that happens, get the mode of 
					// any of the magnets (in practice it will be the last one in the 
					// inventory). Then send that mode to the server so it can update
					// its status. 
					ClientPlayNetworking.send(new ModeChangePayload(newMode == Mode.ACTIVE));
				}
			}
		});
	}
}