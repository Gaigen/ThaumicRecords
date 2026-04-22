package team.torka.thaumicrecords.client.listener;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import team.torka.thaumicrecords.block.AuraNodeBlock;

@EventBusSubscriber(value = Dist.CLIENT)
public class RenderHighlightEventListener {
    @SubscribeEvent
    public static void registerClientExtensions(RenderHighlightEvent.Block event) {
        BlockState state = event.getCamera().getEntity().level().getBlockState(event.getTarget().getBlockPos());
        if (state.getBlock() instanceof AuraNodeBlock) {
            event.setCanceled(true);
        }
    }
}
