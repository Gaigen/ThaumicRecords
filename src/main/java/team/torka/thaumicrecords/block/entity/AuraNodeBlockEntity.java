package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.node.NodeModifier;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.NodeModifierRegistry;
import team.torka.thaumicrecords.registry.NodeTypeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class AuraNodeBlockEntity extends BlockEntity {
    private Holder<NodeType> type = NodeTypeRegistry.NORMAL;
    private Holder<NodeModifier> modifier = NodeModifierRegistry.NORMAL;
    private final AspectList limit = new AspectList();
    private final AspectList current = new AspectList();

    public AuraNodeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.AURA_NODE.get(), pos, blockState);
        limit.put(AspectRegistry.AER.getId(), 20);
        limit.put(AspectRegistry.IGNIS.getId(), 20);
        limit.put(AspectRegistry.AQUA.getId(), 20);
        limit.put(AspectRegistry.TERRA.getId(), 20);
        limit.put(AspectRegistry.ORDO.getId(), 20);
        limit.put(AspectRegistry.PERDITIO.getId(), 20);
        current.put(AspectRegistry.AER.getId(), 20);
        current.put(AspectRegistry.IGNIS.getId(), 20);
        current.put(AspectRegistry.AQUA.getId(), 20);
        current.put(AspectRegistry.TERRA.getId(), 20);
        current.put(AspectRegistry.ORDO.getId(), 20);
        current.put(AspectRegistry.PERDITIO.getId(), 20);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AuraNodeBlockEntity be) {
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("nodeType", Tag.TAG_STRING)) {
            ResourceLocation typeId = ResourceLocation.parse(tag.getString("nodeType"));
            registries.lookup(RegistryKeys.NODE_TYPES).flatMap(r -> r.get(ResourceKey.create(RegistryKeys.NODE_TYPES, typeId))).ifPresent(h -> this.type = h);
        }
        if (tag.contains("nodeModifier", Tag.TAG_STRING)) {
            ResourceLocation modifierId = ResourceLocation.parse(tag.getString("nodeModifier"));
            registries.lookup(RegistryKeys.NODE_MODIFIERS).flatMap(r -> r.get(ResourceKey.create(RegistryKeys.NODE_MODIFIERS, modifierId))).ifPresent(
                    h -> this.modifier = h);
        }
        if (tag.contains("aspectsLimit")) {
            this.limit.readFromNBT(tag.getCompound("aspectsLimit"));
        }
        if (tag.contains("aspectsCurrent")) {
            this.current.readFromNBT(tag.getCompound("aspectsCurrent"));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.type.unwrapKey().ifPresent(key -> tag.putString("nodeType", key.location().toString()));
        this.modifier.unwrapKey().ifPresent(key -> tag.putString("nodeModifier", key.location().toString()));
        tag.put("aspectsLimit", this.limit.writeToNBT());
        tag.put("aspectsCurrent", this.current.writeToNBT());
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(@NotNull Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        this.loadAdditional(tag, lookupProvider);
        if (Objects.nonNull(level) && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public Holder<NodeType> getNodeType() {
        return type;
    }

    public Holder<NodeModifier> getNodeModifier() {
        return modifier;
    }

    public AspectList getCurrentAspect() {
        return current;
    }

    public AspectList getLimitAspect() {
        return limit;
    }

    public int drainAspect(ResourceLocation aspectId, int amount, boolean preserve) {
        int currentAmt = this.current.get(aspectId);
        if (currentAmt <= 0) {
            return 0;
        }
        int toDrain = amount;
        if (preserve && toDrain >= currentAmt) {
            toDrain = currentAmt - 1;
        } else if (toDrain > currentAmt) {
            toDrain = currentAmt;
        }
        if (toDrain > 0) {
            this.current.add(aspectId, -toDrain);
            this.setChanged();
            if (Objects.nonNull(level) && !this.level.isClientSide) {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
            return toDrain;
        }
        return 0;
    }
}
