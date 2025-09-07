package team.torka.thaumicrecords.datagen;

import net.minecraft.DetectedVersion;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.datagen.generator.ItemModelGenerator;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = ThaumicRecords.MOD_ID, value = Dist.CLIENT)
public class DataGenRegistrar {
    private DataGenRegistrar() {}
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(output -> new ModelProvider(output, ThaumicRecords.MOD_ID) {
            @Override
            protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
//                BlockModelAndStateGenerator.run(blockModels);
                ItemModelGenerator.run(itemModels);
            }
        });
    }
}