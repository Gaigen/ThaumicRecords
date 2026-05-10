package team.torka.thaumicrecords.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

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
        withExistingParent(ItemRegistry.AER_SHARD.getId().getPath(), "item/generated").texture("layer0", modLoc("item/shard"));
        withExistingParent(ItemRegistry.IGNIS_SHARD.getId().getPath(), "item/generated").texture("layer0", modLoc("item/shard"));
        withExistingParent(ItemRegistry.AQUA_SHARD.getId().getPath(), "item/generated").texture("layer0", modLoc("item/shard"));
        withExistingParent(ItemRegistry.TERRA_SHARD.getId().getPath(), "item/generated").texture("layer0", modLoc("item/shard"));
        withExistingParent(ItemRegistry.ORDO_SHARD.getId().getPath(), "item/generated").texture("layer0", modLoc("item/shard"));
        withExistingParent(ItemRegistry.PERDITIO_SHARD.getId().getPath(), "item/generated").texture("layer0", modLoc("item/shard"));
        basicItem(ItemRegistry.BALANCED_SHARD.get());
        basicItem(ItemRegistry.SALIS_MUNDUS.get());
        basicItem(ItemRegistry.PRIMORDIAL_PEARL.get());

        basicItem(ItemRegistry.WAND_CAP_IRON.get());
        basicItem(ItemRegistry.WAND_CAP_GOLD.get());
        basicItem(ItemRegistry.WAND_CAP_THAUMIUM.get());
        basicItem(ItemRegistry.WAND_CAP_THAUMIUM_INERT.get());
        basicItem(ItemRegistry.WAND_ROD_GREATWOOD.get());
        basicItem(ItemRegistry.WAND_ROD_SILVERWOOD.get());
        basicItem(ItemRegistry.GOGGLES.get());
        basicItem(ItemRegistry.WISP_ESSENCE.get());
        basicItem(ItemRegistry.SCRIBING_TOOLS.get());

        simpleBlockItem(BlockRegistry.AMBER_ORE.get());

    }
}
