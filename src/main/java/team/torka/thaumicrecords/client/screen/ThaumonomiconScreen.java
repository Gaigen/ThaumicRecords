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
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.helper.ResearchHelper;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.api.research.ResearchCategory;
import team.torka.thaumicrecords.registry.ResearchCategoryRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

public class ThaumonomiconScreen extends Screen {
    private static final ResourceLocation GUI_TEXTURE = ThaumicRecords.createRl("textures/gui/thaumonomicon_gui.png");
    private static final ResourceLocation PARTICLE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/particle/particles.png");
    // from tc4tweaks
    private static final int BORDER_TEXTURE_WIDTH = 256;
    private static final int BORDER_TEXTURE_HEIGHT = 230;
    private static final int BORDER_HEIGHT = 17;
    private static final int BORDER_WIDTH = 16;
    private static final float BACKGROUND_ZLEVEL = -100.0F;
    private static final int[][] PARAMS = {{0, 0, 0, 1}, {0, 1, 1, -1}, {1, -1, 1, 0}};

    // 记忆上次打开GUI情况
    private static ResearchCategory lastCategory = null;
    private static int lastX = -5;
    private static int lastY = -6;

    private double guiMapX;
    private double guiMapY;

    private double targetMapX;
    private double targetMapY;

    private double prevMapX;
    private double prevMapY;


    private boolean isDragging = false;

    private int guiMapTop = 0;
    private int guiMapLeft = 0;
    private int guiMapBottom = 0;
    private int guiMapRight = 0;

    private ResearchCategory selectedCategory = ResearchCategoryRegistry.BASIC.get();

    private List<Research> highlightedResearch = List.of();

    public ThaumonomiconScreen() {
        super(Component.empty());
        if (lastCategory != null) {
            this.selectedCategory = lastCategory;
        }
        this.guiMapX = this.targetMapX = this.prevMapX = lastX * 24 - 112;
        this.guiMapY = this.targetMapY = this.prevMapY = lastY * 24 - 98;
    }


    @Override
    protected void init() {
        super.init();
        updateScrollBounds();
        if (lastX == -5 && lastY == -6) {
            centerViewport();
        }
    }

    private void updateScrollBounds() {
        List<Research> researchList = ResearchHelper.getResearchesByCategory(selectedCategory);
        if (researchList.isEmpty()) {
            guiMapTop = -144;
            guiMapLeft = -158;
            guiMapBottom = 144;
            guiMapRight = 158;
            return;
        }
        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;
        int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
        for (Research r : researchList) {
            minCol = Math.min(minCol, r.col);
            maxCol = Math.max(maxCol, r.col);
            minRow = Math.min(minRow, r.row);
            maxRow = Math.max(maxRow, r.row);
        }

        guiMapTop = minCol * 24 - 85;
        guiMapLeft = minRow * 24 - 112;
        guiMapBottom = maxCol * 24 - 112;
        guiMapRight = maxRow * 24 - 61;

        if (guiMapBottom - guiMapTop < 288) {
            int center = (minCol + maxCol) * 12;
            guiMapTop = center - 144;
            guiMapBottom = center + 144;
        }
        if (guiMapRight - guiMapLeft < 316) {
            int center = (minRow + maxRow) * 12;
            guiMapLeft = center - 158;
            guiMapRight = center + 158;
        }
    }

    private void centerViewport() {
        int contentWidth = BORDER_TEXTURE_WIDTH - 2 * BORDER_WIDTH;   // 224
        int contentHeight = BORDER_TEXTURE_HEIGHT - 2 * BORDER_HEIGHT; // 196
        this.guiMapX = this.targetMapX = this.prevMapX = (guiMapTop + guiMapBottom) / 2.0 - contentWidth / 2.0;
        this.guiMapY = this.targetMapY = this.prevMapY = (guiMapLeft + guiMapRight) / 2.0 - contentHeight / 2.0;
    }

    @Override
    public void tick() {
        this.prevMapX = this.guiMapX;
        this.prevMapY = this.guiMapY;

        double dx = this.targetMapX - this.guiMapX;
        double dy = this.targetMapY - this.guiMapY;

        if (dx * dx + dy * dy < 4.0) {
            this.guiMapX += dx;
            this.guiMapY += dy;
        } else {
            this.guiMapX += dx * 0.85;
            this.guiMapY += dy * 0.85;
        }

        this.guiMapX = Mth.clamp(this.guiMapX, guiMapTop, guiMapBottom);
        this.guiMapY = Mth.clamp(this.guiMapY, guiMapLeft, guiMapRight);
    }


