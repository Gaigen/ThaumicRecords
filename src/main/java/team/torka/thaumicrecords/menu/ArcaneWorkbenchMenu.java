package team.torka.thaumicrecords.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.block.entity.ArcaneWorkbenchBlockEntity;
import team.torka.thaumicrecords.menu.slot.ArcaneWorkbenchResultSlot;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.MenuRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneWorkbenchMenu extends AbstractContainerMenu {
    private final ArcaneWorkbenchBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public ArcaneWorkbenchMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, (ArcaneWorkbenchBlockEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }


    public ArcaneWorkbenchMenu(int containerId, Inventory playerInventory, ArcaneWorkbenchBlockEntity entity) {
        super(MenuRegistry.ARCANE_WORKBENCH.get(), containerId);
        this.blockEntity = entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        IItemHandler inventory = entity.getInventory();

        // craft grid
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(inventory, col + row * 3, 40 + col * 24, 40 + row * 24));
            }
        }
        // craft result
        this.addSlot(new ArcaneWorkbenchResultSlot(playerInventory.player, entity, inventory, 9, 160, 64));
        // wand slot
        this.addSlot(new SlotItemHandler(inventory, 10, 160, 24) {
            @Override
            @ParametersAreNonnullByDefault
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == ItemRegistry.WAND.asItem();
            }
        });
        // player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 16 + col * 18, 151 + row * 18));
            }
        }
        // player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 16 + col * 18, 209));
        }
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 11) {
                if (!this.moveItemStackTo(itemstack1, 11, 47, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else {
                if (itemstack1.getItem() == ItemRegistry.WAND.asItem()) { // 替换为你自己的法杖判断逻辑
                    if (!this.moveItemStackTo(itemstack1, 10, 11, false)) {
                        if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (index < 47) {
                    if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                        if (index < 38) {
                            if (!this.moveItemStackTo(itemstack1, 38, 47, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else {
                            if (!this.moveItemStackTo(itemstack1, 11, 38, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, blockEntity.getBlockState().getBlock());
    }
}
