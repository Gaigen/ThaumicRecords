package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.AspectHelper;
import team.torka.thaumicrecords.menu.DeconstructionTableMenu;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeconstructionTableBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer, ContainerData {

    private ItemStack inputStack = ItemStack.EMPTY;
    @Nullable
    private ResourceLocation currentAspect = null;
    private int breaktime = 0;
    private final java.util.Random random = new java.util.Random();

    @Nullable
    public DeconstructionTableMenu eventHandler;

    public DeconstructionTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.DECONSTRUCTION_TABLE.get(), pos, state);
    }

    // ========== TICK ==========

    public static void serverTick(Level level, BlockPos pos, BlockState state, DeconstructionTableBlockEntity be) {
        if (level.isClientSide) {
            return;
        }

        boolean changed = false;

        // If item in slot AND no current aspect AND item has aspects -> start breaking
        if (!be.inputStack.isEmpty() && be.currentAspect == null) {
            AspectList aspects = AspectHelper.getAspects(be.inputStack);
            if (!aspects.isEmpty()) {
                if (be.breaktime == 0) {
                    be.breaktime = 40;
                    changed = true;
                }
                // Decrement breaktime while item is still valid
                if (be.breaktime > 0) {
                    be.breaktime--;
                    changed = true;

                    if (be.breaktime == 0) {
                        // Break complete: resolve aspect
                        changed = be.resolveAspect();
                    }
                }
            } else {
                // Item has no aspects, reset
                if (be.breaktime > 0) {
                    be.breaktime = 0;
                    changed = true;
                }
            }
        } else {
            // No item or already has aspect, reset breaktime
            if (be.breaktime > 0) {
                be.breaktime = 0;
                changed = true;
            }
        }

        if (changed) {
            be.setChanged();
            be.syncToClient();
            // ContainerData broadcastChanges will sync breaktime automatically
        }
    }

    private boolean resolveAspect() {
        if (inputStack.isEmpty()) {
            return false;
        }

        AspectList allAspects = AspectHelper.getAspects(inputStack);
        if (allAspects.isEmpty()) {
            return false;
        }

        // Reduce all aspects to primals (recursive decomposition)
        AspectList primals = reduceToPrimals(allAspects);
        if (primals.isEmpty()) {
            return false;
        }

        // Random chance: pick if random(80) < total vis size
        int totalVis = primals.values().stream().mapToInt(Integer::intValue).sum();
        if (random.nextInt(80) >= totalVis) {
            inputStack.shrink(1);
            if (inputStack.isEmpty()) {
                inputStack = ItemStack.EMPTY;
            }
            return true;
        }

        // Pick a random primal aspect from the list (not weighted — original picks random index)
        List<ResourceLocation> primalKeys = new ArrayList<>(primals.keySet());
        currentAspect = primalKeys.get(random.nextInt(primalKeys.size()));

        // Consume item
        inputStack.shrink(1);
        if (inputStack.isEmpty()) {
            inputStack = ItemStack.EMPTY;
        }

        return true;
    }

    /**
     * Recursively decompose compound aspects into their primal components.
     * Original TC4: ResearchManager.reduceToPrimals(al)
     */
    private AspectList reduceToPrimals(AspectList aspects) {
        AspectList result = AspectList.empty();
        for (var entry : aspects.entrySet()) {
            ResourceLocation key = entry.getKey();
            int amount = entry.getValue();
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(key);
            if (aspect == null) {
                continue;
            }

            if (aspect.isPrimal()) {
                result.add(key, amount);
            } else {
                Aspect[] comps = aspect.getComponents();
                if (comps != null && comps.length == 2) {
                    ResourceLocation comp1 = AspectRegistry.ASPECT_REGISTRY.getResourceKey(comps[0]).map(k -> k.location()).orElse(null);
                    ResourceLocation comp2 = AspectRegistry.ASPECT_REGISTRY.getResourceKey(comps[1]).map(k -> k.location()).orElse(null);
                    if (comp1 != null && comp2 != null) {
                        AspectList decomposed = AspectList.empty();
                        decomposed.put(comp1, amount);
                        decomposed.put(comp2, amount);
                        AspectList reduced = reduceToPrimals(decomposed);
                        result.merge(reduced);
                    }
                }
            }
        }
        return result;
    }

    // ========== NBT ==========

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (!inputStack.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            inputStack.save(registries, itemTag);
            tag.put("Input", itemTag);
        }

        if (currentAspect != null) {
            tag.putString("CurrentAspect", currentAspect.toString());
        }

        tag.putInt("Breaktime", breaktime);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("Input")) {
            inputStack = ItemStack.parseOptional(registries, tag.getCompound("Input"));
        } else {
            inputStack = ItemStack.EMPTY;
        }

        if (tag.contains("CurrentAspect")) {
            currentAspect = ResourceLocation.parse(tag.getString("CurrentAspect"));
        } else {
            currentAspect = null;
        }

        breaktime = tag.getInt("Breaktime");
    }

    // ========== SYNC ==========

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        super.onDataPacket(net, pkt, registries);
        if (Objects.nonNull(level) && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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

    public void syncToClient() {
        if (Objects.nonNull(level) && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ========== INVENTORY DROP ==========

    public void dropInventory() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (!inputStack.isEmpty()) {
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, inputStack);
            inputStack = ItemStack.EMPTY;
        }
    }

    // ========== MENU ==========

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumicrecords.deconstruction_table");
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        DeconstructionTableMenu menu = new DeconstructionTableMenu(id, inv, this);
        this.eventHandler = menu;
        return menu;
    }

    // ========== Container ==========

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inputStack.isEmpty() && currentAspect == null;
    }

    public ItemStack getInputStack() {
        return inputStack;
    }

    public ItemStack getItem(int slot) {
        return slot == 0 ? inputStack : ItemStack.EMPTY;
    }

    @Override
    @ParametersAreNonnullByDefault
    public ItemStack removeItem(int slot, int amount) {
        if (slot != 0 || inputStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = inputStack.split(amount);
        if (inputStack.isEmpty()) {
            inputStack = ItemStack.EMPTY;
        }
        setChanged();
        return result;
    }

    @Override
    @ParametersAreNonnullByDefault
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack result = inputStack;
        inputStack = ItemStack.EMPTY;
        setChanged();
        return result;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            setChanged();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean stillValid(Player player) {
        if (level == null) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        inputStack = ItemStack.EMPTY;
        setChanged();
    }

    // ========== WorldlyContainer (hopper automation) ==========

    @Override
    @ParametersAreNonnullByDefault
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        // Cannot insert from top
        if (side == Direction.UP) {
            return false;
        }
        return canPlaceItem(slot, stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot != 0) {
            return false;
        }
        // Only accept items that have aspects
        AspectList aspects = AspectHelper.getAspects(stack);
        return !aspects.isEmpty();
    }

    // ========== Getters for Menu ==========

    public int getBreaktime() {
        return breaktime;
    }

    @Nullable
    public ResourceLocation getCurrentAspect() {
        return currentAspect;
    }

    public void clearCurrentAspect() {
        this.currentAspect = null;
        setChanged();
        syncToClient();
    }

    // ========== ContainerData ==========

    @Override
    public int get(int index) {
        return index == 0 ? breaktime : 0;
    }

    @Override
    public void set(int index, int value) {
        if (index == 0) {
            breaktime = value;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }
}
