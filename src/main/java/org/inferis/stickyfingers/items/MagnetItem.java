package org.inferis.stickyfingers.items;

import org.inferis.stickyfingers.StickyFingers;
import org.inferis.stickyfingers.items.MagnetItem.Mode;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MagnetItem extends Item {
    public static String IDENTIFIER = "magnet";
    private static final String NBT_MAGNET_MODE = "mode";

    public MagnetItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType data) {
        super.appendTooltip(stack, context, tooltip, data);
        tooltip.add(getMode(stack).toText());
    }

    public void toggleMode(ItemStack stack) {
        withTag(stack, nbt -> {
            var mode = getMode(stack) == Mode.INACTIVE ? Mode.ACTIVE : Mode.INACTIVE;
            nbt.putBoolean(NBT_MAGNET_MODE, mode.toBoolean());
            return mode;
        });
    }

    public void setMode(ItemStack stack, Mode mode) {
        withTag(stack, nbt -> {
            nbt.putBoolean(NBT_MAGNET_MODE, mode.toBoolean());
            return mode;
        });
    }

    public Mode getMode(ItemStack stack) {
        return withTag(stack, nbt -> {
            return nbt.getBoolean(NBT_MAGNET_MODE) ? Mode.ACTIVE : Mode.INACTIVE;
        });
    }

    private interface ITagHandler<T> {
        T handle(NbtCompound nbt);
    }

    private <T> T withTag(ItemStack stack, ITagHandler<T> handler) {
        if (!stack.isEmpty()) {
            NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
            if (nbt == null) {
                nbt = new NbtCompound();
            }
            if (!nbt.contains(NBT_MAGNET_MODE)) {
                // initialize default to inactive
                nbt.putBoolean(NBT_MAGNET_MODE, false);
            }
            T value = handler.handle(nbt);
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            return value;
        }
        return null;
    } 
    
    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasGlint(ItemStack stack) {
        return getMode(stack) == Mode.ACTIVE;
    }
    
    public interface IMagnetMutator {
        void mutate(ItemStack stack, MagnetItem item, Mode mode);
    }

    public static Mode magnetModeOfPlayer(PlayerEntity player) {
        return mutateMagnetsOfPlayer(player, null);
    }

    public static Mode mutateMagnetsOfPlayer(PlayerEntity player, IMagnetMutator mutator) {
        var mode = Mode.INACTIVE;
        var inventory = player.getInventory();

        // Get the current mode. This is the mode of the first magnet in the inventory.
        for (var slot=0; slot<inventory.size(); ++slot) {
            var stack = inventory.getStack(slot);
            if (stack.getItem() instanceof MagnetItem magnetItem) {
                mode = magnetItem.getMode(stack);
                break;
            }
        }

        // If a mutator was specified, run it against all the magnets in the
        // inventory, passing in the mode of the first one as a "source of truth".
        if (mutator != null) {
            for (var slot=0; slot<inventory.size(); ++slot) {
                var stack = inventory.getStack(slot);
                if (stack.getItem() instanceof MagnetItem magnetItem) { 
                    mutator.mutate(stack, magnetItem, mode);
                }
            }
        }

        return mode;
    }

    public enum Mode {
        ACTIVE(true),
        INACTIVE(false);

        final boolean state;

        private Mode(boolean state) {
            this.state = state;
        }

        public boolean toBoolean() {
            return state;
        }

        public String toString() {
            return "tooltip.stickyfingers.magnet." + (toBoolean() ? "" : "in") + "active";
        }

        public Text toText() {
            return Text.translatable(toString())
                .formatted(state ? Formatting.GREEN : Formatting.RED);
        }
    }
}
