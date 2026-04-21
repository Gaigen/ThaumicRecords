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
import team.torka.thaumicrecords.node.modifier.NormalNodeModifier;

@EventBusSubscriber(modid = ThaumicRecords.MOD_ID)
public class NodeModifierRegistry {
    public static final DeferredRegister<NodeModifier> REGISTRAR =
            DeferredRegister.create(RegistryKeys.NODE_MODIFIERS, ThaumicRecords.MOD_ID);
    public static Registry<NodeModifier> NODE_TYPE_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        NODE_TYPE_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.NODE_MODIFIERS));
    }

    /*@formatter:off*/
    public static final DeferredHolder<NodeModifier,NodeModifier> NORMAL =  REGISTRAR.register("normal",NormalNodeModifier::new);
}

