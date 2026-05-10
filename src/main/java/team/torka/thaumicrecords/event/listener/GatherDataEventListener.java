package team.torka.thaumicrecords.event.listener;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import team.torka.thaumicrecords.datagen.BlockLootGenerator;
import team.torka.thaumicrecords.datagen.BlockStateGenerator;
import team.torka.thaumicrecords.datagen.BlockTagsGenerator;
import team.torka.thaumicrecords.datagen.ItemModelGenerator;
import team.torka.thaumicrecords.datagen.ItemTagsGenerator;
import team.torka.thaumicrecords.datagen.RecipeGenerator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class GatherDataEventListener {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // block loot
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(BlockLootGenerator::new, LootContextParamSets.BLOCK)), lookupProvider));

        // block tags
        BlockTagsGenerator blockTags = new BlockTagsGenerator(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);

        // block state
        generator.addProvider(event.includeClient(), new BlockStateGenerator(packOutput, existingFileHelper));

        // item model
        generator.addProvider(event.includeClient(), new ItemModelGenerator(packOutput, existingFileHelper));

        // item tags
        generator.addProvider(event.includeServer(), new ItemTagsGenerator(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));

        // recipe
        generator.addProvider(event.includeServer(), new RecipeGenerator(packOutput, lookupProvider));
    }
}
