package team.torka.thaumicrecords.datagen;


import net.minecraft.data.PackOutput;
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
    }

}
