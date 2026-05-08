package team.torka.thaumicrecords.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void buildRecipes(RecipeOutput output) {
        ItemStack resultWand = new ItemStack(ItemRegistry.WAND.get());
        resultWand.set(DataComponentRegistry.WAND_ITEM_DATA.get(),
                new WandItemComponent(WandRodRegistry.WAND_ROD_WOOD.getId(), WandCapRegistry.WAND_CAP_IRON.getId(), AspectList.empty()));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultWand)
                .pattern("  C")
                .pattern(" R ")
                .pattern("C  ")
                .define('C', ItemRegistry.WAND_CAP_IRON.get())
                .define('R', Items.STICK)
                .unlockedBy("has_iron_cap", has(ItemRegistry.WAND_CAP_IRON.get()))
                .save(output, "thaumicrecords:iron_wood_wand");
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ItemRegistry.TABLE.get()).pattern("SSS").pattern("P P").define('P', ItemTags.PLANKS).define('S',
                ItemTags.SLABS).unlockedBy("has_planks", has(ItemTags.PLANKS)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.WAND_CAP_IRON.get())
                .pattern("NNN")
                .pattern("N N")
                .define('N', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_nugget", has(Tags.Items.NUGGETS_IRON))
                .save(output);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new RecipeGenerator(packOutput, lookupProvider));
    }
}
