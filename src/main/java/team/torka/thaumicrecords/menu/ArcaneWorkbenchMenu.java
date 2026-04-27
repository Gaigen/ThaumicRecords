package team.torka.thaumicrecords.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.block.entity.ArcaneWorkbenchBlockEntity;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.menu.slot.ArcaneWorkbenchResultSlot;
import team.torka.thaumicrecords.recipe.ArcaneCraftingShapedRecipe;
import team.torka.thaumicrecords.registry.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class ArcaneWorkbenchMenu extends AbstractContainerMenu {
    private final ArcaneWorkbenchBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private ArcaneCraftingShapedRecipe cachedRecipe;
    private boolean isDirty = true;

    public static final int SLOT_CRAFT_RESULT = 9;
    public static final int SLOT_WAND = 10;
    public static final int SLOT_INVENTORY_START = 10;

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
                this.addSlot(new SlotItemHandler(inventory, col + row * 3, 40 + col * 24, 40 + row * 24) {
                    @Override
                    public void setChanged() {
                        super.setChanged();
                        ArcaneWorkbenchMenu.this.markDirty();
                    }
                });
            }
        }
        // craft result
        this.addSlot(new ArcaneWorkbenchResultSlot(this, inventory, SLOT_CRAFT_RESULT, 160, 64));
        // wand slot
        this.addSlot(new SlotItemHandler(inventory, SLOT_WAND, 160, 24) {
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
            if (index < SLOT_INVENTORY_START) {
                if (!this.moveItemStackTo(itemstack1, SLOT_INVENTORY_START, 47, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() == ItemRegistry.WAND.asItem()) {
                    if (!this.moveItemStackTo(itemstack1, SLOT_WAND, SLOT_INVENTORY_START, false)) {
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
                            if (!this.moveItemStackTo(itemstack1, SLOT_INVENTORY_START, 38, false)) {
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

    public ItemStack getWandStack() {
        return this.getSlot(SLOT_WAND).getItem();
    }

    @Nullable
    public ArcaneCraftingShapedRecipe getCachedRecipe() {
        if (this.isDirty) {
            this.cachedRecipe = this.getCurrentRecipe();
            this.updateResultSlot();
            this.isDirty = false;
        }
        return this.cachedRecipe;
    }

    public void updateResultSlot() {
        this.blockEntity.updateRecipeOutput();
    }

    private ArcaneCraftingShapedRecipe getCurrentRecipe() {
        return this.levelAccess.evaluate((level, pos) -> {
            CraftingInput input = getCraftingInput();
            return level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.ARCANE_CRAFTING_SHAPED.get(), input, level).map(RecipeHolder::value);
        }, Optional.<ArcaneCraftingShapedRecipe>empty()).orElse(null);
    }

    private CraftingInput getCraftingInput() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(this.getSlot(i).getItem());
        }
        return CraftingInput.of(3, 3, stacks);
    }

    public void consumeCraftingMaterials(int decrement) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                if (stack.hasCraftingRemainingItem()) {
                    this.getSlot(i).set(stack.getCraftingRemainingItem());
                } else {
                    stack.shrink(decrement);
                }
            }
        }
    }

    public boolean isVisInsufficient() {
        ItemStack wand = getWandStack();
        ArcaneCraftingShapedRecipe recipe = getCachedRecipe();
        if (recipe == null) {
            return false;
        }
        WandItemComponent data = wand.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (Objects.isNull(data)) {
            return true;
        }
        AspectList wandStorage = data.getAspects();
        WandCap wandCap = WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap());
        if (Objects.isNull(wandCap)) {
            return true;
        }
        AspectList cost = recipe.baseVisCost();
        for (Map.Entry<ResourceLocation, Integer> entry : cost.entrySet()) {
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(entry.getKey());
            if (Objects.isNull(aspect) || !aspect.isPrimal()) {
                continue;
            }
            Integer wandVis = wandStorage.getOrDefault(entry.getKey(), 0);
            var actualCost = recipe.getActualCost(entry.getKey(), wandCap);
            if (wandVis < actualCost) {
                return true;
            }
        }
        return false;
    }

    public void markDirty() {
        this.isDirty = true;
    }
}
