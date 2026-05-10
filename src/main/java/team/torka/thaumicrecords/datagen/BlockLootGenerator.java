package team.torka.thaumicrecords.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.registry.BlockRegistry;

import java.util.List;
import java.util.Set;

public class BlockLootGenerator extends BlockLootSubProvider {

    public BlockLootGenerator(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        this.dropSelf(BlockRegistry.ARCANE_WORKBENCH.get());
        this.dropSelf(BlockRegistry.TABLE.get());
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(BlockRegistry.ARCANE_WORKBENCH.get(), BlockRegistry.TABLE.get());
    }

}
