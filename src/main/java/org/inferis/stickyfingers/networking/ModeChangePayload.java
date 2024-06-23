package org.inferis.stickyfingers.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ModeChangePayload(boolean isActive) implements CustomPayload {
    public static final CustomPayload.Id<ModeChangePayload> ID = new CustomPayload.Id<>(Identifier.of("stickyfingers:mode_change"));
    public static final PacketCodec<RegistryByteBuf, ModeChangePayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, ModeChangePayload::isActive, ModeChangePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

