package team.torka.thaumicrecords.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;

public record OreScanPayload(BlockPos center, int range) implements CustomPacketPayload {
    public static final Type<OreScanPayload> TYPE = new Type<>(ThaumicRecords.createRl("ore_scan"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OreScanPayload> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, OreScanPayload::center,
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT, OreScanPayload::range, OreScanPayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
