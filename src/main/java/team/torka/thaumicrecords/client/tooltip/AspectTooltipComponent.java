package team.torka.thaumicrecords.client.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import team.torka.thaumicrecords.api.aspect.AspectList;

public record AspectTooltipComponent(AspectList aspects) implements TooltipComponent {
}
