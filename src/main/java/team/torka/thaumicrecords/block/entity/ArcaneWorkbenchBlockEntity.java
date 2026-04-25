package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneWorkbenchBlockEntity extends BlockEntity/* implements MenuProvider*/ {

    private final ItemStackHandler inventory = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public ArcaneWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ARCANE_WORKBENCH.get(), pos, state);
    }

//    @NotNull
//    @Override
//    public Component getDisplayName() {
//        return Component.translatable("container.thaumicrecords.arcane_worktable");
//    }

//    @Nullable
//    @Override
//    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
//        return new ArcaneWorktableMenu(id, inventory, this);
//    }


    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        super.onDataPacket(net, pkt, registries);
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }
}