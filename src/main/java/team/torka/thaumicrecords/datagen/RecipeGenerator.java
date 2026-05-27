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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.ModTags;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.BlockRegistry;
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
        blockRecipes(output);
        thaumiumToolAndArmorRecipes(output);
        voidToolAndArmorRecipes(output);
        thaumometerRecipe(output);
    }

    private void blockRecipes(RecipeOutput output) {
        // Amber Block: 2x2 amber → 1 (original: itemResource:6 = amber)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.AMBER_BLOCK)
                .pattern("##")
                .pattern("##")
                .define('#', ItemRegistry.AMBER)
                .unlockedBy("has_amber", has(ItemRegistry.AMBER))
                .save(output);
        // Amber Brick: 2x2 amber block → 4
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.AMBER_BRICK, 4).pattern("##").pattern("##").define('#',
                ItemRegistry.AMBER_BLOCK).unlockedBy("has_amber_block", has(ItemRegistry.AMBER_BLOCK)).save(output);
        // Amber Block → 4 amber (reverse)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.AMBER, 4).requires(ItemRegistry.AMBER_BLOCK).unlockedBy("has_amber_block",
                has(ItemRegistry.AMBER_BLOCK)).save(output, ThaumicRecords.createRl("amber_from_block"));
        // Amber Brick → 4 amber (reverse)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.AMBER, 4).requires(ItemRegistry.AMBER_BRICK).unlockedBy("has_amber_brick",
                has(ItemRegistry.AMBER_BRICK)).save(output, ThaumicRecords.createRl("amber_from_brick"));
        // Obsidian Tile: 2x2 obsidian → 4
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.OBSIDIAN_TILE, 4)
                .pattern("##")
                .pattern("##")
                .define('#', Blocks.OBSIDIAN)
                .unlockedBy("has_obsidian", has(Blocks.OBSIDIAN))
                .save(output);
        // Tallow Block: 3x3 tallow → 1
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.TALLOW_BLOCK).pattern("###").pattern("###").pattern("###").define('#',
                ItemRegistry.TALLOW).unlockedBy("has_tallow", has(ItemRegistry.TALLOW)).save(output);
        // Tallow Block → 9 tallow (reverse)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.TALLOW, 9).requires(ItemRegistry.TALLOW_BLOCK).unlockedBy("has_tallow_block",
                has(ItemRegistry.TALLOW_BLOCK)).save(output, ThaumicRecords.createRl("tallow_from_block"));
        // Arcane Stone Brick: 2x2 arcane stone → 4
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.ARCANE_STONE_BRICK, 4).pattern("##").pattern("##").define('#',
                ItemRegistry.ARCANE_STONE).unlockedBy("has_arcane_stone", has(ItemRegistry.ARCANE_STONE)).save(output);
        // Flesh Block: 3x3 rotten flesh → 1
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.FLESH_BLOCK).pattern("###").pattern("###").pattern("###").define('#',
                Items.ROTTEN_FLESH).unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH)).save(output);

        // Ingot/Block/Nugget conversions
        metalConversionRecipes(output);
    }

    private void metalConversionRecipes(RecipeOutput output) {
        // Thaumium: 9 ingots → 1 block
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.THAUMIUM_BLOCK).pattern("###").pattern("###").pattern("###").define('#',
                ItemRegistry.THAUMIUM_INGOT).unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT)).save(output);
        // Thaumium: 1 block → 9 ingots
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.THAUMIUM_INGOT, 9).requires(ItemRegistry.THAUMIUM_BLOCK).unlockedBy(
                "has_thaumium_block", has(ItemRegistry.THAUMIUM_BLOCK)).save(output, ThaumicRecords.createRl("thaumium_ingot_from_block"));
        // Thaumium: 9 nuggets → 1 ingot
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.THAUMIUM_INGOT).pattern("###").pattern("###").pattern("###").define('#',
                ItemRegistry.NUGGET_THAUMIUM).unlockedBy("has_nugget", has(ItemRegistry.NUGGET_THAUMIUM)).save(output,
                ThaumicRecords.createRl("thaumium_ingot_from_nuggets"));
        // Thaumium: 1 ingot → 9 nuggets
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.NUGGET_THAUMIUM, 9).requires(ItemRegistry.THAUMIUM_INGOT).unlockedBy("has_thaumium",
                has(ItemRegistry.THAUMIUM_INGOT)).save(output, ThaumicRecords.createRl("thaumium_nugget_from_ingot"));

        // Void: 9 ingots → 1 block
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.VOID_BLOCK).pattern("###").pattern("###").pattern("###").define('#',
                ItemRegistry.VOID_INGOT).unlockedBy("has_void", has(ItemRegistry.VOID_INGOT)).save(output);
        // Void: 1 block → 9 ingots
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.VOID_INGOT, 9).requires(ItemRegistry.VOID_BLOCK).unlockedBy("has_void_block",
                has(ItemRegistry.VOID_BLOCK)).save(output, ThaumicRecords.createRl("void_ingot_from_block"));
        // Void: 9 nuggets → 1 ingot
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.VOID_INGOT).pattern("###").pattern("###").pattern("###").define('#',
                ItemRegistry.NUGGET_VOID).unlockedBy("has_nugget", has(ItemRegistry.NUGGET_VOID)).save(output,
                ThaumicRecords.createRl("void_ingot_from_nuggets"));
        // Void: 1 ingot → 9 nuggets
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.NUGGET_VOID, 9).requires(ItemRegistry.VOID_INGOT).unlockedBy("has_void",
                has(ItemRegistry.VOID_INGOT)).save(output, ThaumicRecords.createRl("void_nugget_from_ingot"));

        // Quicksilver: 9 nuggets → 1 quicksilver
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.QUICKSILVER).pattern("###").pattern("###").pattern("###").define('#',
                ItemRegistry.NUGGET_QUICKSILVER).unlockedBy("has_nugget", has(ItemRegistry.NUGGET_QUICKSILVER)).save(output,
                ThaumicRecords.createRl("quicksilver_from_nuggets"));
        // Quicksilver: 1 quicksilver → 9 nuggets
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.NUGGET_QUICKSILVER, 9).requires(ItemRegistry.QUICKSILVER).unlockedBy(
                "has_quicksilver", has(ItemRegistry.QUICKSILVER)).save(output, ThaumicRecords.createRl("quicksilver_nugget_from_quicksilver"));

        // Knowledge Fragment: 9 fragments → 1 research notes (original TC4 recipe)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.RESEARCH_NOTES).pattern("###").pattern("###").pattern("###").define('#',
                ItemRegistry.KNOWLEDGE_FRAGMENT).unlockedBy("has_knowledge_fragment", has(ItemRegistry.KNOWLEDGE_FRAGMENT)).save(output);

        // Planks
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.GREATWOOD_PLANKS, 4)
                .pattern("#")
                .define('#', BlockRegistry.GREATWOOD_LOG)
                .unlockedBy("has_greatwood", has(BlockRegistry.GREATWOOD_LOG))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.SILVERWOOD_PLANKS, 4)
                .pattern("#")
                .define('#', BlockRegistry.SILVERWOOD_LOG)
                .unlockedBy("has_silverwood", has(BlockRegistry.SILVERWOOD_LOG))
                .save(output);

        // Stairs (original TC4: 6 blocks → 4 stairs)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.ARCANE_STONE_STAIRS, 4).pattern("#  ").pattern("## ").pattern("###").define('#',
                ItemRegistry.ARCANE_STONE_BRICK).unlockedBy("has_arcane_stone_brick", has(ItemRegistry.ARCANE_STONE_BRICK)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.GREATWOOD_STAIRS, 4).pattern("#  ").pattern("## ").pattern("###").define('#',
                ItemRegistry.GREATWOOD_PLANKS).unlockedBy("has_greatwood_planks", has(ItemRegistry.GREATWOOD_PLANKS)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.SILVERWOOD_STAIRS, 4).pattern("#  ").pattern("## ").pattern("###").define('#',
                ItemRegistry.SILVERWOOD_PLANKS).unlockedBy("has_silverwood_planks", has(ItemRegistry.SILVERWOOD_PLANKS)).save(output);

        // Slabs (original TC4: 3 blocks → 6 slabs)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.ARCANE_STONE_SLAB, 6).pattern("###").define('#',
                ItemRegistry.ARCANE_STONE_BRICK).unlockedBy("has_arcane_stone_brick", has(ItemRegistry.ARCANE_STONE_BRICK)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.GREATWOOD_SLAB, 6)
                .pattern("###")
                .define('#', ItemRegistry.GREATWOOD_PLANKS)
                .unlockedBy("has_greatwood_planks", has(ItemRegistry.GREATWOOD_PLANKS))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.SILVERWOOD_SLAB, 6)
                .pattern("###")
                .define('#', ItemRegistry.SILVERWOOD_PLANKS)
                .unlockedBy("has_silverwood_planks", has(ItemRegistry.SILVERWOOD_PLANKS))
                .save(output);
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

    private void thaumometerRecipe(RecipeOutput output) {
        // Thaumometer: _1_ / IGI / _1_ (I=gold_ingot, G=glass_pane, 1=any shard)
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.THAUMOMETER)
                .pattern(" S ")
                .pattern("IGI")
                .pattern(" S ")
                .define('S', ModTags.SHARD)
                .define('I', Items.GOLD_INGOT)
                .define('G', Items.GLASS_PANE)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(output);
    }

    private void thaumiumToolAndArmorRecipes(RecipeOutput output) {
        // Tools
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.THAUMIUM_PICKAXE).pattern("III").pattern(" S ").pattern(" S ").define('I',
                ItemRegistry.THAUMIUM_INGOT).define('S', Items.STICK).unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.THAUMIUM_SWORD).pattern("I").pattern("I").pattern("S").define('I',
                ItemRegistry.THAUMIUM_INGOT).define('S', Items.STICK).unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.THAUMIUM_AXE).pattern("II").pattern("SI").pattern("S ").define('I',
                ItemRegistry.THAUMIUM_INGOT).define('S', Items.STICK).unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.THAUMIUM_SHOVEL).pattern("I").pattern("S").pattern("S").define('I',
                ItemRegistry.THAUMIUM_INGOT).define('S', Items.STICK).unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.THAUMIUM_HOE).pattern("II").pattern("S ").pattern("S ").define('I',
                ItemRegistry.THAUMIUM_INGOT).define('S', Items.STICK).unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT)).save(output);
        // Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.THAUMIUM_HELMET)
                .pattern("III")
                .pattern("I I")
                .define('I', ItemRegistry.THAUMIUM_INGOT)
                .unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.THAUMIUM_CHESTPLATE).pattern("I I").pattern("III").pattern("III").define('I',
                ItemRegistry.THAUMIUM_INGOT).unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.THAUMIUM_LEGGINGS).pattern("III").pattern("I I").pattern("I I").define('I',
                ItemRegistry.THAUMIUM_INGOT).unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.THAUMIUM_BOOTS)
                .pattern("I I")
                .pattern("I I")
                .define('I', ItemRegistry.THAUMIUM_INGOT)
                .unlockedBy("has_thaumium", has(ItemRegistry.THAUMIUM_INGOT))
                .save(output);
    }

    private void voidToolAndArmorRecipes(RecipeOutput output) {
        // Tools
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.VOID_PICKAXE).pattern("III").pattern(" S ").pattern(" S ").define('I',
                ItemRegistry.VOID_INGOT).define('S', Items.STICK).unlockedBy("has_void", has(ItemRegistry.VOID_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.VOID_SWORD)
                .pattern("I")
                .pattern("I")
                .pattern("S")
                .define('I', ItemRegistry.VOID_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_void", has(ItemRegistry.VOID_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.VOID_AXE)
                .pattern("II")
                .pattern("SI")
                .pattern("S ")
                .define('I', ItemRegistry.VOID_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_void", has(ItemRegistry.VOID_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.VOID_SHOVEL)
                .pattern("I")
                .pattern("S")
                .pattern("S")
                .define('I', ItemRegistry.VOID_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_void", has(ItemRegistry.VOID_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.VOID_HOE)
                .pattern("II")
                .pattern("S ")
                .pattern("S ")
                .define('I', ItemRegistry.VOID_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_void", has(ItemRegistry.VOID_INGOT))
                .save(output);
        // Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.VOID_HELMET)
                .pattern("III")
                .pattern("I I")
                .define('I', ItemRegistry.VOID_INGOT)
                .unlockedBy("has_void", has(ItemRegistry.VOID_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.VOID_CHESTPLATE).pattern("I I").pattern("III").pattern("III").define('I',
                ItemRegistry.VOID_INGOT).unlockedBy("has_void", has(ItemRegistry.VOID_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.VOID_LEGGINGS).pattern("III").pattern("I I").pattern("I I").define('I',
                ItemRegistry.VOID_INGOT).unlockedBy("has_void", has(ItemRegistry.VOID_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.VOID_BOOTS)
                .pattern("I I")
                .pattern("I I")
                .define('I', ItemRegistry.VOID_INGOT)
                .unlockedBy("has_void", has(ItemRegistry.VOID_INGOT))
                .save(output);
    }

    private void burningRecipe(RecipeOutput output) {
        oreSmelting(output, List.of(ItemRegistry.AMBER_ORE), RecipeCategory.MISC, ItemRegistry.AMBER, 0.7F, 200, "amber");
        oreBlasting(output, List.of(ItemRegistry.AMBER_ORE), RecipeCategory.MISC, ItemRegistry.AMBER, 0.7F, 100, "amber");
        oreSmelting(output, List.of(ItemRegistry.CINNABAR_ORE), RecipeCategory.MISC, ItemRegistry.QUICKSILVER, 0.7F, 200, "quicksilver");
        oreBlasting(output, List.of(ItemRegistry.CINNABAR_ORE), RecipeCategory.MISC, ItemRegistry.QUICKSILVER, 0.7F, 100, "quicksilver");

        // Salis Mundus: smelt balanced_shard (original TC4 recipe)
        oreSmelting(output, List.of(ItemRegistry.BALANCED_SHARD), RecipeCategory.MISC, ItemRegistry.SALIS_MUNDUS, 1.0F, 200, "salis_mundus");
    }

}
