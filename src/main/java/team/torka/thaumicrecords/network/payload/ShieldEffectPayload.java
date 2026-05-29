package team.torka.thaumicrecords.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;

/**
 * Port of TC4's PacketFXShield.
 * Sends shield hit effect to nearby clients.
 * target >= 0: entity ID of damage source
 * target = -1: no source (environmental/generic)
 * target = -2: fall damage
 * target = -3: falling block damage
 */
public record ShieldEffectPayload(int sourceEntityId, int targetEntityId) implements CustomPacketPayload {

    public static final Type<ShieldEffectPayload> TYPE = new Type<>(ThaumicRecords.createRl("shield_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldEffectPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT,
            ShieldEffectPayload::sourceEntityId, ByteBufCodecs.VAR_INT, ShieldEffectPayload::targetEntityId, ShieldEffectPayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
