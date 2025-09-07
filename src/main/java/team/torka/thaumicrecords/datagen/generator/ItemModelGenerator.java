package team.torka.thaumicrecords.datagen.generator;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import team.torka.thaumicrecords.common.register.TRItems;

public class ItemModelGenerator {

    public static void run(ItemModelGenerators itemModels){
        itemModels.generateFlatItem(TRItems.AMBER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(TRItems.BATH_SALTS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(TRItems.PRIMAL_CHARM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(TRItems.ENCHANTED_FABRIC.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(TRItems.COIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(TRItems.SALIS_MUNDUS.get(), ModelTemplates.FLAT_ITEM);
    }
}
