package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.entity.ArcanePedestalBlockEntity;
import team.torka.thaumicrecords.block.entity.ArcaneWorkbenchBlockEntity;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.block.entity.CrucibleBlockEntity;
import team.torka.thaumicrecords.block.entity.JarBlockEntity;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.block.entity.TableBlockEntity;
import team.torka.thaumicrecords.block.entity.ThaumatoriumBlockEntity;

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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcanePedestalBlockEntity>> ARCANE_PEDESTAL = REGISTRAR.register("arcane_pedestal",
            () -> BlockEntityType.Builder.of(ArcanePedestalBlockEntity::new, BlockRegistry.ARCANE_PEDESTAL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = REGISTRAR.register("crucible",
            () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, BlockRegistry.CRUCIBLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThaumatoriumBlockEntity>> THAUMATORIUM = REGISTRAR.register("thaumatorium",
            () -> BlockEntityType.Builder.of(ThaumatoriumBlockEntity::new, BlockRegistry.THAUMATORIUM.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<JarBlockEntity>> JAR = REGISTRAR.register("jar",
            () -> BlockEntityType.Builder.of(JarBlockEntity::new, BlockRegistry.JAR.get()).build(null));
}
