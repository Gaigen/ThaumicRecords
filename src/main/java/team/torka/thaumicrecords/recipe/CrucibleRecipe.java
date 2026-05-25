package team.torka.thaumicrecords.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.registry.RecipeSerializerRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 */
public record CrucibleRecipe(Ingredient catalyst, AspectList requiredAspects, ItemStack result) implements Recipe<RecipeInput> {

    public AspectList aspects() {
        return requiredAspects;
    }

    /**
     */
    public boolean matches(AspectList availableAspects, ItemStack catalystStack) {
        if (!catalyst.test(catalystStack)) return false;
        for (var entry : requiredAspects.entrySet()) {
            int available = availableAspects.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) return false;
        }
        return true;
    }

    /**
     */
    public boolean catalystMatches(ItemStack stack) {
        return catalyst.test(stack);
    }

    /**
     */
    public boolean matches(ItemStack inputItem, AspectList availableAspects) {
        if (!catalyst.test(inputItem)) return false;
        for (var entry : requiredAspects.entrySet()) {
            int available = availableAspects.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) return false;
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
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
        return RecipeSerializerRegistry.CRUCIBLE.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return RecipeTypeRegistry.CRUCIBLE.get();
    }
}
