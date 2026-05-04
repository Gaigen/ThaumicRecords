package team.torka.thaumicrecords.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;

public record PlayerCombineAspectPayload(BlockPos blockPos, ResourceLocation left, ResourceLocation right) implements CustomPacketPayload {
    public static final Type<PlayerCombineAspectPayload> TYPE = new Type<>(ThaumicRecords.createRl("player_combine_aspect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerCombineAspectPayload> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC,
            PlayerCombineAspectPayload::blockPos, ResourceLocation.STREAM_CODEC, PlayerCombineAspectPayload::left, ResourceLocation.STREAM_CODEC,
            PlayerCombineAspectPayload::right, PlayerCombineAspectPayload::new);

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
