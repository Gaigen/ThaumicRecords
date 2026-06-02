package team.torka.thaumicrecords.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.registry.RecipeSerializerRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Infusion crafting recipe — ported from TC4's InfusionRecipe.
 *
 * @param research    required research (empty = no requirement)
 * @param input       ingredient on the central pedestal
 * @param aspects     required essentia (aspect → amount)
 * @param instability instability level (0-10)
 * @param components  ingredients on surrounding pedestals (order-independent)
 * @param result      output item
 */
public record InfusionRecipe(ResourceLocation research, Ingredient input, AspectList aspects, int instability, List<Ingredient> components,
                             ItemStack result) implements Recipe<RecipeInput> {

    /**
     * Check if this recipe matches the given items.
     * Central item must match input ingredient.
     * All components must be present on pedestals (order-independent).
     * Extra pedestal items are allowed (TC4 behavior).
     */
    public boolean matches(List<ItemStack> pedestalItems, ItemStack centralItem) {
        if (!input.test(centralItem)) {
            return false;
        }

        // Copy pedestal list so we can remove matched items
        List<ItemStack> remaining = new ArrayList<>();
        for (ItemStack stack : pedestalItems) {
            remaining.add(stack.copy());
        }

        // Try to match each component against remaining pedestal items
        for (Ingredient component : components) {
            boolean matched = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (component.test(remaining.get(i))) {
                    remaining.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(RecipeInput input, Level level) {
        return false; // use custom matches() instead
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
        return RecipeSerializerRegistry.INFUSION.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return RecipeTypeRegistry.INFUSION.get();
    }
}
