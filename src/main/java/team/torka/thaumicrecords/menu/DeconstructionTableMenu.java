package team.torka.thaumicrecords.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.AspectHelper;
import team.torka.thaumicrecords.api.helper.ResearchHelper;
import team.torka.thaumicrecords.block.entity.DeconstructionTableBlockEntity;
import team.torka.thaumicrecords.registry.MenuRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class DeconstructionTableMenu extends AbstractContainerMenu {

    public final DeconstructionTableBlockEntity blockEntity;
    private final ContainerData data;
    public ItemStack cachedSlotStack = ItemStack.EMPTY;

    // Client constructor (from buf)
    public DeconstructionTableMenu(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(id, inv, (DeconstructionTableBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    // Server constructor
    public DeconstructionTableMenu(int id, Inventory inv, DeconstructionTableBlockEntity be) {
        super(MenuRegistry.DECONSTRUCTION_TABLE.get(), id);
        this.blockEntity = be;
        this.data = be;
        this.cachedSlotStack = be.getInputStack().copy();
        this.addDataSlots(data);

        // Input slot at (64, 16) — cached like thaumatorium to prevent flickering
        this.addSlot(new Slot(be, 0, 64, 16) {
            @Override
            public ItemStack getItem() {
                return cachedSlotStack;
            }

            @Override
            public void set(ItemStack stack) {
                cachedSlotStack = stack.copy();
                blockEntity.setItem(0, stack);
            }

            @Override
            public ItemStack remove(int amount) {
                if (cachedSlotStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                ItemStack result = cachedSlotStack.split(amount);
                if (cachedSlotStack.isEmpty()) {
                    cachedSlotStack = ItemStack.EMPTY;
                }
                blockEntity.setItem(0, cachedSlotStack);
                return result;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                AspectList aspects = AspectHelper.getAspects(stack);
                return !aspects.isEmpty();
            }

            @Override
            public int getMaxStackSize() {
                return 64;
            }
        });

        // Player inventory — same positions as ThaumatoriumMenu
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inv, j, 8 + j * 18, 142));
        }
    }

    public int getBreaktime() {
        return data.get(0);
    }

    /**
     * Get the current aspect from the block entity (synced via NBT update tag).
     */
    @javax.annotation.Nullable
    public ResourceLocation getCurrentAspect() {
        return blockEntity.getCurrentAspect();
    }

    /**
     * Button 1 = collect the current aspect into player's research pool.
     */
    @Override
    public boolean clickMenuButton(@NotNull Player player, int button) {
        if (button == 1) {
            ResourceLocation aspect = blockEntity.getCurrentAspect();
            if (aspect != null) {
                AspectList toAdd = AspectList.empty();
                toAdd.add(aspect, 1);
                if (player instanceof ServerPlayer serverPlayer) {
                    ResearchHelper.modifyResearchPoint(serverPlayer, toAdd);
                }
                blockEntity.clearCurrentAspect();
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (slotIndex == 0) {
            if (!moveItemStackTo(stack, 1, 37, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void slotsChanged(Container container) {
        cachedSlotStack = blockEntity.getInputStack().copy();
        super.slotsChanged(container);
    }

    @Override
    public void broadcastChanges() {
        ItemStack actual = blockEntity.getInputStack();
        if (!ItemStack.matches(cachedSlotStack, actual)) {
            cachedSlotStack = actual.copy();
        }
        super.broadcastChanges();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }
}
