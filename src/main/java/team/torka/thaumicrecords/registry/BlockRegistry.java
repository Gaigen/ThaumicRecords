package team.torka.thaumicrecords.registry;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.AlchemicalConstructBlock;
import team.torka.thaumicrecords.block.ArcanePedestalBlock;
import team.torka.thaumicrecords.block.ArcaneWorkbenchBlock;
import team.torka.thaumicrecords.block.AuraNodeBlock;
import team.torka.thaumicrecords.block.CrucibleBlock;
import team.torka.thaumicrecords.block.JarBlock;
import team.torka.thaumicrecords.block.PavingStoneOfTravelBlock;
import team.torka.thaumicrecords.block.PavingStoneOfWardingBlock;
import team.torka.thaumicrecords.block.ResearchTableBlock;
import team.torka.thaumicrecords.block.TableBlock;
import team.torka.thaumicrecords.block.ThaumatoriumBlock;
import team.torka.thaumicrecords.block.WardingBarrierBlock;
import team.torka.thaumicrecords.world.tree.TreeGrowers;


public class BlockRegistry {
    public static final DeferredRegister.Blocks REGISTRAR = DeferredRegister.createBlocks(ThaumicRecords.MOD_ID);

    public static final DeferredBlock<Block> AER_INFUSED_STONE = REGISTRAR.register("aer_infused_stone",
            () -> new DropExperienceBlock(UniformInt.of(0, 3), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 5.0F)
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<Block> IGNIS_INFUSED_STONE = REGISTRAR.register("ignis_infused_stone",
            () -> new DropExperienceBlock(UniformInt.of(0, 3), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 5.0F)
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<Block> AQUA_INFUSED_STONE = REGISTRAR.register("aqua_infused_stone",
            () -> new DropExperienceBlock(UniformInt.of(0, 3), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 5.0F)
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<Block> TERRA_INFUSED_STONE = REGISTRAR.register("terra_infused_stone",
            () -> new DropExperienceBlock(UniformInt.of(0, 3), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 5.0F)
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<Block> ORDO_INFUSED_STONE = REGISTRAR.register("ordo_infused_stone",
            () -> new DropExperienceBlock(UniformInt.of(0, 3), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 5.0F)
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<Block> PERDITIO_INFUSED_STONE = REGISTRAR.register("perditio_infused_stone",
            () -> new DropExperienceBlock(UniformInt.of(0, 3), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 5.0F)
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<Block> AMBER_ORE = REGISTRAR.register("amber_ore",
            () -> new DropExperienceBlock(UniformInt.of(1, 4), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 5.0F)
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<Block> CINNABAR_ORE = REGISTRAR.registerSimpleBlock("cinnabar_ore", BlockBehaviour.Properties.of().mapColor(
            MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 5.0F).sound(SoundType.STONE));
    public static final DeferredBlock<ArcanePedestalBlock> ARCANE_PEDESTAL = REGISTRAR.register("arcane_pedestal",
            () -> new ArcanePedestalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 25.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<JarBlock> JAR = REGISTRAR.register("jar", () -> new JarBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.NONE)
            .strength(0.3F)
            .sound(SoundType.GLASS)
            .noOcclusion()));

    public static final DeferredBlock<ThaumatoriumBlock> THAUMATORIUM = REGISTRAR.register("thaumatorium",
            () -> new ThaumatoriumBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 10.0F)
                    .noOcclusion()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<CrucibleBlock> CRUCIBLE = REGISTRAR.register("crucible", () -> new CrucibleBlock(BlockBehaviour.Properties.of().mapColor(
            MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 10.0F).noOcclusion().sound(SoundType.METAL)));

    public static final DeferredBlock<AlchemicalConstructBlock> ALCHEMICAL_CONSTRUCT = REGISTRAR.register("alchemical_construct",
            () -> new AlchemicalConstructBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 17.0F).sound(SoundType.METAL)));

    public static final DeferredBlock<AuraNodeBlock> AURA_NODE = REGISTRAR.register("aura_node", () -> new AuraNodeBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.NONE)
            .strength(2.0F, 200.0F)
            .noOcclusion()
            .noCollission()
            .noLootTable()
            .randomTicks()
            .dynamicShape()
            .forceSolidOn()
            .sound(SoundType.WOOL)
            .pushReaction(PushReaction.BLOCK)));

    public static final DeferredBlock<ArcaneWorkbenchBlock> ARCANE_WORKBENCH = REGISTRAR.register("arcane_workbench",
            () -> new ArcaneWorkbenchBlock(BlockBehaviour.Properties.of()
                    .strength(2.5F, 3.0F)
                    .noOcclusion()
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.BLOCK)));

    public static final DeferredBlock<TableBlock> TABLE = REGISTRAR.register("table", () -> new TableBlock(BlockBehaviour.Properties.of()
            .strength(2.5F, 3.0F)
            .noOcclusion()
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.NORMAL)));

    public static final DeferredBlock<ResearchTableBlock> RESEARCH_TABLE = REGISTRAR.register("research_table",
            () -> new ResearchTableBlock(BlockBehaviour.Properties.of()
                    .strength(2.5F, 3.0F)
                    .noOcclusion()
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.BLOCK)));

    public static final DeferredBlock<SaplingBlock> SILVERWOOD_SAPLING = REGISTRAR.register("silverwood_sapling",
            () -> new SaplingBlock(TreeGrowers.SILVERWOOD_TREE, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> SILVERWOOD_LOG = REGISTRAR.register("silverwood_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));
    public static final DeferredBlock<Block> SILVERWOOD_LEAVES = REGISTRAR.register("silverwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)));

    public static final DeferredBlock<SaplingBlock> GREATWOOD_SAPLING = REGISTRAR.register("greatwood_sapling",
            () -> new SaplingBlock(TreeGrowers.GREATWOOD_TREE, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> GREATWOOD_LOG = REGISTRAR.register("greatwood_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));
    public static final DeferredBlock<Block> GREATWOOD_LEAVES = REGISTRAR.register("greatwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).lightLevel(state -> 7)));

    public static final DeferredBlock<PavingStoneOfTravelBlock> PAVING_STONE_OF_TRAVEL = REGISTRAR.register("paving_stone_of_travel",
            () -> new PavingStoneOfTravelBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 10.0F)
                    .lightLevel(state -> 9)
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<PavingStoneOfWardingBlock> PAVING_STONE_OF_WARDING = REGISTRAR.register("paving_stone_of_warding",
            () -> new PavingStoneOfWardingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 10.0F)
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<WardingBarrierBlock> WARDING_BARRIER = REGISTRAR.register("warding_barrier",
            () -> new WardingBarrierBlock(BlockBehaviour.Properties.of()
                    .replaceable()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .pushReaction(PushReaction.BLOCK)));

    // Metal Blocks
    public static final DeferredBlock<Block> THAUMIUM_BLOCK = REGISTRAR.registerSimpleBlock("thaumium_block", BlockBehaviour.Properties.of().mapColor(
            MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(4.0F, 10.0F).sound(SoundType.METAL));
    public static final DeferredBlock<Block> VOID_BLOCK = REGISTRAR.registerSimpleBlock("void_block", BlockBehaviour.Properties.of().mapColor(
            MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(4.0F, 10.0F).sound(SoundType.METAL));
}
