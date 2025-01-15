package org.inferis.stickyfingers.items;

import org.inferis.stickyfingers.StickyFingers;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item MAGNET;

    interface ItemRegistration<T> {
        T makeItem(RegistryKey<Item> key);
    }

    private static <T extends Item> T registerItem(String name, ItemRegistration<T> itemRegistration) {
        StickyFingers.LOGGER.info("Registering item: " + StickyFingers.MODID + ":" + name);
        Identifier identifier = Identifier.of(StickyFingers.MODID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, identifier);
        T item = itemRegistration.makeItem(key);
        return Registry.register(Registries.ITEM, identifier, item);
    }

    public static void registerItems() {
        MAGNET = registerItem(
            MagnetItem.IDENTIFIER, 
            key -> {
                return new MagnetItem(new Item.Settings());
            }
        );

        // Add magnet to "Tools" group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
	        content.add(MAGNET);
        });
    }
}