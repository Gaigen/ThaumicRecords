package team.torka.thaumicrecords.recipe.serializer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.recipe.InfusionRecipe;

public class InfusionRecipeSerializer implements RecipeSerializer<InfusionRecipe> {

    private static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(ResourceLocation.CODEC.fieldOf("research").forGetter(InfusionRecipe::research),
                    Ingredient.CODEC.fieldOf("input").forGetter(InfusionRecipe::input), AspectList.CODEC.fieldOf("aspects").forGetter(InfusionRecipe::aspects),
                    com.mojang.serialization.Codec.INT.fieldOf("instability").forGetter(InfusionRecipe::instability),
                    Ingredient.CODEC.listOf().fieldOf("components").forGetter(InfusionRecipe::components),
                    ItemStack.CODEC.fieldOf("result").forGetter(InfusionRecipe::result)).apply(inst, InfusionRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC,
            InfusionRecipe::research, Ingredient.CONTENTS_STREAM_CODEC, InfusionRecipe::input, AspectList.STREAM_CODEC, InfusionRecipe::aspects,
            net.minecraft.network.codec.ByteBufCodecs.INT, InfusionRecipe::instability,
            Ingredient.CONTENTS_STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()), InfusionRecipe::components, ItemStack.STREAM_CODEC,
            InfusionRecipe::result, InfusionRecipe::new);

    @NotNull
    @Override
    public MapCodec<InfusionRecipe> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
