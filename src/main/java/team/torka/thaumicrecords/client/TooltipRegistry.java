package team.torka.thaumicrecords.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.client.tooltip.ClientAspectTooltipComponent;
import team.torka.thaumicrecords.client.tooltip.AspectTooltipComponent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = ThaumicRecords.MOD_ID, value = Dist.CLIENT)
public class TooltipRegistry {

    private static Map<Aspect,Long> DUMMY=new HashMap<>();
    static {
        DUMMY.put(Aspect.TERRA,1L);
    }

    @SubscribeEvent
    public static void onRegisterClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AspectTooltipComponent.class, ClientAspectTooltipComponent::new);
    }

    @SubscribeEvent
    public static void onPreRenderTooltipEvent(RenderTooltipEvent.GatherComponents event){
        if (Minecraft.getInstance().level==null || event.getItemStack().isEmpty()){
            return;
        }
        Item item = event.getItemStack().getItem();
        if (item== Items.DIRT){
            event.getTooltipElements().add(Either.right(new AspectTooltipComponent(DUMMY)));
        }

    }
}
