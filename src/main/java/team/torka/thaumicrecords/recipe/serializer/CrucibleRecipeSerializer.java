package team.torka.thaumicrecords.recipe.serializer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.recipe.CrucibleRecipe;

public class CrucibleRecipeSerializer implements RecipeSerializer<CrucibleRecipe> {

    private static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    Ingredient.CODEC.fieldOf("catalyst").forGetter(CrucibleRecipe::catalyst),
                    AspectList.CODEC.fieldOf("required_aspects").forGetter(CrucibleRecipe::requiredAspects),
                    ItemStack.CODEC.fieldOf("result").forGetter(CrucibleRecipe::result)
            ).apply(inst, CrucibleRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, CrucibleRecipe::catalyst,
            AspectList.STREAM_CODEC, CrucibleRecipe::requiredAspects,
            ItemStack.STREAM_CODEC, CrucibleRecipe::result,
            CrucibleRecipe::new);

    @NotNull
    @Override
    public MapCodec<CrucibleRecipe> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
