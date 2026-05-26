package team.torka.thaumicrecords.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.block.entity.ThaumatoriumBlockEntity;
import team.torka.thaumicrecords.recipe.CrucibleRecipe;
import team.torka.thaumicrecords.registry.MenuRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class ThaumatoriumMenu extends AbstractContainerMenu {

    public final ThaumatoriumBlockEntity blockEntity;
    public final List<CrucibleRecipe> recipes = new ArrayList<>();
    public final List<ResourceLocation> recipeIds = new ArrayList<>();

    public ItemStack cachedSlotStack = ItemStack.EMPTY;

    private final ContainerData data;

    public ThaumatoriumMenu(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(id, inv, (ThaumatoriumBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ThaumatoriumMenu(int id, Inventory inv, ThaumatoriumBlockEntity be) {
        super(MenuRegistry.THAUMATORIUM.get(), id);
        this.blockEntity = be;
        this.data = new SimpleContainerData(2);
        this.cachedSlotStack = be.inputStack.copy();

        this.addSlot(new Slot(be, 0, 48, 16) {
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
                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 64;
            }
        });

        // Player inventory (3 rows + hotbar)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inv, j, 8 + j * 18, 142));
        }

        this.addDataSlots(data);
        updateRecipes();
    }

    /**
     *
     */
    @Override
    @ParametersAreNonnullByDefault
    public void setItem(int slotId, int stateId, ItemStack stack) {
        super.setItem(slotId, stateId, stack);
        updateRecipes();
    }

    public void updateRecipes() {
        recipes.clear();
        recipeIds.clear();

        ItemStack inputStack = cachedSlotStack;

        if (!inputStack.isEmpty() || !blockEntity.recipeIds.isEmpty()) {
            RecipeManager recipeManager = blockEntity.getLevel() != null ? blockEntity.getLevel().getRecipeManager() : null;
            if (recipeManager == null) {
                return;
            }
            var allRecipes = recipeManager.getAllRecipesFor(RecipeTypeRegistry.CRUCIBLE.get());
            for (var holder : allRecipes) {
                CrucibleRecipe recipe = holder.value();
                if (recipe != null) {
                    boolean assigned = blockEntity.recipeIds.contains(holder.id());
                    boolean matchesInput = !inputStack.isEmpty() && recipe.catalystMatches(inputStack);
                    if (matchesInput || assigned) {
                        recipes.add(recipe);
                        recipeIds.add(holder.id());
                    }
                }
            }
        }
    }

    /**
     * TC4: enchantItem = toggle recipe assignment.
     */
    @Override
    public boolean clickMenuButton(@NotNull Player player, int button) {
        if (button < 0 || button >= recipes.size()) {
            return false;
        }

        CrucibleRecipe recipe = recipes.get(button);
        if (blockEntity.getLevel() == null) {
            return false;
        }
        var allRecipes = blockEntity.getLevel().getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.CRUCIBLE.get());
        ResourceLocation targetId = null;
        for (var holder : allRecipes) {
            if (holder.value() == recipe) {
                targetId = holder.id();
                break;
            }
        }
        if (targetId == null) {
            return false;
        }

        blockEntity.toggleRecipe(targetId, player.getScoreboardName());
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        updateRecipes();
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
    public boolean stillValid(Player player) {
        return true;
    }
}