package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.recipe.ArcaneCraftingShapedRecipe;
import team.torka.thaumicrecords.recipe.AspectRecipe;

public class RecipeTypeRegistry {
    public static final DeferredRegister<RecipeType<?>> REGISTRAR = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AspectRecipe>> ASPECT_REGISTRATION = REGISTRAR.register("aspect_registration",
            () -> new RecipeType<>() {
            });
    public static final DeferredHolder<RecipeType<?>, RecipeType<ArcaneCraftingShapedRecipe>> ARCANE_CRAFTING_SHAPED = REGISTRAR.register(
            "arcane_crafting_shaped", () -> new RecipeType<>() {
            });

}