    @Override
    @ParametersAreNonnullByDefault
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        double smoothMapX = this.prevMapX + (this.guiMapX - this.prevMapX) * partialTick;
        double smoothMapY = this.prevMapY + (this.guiMapY - this.prevMapY) * partialTick;

        smoothMapX = Mth.clamp(smoothMapX, guiMapTop, guiMapBottom);
        smoothMapY = Mth.clamp(smoothMapY, guiMapLeft, guiMapRight);

        int renderStartX = (this.width - BORDER_TEXTURE_WIDTH) / 2;
        int renderStartY = (this.height - BORDER_TEXTURE_HEIGHT) / 2;

        int contentX1 = renderStartX + BORDER_WIDTH;
        int contentY1 = renderStartY + BORDER_HEIGHT;
        int contentWidth = BORDER_TEXTURE_WIDTH - 2 * BORDER_WIDTH;
        int contentHeight = BORDER_TEXTURE_HEIGHT - 2 * BORDER_HEIGHT;

        enableContentScissor(contentX1, contentY1, contentWidth, contentHeight);
        drawBackground(guiGraphics, selectedCategory.background, smoothMapX, smoothMapY);
        drawResearchNodes(guiGraphics, smoothMapX, smoothMapY, contentX1, contentY1);
        RenderSystem.disableScissor();
        drawCategoryTags(guiGraphics, renderStartX, renderStartY);

        drawBorders(guiGraphics, renderStartX, renderStartY, BORDER_TEXTURE_WIDTH, BORDER_TEXTURE_HEIGHT);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int renderStartX = (this.width - BORDER_TEXTURE_WIDTH) / 2;
            int renderStartY = (this.height - BORDER_TEXTURE_HEIGHT) / 2;
            double contentX1 = renderStartX + BORDER_WIDTH;
            double contentY1 = renderStartY + BORDER_HEIGHT;
            double contentX2 = renderStartX + BORDER_TEXTURE_WIDTH - BORDER_WIDTH;
            double contentY2 = renderStartY + BORDER_TEXTURE_HEIGHT - BORDER_HEIGHT;

            ResearchCategory clickedCategory = getCategoryAtPosition(mouseX, mouseY, renderStartX, renderStartY);
            if (Objects.nonNull(clickedCategory) && !clickedCategory.equals(selectedCategory)) {
                selectedCategory = clickedCategory;
                updateScrollBounds();
                centerViewport();
                if (Objects.nonNull(Minecraft.getInstance().player)) {
                    Minecraft.getInstance().player.playSound(SoundRegistry.CAMERA_TICKS.get(), 0.4F, 1.0F);
                }
                return true;
            }

            if (mouseX >= contentX1 && mouseX < contentX2 && mouseY >= contentY1 && mouseY < contentY2) {
                this.isDragging = true;
                this.targetMapX = this.guiMapX;
                this.targetMapY = this.guiMapY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Nullable
    private ResearchCategory getCategoryAtPosition(double mouseX, double mouseY, int renderStartX, int renderStartY) {
        List<ResearchCategory> categories = ResearchCategoryRegistry.RESEARCH_REGISTRY.stream().toList();
        int count = 0;
        boolean mirror = false;
        int tabPerSide = 9;
        int tabDistance = 264;

        for (ResearchCategory category : categories) {
            if (count == tabPerSide) {
                count = 0;
                mirror = true;
            }
            int s0 = !mirror ? 0 : tabDistance;
            double xStart = mirror ? (renderStartX + s0 - 8) : (renderStartX - 24 + s0);
            double yStart = renderStartY + count * 24;

            if (mouseX >= xStart && mouseX < xStart + 24 && mouseY >= yStart && mouseY < yStart + 24) {
                return category;
            }
            ++count;
        }
        return null;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isDragging) {
            this.isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDragging && button == 0) {
            this.guiMapX -= dragX;
            this.guiMapY -= dragY;

            this.targetMapX = this.guiMapX;
            this.targetMapY = this.guiMapY;

            this.guiMapX = Mth.clamp(this.guiMapX, guiMapTop, guiMapBottom);
            this.guiMapY = Mth.clamp(this.guiMapY, guiMapLeft, guiMapRight);
            this.targetMapX = Mth.clamp(this.targetMapX, guiMapTop, guiMapBottom);
            this.targetMapY = Mth.clamp(this.targetMapY, guiMapLeft, guiMapRight);

            return true;
        }
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
        lastX = (int) ((this.guiMapX + 141 / 2.0 + 12.0) / 24.0);
        lastY = (int) ((this.guiMapY + 141 / 2.0) / 24.0);
        lastCategory = this.selectedCategory;
        super.onClose();
    }

