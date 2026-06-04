package team.torka.thaumicrecords.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
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
        this.dropSelf(BlockRegistry.CINDERPEARL.get());
        this.dropSelf(BlockRegistry.SHIMMERLEAF.get());
        this.dropSelf(BlockRegistry.ARCANE_WORKBENCH.get());
        this.dropSelf(BlockRegistry.TABLE.get());
        this.dropSelf(BlockRegistry.CINNABAR_ORE.get());
        this.dropSelf(BlockRegistry.ARCANE_PEDESTAL.get());
        this.dropSelf(BlockRegistry.PAVING_STONE_OF_TRAVEL.get());
        this.dropSelf(BlockRegistry.PAVING_STONE_OF_WARDING.get());
        this.dropSelf(BlockRegistry.THAUMIUM_BLOCK.get());
        this.dropSelf(BlockRegistry.VOID_BLOCK.get());
        this.dropSelf(BlockRegistry.AMBER_BLOCK.get());
        this.dropSelf(BlockRegistry.AMBER_BRICK.get());
        this.dropSelf(BlockRegistry.OBSIDIAN_TILE.get());
        this.dropSelf(BlockRegistry.TALLOW_BLOCK.get());
        this.dropSelf(BlockRegistry.ARCANE_STONE.get());
        this.dropSelf(BlockRegistry.ARCANE_STONE_BRICK.get());
        // Flesh Block: silk touch → block, otherwise → 9 rotten flesh
        this.add(BlockRegistry.FLESH_BLOCK.get(), block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block,
                LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(UniformGenerator.between(9.0F, 9.0F))))));
        this.dropSelf(BlockRegistry.GREATWOOD_PLANKS.get());
        this.dropSelf(BlockRegistry.SILVERWOOD_PLANKS.get());
        this.dropSelf(BlockRegistry.ARCANE_STONE_STAIRS.get());
        this.dropSelf(BlockRegistry.GREATWOOD_STAIRS.get());
        this.dropSelf(BlockRegistry.SILVERWOOD_STAIRS.get());
        this.dropSelf(BlockRegistry.ARCANE_STONE_SLAB.get());
        this.dropSelf(BlockRegistry.GREATWOOD_SLAB.get());
        this.dropSelf(BlockRegistry.SILVERWOOD_SLAB.get());
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
                block -> createSilkTouchDispatchTable(block, this.applyExplosionCondition(block, LootItem.lootTableItem(ItemRegistry.AQUA_SHARD)
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

        // Infusion Altar
        this.dropSelf(BlockRegistry.INFUSION_MATRIX.get());
        // Pillar drops 1 brick + 1 stone block
        this.add(BlockRegistry.INFUSION_PILLAR.get(), block -> LootTable.lootTable()
                .withPool(this.applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemRegistry.ARCANE_STONE_BRICK.get()))))
                .withPool(this.applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemRegistry.ARCANE_STONE.get())))));
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
        blocks.add(BlockRegistry.ARCANE_PEDESTAL.get());
        blocks.add(BlockRegistry.PAVING_STONE_OF_TRAVEL.get());
        blocks.add(BlockRegistry.PAVING_STONE_OF_WARDING.get());
        blocks.add(BlockRegistry.THAUMIUM_BLOCK.get());
        blocks.add(BlockRegistry.VOID_BLOCK.get());
        blocks.add(BlockRegistry.AMBER_BLOCK.get());
        blocks.add(BlockRegistry.AMBER_BRICK.get());
        blocks.add(BlockRegistry.OBSIDIAN_TILE.get());
        blocks.add(BlockRegistry.TALLOW_BLOCK.get());
        blocks.add(BlockRegistry.ARCANE_STONE.get());
        blocks.add(BlockRegistry.ARCANE_STONE_BRICK.get());
        blocks.add(BlockRegistry.FLESH_BLOCK.get());
        blocks.add(BlockRegistry.GREATWOOD_PLANKS.get());
        blocks.add(BlockRegistry.SILVERWOOD_PLANKS.get());
        blocks.add(BlockRegistry.ARCANE_STONE_STAIRS.get());
        blocks.add(BlockRegistry.GREATWOOD_STAIRS.get());
        blocks.add(BlockRegistry.SILVERWOOD_STAIRS.get());
        blocks.add(BlockRegistry.ARCANE_STONE_SLAB.get());
        blocks.add(BlockRegistry.GREATWOOD_SLAB.get());
        blocks.add(BlockRegistry.SILVERWOOD_SLAB.get());
        blocks.add(BlockRegistry.SHIMMERLEAF.get());
        blocks.add(BlockRegistry.CINDERPEARL.get());
        blocks.add(BlockRegistry.INFUSION_MATRIX.get());
        blocks.add(BlockRegistry.INFUSION_PILLAR.get());
        return blocks;
    }

}
