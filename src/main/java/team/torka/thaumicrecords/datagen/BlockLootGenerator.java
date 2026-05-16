package team.torka.thaumicrecords.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

import java.util.ArrayList;
import java.util.Set;

public class BlockLootGenerator extends BlockLootSubProvider {

    public BlockLootGenerator(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        this.dropSelf(BlockRegistry.ARCANE_WORKBENCH.get());
        this.dropSelf(BlockRegistry.TABLE.get());
        this.dropSelf(BlockRegistry.CINNABAR_ORE.get());
        // 琥珀矿石
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        this.add(BlockRegistry.AMBER_ORE.get(), block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block, LootItem.lootTableItem(
                        ItemRegistry.AMBER)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE))))));
        this.add(BlockRegistry.AER_INFUSED_STONE.get(), block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block, LootItem.lootTableItem(
                        ItemRegistry.AER_SHARD)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE))))));
        this.add(BlockRegistry.IGNIS_INFUSED_STONE.get(),
                block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block, LootItem.lootTableItem(ItemRegistry.IGNIS_SHARD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE))))));
        this.add(BlockRegistry.AQUA_INFUSED_STONE.get(),
                block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block, LootItem.lootTableItem(ItemRegistry.AQUA_INFUSED_STONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE))))));
        this.add(BlockRegistry.TERRA_INFUSED_STONE.get(),
                block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block, LootItem.lootTableItem(ItemRegistry.TERRA_SHARD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE))))));
        this.add(BlockRegistry.ORDO_INFUSED_STONE.get(),
                block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block, LootItem.lootTableItem(ItemRegistry.ORDO_SHARD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE))))));
        this.add(BlockRegistry.PERDITIO_INFUSED_STONE.get(),
                block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block, LootItem.lootTableItem(ItemRegistry.PERDITIO_SHARD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE))))));
        this.dropSelf(BlockRegistry.SILVERWOOD_LOG.get());
        this.dropSelf(BlockRegistry.SILVERWOOD_SAPLING.get());
        this.add(BlockRegistry.SILVERWOOD_LEAVES.get(), block -> createLeavesDrops(block, BlockRegistry.SILVERWOOD_SAPLING.get(), 0.004F));
        this.dropSelf(BlockRegistry.GREATWOOD_LOG.get());
        this.dropSelf(BlockRegistry.GREATWOOD_SAPLING.get());
        this.add(BlockRegistry.GREATWOOD_LEAVES.get(), block -> createLeavesDrops(block, BlockRegistry.SILVERWOOD_SAPLING.get(), 0.005F));

    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(BlockRegistry.ARCANE_WORKBENCH.get());
        blocks.add(BlockRegistry.TABLE.get());
        blocks.add(BlockRegistry.AMBER_ORE.get());
        blocks.add(BlockRegistry.CINNABAR_ORE.get());
        blocks.add(BlockRegistry.AER_INFUSED_STONE.get());
        blocks.add(BlockRegistry.IGNIS_INFUSED_STONE.get());
        blocks.add(BlockRegistry.AQUA_INFUSED_STONE.get());
        blocks.add(BlockRegistry.TERRA_INFUSED_STONE.get());
        blocks.add(BlockRegistry.ORDO_INFUSED_STONE.get());
        blocks.add(BlockRegistry.PERDITIO_INFUSED_STONE.get());
        blocks.add(BlockRegistry.SILVERWOOD_SAPLING.get());
        blocks.add(BlockRegistry.SILVERWOOD_LOG.get());
        blocks.add(BlockRegistry.SILVERWOOD_LEAVES.get());
        blocks.add(BlockRegistry.GREATWOOD_SAPLING.get());
        blocks.add(BlockRegistry.GREATWOOD_LOG.get());
        blocks.add(BlockRegistry.GREATWOOD_LEAVES.get());
        return blocks;
    }

}
