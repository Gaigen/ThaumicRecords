package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.node.type.NormalNodeType;

@EventBusSubscriber
public class NodeTypeRegistry {
    public static final DeferredRegister<NodeType> REGISTRAR = DeferredRegister.create(RegistryKeys.NODE_TYPES, ThaumicRecords.MOD_ID);
    public static Registry<NodeType> NODE_TYPE_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        NODE_TYPE_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.NODE_TYPES));
    }

    /*@formatter:off*/
    public static final DeferredHolder<NodeType,NodeType> NORMAL =  REGISTRAR.register("normal",NormalNodeType::new);
}
