package team.torka.thaumicrecords.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void buildRecipes(RecipeOutput output) {
        ItemStack resultWand = new ItemStack(ItemRegistry.WAND.get());
        resultWand.set(DataComponentRegistry.WAND_ITEM_DATA,
                new WandItemComponent(WandRodRegistry.WAND_ROD_WOOD.getId(), WandCapRegistry.WAND_CAP_IRON.getId(), AspectList.empty()));
        // 法杖
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultWand).pattern("  C").pattern(" R ").pattern("C  ").define('C', ItemRegistry.WAND_CAP_IRON).define(
                'R', Items.STICK).unlockedBy("has_iron_cap", has(ItemRegistry.WAND_CAP_IRON)).save(output, ThaumicRecords.createRl("iron_wood_wand"));
        // 台桌
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ItemRegistry.TABLE).pattern("SSS").pattern("P P").define('P', ItemTags.PLANKS).define('S',
                ItemTags.SLABS).unlockedBy("has_planks", has(ItemTags.PLANKS)).save(output);
        // 铁杖端
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.WAND_CAP_IRON)
                .pattern("NNN")
                .pattern("N N")
                .define('N', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_nugget", has(Tags.Items.NUGGETS_IRON))
                .save(output);
        // 笔与墨
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SCRIBING_TOOLS).requires(Items.GLASS_BOTTLE).requires(Items.FEATHER).requires(
                Tags.Items.DYES_BLACK).unlockedBy("has_ink", has(Tags.Items.DYES_BLACK)).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SCRIBING_TOOLS)
                .requires(ItemRegistry.SCRIBING_TOOLS)
                .requires(Tags.Items.DYES_BLACK)
                .unlockedBy("has_ink", has(Tags.Items.DYES_BLACK))
                .save(output, ThaumicRecords.createRl("scribing_tools_fill_ink"));

        burningRecipe(output);
        tripleMeatTreatRecipes(output);
    }

    private void tripleMeatTreatRecipes(RecipeOutput output) {
        // Sugar + any 3 of 4 meat nuggets → Triple Meat Treat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.TRIPLE_MEAT_TREAT).requires(Items.SUGAR).requires(ItemRegistry.NUGGET_BEEF).requires(
                ItemRegistry.NUGGET_CHICKEN).requires(ItemRegistry.NUGGET_PORK).unlockedBy("has_nugget", has(ItemRegistry.NUGGET_BEEF)).save(output,
                ThaumicRecords.createRl("triple_meat_treat_beef_chicken_pork"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.TRIPLE_MEAT_TREAT).requires(Items.SUGAR).requires(ItemRegistry.NUGGET_BEEF).requires(
                ItemRegistry.NUGGET_CHICKEN).requires(ItemRegistry.NUGGET_FISH).unlockedBy("has_nugget", has(ItemRegistry.NUGGET_BEEF)).save(output,
                ThaumicRecords.createRl("triple_meat_treat_beef_chicken_fish"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.TRIPLE_MEAT_TREAT).requires(Items.SUGAR).requires(ItemRegistry.NUGGET_BEEF).requires(
                ItemRegistry.NUGGET_FISH).requires(ItemRegistry.NUGGET_PORK).unlockedBy("has_nugget", has(ItemRegistry.NUGGET_BEEF)).save(output,
                ThaumicRecords.createRl("triple_meat_treat_beef_fish_pork"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.TRIPLE_MEAT_TREAT).requires(Items.SUGAR).requires(ItemRegistry.NUGGET_FISH).requires(
                ItemRegistry.NUGGET_CHICKEN).requires(ItemRegistry.NUGGET_PORK).unlockedBy("has_nugget", has(ItemRegistry.NUGGET_FISH)).save(output,
                ThaumicRecords.createRl("triple_meat_treat_fish_chicken_pork"));
    }

    private void burningRecipe(RecipeOutput output) {
        oreSmelting(output, List.of(ItemRegistry.AMBER_ORE), RecipeCategory.MISC, ItemRegistry.AMBER, 0.7F, 200, "amber");
        oreBlasting(output, List.of(ItemRegistry.AMBER_ORE), RecipeCategory.MISC, ItemRegistry.AMBER, 0.7F, 100, "amber");
        oreSmelting(output, List.of(ItemRegistry.CINNABAR_ORE), RecipeCategory.MISC, ItemRegistry.QUICKSILVER, 0.7F, 200, "quicksilver");
        oreBlasting(output, List.of(ItemRegistry.CINNABAR_ORE), RecipeCategory.MISC, ItemRegistry.QUICKSILVER, 0.7F, 100, "quicksilver");
    }

}
