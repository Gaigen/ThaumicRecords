package team.torka.thaumicrecords.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.registry.AspectRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class ClientAspectTooltipComponent implements ClientTooltipComponent {

    private final AspectList aspects;

    public ClientAspectTooltipComponent(AspectTooltipComponent aspectTooltipComponent) {
        this.aspects = aspectTooltipComponent.aspects();
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getWidth(Font font) {
        return aspects.size() * 18;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int currentX = x;
        for (var entry : aspects.entrySet()) {
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(entry.getKey());
            if (aspect == null) {
                continue;
            }
            ResourceLocation image = aspect.getImage();
            int argbColor = aspect.getARGBColor();
            float a = (argbColor >> 24 & 255) / 255.0F;
            float r = (argbColor >> 16 & 255) / 255.0F;
            float g = (argbColor >> 8 & 255) / 255.0F;
            float b = (argbColor & 255) / 255.0F;
            graphics.setColor(r, g, b, a);
            graphics.blit(image, currentX, y, 0, 0, 0, 16, 16, 16, 16);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            String amount = String.valueOf(entry.getValue());
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200);
            graphics.drawString(font, amount, currentX + 16 - font.width(amount), y + 10, 0xFFFFFFFF, true);
            graphics.pose().popPose();
            currentX += getWidth(font);
        }
    }
}
