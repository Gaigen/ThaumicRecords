package team.torka.thaumicrecords.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;

import java.util.Map;

public class ClientAspectTooltipComponent implements ClientTooltipComponent {

    private final AspectTooltipComponent aspectTooltipComponent;

    public ClientAspectTooltipComponent(AspectTooltipComponent aspectTooltipComponent) {
        this.aspectTooltipComponent = aspectTooltipComponent;
    }

    @Override
    public int getHeight() {
        return 8 * aspectTooltipComponent.aspectNum().size();
    }

    @Override
    public int getWidth(Font font) {
        return 8;
    }

    @Override
    public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        ClientTooltipComponent.super.renderText(font, mouseX, mouseY, matrix, bufferSource);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int offsetY = 0;
        for (Map.Entry<Aspect, Long> entry : aspectTooltipComponent.aspectNum().entrySet()) {
            Aspect aspect = entry.getKey();
            String line = entry.getValue().toString();
            ResourceLocation image = ThaumicRecords.createRl("textures/aspects/" + aspect.getName() + ".png");
            guiGraphics.blit(image, x, y + offsetY * 8, 0, 0, 8, 8, 8, 8, aspect.getARGBColor());
            guiGraphics.drawString(Minecraft.getInstance().font, line, x + 8, y + offsetY * 8, aspect.getARGBColor());
            offsetY += 1;
        }
    }
}
