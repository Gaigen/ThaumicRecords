package team.torka.thaumicrecords.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;

public record InfusionSourcePayload(BlockPos matrixPos, BlockPos sourcePos, ItemStack item) implements CustomPacketPayload {

    public static final Type<InfusionSourcePayload> TYPE = new Type<>(ThaumicRecords.createRl("infusion_source"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfusionSourcePayload> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC,
            InfusionSourcePayload::matrixPos, BlockPos.STREAM_CODEC, InfusionSourcePayload::sourcePos, ItemStack.STREAM_CODEC, InfusionSourcePayload::item,
            InfusionSourcePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
