package team.torka.thaumicrecords.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.ArcaneWorkbenchBlock;
import team.torka.thaumicrecords.block.AuraNodeBlock;
import team.torka.thaumicrecords.block.ResearchTableBlock;
import team.torka.thaumicrecords.block.TableBlock;


public class BlockRegistry {
    public static final DeferredRegister.Blocks REGISTRAR = DeferredRegister.createBlocks(ThaumicRecords.MOD_ID);

    public static final DeferredBlock<Block> AMBER_ORE = REGISTRAR.registerSimpleBlock("amber_ore", BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 5.0F)
            .sound(SoundType.STONE));

    public static final DeferredBlock<Block> CINNABAR_ORE = REGISTRAR.registerSimpleBlock("cinnabar_ore", BlockBehaviour.Properties.of().mapColor(
            MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 5.0F).sound(SoundType.STONE));

    public static final DeferredBlock<AuraNodeBlock> AURA_NODE = REGISTRAR.register("aura_node", () -> new AuraNodeBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.NONE)
            .strength(2.0F, 200.0F)
            .noOcclusion()
            .noCollission()
            .noLootTable()
            .randomTicks()
            .dynamicShape()
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
}
