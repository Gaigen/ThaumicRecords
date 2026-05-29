package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.api.research.ResearchCategory;
import team.torka.thaumicrecords.registry.ResearchCategoryRegistry;
import team.torka.thaumicrecords.registry.ResearchRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class ThaumonomiconScreen extends Screen {
    private static final ResourceLocation GUI_TEXTURE = ThaumicRecords.createRl("textures/gui/thaumonomicon_gui.png");
    // from tc4tweaks
    private static final int BORDER_TEXTURE_WIDTH = 256;
    private static final int BORDER_TEXTURE_HEIGHT = 230;
    private static final int BORDER_HEIGHT = 17;
    private static final int BORDER_WIDTH = 16;
    private static final float BACKGROUND_ZLEVEL = -100.0F;
    private static final int[][] PARAMS = {{0, 0, 0, 1}, {0, 1, 1, -1}, {1, -1, 1, 0}};
    private static int guiX, guiY;

    // FIXME test category and research
    private static final Research test = ResearchRegistry.TEST.get();
    private static final ResearchCategory testCategory = ResearchCategoryRegistry.TEST.get();


    public ThaumonomiconScreen() {
        super(Component.empty());
    }


    @Override
    protected void init() {
        super.init();
    }


    @Override
    @ParametersAreNonnullByDefault
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        int renderStartX = (this.width - BORDER_TEXTURE_WIDTH) / 2;
        int renderStartY = (this.height - BORDER_TEXTURE_HEIGHT) / 2;
        drawBackground(guiGraphics, testCategory.background, 0, 0);
        drawBorders(guiGraphics, renderStartX, renderStartY, BORDER_TEXTURE_WIDTH, BORDER_TEXTURE_HEIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
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

    public void drawBorders(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.flush();
        int oldDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        PoseStack poseStack = guiGraphics.pose();
        for (int[] paramX : PARAMS) {
            for (int[] paramY : PARAMS) {
                double x1 = x + paramX[0] * width + paramX[1] * BORDER_WIDTH;
                double x2 = x + paramX[2] * width + paramX[3] * BORDER_WIDTH;
                double y1 = y + paramY[0] * height + paramY[1] * BORDER_HEIGHT;
                double y2 = y + paramY[2] * height + paramY[3] * BORDER_HEIGHT;

                double u1 = paramX[0] * BORDER_TEXTURE_WIDTH + paramX[1] * BORDER_WIDTH;
                double u2 = paramX[2] * BORDER_TEXTURE_WIDTH + paramX[3] * BORDER_WIDTH;
                double v1 = paramY[0] * BORDER_TEXTURE_HEIGHT + paramY[1] * BORDER_HEIGHT;
                double v2 = paramY[2] * BORDER_TEXTURE_HEIGHT + paramY[3] * BORDER_HEIGHT;

                drawRectTextured(poseStack, GUI_TEXTURE, x1, x2, y1, y2, u1, u2, v1, v2, BACKGROUND_ZLEVEL);
            }
        }
        guiGraphics.flush();
        RenderSystem.depthFunc(oldDepthFunc);
        guiX = x;
        guiY = y;
    }

    public void drawBackground(GuiGraphics guiGraphics, ResourceLocation texture, double uOffset, double vOffset) {

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        double startX = (screenWidth - BORDER_TEXTURE_WIDTH) / 2.0;
        double startY = (screenHeight - BORDER_TEXTURE_HEIGHT) / 2.0;

        double canvasX1 = startX + BORDER_WIDTH;
        double canvasY1 = startY + BORDER_HEIGHT;
        double canvasX2 = startX + BORDER_TEXTURE_WIDTH - BORDER_WIDTH;
        double canvasY2 = startY + BORDER_TEXTURE_HEIGHT - BORDER_HEIGHT;

        double canvasWidth = canvasX2 - canvasX1;
        double canvasHeight = canvasY2 - canvasY1;

        guiGraphics.flush();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        double u1 = uOffset;
        double v1 = vOffset;
        double u2 = u1 + canvasWidth;
        double v2 = v1 + canvasHeight;

        drawRectTextured(poseStack, texture, canvasX1, canvasX2, canvasY1, canvasY2, u1, u2, v1, v2, BACKGROUND_ZLEVEL);

        guiGraphics.flush();
        poseStack.popPose();
    }

    private static void drawRectTextured(PoseStack poseStack, ResourceLocation texture, double x1, double x2, double y1, double y2, double u1, double u2,
                                         double v1, double v2, float zLevel) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float f = 1.0F / 256.0F;
        float f1 = 1.0F / 256.0F;

        bufferBuilder.addVertex(matrix, (float) x1, (float) y2, zLevel).setUv((float) (u1 * f), (float) (v2 * f1));
        bufferBuilder.addVertex(matrix, (float) x2, (float) y2, zLevel).setUv((float) (u2 * f), (float) (v2 * f1));
        bufferBuilder.addVertex(matrix, (float) x2, (float) y1, zLevel).setUv((float) (u2 * f), (float) (v1 * f1));
        bufferBuilder.addVertex(matrix, (float) x1, (float) y1, zLevel).setUv((float) (u1 * f), (float) (v1 * f1));

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.disableBlend();
    }
}
