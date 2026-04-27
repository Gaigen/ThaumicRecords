package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.recipe.ArcaneCraftingShapedRecipe;
import team.torka.thaumicrecords.recipe.AspectRecipe;
import team.torka.thaumicrecords.recipe.serializer.AspectRecipeSerializer;
import team.torka.thaumicrecords.recipe.serializer.ShapedArcaneCraftingRecipeSerializer;

public class RecipeSerializerRegistry {

    public static final DeferredRegister<RecipeSerializer<?>> REGISTRAR = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AspectRecipe>> ASPECT_REGISTRATION = REGISTRAR.register("aspect_registration",
            AspectRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ArcaneCraftingShapedRecipe>> ARCANE_CRAFTING_SHAPED = REGISTRAR.register(
            "arcane_crafting_shaped", ShapedArcaneCraftingRecipeSerializer::new);
}
