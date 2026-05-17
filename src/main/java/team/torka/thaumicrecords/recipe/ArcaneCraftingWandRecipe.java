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

public class ArcaneCraftingWandRecipe extends CustomRecipe {
    public ArcaneCraftingWandRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() < 3 || input.height() < 3) {
            return false;
        }
        ItemStack topRight = input.getItem(2);
        ItemStack center = input.getItem(4);
        ItemStack bottomLeft = input.getItem(6);
        for (int i = 0; i < input.size(); i++) {
            if (i != 2 && i != 4 && i != 6) {
                if (!input.getItem(i).isEmpty()) {
                    return false;
                }
            }
        }
        if (!ItemStack.isSameItemSameComponents(topRight, bottomLeft)) {
            return false;
        }
        WandRod rod = null;
        WandCap cap = null;
        for (WandRod wandRod : WandRodRegistry.WAND_ROD_REGISTRY) {
            if (center.is(wandRod.getItem())) {
                rod = wandRod;
            }
        }
        for (WandCap wandCap : WandCapRegistry.WAND_CAP_REGISTRY) {
            if (topRight.is(wandCap.getItem()) && bottomLeft.is(wandCap.getItem())) {
                cap = wandCap;
            }
        }
        if (Objects.nonNull(rod) && Objects.nonNull(cap)) {
            return rod != WandRodRegistry.WAND_ROD_WOOD.get() || cap != WandCapRegistry.WAND_CAP_IRON.get();
        }
        return false;
    }

    public AspectList getVisCost(CraftingInput input, Level level) {
        if (!matches(input, level)) {
            return AspectList.empty();
        }
        ItemStack topRight = input.getItem(2);
        ItemStack center = input.getItem(4);
        ItemStack bottomLeft = input.getItem(6);
        WandRod rod = null;
        WandCap cap = null;
        for (WandRod wandRod : WandRodRegistry.WAND_ROD_REGISTRY) {
            if (center.is(wandRod.getItem())) {
                rod = wandRod;
            }
        }
        for (WandCap wandCap : WandCapRegistry.WAND_CAP_REGISTRY) {
            if (topRight.is(wandCap.getItem()) && bottomLeft.is(wandCap.getItem())) {
                cap = wandCap;
            }
        }
        if (Objects.nonNull(rod) && Objects.nonNull(cap)) {
            AspectList aspectList = new AspectList();
            int rodCost = rod.getCraftCost();
            int capCost = cap.getCraftCost();
            for (ResourceLocation resourceLocation : Aspect.getPrimalList()) {
                aspectList.put(resourceLocation, rodCost * capCost * 100);
            }
            return aspectList;
        }
        return AspectList.empty();
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack topRight = input.getItem(2);
        ItemStack center = input.getItem(4);
        ItemStack bottomLeft = input.getItem(6);
        WandRod rod = null;
        WandCap cap = null;
        for (WandRod wandRod : WandRodRegistry.WAND_ROD_REGISTRY) {
            if (center.is(wandRod.getItem())) {
                rod = wandRod;
            }
        }
        for (WandCap wandCap : WandCapRegistry.WAND_CAP_REGISTRY) {
            if (topRight.is(wandCap.getItem()) && bottomLeft.is(wandCap.getItem())) {
                cap = wandCap;
            }
        }
        if (Objects.nonNull(rod) && Objects.nonNull(cap)) {
            ItemStack itemStack = new ItemStack(ItemRegistry.WAND.get());
            itemStack.set(DataComponentRegistry.WAND_ITEM_DATA,
                    new WandItemComponent(WandRodRegistry.WAND_ROD_REGISTRY.getKey(rod), WandCapRegistry.WAND_CAP_REGISTRY.getKey(cap), AspectList.empty()));
            return itemStack;
        }
        // fail
        ItemStack itemStack = new ItemStack(ItemRegistry.WAND.get());
        itemStack.set(DataComponentRegistry.WAND_ITEM_DATA,
                new WandItemComponent(WandRodRegistry.WAND_ROD_WOOD.getId(), WandCapRegistry.WAND_CAP_IRON.getId(), AspectList.empty()));
        return itemStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack itemStack = new ItemStack(ItemRegistry.WAND.get());
        itemStack.set(DataComponentRegistry.WAND_ITEM_DATA,
                new WandItemComponent(WandRodRegistry.WAND_ROD_WOOD.getId(), WandCapRegistry.WAND_CAP_IRON.getId(), AspectList.empty()));
        return itemStack;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.ARCANE_CRAFTING_WAND.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return RecipeTypeRegistry.ARCANE_CRAFTING_WAND.get();
    }
}
