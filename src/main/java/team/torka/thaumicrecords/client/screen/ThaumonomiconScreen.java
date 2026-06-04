package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.helper.ResearchHelper;
import team.torka.thaumicrecords.api.item.ScribingTool;
import team.torka.thaumicrecords.api.research.Research;
import team.torka.thaumicrecords.api.research.ResearchCategory;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.attachment.ResearchUnlocked;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.network.payload.PlayerUnlockResearchPayload;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.ResearchCategoryRegistry;
import team.torka.thaumicrecords.registry.ResearchRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ThaumonomiconScreen extends Screen {
    private static final ResourceLocation GUI_TEXTURE = ThaumicRecords.createRl("textures/gui/thaumonomicon_gui.png");
    // 好tm震惊这玩意居然是和污染节点用的同一个材质
    private static final ResourceLocation FORBIDDEN_TEXTURE = ThaumicRecords.createRl("textures/misc/node/tainted.png");
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
    private static Set<ResourceLocation> lastCompletedResearches = null;

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

    private Set<ResourceLocation> highlightedResearches = Collections.emptySet();

    private long popupEndTime = 0;
    private String popupMessage = "";

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
        var player = Minecraft.getInstance().player;
        ResearchUnlocked researchUnlocked = player != null ? player.getData(AttachmentRegistry.RESEARCH_UNLOCKED) : ResearchUnlocked.EMPTY;
        if (lastCompletedResearches != null) {
            Set<ResourceLocation> current = researchUnlocked.completedResearches();
            Set<ResourceLocation> diff = new HashSet<>(current);
            diff.removeAll(lastCompletedResearches);
            this.highlightedResearches = diff;
        } else {
            this.highlightedResearches = Collections.emptySet();
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

    }

    private void centerViewport() {
        int contentWidth = BORDER_TEXTURE_WIDTH - 2 * BORDER_WIDTH;   // 224
        int contentHeight = BORDER_TEXTURE_HEIGHT - 2 * BORDER_HEIGHT; // 196
        this.guiMapX = this.targetMapX = this.prevMapX = -contentWidth / 2.0;
        this.guiMapY = this.targetMapY = this.prevMapY = -contentHeight / 2.0;
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
        int smoothMapX = Mth.floor(this.prevMapX + (this.guiMapX - this.prevMapX) * partialTick);
        int smoothMapY = Mth.floor(this.prevMapY + (this.guiMapY - this.prevMapY) * partialTick);

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
        drawResearchNodes(guiGraphics, smoothMapX, smoothMapY, contentX1, contentY1, partialTick);
        RenderSystem.disableScissor();
        drawCategoryTags(guiGraphics, renderStartX, renderStartY);

        drawBorders(guiGraphics, renderStartX, renderStartY, BORDER_TEXTURE_WIDTH, BORDER_TEXTURE_HEIGHT);

        drawCategoryTooltip(guiGraphics, mouseX, mouseY, renderStartX, renderStartY);
        drawResearchTooltip(guiGraphics, mouseX, mouseY, smoothMapX, smoothMapY, contentX1, contentY1);
        drawPopup(guiGraphics);
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
                int offsetX = Mth.floor(this.guiMapX);
                int offsetY = Mth.floor(this.guiMapY);
                Research clickedResearch = getResearchAtPosition(mouseX, mouseY, offsetX, offsetY, (int) contentX1, (int) contentY1);
                if (clickedResearch != null) {
                    ResearchUnlocked researchUnlocked = this.minecraft.player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
                    ResourceLocation researchKey = ResearchRegistry.RESEARCH_REGISTRY.getKey(clickedResearch);
                    boolean isCompleted = researchUnlocked.isResearchCompleted(researchKey);
                    boolean parentsCompleted = ResearchUnlocked.areParentsCompleted(clickedResearch, researchUnlocked.completedResearches());
                    if (isCompleted) {
                        this.minecraft.pushGuiLayer(new ResearchDetailScreen(researchKey));
                        return true;
                    }
                    boolean hasNote = false;
                    for (ItemStack stack : this.minecraft.player.getInventory().items) {
                        if (stack.is(ItemRegistry.RESEARCH_NOTES.get())) {
                            ResearchNoteComponent noteData = stack.get(DataComponentRegistry.RESEARCH_NOTE);
                            if (noteData != null && noteData.research().equals(researchKey)) {
                                hasNote = true;
                                break;
                            }
                        }
                    }
                    if (parentsCompleted && !hasNote) {
                        this.popupEndTime = System.currentTimeMillis() + 3000L;
                        this.popupMessage = Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.get_not_popup"),
                                Component.translatable(clickedResearch.nameTranslationKey)).getString();
                        this.minecraft.player.playSound(SoundRegistry.LEARN.get(), 0.75F, 1.0F);
                        PacketDistributor.sendToServer(new PlayerUnlockResearchPayload(researchKey));
                    }
                }
                this.isDragging = true;
                this.targetMapX = this.guiMapX;
                this.targetMapY = this.guiMapY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Nullable
    private Research getResearchAtPosition(double mouseX, double mouseY, int offsetX, int offsetY, int contentX1, int contentY1) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return null;
        }
        ResearchUnlocked researchUnlocked = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);
        List<Research> researchList = ResearchHelper.getResearchesByCategory(selectedCategory);
        for (Research research : researchList) {
            ResourceLocation researchKey = ResearchRegistry.RESEARCH_REGISTRY.getKey(research);
            if (researchKey == null || !researchUnlocked.isResearchDiscovered(researchKey)) {
                continue;
            }
            int researchX = research.col * 24 - offsetX + contentX1;
            int researchY = research.row * 24 - offsetY + contentY1;
            if (mouseX >= researchX && mouseX <= researchX + 22 && mouseY >= researchY && mouseY <= researchY + 22) {
                return research;
            }
        }
        return null;
    }

    @Nullable
    private ResearchCategory getCategoryAtPosition(double mouseX, double mouseY, int renderStartX, int renderStartY) {
        var player = Minecraft.getInstance().player;
        ResearchUnlocked researchUnlocked = player != null ? player.getData(AttachmentRegistry.RESEARCH_UNLOCKED) : ResearchUnlocked.EMPTY;

        List<ResearchCategory> categories = ResearchCategoryRegistry.RESEARCH_REGISTRY.stream().filter(cat -> {
            ResourceLocation key = ResearchCategoryRegistry.RESEARCH_REGISTRY.getKey(cat);
            return key != null && researchUnlocked.isCategoryDiscovered(key);
        }).toList();

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
        lastX = (int) ((this.guiMapX + 112.0) / 24.0);
        lastY = (int) ((this.guiMapY + 98.0) / 24.0);
        lastCategory = this.selectedCategory;
        var player = Minecraft.getInstance().player;
        if (player != null) {
            lastCompletedResearches = new HashSet<>(player.getData(AttachmentRegistry.RESEARCH_UNLOCKED).completedResearches());
        }
        super.onClose();
    }

    private void drawResearchNodes(GuiGraphics guiGraphics, double viewOffsetX, double viewOffsetY, int contentX1, int contentY1, float partialTick) {
        List<Research> researchList = ResearchHelper.getResearchesByCategory(selectedCategory);
        if (researchList.isEmpty()) {
            return;
        }

        var player = Minecraft.getInstance().player;
        ResearchUnlocked researchUnlocked = player != null ? player.getData(AttachmentRegistry.RESEARCH_UNLOCKED) : ResearchUnlocked.EMPTY;

        PoseStack poseStack = guiGraphics.pose();
        int contentWidth = BORDER_TEXTURE_WIDTH - 2 * BORDER_WIDTH;   // 224
        int contentHeight = BORDER_TEXTURE_HEIGHT - 2 * BORDER_HEIGHT; // 196
        int offsetX = (int) viewOffsetX;
        int offsetY = (int) viewOffsetY;

        guiGraphics.flush();
        drawResearchConnections(poseStack, researchList, researchUnlocked, offsetX, offsetY, contentX1, contentY1, partialTick);

        for (Research research : researchList) {
            ResourceLocation researchKey = ResearchRegistry.RESEARCH_REGISTRY.getKey(research);
            if (researchKey == null || !researchUnlocked.isResearchDiscovered(researchKey)) {
                continue;
            }

            int researchX = research.col * 24 - offsetX + contentX1;
            int researchY = research.row * 24 - offsetY + contentY1;

            if (researchX + 24 < contentX1 || researchY + 24 < contentY1 || researchX - 2 > contentX1 + contentWidth || researchY - 2 > contentY1 + contentHeight) {
                continue;
            }

            boolean isCompleted = researchUnlocked.isResearchCompleted(researchKey);
            boolean parentsCompleted = ResearchUnlocked.areParentsCompleted(research, researchUnlocked.completedResearches());

            // 三种渲染状态：0=已完成 1=parents已完成但当前未完成 2=parents未完成
            int renderState = isCompleted ? 0 : (parentsCompleted ? 1 : 2);

            // 禁忌研究光晕（在形状下面渲染）
            if (research.warp > 0) {
                drawForbiddenGlow(poseStack, researchX + 11, researchY + 11);
            }

            drawResearchShape(poseStack, researchX, researchY, research.renderStrategy, renderState);

            // 图标渲染
            if (research.iconItem != null) {
                guiGraphics.flush();
                if (renderState == 1) {
                    float brightness = (float) (Math.sin((double) (Util.getMillis() % 600L) / 600.0 * Math.PI * 2.0) * 0.25 + 0.75);
                    RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);
                } else if (renderState == 2) {
                    RenderSystem.setShaderColor(0.2F, 0.2F, 0.2F, 1.0F);
                }
                guiGraphics.renderFakeItem(research.iconItem, researchX + 3, researchY + 3);
                if (renderState != 0) {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
                guiGraphics.flush();
            } else if (research.icon != null) {
                if (renderState == 1) {
                    float brightness = (float) (Math.sin((double) (Util.getMillis() % 600L) / 600.0 * Math.PI * 2.0) * 0.25 + 0.75);
                    RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);
                } else if (renderState == 2) {
                    RenderSystem.setShaderColor(0.2F, 0.2F, 0.2F, 1.0F);
                }
                drawRectTextured(poseStack, research.icon, researchX + 3, researchX + 19, researchY + 3, researchY + 19, 0, 256, 0, 256, 0);
                if (renderState != 0) {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }

    private void drawResearchConnections(PoseStack poseStack, List<Research> researchList, ResearchUnlocked researchUnlocked, int offsetX, int offsetY,
                                         int contentX1, int contentY1, float partialTick) {
        for (Research research : researchList) {
            ResourceLocation researchKey = ResearchRegistry.RESEARCH_REGISTRY.getKey(research);
            if (researchKey == null || !researchUnlocked.isResearchDiscovered(researchKey)) {
                continue;
            }
            if (research.parents == null || research.parents.length == 0) {
                continue;
            }

            boolean childCompleted = researchUnlocked.isResearchCompleted(researchKey);
            int childCenterX = research.col * 24 - offsetX + contentX1 + 11;
            int childCenterY = research.row * 24 - offsetY + contentY1 + 11;

            for (ResourceLocation parentKey : research.parents) {
                if (parentKey == null) {
                    continue;
                }
                Research parent = ResearchRegistry.RESEARCH_REGISTRY.get(parentKey);
                if (parent == null || !parent.category.equals(ResearchCategoryRegistry.RESEARCH_REGISTRY.getKey(selectedCategory))) {
                    continue;
                }
                if (!researchUnlocked.isResearchDiscovered(parentKey)) {
                    continue;
                }

                int parentCenterX = parent.col * 24 - offsetX + contentX1 + 11;
                int parentCenterY = parent.row * 24 - offsetY + contentY1 + 11;

                boolean parentCompleted = researchUnlocked.isResearchCompleted(parentKey);

                if (childCompleted) {
                    drawConnectionLine(poseStack, childCenterX, childCenterY, parentCenterX, parentCenterY, 0.1F, 0.1F, 0.1F, partialTick, false);
                } else {
                    if (parentCompleted) {
                        drawConnectionLine(poseStack, childCenterX, childCenterY, parentCenterX, parentCenterY, 0.0F, 1.0F, 0.0F, partialTick, true);
                    } else {
                        drawConnectionLine(poseStack, childCenterX, childCenterY, parentCenterX, parentCenterY, 0.0F, 0.0F, 1.0F, partialTick, true);
                    }
                }
            }
        }
    }

    private void drawConnectionLine(PoseStack poseStack, int x1, int y1, int x2, int y2, float r, float g, float b, float partialTick, boolean wiggle) {
        float count = (float) (Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount : 0) + partialTick;

        double dx = x1 - x2;
        double dy = y1 - y2;
        float dist = Mth.sqrt((float) (dx * dx + dy * dy));
        int inc = Math.max(1, (int) (dist / 2.0F));
        float stepX = (float) (dx / inc);
        float stepY = (float) (dy / inc);
        if (Math.abs(dx) > Math.abs(dy)) {
            stepX *= 2.0F;
        } else {
            stepY *= 2.0F;
        }

        float[] positionsX = new float[inc + 1];
        float[] positionsY = new float[inc + 1];
        float[] colorsR = new float[inc + 1];
        float[] colorsG = new float[inc + 1];
        float[] colorsB = new float[inc + 1];
        float[] colorsA = new float[inc + 1];

        float curStepX = stepX;
        float curStepY = stepY;

        for (int a = 0; a <= inc; ++a) {
            float r2 = r;
            float g2 = g;
            float b2 = b;
            float mx = 0.0F;
            float my = 0.0F;
            float op = 0.6F;

            if (wiggle) {
                float phase = (float) a / (float) inc;
                mx = Mth.sin((count + a) / 7.0F) * 5.0F * (1.0F - phase);
                my = Mth.sin((count + a) / 5.0F) * 5.0F * (1.0F - phase);
                r2 = r * (1.0F - phase);
                g2 = g * (1.0F - phase);
                b2 = b * (1.0F - phase);
                op *= phase;
            }

            positionsX[a] = x1 - curStepX * a + mx;
            positionsY[a] = y1 - curStepY * a + my;
            colorsR[a] = r2;
            colorsG[a] = g2;
            colorsB[a] = b2;
            colorsA[a] = op;

            if (Math.abs(dx) > Math.abs(dy)) {
                curStepX *= 1.0F - 1.0F / (inc * 3.0F / 2.0F);
            } else {
                curStepY *= 1.0F - 1.0F / (inc * 3.0F / 2.0F);
            }
        }

        float halfWidth = 0.75F;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (int a = 0; a < inc; ++a) {
            float segDx = positionsX[a + 1] - positionsX[a];
            float segDy = positionsY[a + 1] - positionsY[a];
            float segLen = Mth.sqrt(segDx * segDx + segDy * segDy);
            float nx = segLen > 0.001F ? -segDy / segLen * halfWidth : 0.0F;
            float ny = segLen > 0.001F ? segDx / segLen * halfWidth : halfWidth;

            bufferBuilder.addVertex(matrix, positionsX[a] + nx, positionsY[a] + ny, 0.0F).setColor(colorsR[a], colorsG[a], colorsB[a], colorsA[a]);
            bufferBuilder.addVertex(matrix, positionsX[a] - nx, positionsY[a] - ny, 0.0F).setColor(colorsR[a], colorsG[a], colorsB[a], colorsA[a]);
            bufferBuilder.addVertex(matrix, positionsX[a + 1] - nx, positionsY[a + 1] - ny, 0.0F).setColor(colorsR[a + 1], colorsG[a + 1], colorsB[a + 1],
                    colorsA[a + 1]);
            bufferBuilder.addVertex(matrix, positionsX[a + 1] + nx, positionsY[a + 1] + ny, 0.0F).setColor(colorsR[a + 1], colorsG[a + 1], colorsB[a + 1],
                    colorsA[a + 1]);
        }

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void drawResearchShape(PoseStack poseStack, int x, int y, Research.RenderStrategy strategy, int renderState) {
        double u;
        double v = 230.0;

        switch (strategy) {
            case SPIKY -> {
                if (renderState == 1) {
                    float brightness = (float) (Math.sin((double) (Util.getMillis() % 600L) / 600.0 * Math.PI * 2.0) * 0.25 + 0.75);
                    RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);
                } else if (renderState == 2) {
                    RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1.0F);
                }
                drawRectTextured(poseStack, GUI_TEXTURE, x - 2, x + 24, y - 2, y + 24, 54.0, 80.0, v, v + 26, 0);
                drawRectTextured(poseStack, GUI_TEXTURE, x - 2, x + 24, y - 2, y + 24, 26.0, 52.0, v, v + 26, 0);
                if (renderState != 0) {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
                return;
            }
            case ROUND -> u = 54.0;
            case HEXAGON -> u = 110.0;
            default -> u = 0.0;
        }

        if (renderState == 1) {
            float brightness = (float) (Math.sin((double) (Util.getMillis() % 600L) / 600.0 * Math.PI * 2.0) * 0.25 + 0.75);
            RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);
        } else if (renderState == 2) {
            RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1.0F);
        }
        drawRectTextured(poseStack, GUI_TEXTURE, x - 2, x + 24, y - 2, y + 24, u, u + 26, v, v + 26, 0);
        if (renderState != 0) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void drawCategoryTags(GuiGraphics guiGraphics, int renderStartX, int renderStartY) {
        var player = Minecraft.getInstance().player;
        ResearchUnlocked researchUnlocked = player != null ? player.getData(AttachmentRegistry.RESEARCH_UNLOCKED) : ResearchUnlocked.EMPTY;

        // 只渲染已发现的类别
        List<ResearchCategory> categories = ResearchCategoryRegistry.RESEARCH_REGISTRY.stream().filter(cat -> {
            ResourceLocation key = ResearchCategoryRegistry.RESEARCH_REGISTRY.getKey(cat);
            return key != null && researchUnlocked.isCategoryDiscovered(key);
        }).toList();

        int count = 0;
        boolean mirror = false;
        int tabPerSide = 9;
        int tabDistance = 264;

        PoseStack poseStack = guiGraphics.pose();

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

    private void drawCategoryTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int renderStartX, int renderStartY) {
        var player = Minecraft.getInstance().player;
        ResearchUnlocked researchUnlocked = player != null ? player.getData(AttachmentRegistry.RESEARCH_UNLOCKED) : ResearchUnlocked.EMPTY;

        List<ResearchCategory> categories = ResearchCategoryRegistry.RESEARCH_REGISTRY.stream().filter(cat -> {
            ResourceLocation key = ResearchCategoryRegistry.RESEARCH_REGISTRY.getKey(cat);
            return key != null && researchUnlocked.isCategoryDiscovered(key);
        }).toList();

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
                guiGraphics.drawString(this.font, Component.translatable(category.nameTranslateKey), mouseX, mouseY - 8, 0xFFFFFF, true);
                return;
            }
            ++count;
        }
    }

    private void drawResearchTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int offsetX, int offsetY, int contentX1, int contentY1) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ResearchUnlocked researchUnlocked = player.getData(AttachmentRegistry.RESEARCH_UNLOCKED);

        List<Research> researchList = ResearchHelper.getResearchesByCategory(selectedCategory);
        if (researchList.isEmpty()) {
            return;
        }

        int contentWidth = BORDER_TEXTURE_WIDTH - 2 * BORDER_WIDTH;
        int contentHeight = BORDER_TEXTURE_HEIGHT - 2 * BORDER_HEIGHT;

        if (mouseX < contentX1 || mouseX >= contentX1 + contentWidth || mouseY < contentY1 || mouseY >= contentY1 + contentHeight) {
            return;
        }

        Research hoveredResearch = null;
        for (Research research : researchList) {
            ResourceLocation researchKey = ResearchRegistry.RESEARCH_REGISTRY.getKey(research);
            if (researchKey == null || !researchUnlocked.isResearchDiscovered(researchKey)) {
                continue;
            }

            int researchX = research.col * 24 - offsetX + contentX1;
            int researchY = research.row * 24 - offsetY + contentY1;

            if (mouseX >= researchX && mouseX <= researchX + 22 && mouseY >= researchY && mouseY <= researchY + 22) {
                hoveredResearch = research;
                break;
            }
        }

        if (hoveredResearch == null) {
            return;
        }

        ResourceLocation researchKey = ResearchRegistry.RESEARCH_REGISTRY.getKey(hoveredResearch);
        boolean isCompleted = researchUnlocked.isResearchCompleted(researchKey);
        boolean parentsCompleted = ResearchUnlocked.areParentsCompleted(hoveredResearch, researchUnlocked.completedResearches());
        boolean canUnlock = isCompleted || parentsCompleted;
        boolean isSpecial = hoveredResearch.renderStrategy == Research.RenderStrategy.SPIKY;

        int nameColor;
        if (canUnlock) {
            nameColor = isSpecial ? 0xFFFF80 : ChatFormatting.WHITE.getColor();
        } else {
            nameColor = isSpecial ? 0x808040 : ChatFormatting.GRAY.getColor();
        }
        int descColor = 0x9090FF;
        int missingParentColor = 0x705050;
        int warpColor = 0xAA00AA;
        int hasNoteColor = 0xFFA500;
        int insufficientRpColor = 0xDC143C;
        int unlockWithRpColor = 0x87CEEB;
        int getNoteColor = 0x87CEEB;
        int noScribingTool = 0xDC143C;

        Component name = Component.translatable(hoveredResearch.nameTranslationKey);

        if (!canUnlock) {
            // 不能获取笔记的
            List<Component> lines = new ArrayList<>();
            lines.add(name.copy().withColor(nameColor));
            lines.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.missing_parent")).withColor(missingParentColor));
            guiGraphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        } else if (!isCompleted) {
            // 可以获取笔记的
            List<Component> lines = new ArrayList<>();
            lines.add(name.copy().withColor(nameColor));
            lines.add(Component.translatable(hoveredResearch.descTranslationKey).withColor(descColor));
            if (hoveredResearch.warp > 0) {
                lines.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.forbidden"), getWarpLevel(hoveredResearch.warp))
                        .withColor(warpColor));
            }
            if (hoveredResearch.unlockStrategy == Research.UnlockStrategy.POINTS) {
                ResearchPoint rp = this.minecraft.player.getData(AttachmentRegistry.RESEARCH_POINT);
                boolean hasEnoughPoints = hoveredResearch.aspects.entrySet().stream().allMatch(
                        entry -> rp.points().getOrZero(entry.getKey()) >= entry.getValue());
                lines.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.complete_with_points"))
                        .withColor(hasEnoughPoints ? unlockWithRpColor : insufficientRpColor));
            } else if (hoveredResearch.unlockStrategy == Research.UnlockStrategy.RESEARCH) {
                boolean hasScribingTool = false;
                boolean hasPaper = false;
                for (ItemStack stack : this.minecraft.player.getInventory().items) {
                    if (!hasScribingTool && stack.getItem() instanceof ScribingTool scribingTool && scribingTool.canScribe(stack, this.minecraft.player)) {
                        hasScribingTool = true;
                    }
                    if (!hasPaper && stack.is(Items.PAPER)) {
                        hasPaper = true;
                    }
                }
                boolean hasNote = false;
                for (ItemStack stack : this.minecraft.player.getInventory().items) {
                    if (stack.is(ItemRegistry.RESEARCH_NOTES.get())) {
                        ResearchNoteComponent noteData = stack.get(DataComponentRegistry.RESEARCH_NOTE);
                        if (noteData != null && noteData.research().equals(researchKey)) {
                            hasNote = true;
                            break;
                        }
                    }
                }
                if (hasNote) {
                    lines.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.has_note")).withColor(hasNoteColor));
                } else if (hasScribingTool && hasPaper) {
                    lines.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.get_research_note")).withColor(getNoteColor));
                } else {
                    lines.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.cant_get_note")).withColor(noScribingTool));
                }
            }
            guiGraphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        } else {
            // 已完成的研究
            List<Component> lines = new ArrayList<>();
            lines.add(name.copy().withColor(nameColor));
            lines.add(Component.translatable(hoveredResearch.descTranslationKey).withColor(descColor));
            if (hoveredResearch.warp > 0) {
                lines.add(Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.forbidden"), getWarpLevel(hoveredResearch.warp))
                        .withColor(warpColor));
            }
            guiGraphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        }
    }

    private static Component getWarpLevel(int warp) {
        int level = Math.min(warp, 5);
        return Component.translatable(ThaumicRecords.createTranslationKey("tooltip", "research.forbidden.level." + level));
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

    private void drawForbiddenGlow(PoseStack poseStack, double x, double y) {
        int ticks = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount : 0;
        int frames = 32;
        int part = ticks % frames;
        int currentFrame = frames - 1 - part;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, FORBIDDEN_TEXTURE);
        int color = 0x200080; // TODO i cannot find the exact color to mix
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float alpha = 0.8F;
        RenderSystem.setShaderColor(r, g, b, alpha);

        float scale = 40.0F;

        float minU = (float) currentFrame / frames;
        float maxU = (float) (currentFrame + 1) / frames;
        float minV = 0.0F;
        float maxV = 1.0F;

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        bufferBuilder.addVertex(matrix, (float) (x - scale), (float) (y + scale), 0.0F).setUv(minU, maxV);
        bufferBuilder.addVertex(matrix, (float) (x + scale), (float) (y + scale), 0.0F).setUv(maxU, maxV);
        bufferBuilder.addVertex(matrix, (float) (x + scale), (float) (y - scale), 0.0F).setUv(maxU, minV);
        bufferBuilder.addVertex(matrix, (float) (x - scale), (float) (y - scale), 0.0F).setUv(minU, minV);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void enableContentScissor(int x, int y, int width, int height) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        int scissorX = (int) Math.round(x * scale);
        int scissorY = (int) Math.round((this.height - y - height) * scale);
        int scissorWidth = (int) Math.round(width * scale);
        int scissorHeight = (int) Math.round(height * scale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    private void drawPopup(GuiGraphics guiGraphics) {
        if (this.popupEndTime <= System.currentTimeMillis()) {
            return;
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int halfHeight = this.font.getSplitter().splitLines(this.popupMessage, 150, Style.EMPTY).size() * 9 / 2;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 400);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        guiGraphics.fill(centerX - 78, centerY - halfHeight - 3, centerX + 78, centerY + halfHeight + 3, 0xC0000000);
        guiGraphics.drawWordWrap(this.font, Component.literal(this.popupMessage), centerX - 75, centerY - halfHeight, 150, 0x9090FF);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }
}
