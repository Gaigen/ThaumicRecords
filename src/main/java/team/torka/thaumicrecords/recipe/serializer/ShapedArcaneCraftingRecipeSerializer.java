package team.torka.thaumicrecords.recipe.serializer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.recipe.ShapedArcaneCraftingRecipe;

import java.util.function.Function;

public class ShapedArcaneCraftingRecipeSerializer implements RecipeSerializer<ShapedArcaneCraftingRecipe> {
    public static final MapCodec<ShapedArcaneCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(ShapedRecipePattern.MAP_CODEC.forGetter(ShapedArcaneCraftingRecipe::pattern), AspectList.CODEC.xmap(
                                            list -> list.copy().multiply(100), Function.identity())
                                    .optionalFieldOf("baseVisCost", AspectList.empty())
                                    .forGetter(ShapedArcaneCraftingRecipe::baseVisCost),
                            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapedArcaneCraftingRecipe::result),
                            ResourceLocation.CODEC.listOf().fieldOf("requiredResearch").forGetter(ShapedArcaneCraftingRecipe::requiredResearch))
                    .apply(inst, ShapedArcaneCraftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedArcaneCraftingRecipe> STREAM_CODEC = StreamCodec.composite(ShapedRecipePattern.STREAM_CODEC,
            ShapedArcaneCraftingRecipe::pattern, AspectList.STREAM_CODEC, ShapedArcaneCraftingRecipe::baseVisCost, ItemStack.STREAM_CODEC,
            ShapedArcaneCraftingRecipe::result, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), ShapedArcaneCraftingRecipe::requiredResearch,
            ShapedArcaneCraftingRecipe::new);

    @NotNull
    @Override
    public MapCodec<ShapedArcaneCraftingRecipe> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapedArcaneCraftingRecipe> streamCodec() {
        return STREAM_CODEC;
    }

}
