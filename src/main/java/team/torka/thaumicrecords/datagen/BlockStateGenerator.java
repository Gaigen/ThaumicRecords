package team.torka.thaumicrecords.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.BlockRegistry;

public class BlockStateGenerator extends BlockStateProvider {
    public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ThaumicRecords.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(BlockRegistry.AER_INFUSED_STONE.get(),
                multiLayersCubeAll("aer_infused_stone", modLoc("block/infused_stone"), modLoc("block/infused_stone_overlay")).renderType("cutout"));
        simpleBlock(BlockRegistry.IGNIS_INFUSED_STONE.get(),
                multiLayersCubeAll("ignis_infused_stone", modLoc("block/infused_stone"), modLoc("block/infused_stone_overlay")).renderType("cutout"));
        simpleBlock(BlockRegistry.AQUA_INFUSED_STONE.get(),
                multiLayersCubeAll("aqua_infused_stone", modLoc("block/infused_stone"), modLoc("block/infused_stone_overlay")).renderType("cutout"));
        simpleBlock(BlockRegistry.TERRA_INFUSED_STONE.get(),
                multiLayersCubeAll("terra_infused_stone", modLoc("block/infused_stone"), modLoc("block/infused_stone_overlay")).renderType("cutout"));
        simpleBlock(BlockRegistry.ORDO_INFUSED_STONE.get(),
                multiLayersCubeAll("ordo_infused_stone", modLoc("block/infused_stone"), modLoc("block/infused_stone_overlay")).renderType("cutout"));
        simpleBlock(BlockRegistry.PERDITIO_INFUSED_STONE.get(),
                multiLayersCubeAll("perditio_infused_stone", modLoc("block/infused_stone"), modLoc("block/infused_stone_overlay")).renderType("cutout"));
        simpleBlock(BlockRegistry.AMBER_ORE.get(), models().cubeAll("amber_ore", modLoc("block/amber_ore")));
        simpleBlock(BlockRegistry.CINNABAR_ORE.get(), models().cubeAll("cinnabar_ore", modLoc("block/cinnabar_ore")));
        logBlock((RotatedPillarBlock) BlockRegistry.SILVERWOOD_LOG.get());
        simpleBlock(BlockRegistry.SILVERWOOD_LEAVES.get(), models().withExistingParent("silverwood_leaves", "block/leaves")
                .texture("all", modLoc("block/silverwood_leaves"))
                .renderType("cutout"));
        simpleBlock(BlockRegistry.SILVERWOOD_SAPLING.get(), models().cross("silverwood_sapling", modLoc("block/silverwood_sapling")).renderType("cutout"));
        logBlock((RotatedPillarBlock) BlockRegistry.GREATWOOD_LOG.get());
        simpleBlock(BlockRegistry.GREATWOOD_LEAVES.get(), models().withExistingParent("greatwood_leaves", "block/leaves")
                .texture("all", modLoc("block/greatwood_leaves"))
                .renderType("cutout"));
        simpleBlock(BlockRegistry.GREATWOOD_SAPLING.get(), models().cross("greatwood_sapling", modLoc("block/greatwood_sapling")).renderType("cutout"));
    }

    private ModelBuilder<BlockModelBuilder> multiLayersCubeAll(String name, ResourceLocation layer0, ResourceLocation layer1) {
        return models().withExistingParent(name, "minecraft:block/block")
                .texture("particle", layer0)
                .texture("layer0", layer0)
                .texture("layer1", layer1)
                .element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 0, 16, 16).texture("#layer0").cullface(direction).tintindex(0))
                .end()
                .element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 0, 16, 16).texture("#layer1").cullface(direction).tintindex(1))
                .end();
    }

}
