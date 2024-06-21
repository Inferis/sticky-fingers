package org.inferis.stickyfingers.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ModeChangePayload(boolean active) implements CustomPayload {
    public static final CustomPayload.Id<ModeChangePayload> ID = new CustomPayload.Id<>(NetworkingConstants.MODE_CHANGE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ModeChangePayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, ModeChangePayload::active, ModeChangePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

