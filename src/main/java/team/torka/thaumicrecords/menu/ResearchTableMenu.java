package team.torka.thaumicrecords.menu;

import net.minecraft.core.BlockPos;
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
import team.torka.thaumicrecords.api.ModTags;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.MenuRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class ResearchTableMenu extends AbstractContainerMenu {
    private final ResearchTableBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    public static final int SLOT_SCRIBE_TOOLS = 0;
    public static final int SLOT_RESEARCH_NOTE = 1;
    public static final int SLOT_INVENTORY_START = 2;

    public ResearchTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, (ResearchTableBlockEntity) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ResearchTableMenu(int containerId, Inventory playerInventory, ResearchTableBlockEntity entity) {
        super(MenuRegistry.RESEARCH_TABLE.get(), containerId);
        this.blockEntity = entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());

        IItemHandler inventory = entity.getInventory();

        this.addSlot(new SlotItemHandler(inventory, SLOT_SCRIBE_TOOLS, 14, 10) {
            @Override
            @ParametersAreNonnullByDefault
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModTags.SCRIBING_TOOLS);
            }
        });

        this.addSlot(new SlotItemHandler(inventory, SLOT_RESEARCH_NOTE, 70, 10) {
            @Override
            @ParametersAreNonnullByDefault
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ItemRegistry.RESEARCH_NOTES);
            }
        });
        // player inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 48 + j * 18, 175 + i * 18));
            }
        }
        // player hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 48 + i * 18, 233));
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
            if (index < SLOT_INVENTORY_START) {
                if (!this.moveItemStackTo(itemstack1, SLOT_INVENTORY_START, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.is(ModTags.SCRIBING_TOOLS)) {
                    if (!this.moveItemStackTo(itemstack1, SLOT_SCRIBE_TOOLS, SLOT_SCRIBE_TOOLS + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.is(ItemRegistry.RESEARCH_NOTES)) {
                    if (!this.moveItemStackTo(itemstack1, SLOT_RESEARCH_NOTE, SLOT_RESEARCH_NOTE + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 29) {
                    if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, SLOT_INVENTORY_START, 29, false)) {
                        return ItemStack.EMPTY;
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

    public ItemStack getResearchNotes() {
        return blockEntity.getResearchNotes();
    }

    public BlockPos getBlockEntityPos() {
        return blockEntity.getBlockPos();
    }
}