    private void drawResearchNodes(GuiGraphics guiGraphics, double viewOffsetX, double viewOffsetY, int contentX1, int contentY1) {
        List<Research> researchList = ResearchHelper.getResearchesByCategory(selectedCategory);
        if (researchList.isEmpty()) {
            return;
        }

        PoseStack poseStack = guiGraphics.pose();
        int contentWidth = BORDER_TEXTURE_WIDTH - 2 * BORDER_WIDTH;   // 224
        int contentHeight = BORDER_TEXTURE_HEIGHT - 2 * BORDER_HEIGHT; // 196

        for (Research research : researchList) {
            double researchX = research.col * 24.0 - viewOffsetX + contentX1;
            double researchY = research.row * 24.0 - viewOffsetY + contentY1;

            if (researchX + 24 < contentX1 || researchY + 24 < contentY1 || researchX - 2 > contentX1 + contentWidth || researchY - 2 > contentY1 + contentHeight) {
                continue;
            }

            drawResearchShape(poseStack, researchX, researchY, research.renderStrategy);

            if (research.iconItem != null) {
                guiGraphics.flush();
                guiGraphics.renderFakeItem(research.iconItem, (int) researchX + 3, (int) researchY + 3);
                guiGraphics.flush();
            } else if (research.icon != null) {
                drawRectTextured(poseStack, research.icon, researchX + 3, researchX + 19, researchY + 3, researchY + 19, 0, 256, 0, 256, 0);
            }
        }
    }


    private void drawResearchShape(PoseStack poseStack, double x, double y, Research.RenderStrategy strategy) {
        double u;
        double v = 230.0;
        switch (strategy) {
            case ROUND -> u = 54.0;
            case SPIKY -> u = 26.0;
            case HEXAGON -> u = 110.0;
            default -> u = 0.0;
        }
        drawRectTextured(poseStack, GUI_TEXTURE, x - 2, x + 24, y - 2, y + 24, u, u + 26, v, v + 26, 0);
    }


    private void drawCategoryTags(GuiGraphics guiGraphics, int renderStartX, int renderStartY) {
        // TODO 获取玩家解锁研究
        List<ResearchCategory> categories = (ResearchCategoryRegistry.RESEARCH_REGISTRY.stream().toList());

        int count = 0;
        boolean mirror = false; // 按钮是否放到另外一边
        int tabPerSide = 9;     // 单侧最多放9个标签
        int tabDistance = 264;  // 右侧标签栏的横向偏移量

        PoseStack poseStack = guiGraphics.pose();
        long gameTime = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0L;

        for (ResearchCategory category : categories) {
            if (count == tabPerSide) {
                count = 0;
                mirror = true;
            }

            int s0 = !mirror ? 0 : tabDistance;
            int s1 = 0;
            int s2 = mirror ? 14 : 0;

            boolean isSelected = selectedCategory.equals(category);
            if (!isSelected) {
                s1 = 24;
                s2 = mirror ? 6 : 8;
            }

            double xStart = mirror ? (renderStartX + s0 - 8) : (renderStartX - 24 + s0);
            double yStart = renderStartY + count * 24;

            double x1 = xStart;
            double x2 = xStart + 24.0;
            double y1 = yStart;
            double y2 = yStart + 24.0;

            double baseU = mirror ? (176 + s1) : (152 + s1);
            double baseV = 232.0;

            double u1, u2;
            if (mirror) {
                u1 = baseU + 24.0;
                u2 = baseU;
            } else {
                u1 = baseU;
                u2 = baseU + 24.0;
            }
            double v1 = baseV;
            double v2 = baseV + 24.0;

            drawRectTextured(poseStack, GUI_TEXTURE, x1, x2, y1, y2, u1, u2, v1, v2, BACKGROUND_ZLEVEL);

            // 画类别图标
            if (Objects.nonNull(category.iconItem)) {
                int itemX = (int) xStart + 4;
                int itemY = (int) yStart + 4;
                guiGraphics.flush();
                guiGraphics.renderFakeItem(category.iconItem, itemX, itemY);
            } else {
                if (category.icon != null) {
                    double iX1 = renderStartX - 20 + s2 + s0;
                    double iY1 = renderStartY + 4 + count * 24;
                    double iX2 = iX1 + 16.0;
                    double iY2 = iY1 + 16.0;

                    drawRectTextured(poseStack, category.icon, iX1, iX2, iY1, iY2, 0.0, 256.0, 0.0, 256.0, BACKGROUND_ZLEVEL);
                }
            }

            // 新研究的发光图标
            if (highlightedResearch.contains(category)) {
                int px = (int) (16L * (gameTime % 16L));
                double pX1 = renderStartX - 27 + s2 + s0;
                double pY1 = renderStartY - 4 + count * 24;
                drawRectTextured(poseStack, PARTICLE_TEXTURE, pX1, pX1 + 16, pY1, pY1 + 16, px, px + 16, 80, 96, BACKGROUND_ZLEVEL);
            }

            // 未选择时候的阴影
            if (!isSelected) {
                double shadowU = mirror ? 224.0 : 200.0;
                double sU1 = mirror ? (shadowU + 24.0) : shadowU;
                double sU2 = mirror ? shadowU : (shadowU + 24.0);
                drawRectTextured(poseStack, GUI_TEXTURE, x1, x2, y1, y2, sU1, sU2, baseV, baseV + 24.0, BACKGROUND_ZLEVEL);
            }
            ++count;
        }
    }

