package team.torka.thaumicrecords.client.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import team.torka.thaumicrecords.api.aspect.Aspect;

import java.util.Map;

public record AspectTooltipComponent(Map<Aspect,Long> aspectNum) implements TooltipComponent {}
