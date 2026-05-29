package team.torka.thaumicrecords.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.RecipeSerializerRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class ArcaneSceptreRecipe extends CustomRecipe {
    public ArcaneSceptreRecipe(CraftingBookCategory category) {
        super(category);
    }

    /*
     * Pattern: " TF" / " RT" / "T  "
     * Slot layout (index = row * 3 + col):
     *   [0=empty] [1=T cap]  [2=F charm]
     *   [3=empty] [4=R rod]  [5=T cap]
     *   [6=T cap] [7=empty]  [8=empty]
     */

    private WandRod findRod(ItemStack stack) {
        for (WandRod wandRod : WandRodRegistry.WAND_ROD_REGISTRY) {
            if (stack.is(wandRod.getItem())) {
                return wandRod;
            }
        }
        return null;
    }

    private WandCap findCap(ItemStack stack) {
        for (WandCap wandCap : WandCapRegistry.WAND_CAP_REGISTRY) {
            if (stack.is(wandCap.getItem())) {
                return wandCap;
            }
        }
        return null;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() < 3 || input.height() < 3) {
            return false;
        }

        ItemStack slot0 = input.getItem(0);  // empty
        ItemStack slot1 = input.getItem(1);  // cap T
        ItemStack slot2 = input.getItem(2);  // primal charm F
        ItemStack slot3 = input.getItem(3);  // empty
        ItemStack slot4 = input.getItem(4);  // rod R
        ItemStack slot5 = input.getItem(5);  // cap T
        ItemStack slot6 = input.getItem(6);  // cap T
        ItemStack slot7 = input.getItem(7);  // empty
        ItemStack slot8 = input.getItem(8);  // empty

        // Check empty slots
        if (!slot0.isEmpty() || !slot3.isEmpty() || !slot7.isEmpty() || !slot8.isEmpty()) {
            return false;
        }

        // Check primal charm at slot 2
        if (!slot2.is(ItemRegistry.PRIMAL_CHARM.get())) {
            return false;
        }

        // Check all 3 caps are the same
        if (!ItemStack.isSameItemSameComponents(slot1, slot5) || !ItemStack.isSameItemSameComponents(slot5, slot6)) {
            return false;
        }

        // Find rod and cap in registries
        WandRod rod = findRod(slot4);
        WandCap cap = findCap(slot1);

        return Objects.nonNull(rod) && Objects.nonNull(cap);
    }

    public AspectList getVisCost(CraftingInput input, Level level) {
        if (!matches(input, level)) {
            return AspectList.empty();
        }
        WandRod rod = findRod(input.getItem(4));
        WandCap cap = findCap(input.getItem(1));
        if (Objects.nonNull(rod) && Objects.nonNull(cap)) {
            AspectList aspectList = new AspectList();
            int cost = rod.getCraftCost() * cap.getCraftCost() * 200;
            for (ResourceLocation resourceLocation : Aspect.getPrimalList()) {
                aspectList.put(resourceLocation, cost);
            }
            return aspectList;
        }
        return AspectList.empty();
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        WandRod rod = findRod(input.getItem(4));
        WandCap cap = findCap(input.getItem(1));
        if (Objects.nonNull(rod) && Objects.nonNull(cap)) {
            ItemStack itemStack = new ItemStack(ItemRegistry.WAND.get());
            itemStack.set(DataComponentRegistry.WAND_ITEM_DATA,
                    new WandItemComponent(WandRodRegistry.WAND_ROD_REGISTRY.getKey(rod), WandCapRegistry.WAND_CAP_REGISTRY.getKey(cap), AspectList.empty(), true));
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.ARCANE_SCEPTRE.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return RecipeTypeRegistry.ARCANE_SCEPTRE.get();
    }
}
