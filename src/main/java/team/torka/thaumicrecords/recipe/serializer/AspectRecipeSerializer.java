package team.torka.thaumicrecords.recipe.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.recipe.AspectRecipe;

public class AspectRecipeSerializer implements RecipeSerializer<AspectRecipe> {

    private static final MapCodec<AspectRecipe> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(AspectRecipe::ingredient),
                    AspectList.CODEC.fieldOf("aspects").forGetter(AspectRecipe::aspects),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(AspectRecipe::replace)).apply(inst, AspectRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AspectRecipe> STREAM_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC,
            AspectRecipe::ingredient, AspectList.STREAM_CODEC, AspectRecipe::aspects, ByteBufCodecs.BOOL, AspectRecipe::replace, AspectRecipe::new);

    @NotNull
    @Override
    public MapCodec<AspectRecipe> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AspectRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
