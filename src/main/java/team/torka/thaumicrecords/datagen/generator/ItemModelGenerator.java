package team.torka.thaumicrecords.datagen.generator;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import team.torka.thaumicrecords.registry.ItemRegistry;

public class ItemModelGenerator {

    public static void run(ItemModelGenerators itemModels){
        itemModels.generateFlatItem(ItemRegistry.AMBER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistry.BATH_SALTS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistry.PRIMAL_CHARM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistry.ENCHANTED_FABRIC.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistry.COIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ItemRegistry.SALIS_MUNDUS.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ItemRegistry.WAND_CAP_IRON.get(),ModelTemplates.FLAT_ITEM);
    }
}