    private void drawBorders(GuiGraphics guiGraphics, int x, int y, int width, int height) {
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
    }

    private void drawBackground(GuiGraphics guiGraphics, ResourceLocation texture, double viewOffsetX, double viewOffsetY) {

        double startX = (this.width - BORDER_TEXTURE_WIDTH) / 2.0;
        double startY = (this.height - BORDER_TEXTURE_HEIGHT) / 2.0;

        double canvasX1 = startX + BORDER_WIDTH;
        double canvasY1 = startY + BORDER_HEIGHT;
        double canvasWidth = BORDER_TEXTURE_WIDTH - 2 * BORDER_WIDTH;
        double canvasHeight = BORDER_TEXTURE_HEIGHT - 2 * BORDER_HEIGHT;


        double scrollRangeX = guiMapBottom - guiMapTop;
        double scrollRangeY = guiMapRight - guiMapLeft;

        double uOffset = scrollRangeX > 0 ? (viewOffsetX - guiMapTop) / scrollRangeX * 288.0 : 0;
        double vOffset = scrollRangeY > 0 ? (viewOffsetY - guiMapLeft) / scrollRangeY * 316.0 : 0;


        guiGraphics.flush();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 1.0F);
        double halfCanvasX1 = canvasX1 / 2.0;
        double halfCanvasY1 = canvasY1 / 2.0;
        double halfCanvasX2 = halfCanvasX1 + canvasWidth / 2.0;
        double halfCanvasY2 = halfCanvasY1 + canvasHeight / 2.0;

        double halfUOffset = uOffset / 2.0;
        double halfVOffset = vOffset / 2.0;
        double halfCanvasW = canvasWidth / 2.0;
        double halfCanvasH = canvasHeight / 2.0;

        drawRectTextured(poseStack, texture, halfCanvasX1, halfCanvasX2, halfCanvasY1, halfCanvasY2, halfUOffset, halfUOffset + halfCanvasW, halfVOffset,
                halfVOffset + halfCanvasH, BACKGROUND_ZLEVEL);
        guiGraphics.flush();
        poseStack.popPose();
    }

    private void drawRectTextured(PoseStack poseStack, ResourceLocation texture, double x1, double x2, double y1, double y2, double u1, double u2, double v1,
                                  double v2, float zLevel) {
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

    private void enableContentScissor(int x, int y, int width, int height) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        int scissorX = (int) Math.round(x * scale);
        int scissorY = (int) Math.round((this.height - y - height) * scale);
        int scissorWidth = (int) Math.round(width * scale);
        int scissorHeight = (int) Math.round(height * scale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }
}
