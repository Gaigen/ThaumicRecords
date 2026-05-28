package team.torka.thaumicrecords.client.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import team.torka.thaumicrecords.api.IRunicArmor;
import team.torka.thaumicrecords.registry.DataComponentRegistry;

@EventBusSubscriber(Dist.CLIENT)
public class RunicTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IRunicArmor) {
            int charge = ((IRunicArmor) stack.getItem()).getRunicCharge(stack) + stack.getOrDefault(DataComponentRegistry.RUNIC_HARDEN.get(), 0);
            if (charge > 0) {
                event.getToolTip().add(Component.literal(ChatFormatting.GOLD + "+ " + charge + " Runic Charge"));
            }
        }
    }
}
