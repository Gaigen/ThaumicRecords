package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.block.AspectRenderable;
import team.torka.thaumicrecords.api.node.NodeModifier;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.NodeModifierRegistry;
import team.torka.thaumicrecords.registry.NodeTypeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class AuraNodeBlockEntity extends BlockEntity implements AspectRenderable {
    private String id;
    private ResourceLocation type;
    private ResourceLocation modifier;
    private final AspectList limit = new AspectList();
    private final AspectList current = new AspectList();

    private int tickCount = 0;
    private int waitAfterDrain = 0;

    public AuraNodeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.AURA_NODE.get(), pos, blockState);


        // TODO for test purpose
        Random random = new Random();
        var registeredTypes = NodeTypeRegistry.REGISTRAR.getEntries().stream().toList();
        type = registeredTypes.get(random.nextInt(registeredTypes.size())).getId();
        var registeredModifiers = NodeModifierRegistry.REGISTRAR.getEntries().stream().toList();
        modifier = registeredModifiers.get(random.nextInt(registeredModifiers.size())).getId();


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

    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source) {
        NodeType nodeType = NodeTypeRegistry.NODE_TYPE_REGISTRY.get(type);
        NodeModifier nodeModifier = NodeModifierRegistry.NODE_MODIFIER_REGISTRY.get(modifier);
        if (Objects.isNull(nodeType) || Objects.isNull(nodeModifier)) {
            return;
        }
        nodeType.onRandomTick(state, level, pos, source);
    }

    public static void onTick(Level level, BlockPos pos, BlockState state, AuraNodeBlockEntity be) {
        NodeType nodeType = NodeTypeRegistry.NODE_TYPE_REGISTRY.get(be.type);
        NodeModifier nodeModifier = NodeModifierRegistry.NODE_MODIFIER_REGISTRY.get(be.modifier);
        if (Objects.isNull(nodeType) || Objects.isNull(nodeModifier)) {
            return;
        }
        if (level.isClientSide) {
            return;
        }
        be.tickCount++;
        if (be.waitAfterDrain > 0) {
            be.waitAfterDrain--;
        }

        int regenFrequency = nodeType.getRegenFrequency();
        double regenFrequencyModifier = nodeModifier.getRegenFrequencyModifier();
        int actualRegenFrequency = (int) (regenFrequency * regenFrequencyModifier);
        if (actualRegenFrequency > 0 && be.waitAfterDrain == 0 && be.tickCount % actualRegenFrequency == 0) {
            be.handleNodeRegen(level);
        }
        nodeType.onTick(level, pos, state, be);
    }

    private void handleNodeRegen(Level level) {
        ArrayList<ResourceLocation> toRegenAspects = new ArrayList<>();
        for (var aspect : limit.keySet()) {
            if (current.getOrZero(aspect) < limit.getOrZero(aspect)) {
                toRegenAspects.add(aspect);
            }
        }
        if (toRegenAspects.isEmpty()) {
            return;
        }
        ResourceLocation regenAspect = toRegenAspects.get(level.random.nextInt(toRegenAspects.size()));
        current.add(regenAspect, 1);
        this.syncToClient();
    }

    public String getId() {
        if (Objects.isNull(id)) {
            if (Objects.nonNull(level)) {
                BlockPos blockPos = getBlockPos();
                this.id = level.dimension().location() + "|" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
            } else {
                return "unloaded|0,0,0";
            }
        }
        return id;
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
            this.type = ResourceLocation.parse(tag.getString("nodeType"));
        }
        if (tag.contains("nodeModifier", Tag.TAG_STRING)) {
            this.modifier = ResourceLocation.parse(tag.getString("nodeModifier"));
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
        tag.putString("nodeType", this.type.toString());
        tag.putString("nodeModifier", this.modifier.toString());
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

    public void syncToClient() {
        if (Objects.nonNull(level) && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ResourceLocation getNodeType() {
        return type;
    }

    public ResourceLocation getNodeModifier() {
        return modifier;
    }

    public AspectList getCurrentAspect() {
        return current;
    }

    public AspectList getLimitAspect() {
        return limit;
    }

    public void setNodeType(ResourceLocation nodeType) {
        this.type = nodeType;
    }

    public void setNodeModifier(ResourceLocation nodeModifier) {
        this.modifier = nodeModifier;
    }

    public int drainAspect(ResourceLocation aspectId, int amount, boolean preserve) {
        int currentAmt = this.current.getOrZero(aspectId);
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
            this.syncToClient();
            return toDrain;
        }
        return 0;
    }

    @Override
    public AspectList getAspectRendered() {
        AspectList temp = new AspectList();
        limit.forEach((k, v) -> {
            if (current.getOrZero(k) > 0) {
                temp.put(k, v);
            } else {
                temp.put(k, 0);
            }
        });
        current.forEach((k, v) -> {
            if (!temp.containsKey(k)) {
                temp.add(k, v);
            }
        });
        return temp;
    }

    @Override
    public float getRenderYOffset() {
        return 0;
    }
}
