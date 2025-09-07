package team.torka.thaumicrecords.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;

public class TRBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThaumicRecords.MOD_ID);


//    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AuraNodeBlockEntity>> AURA_NODE =
//            REGISTER.register("aura_node_blockentity",
//                    () -> BlockEntityType.Builder.of(AuraNodeBlockEntity::new, TRBlocks.AURA_NODE.get())
//                                                 .build(null));
}
