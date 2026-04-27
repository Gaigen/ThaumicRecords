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
import team.torka.thaumicrecords.recipe.ArcaneCraftingShapedRecipe;

import java.util.function.Function;

public class ShapedArcaneCraftingRecipeSerializer implements RecipeSerializer<ArcaneCraftingShapedRecipe> {
    public static final MapCodec<ArcaneCraftingShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(ShapedRecipePattern.MAP_CODEC.forGetter(ArcaneCraftingShapedRecipe::pattern), AspectList.CODEC.xmap(
                                            list -> list.copy().multiply(100), Function.identity())
                                    .optionalFieldOf("baseVisCost", AspectList.empty())
                                    .forGetter(ArcaneCraftingShapedRecipe::baseVisCost),
                            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ArcaneCraftingShapedRecipe::result),
                            ResourceLocation.CODEC.listOf().fieldOf("requiredResearch").forGetter(ArcaneCraftingShapedRecipe::requiredResearch))
                    .apply(inst, ArcaneCraftingShapedRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArcaneCraftingShapedRecipe> STREAM_CODEC = StreamCodec.composite(ShapedRecipePattern.STREAM_CODEC,
            ArcaneCraftingShapedRecipe::pattern, AspectList.STREAM_CODEC, ArcaneCraftingShapedRecipe::baseVisCost, ItemStack.STREAM_CODEC,
            ArcaneCraftingShapedRecipe::result, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), ArcaneCraftingShapedRecipe::requiredResearch,
            ArcaneCraftingShapedRecipe::new);

    @NotNull
    @Override
    public MapCodec<ArcaneCraftingShapedRecipe> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ArcaneCraftingShapedRecipe> streamCodec() {
        return STREAM_CODEC;
    }

}
