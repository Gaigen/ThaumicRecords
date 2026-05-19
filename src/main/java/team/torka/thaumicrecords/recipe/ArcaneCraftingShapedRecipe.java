package team.torka.thaumicrecords.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.registry.RecipeSerializerRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record ArcaneCraftingShapedRecipe(ShapedRecipePattern pattern, AspectList baseVisCost, ItemStack result,
                                         List<ResourceLocation> requiredResearch) implements Recipe<CraftingInput> {
    public ArcaneCraftingShapedRecipe {
        if (Objects.isNull(baseVisCost)) {
            baseVisCost = new AspectList();
        }
        if (Objects.isNull(requiredResearch)) {
            requiredResearch = Collections.emptyList();
        }
        if (Objects.isNull(result)) {
            result = ItemStack.EMPTY;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.pattern.width() && height >= this.pattern.height();
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.ARCANE_CRAFTING_SHAPED.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return RecipeTypeRegistry.ARCANE_CRAFTING_SHAPED.get();
    }
}