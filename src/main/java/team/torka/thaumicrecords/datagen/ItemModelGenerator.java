package team.torka.thaumicrecords.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.ItemRegistry;

@EventBusSubscriber(modid = ThaumicRecords.MOD_ID)
public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ThaumicRecords.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ItemRegistry.AMBER.get());
        basicItem(ItemRegistry.AMBER.get());
        basicItem(ItemRegistry.BATH_SALTS.get());
        basicItem(ItemRegistry.PRIMAL_CHARM.get());
        basicItem(ItemRegistry.ENCHANTED_FABRIC.get());
        basicItem(ItemRegistry.COIN.get());
        basicItem(ItemRegistry.SALIS_MUNDUS.get());
        basicItem(ItemRegistry.PRIMORDIAL_PEARL.get());

        basicItem(ItemRegistry.WAND_CAP_IRON.get());
        basicItem(ItemRegistry.WAND_CAP_GOLD.get());
        basicItem(ItemRegistry.WAND_CAP_THAUMIUM.get());
        basicItem(ItemRegistry.WAND_CAP_THAUMIUM_INERT.get());
        basicItem(ItemRegistry.WAND_ROD_GREATWOOD.get());
        basicItem(ItemRegistry.WAND_ROD_SILVERWOOD.get());
//        handheldItem(ItemRegistry.WAND.get());

    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ItemModelGenerator(output, existingFileHelper));
    }
}
