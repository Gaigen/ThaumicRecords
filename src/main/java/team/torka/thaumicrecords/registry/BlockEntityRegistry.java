package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.entity.ArcaneWorkbenchBlockEntity;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.block.entity.TableBlockEntity;

public class BlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRAR = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AuraNodeBlockEntity>> AURA_NODE = REGISTRAR.register("aura_node",
            () -> BlockEntityType.Builder.of(AuraNodeBlockEntity::new, BlockRegistry.AURA_NODE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH = REGISTRAR.register(
            "arcane_workbench", () -> BlockEntityType.Builder.of(ArcaneWorkbenchBlockEntity::new, BlockRegistry.ARCANE_WORKBENCH.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TableBlockEntity>> TABLE = REGISTRAR.register("table",
            () -> BlockEntityType.Builder.of(TableBlockEntity::new, BlockRegistry.TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE = REGISTRAR.register("research_table",
            () -> BlockEntityType.Builder.of(ResearchTableBlockEntity::new, BlockRegistry.RESEARCH_TABLE.get()).build(null));
}
