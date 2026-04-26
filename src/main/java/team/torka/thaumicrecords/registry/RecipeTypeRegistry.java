package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.recipe.AspectRecipe;
import team.torka.thaumicrecords.recipe.ShapedArcaneCraftingRecipe;

public class RecipeTypeRegistry {
    public static final DeferredRegister<RecipeType<?>> REGISTRAR = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AspectRecipe>> ASPECT_REGISTRATION = REGISTRAR.register("aspect_registration",
            () -> new RecipeType<>() {
            });
    public static final DeferredHolder<RecipeType<?>, RecipeType<ShapedArcaneCraftingRecipe>> SHAPED_ARCANE_CRAFTING = REGISTRAR.register(
            "shaped_arcane_crafting", () -> new RecipeType<>() {
            });

}
