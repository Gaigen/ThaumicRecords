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
        basicItem(ItemRegistry.QUICKSILVER.get());
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
        basicItem(ItemRegistry.FORTRESS_HELMET.get());
        basicItem(ItemRegistry.FORTRESS_CHESTPLATE.get());
        basicItem(ItemRegistry.FORTRESS_LEGGINGS.get());
        basicItem(ItemRegistry.BOOTS_TRAVELLER.get());
        basicItem(ItemRegistry.THAUMIUM_INGOT.get());
        basicItem(ItemRegistry.THAUMIUM_HELMET.get());
        basicItem(ItemRegistry.THAUMIUM_CHESTPLATE.get());
        basicItem(ItemRegistry.THAUMIUM_LEGGINGS.get());
        basicItem(ItemRegistry.THAUMIUM_BOOTS.get());
        basicItem(ItemRegistry.VOID_INGOT.get());
        basicItem(ItemRegistry.VOID_HELMET.get());
        basicItem(ItemRegistry.VOID_CHESTPLATE.get());
        basicItem(ItemRegistry.VOID_LEGGINGS.get());
        basicItem(ItemRegistry.VOID_BOOTS.get());
        withExistingParent(ItemRegistry.VOID_ROBE_HELMET.getId().getPath(), mcLoc("item/generated")).texture("layer0", modLoc("item/void_robe_helmet_overlay"))
                .texture("layer1", modLoc("item/void_robe_helmet"));
        withExistingParent(ItemRegistry.VOID_ROBE_CHESTPLATE.getId().getPath(), mcLoc("item/generated")).texture("layer0",
                modLoc("item/void_robe_chestplate_overlay")).texture("layer1", modLoc("item/void_robe_chestplate"));
        withExistingParent(ItemRegistry.VOID_ROBE_LEGGINGS.getId().getPath(), mcLoc("item/generated")).texture("layer0",
                modLoc("item/void_robe_leggings_overlay")).texture("layer1", modLoc("item/void_robe_leggings"));
        basicItem(ItemRegistry.CRIMSON_ROBE_HELMET.get());
        basicItem(ItemRegistry.CRIMSON_ROBE_CHESTPLATE.get());
        basicItem(ItemRegistry.CRIMSON_ROBE_LEGGINGS.get());
        basicItem(ItemRegistry.CRIMSON_PLATE_HELMET.get());
        basicItem(ItemRegistry.CRIMSON_PLATE_CHESTPLATE.get());
        basicItem(ItemRegistry.CRIMSON_PLATE_LEGGINGS.get());
        basicItem(ItemRegistry.CRIMSON_LEADER_HELMET.get());
        basicItem(ItemRegistry.CRIMSON_LEADER_CHESTPLATE.get());
        basicItem(ItemRegistry.CRIMSON_LEADER_LEGGINGS.get());
        basicItem(ItemRegistry.CRIMSON_BOOTS.get());
        withExistingParent(ItemRegistry.ROBE_CHESTPLATE.getId().getPath(), mcLoc("item/generated")).texture("layer0", modLoc("item/robe_chestplate_overlay"))
                .texture("layer1", modLoc("item/robe_chestplate"));
        withExistingParent(ItemRegistry.ROBE_LEGGINGS.getId().getPath(), mcLoc("item/generated")).texture("layer0", modLoc("item/robe_leggings_overlay"))
                .texture("layer1", modLoc("item/robe_leggings"));
        withExistingParent(ItemRegistry.ROBE_BOOTS.getId().getPath(), mcLoc("item/generated")).texture("layer0", modLoc("item/robe_boots_overlay")).texture(
                "layer1", modLoc("item/robe_boots"));
        basicItem(ItemRegistry.WISP_ESSENCE.get());
        basicItem(ItemRegistry.SCRIBING_TOOLS.get());
        withExistingParent(ItemRegistry.SILVERWOOD_SAPLING.getId().getPath(), mcLoc("item/generated")).texture("layer0", modLoc("block/silverwood_sapling"));
        withExistingParent(ItemRegistry.GREATWOOD_SAPLING.getId().getPath(), mcLoc("item/generated")).texture("layer0", modLoc("block/greatwood_sapling"));

        simpleBlockItem(BlockRegistry.AER_INFUSED_STONE.get());
        simpleBlockItem(BlockRegistry.IGNIS_INFUSED_STONE.get());
        simpleBlockItem(BlockRegistry.AQUA_INFUSED_STONE.get());
        simpleBlockItem(BlockRegistry.TERRA_INFUSED_STONE.get());
        simpleBlockItem(BlockRegistry.ORDO_INFUSED_STONE.get());
        simpleBlockItem(BlockRegistry.PERDITIO_INFUSED_STONE.get());
        simpleBlockItem(BlockRegistry.AMBER_ORE.get());
        simpleBlockItem(BlockRegistry.CINNABAR_ORE.get());
        simpleBlockItem(BlockRegistry.SILVERWOOD_LEAVES.get());
        simpleBlockItem(BlockRegistry.SILVERWOOD_LOG.get());
        simpleBlockItem(BlockRegistry.GREATWOOD_LEAVES.get());
        simpleBlockItem(BlockRegistry.GREATWOOD_LOG.get());
        withExistingParent(ItemRegistry.ARCANE_PEDESTAL.getId().getPath(), modLoc("block/arcane_pedestal"));

        // Thaumium Tools
        handheldItem(ItemRegistry.THAUMIUM_PICKAXE);
        handheldItem(ItemRegistry.THAUMIUM_SWORD);
        handheldItem(ItemRegistry.THAUMIUM_AXE);
        handheldItem(ItemRegistry.THAUMIUM_SHOVEL);
        handheldItem(ItemRegistry.THAUMIUM_HOE);

        // Void Tools
        handheldItem(ItemRegistry.VOID_PICKAXE);
        handheldItem(ItemRegistry.VOID_SWORD);
        handheldItem(ItemRegistry.VOID_AXE);
        handheldItem(ItemRegistry.VOID_SHOVEL);
        handheldItem(ItemRegistry.VOID_HOE);

        // Crimson Blade & Primal Crusher
        handheldItem(ItemRegistry.CRIMSON_BLADE);
        handheldItem(ItemRegistry.PRIMAL_CRUSHER);
    }

    private void handheldItem(net.neoforged.neoforge.registries.DeferredItem<?> item) {
        withExistingParent(item.getId().getPath(), mcLoc("item/handheld")).texture("layer0", modLoc("item/" + item.getId().getPath()));
    }
}
