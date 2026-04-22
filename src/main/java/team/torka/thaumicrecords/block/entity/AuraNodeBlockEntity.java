package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

public class AuraNodeBlockEntity extends BlockEntity {
    private Holder<NodeType> type = NodeTypeRegistry.NORMAL;
    private Holder<NodeModifier> modifier = NodeModifierRegistry.NORMAL;
    private final AspectList limit = new AspectList();
    private final AspectList current = new AspectList();

    public AuraNodeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.AURA_NODE.get(), pos, blockState);
        limit.put(AspectRegistry.VITIUM.getId(), 10);
        current.put(AspectRegistry.VITIUM.getId(), 10);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AuraNodeBlockEntity be) {
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
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
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        this.type.unwrapKey().ifPresent(key -> tag.putString("nodeType", key.location().toString()));
        this.modifier.unwrapKey().ifPresent(key -> tag.putString("nodeModifier", key.location().toString()));
        tag.put("aspectsLimit", this.limit.writeToNBT());
        tag.put("aspectsCurrent", this.current.writeToNBT());
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
}
