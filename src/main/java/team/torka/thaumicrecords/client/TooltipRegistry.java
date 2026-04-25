package team.torka.thaumicrecords.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.AspectHelper;
import team.torka.thaumicrecords.client.tooltip.AspectTooltipComponent;
import team.torka.thaumicrecords.client.tooltip.ClientAspectTooltipComponent;

@EventBusSubscriber(modid = ThaumicRecords.MOD_ID, value = Dist.CLIENT)
public class TooltipRegistry {

    @SubscribeEvent
    public static void onRegisterClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AspectTooltipComponent.class, ClientAspectTooltipComponent::new);
    }

    @SubscribeEvent
    public static void onPreRenderTooltipEvent(RenderTooltipEvent.GatherComponents event) {
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
