package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import team.torka.thaumicrecords.ThaumicRecords;

import javax.annotation.ParametersAreNonnullByDefault;

public class ResearchDetailScreen extends Screen {

    private static final ResourceLocation GUI_TEXTURE = ThaumicRecords.createRl("textures/gui/researchbook.png");

    private static final int PANE_WIDTH = 256;
    private static final int PANE_HEIGHT = 181;

    private final ResourceLocation researchKey;

    public ResearchDetailScreen(ResourceLocation researchKey) {
        super(Component.empty());
        this.researchKey = researchKey;
        ThaumicRecords.LOGGER.debug("opened {}", researchKey.toString());
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        drawBookBackground(g);
    }

    private void drawBookBackground(GuiGraphics g) {

        float scaledStartX = (this.width - PANE_WIDTH * 1.3F) / 2.0F;
        float scaledStartY = (this.height - PANE_HEIGHT * 1.3F) / 2.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        PoseStack poseStack = g.pose();
        poseStack.pushPose();

        poseStack.translate(scaledStartX, scaledStartY, 0.0F);
        poseStack.scale(1.3F, 1.3F, 1.0F);

        g.blit(GUI_TEXTURE, 0, 0, 0, 0, PANE_WIDTH, PANE_HEIGHT, 256, 256);

        poseStack.popPose();
        RenderSystem.disableBlend();
    }


    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
