package org.inferis.stickyfingers.items;

import org.inferis.stickyfingers.StickyFingers;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item MAGNET;

    private static <T extends Item> T registerItem(String name, T item) {
        StickyFingers.LOGGER.info("Registering item: " + StickyFingers.MODID + ":" + name);
        return Registry.register(Registries.ITEM, Identifier.of(StickyFingers.MODID + ":" + name), item);
    }

    public static void registerItems() {
        MAGNET = registerItem(
            MagnetItem.IDENTIFIER, 
            new MagnetItem(new Item.Settings())
        );

        // Add magnet to "Tools" group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
	        content.add(MAGNET);
        });
    }
}