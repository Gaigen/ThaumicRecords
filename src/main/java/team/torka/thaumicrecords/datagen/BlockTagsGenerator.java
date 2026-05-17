package team.torka.thaumicrecords.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.BlockRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

public class BlockTagsGenerator extends BlockTagsProvider {

    public BlockTagsGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ThaumicRecords.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.RESEARCH_TABLE.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.TABLE.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.ARCANE_WORKBENCH.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.AMBER_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(BlockRegistry.AMBER_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.CINNABAR_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(BlockRegistry.CINNABAR_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.AER_INFUSED_STONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.IGNIS_INFUSED_STONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.AQUA_INFUSED_STONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.TERRA_INFUSED_STONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.ORDO_INFUSED_STONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.PERDITIO_INFUSED_STONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.ARCANE_PEDESTAL.get());
        this.tag(BlockTags.LOGS).add(BlockRegistry.SILVERWOOD_LOG.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.SILVERWOOD_LOG.get());
        this.tag(BlockTags.LEAVES).add(BlockRegistry.SILVERWOOD_LEAVES.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.SILVERWOOD_LEAVES.get());
        this.tag(BlockTags.SAPLINGS).add(BlockRegistry.SILVERWOOD_SAPLING.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.SILVERWOOD_LOG.get());
        this.tag(BlockTags.LOGS).add(BlockRegistry.GREATWOOD_LOG.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.GREATWOOD_LOG.get());
        this.tag(BlockTags.LEAVES).add(BlockRegistry.GREATWOOD_LEAVES.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.GREATWOOD_LEAVES.get());
        this.tag(BlockTags.SAPLINGS).add(BlockRegistry.GREATWOOD_SAPLING.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.GREATWOOD_LOG.get());
    }
}
