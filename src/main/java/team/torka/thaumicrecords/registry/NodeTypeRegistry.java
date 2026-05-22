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
import team.torka.thaumicrecords.api.node.Node;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.node.EerieNode;
import team.torka.thaumicrecords.node.HungryNode;
import team.torka.thaumicrecords.node.NormalNode;
import team.torka.thaumicrecords.node.PureNode;
import team.torka.thaumicrecords.node.TaintedNode;
import team.torka.thaumicrecords.node.UnstableNode;

@EventBusSubscriber
public class NodeTypeRegistry {
    public static final DeferredRegister<Node> REGISTRAR = DeferredRegister.create(RegistryKeys.NODE_TYPES, ThaumicRecords.MOD_ID);
    public static Registry<Node> NODE_TYPE_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        NODE_TYPE_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.NODE_TYPES));
    }

    public static DeferredHolder<Node,Node> getHolderFromType(NodeType type){
        switch (type){
            case EERIE:
                return NodeTypeRegistry.EERIE;
            case HUNGRY:
                return NodeTypeRegistry.HUNGRY;
            case NORMAL :
                return NodeTypeRegistry.NORMAL;
            case PURE:
                return NodeTypeRegistry.PURE;
            case TAINTED:
                return NodeTypeRegistry.TAINTED;
            case UNSTABLE:
                return NodeTypeRegistry.UNSTABLE;
        }
        return NodeTypeRegistry.NORMAL;
    }

    /*@formatter:off*/
    public static final DeferredHolder<Node,Node> NORMAL =  REGISTRAR.register("normal",NormalNode::new);
    public static final DeferredHolder<Node,Node> EERIE =  REGISTRAR.register("eerie",EerieNode::new);
    public static final DeferredHolder<Node,Node> PURE =  REGISTRAR.register("pure",PureNode::new);
    public static final DeferredHolder<Node,Node> HUNGRY =  REGISTRAR.register("hungry",HungryNode::new);
    public static final DeferredHolder<Node,Node> TAINTED =  REGISTRAR.register("tainted",TaintedNode::new);
    public static final DeferredHolder<Node,Node> UNSTABLE =  REGISTRAR.register("unstable",UnstableNode::new);
}
