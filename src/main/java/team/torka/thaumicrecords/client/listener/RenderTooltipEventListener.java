package team.torka.thaumicrecords.client.listener;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.AspectHelper;
import team.torka.thaumicrecords.client.tooltip.AspectTooltipComponent;

@EventBusSubscriber(value = Dist.CLIENT)
public class RenderTooltipEventListener {

    @SubscribeEvent
    public static void onGatherComponentsEvent(RenderTooltipEvent.GatherComponents event) {
        if (Minecraft.getInstance().level == null || event.getItemStack().isEmpty()) {
            return;
        }
        if (Screen.hasShiftDown()) {
            AspectList aspects = AspectHelper.getAspects(event.getItemStack());
            if (!aspects.isEmpty()) {
                event.getTooltipElements().add(Either.right(new AspectTooltipComponent(aspects)));
            }
        }
    }
}
