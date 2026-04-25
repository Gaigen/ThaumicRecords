package team.torka.thaumicrecords.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.ArcaneWorkbenchBlock;
import team.torka.thaumicrecords.block.AuraNodeBlock;


public class BlockRegistry {
    public static final DeferredRegister.Blocks REGISTRAR = DeferredRegister.createBlocks(ThaumicRecords.MOD_ID);

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
}
