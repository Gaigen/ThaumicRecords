package team.torka.thaumicrecords.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;

/**
 * Shield charge sync payload. Sent to client to update HUD.
 */
public record RunicShieldPayload(int currentCharge, int maxCharge) implements CustomPacketPayload {

    public static final Type<RunicShieldPayload> TYPE = new Type<>(ThaumicRecords.createRl("runic_shield"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RunicShieldPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT,
            RunicShieldPayload::currentCharge, ByteBufCodecs.VAR_INT, RunicShieldPayload::maxCharge, RunicShieldPayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
