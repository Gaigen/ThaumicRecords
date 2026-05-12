package team.torka.thaumicrecords.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.BlockRegistry;

public class BlockStateGenerator extends BlockStateProvider {
    public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ThaumicRecords.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(BlockRegistry.AMBER_ORE.get(), models().cubeAll("amber_ore", modLoc("block/amber_ore")));
        simpleBlock(BlockRegistry.CINNABAR_ORE.get(), models().cubeAll("cinnabar_ore", modLoc("block/cinnabar_ore")));
        logBlock((RotatedPillarBlock) BlockRegistry.SILVERWOOD_LOG.get());
        simpleBlock(BlockRegistry.SILVERWOOD_LEAVES.get(), models().withExistingParent("silverwood_leaves", "block/leaves")
                .texture("all", modLoc("block/silverwood_leaves"))
                .renderType("cutout"));
        simpleBlock(BlockRegistry.SILVERWOOD_SAPLING.get(), models().cross("silverwood_sapling", modLoc("block/silverwood_sapling")).renderType("cutout"));
    }

}
