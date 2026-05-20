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
import team.torka.thaumicrecords.api.node.NodeModifier;

@EventBusSubscriber
public class NodeModifierRegistry {
    public static final DeferredRegister<NodeModifier> REGISTRAR = DeferredRegister.create(RegistryKeys.NODE_MODIFIERS, ThaumicRecords.MOD_ID);
    public static Registry<NodeModifier> NODE_MODIFIER_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        NODE_MODIFIER_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.NODE_MODIFIERS));
    }

    /*@formatter:off*/
    public static final DeferredHolder<NodeModifier,NodeModifier> NORMAL =  REGISTRAR.register("normal",()->new NodeModifier(ThaumicRecords.createTranslationKey("node_type", "normal"), 1));
    public static final DeferredHolder<NodeModifier,NodeModifier> BRIGHT =  REGISTRAR.register("bright",()->new NodeModifier(ThaumicRecords.createTranslationKey("node_type", "bright"), 0.6666667));
    public static final DeferredHolder<NodeModifier,NodeModifier> PALE =  REGISTRAR.register("pale",()->new NodeModifier(ThaumicRecords.createTranslationKey("node_type", "pale"), 1.5));
    public static final DeferredHolder<NodeModifier,NodeModifier> FADING =  REGISTRAR.register("fading",()->new NodeModifier(ThaumicRecords.createTranslationKey("node_type", "fading"), 0));
}

