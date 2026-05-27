package team.torka.thaumicrecords.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.ModTags;
import team.torka.thaumicrecords.registry.ItemRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

public class ItemTagsGenerator extends ItemTagsProvider {
    public ItemTagsGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags,
                             ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, ThaumicRecords.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.SHARD).add(ItemRegistry.AER_SHARD.get()).add(ItemRegistry.IGNIS_SHARD.get()).add(ItemRegistry.AQUA_SHARD.get()).add(
                ItemRegistry.TERRA_SHARD.get()).add(ItemRegistry.ORDO_SHARD.get()).add(ItemRegistry.PERDITIO_SHARD.get()).add(
                ItemRegistry.BALANCED_SHARD.get());
        this.tag(ModTags.SHOW_NODE_WHEN_EQUIPPED).add(ItemRegistry.GOGGLES.get()).add(ItemRegistry.FORTRESS_HELMET.get());
        this.tag(ModTags.SHOW_NODE_WHEN_HELD).add(ItemRegistry.THAUMOMETER.get());
        this.tag(ItemTags.DYEABLE).add(ItemRegistry.ROBE_CHESTPLATE.get(), ItemRegistry.ROBE_LEGGINGS.get(), ItemRegistry.ROBE_BOOTS.get(),
                ItemRegistry.VOID_ROBE_HELMET.get(), ItemRegistry.VOID_ROBE_CHESTPLATE.get(), ItemRegistry.VOID_ROBE_LEGGINGS.get());
    }
}