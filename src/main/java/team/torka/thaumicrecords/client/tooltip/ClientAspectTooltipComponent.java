package team.torka.thaumicrecords.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;

import java.util.Map;

public class ClientAspectTooltipComponent implements ClientTooltipComponent {

    private final AspectTooltipComponent aspectTooltipComponent;

    public ClientAspectTooltipComponent(AspectTooltipComponent aspectTooltipComponent) {
        this.aspectTooltipComponent = aspectTooltipComponent;
    }

    @Override
    public int getHeight(Font font) {
        return 8;
    }

    @Override
    public int getWidth(Font font) {
        return 8;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        for (Map.Entry<Aspect, Long> entry : aspectTooltipComponent.aspectNum().entrySet()) {
            Aspect aspect = entry.getKey();
            String line = aspect.getName() + " x " + entry.getValue();
            ResourceLocation image = ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID,
                    "textures/aspects/" + aspect.getName() + ".png");
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, image, x, y, 0, 0, 8, 8, 8, 8, aspect.getARGBColor());
            guiGraphics.pose().pushMatrix();
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, line, ((x + 8)), ((y + 15)),
                    aspect.getARGBColor());
            guiGraphics.pose().popMatrix();
        }
    }
}
