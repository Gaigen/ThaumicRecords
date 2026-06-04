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
        basicItem(ItemRegistry.THAUMONOMICON.get());
        basicItem(ItemRegistry.CRIMSON_RITES.get());
        withExistingParent("phial_filled", "item/generated").texture("layer0", modLoc("item/phial")).texture("layer1", "item/essence");

        basicItem(ItemRegistry.WAND_CAP_IRON.get());
        basicItem(ItemRegistry.WAND_CAP_GOLD.get());
        basicItem(ItemRegistry.WAND_CAP_THAUMIUM.get());
        basicItem(ItemRegistry.WAND_CAP_THAUMIUM_INERT.get());
        basicItem(ItemRegistry.WAND_CAP_COPPER.get());
        basicItem(ItemRegistry.WAND_CAP_SILVER.get());
        basicItem(ItemRegistry.WAND_CAP_VOID.get());
        basicItem(ItemRegistry.WAND_CAP_VOID_INERT.get());
        basicItem(ItemRegistry.WAND_CAP_SILVER_INERT.get());
        basicItem(ItemRegistry.WAND_ROD_GREATWOOD.get());
        basicItem(ItemRegistry.WAND_ROD_SILVERWOOD.get());
        basicItem(ItemRegistry.WAND_ROD_OBSIDIAN.get());
        basicItem(ItemRegistry.WAND_ROD_BLAZE.get());
        basicItem(ItemRegistry.WAND_ROD_ICE.get());
        basicItem(ItemRegistry.WAND_ROD_QUARTZ.get());
        basicItem(ItemRegistry.WAND_ROD_BONE.get());
        basicItem(ItemRegistry.WAND_ROD_REED.get());
        basicItem(ItemRegistry.STAFF_ROD_GREATWOOD.get());
        basicItem(ItemRegistry.STAFF_ROD_OBSIDIAN.get());
        basicItem(ItemRegistry.STAFF_ROD_BLAZE.get());
        basicItem(ItemRegistry.STAFF_ROD_ICE.get());
        basicItem(ItemRegistry.STAFF_ROD_QUARTZ.get());
        basicItem(ItemRegistry.STAFF_ROD_BONE.get());
        basicItem(ItemRegistry.STAFF_ROD_REED.get());
        basicItem(ItemRegistry.STAFF_ROD_SILVERWOOD.get());
        basicItem(ItemRegistry.STAFF_ROD_PRIMAL.get());
        basicItem(ItemRegistry.GOGGLES.get());
        basicItem(ItemRegistry.FORTRESS_HELMET.get());
        basicItem(ItemRegistry.FORTRESS_CHESTPLATE.get());
        basicItem(ItemRegistry.FORTRESS_LEGGINGS.get());
        basicItem(ItemRegistry.BOOTS_TRAVELLER.get());
        basicItem(ItemRegistry.THAUMIUM_INGOT.get());
        basicItem(ItemRegistry.NUGGET_THAUMIUM.get());
        basicItem(ItemRegistry.NUGGET_VOID.get());
        basicItem(ItemRegistry.NUGGET_QUICKSILVER.get());
        basicItem(ItemRegistry.NUGGET_CHICKEN.get());
        basicItem(ItemRegistry.NUGGET_BEEF.get());
        basicItem(ItemRegistry.NUGGET_PORK.get());
        basicItem(ItemRegistry.NUGGET_FISH.get());
        basicItem(ItemRegistry.TRIPLE_MEAT_TREAT.get());
        basicItem(ItemRegistry.TALLOW.get());
        basicItem(ItemRegistry.VOID_SEED.get());
        basicItem(ItemRegistry.ZOMBIE_BRAIN.get());
        basicItem(ItemRegistry.KNOWLEDGE_FRAGMENT.get());
        basicItem(ItemRegistry.ESSENTIA_FILTER.get());
        basicItem(ItemRegistry.MIRROR_GLASS.get());
        basicItem(ItemRegistry.TAINT_SLIME.get());
        basicItem(ItemRegistry.TAINT_TENDRIL.get());
        withExistingParent(ItemRegistry.FLUX_GOO.getId().getPath(), mcLoc("item/generated")).texture("layer0", modLoc("block/flux_goo_still"));
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
        simpleBlockItem(BlockRegistry.PAVING_STONE_OF_TRAVEL.get());
        simpleBlockItem(BlockRegistry.PAVING_STONE_OF_WARDING.get());
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

        // Elemental Tools
        handheldItem(ItemRegistry.ELEMENTAL_SHOVEL);
        handheldItem(ItemRegistry.ELEMENTAL_PICKAXE);
        handheldItem(ItemRegistry.ELEMENTAL_AXE);
        handheldItem(ItemRegistry.ELEMENTAL_HOE);
    }

    private void handheldItem(net.neoforged.neoforge.registries.DeferredItem<?> item) {
        withExistingParent(item.getId().getPath(), mcLoc("item/handheld")).texture("layer0", modLoc("item/" + item.getId().getPath()));
    }
}
