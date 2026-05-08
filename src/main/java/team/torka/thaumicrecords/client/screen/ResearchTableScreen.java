package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.CubeCoordinateHelper;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.menu.ResearchTableMenu;
import team.torka.thaumicrecords.network.payload.PlayerCombineAspectPayload;
import team.torka.thaumicrecords.network.payload.PlayerEraseNotePayload;
import team.torka.thaumicrecords.network.payload.PlayerWriteNotePayload;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ResearchTableScreen extends AbstractContainerScreen<ResearchTableMenu> {
    private static final ResourceLocation GUI_TEX = ThaumicRecords.createRl("textures/gui/research_table.png");
    private static final ResourceLocation PAPER_TEX = ThaumicRecords.createRl("textures/gui/research_table_parchment.png");
    private static final ResourceLocation HEX_TEX = ThaumicRecords.createRl("textures/gui/research_table_hex_dark.png");
    private static final ResourceLocation HEX_LIGHT_TEX = ThaumicRecords.createRl("textures/gui/research_table_hex_light.png");
    private static final ResourceLocation RUNE_TEX = ThaumicRecords.createRl("textures/misc/runes.png");
    private static final ResourceLocation PARTICLES_TEX = ThaumicRecords.createRl("textures/misc/particles.png");
    private final Map<String, Rune> runes = new ConcurrentHashMap<>();
    private long lastRuneCheck = 0L;
    private int page = 0;
    private int lastPage = 0;
    private Aspect left;
    private Aspect right;
    private Aspect draggingAspect;
    private long buttonCombineTime;
    private boolean dragging;

    public ResearchTableScreen(ResearchTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 255;
        this.imageHeight = 255;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        RenderSystem.enableBlend();
        graphics.blit(GUI_TEX, x, y, 0, 0, 255, 167);
        graphics.blit(GUI_TEX, x + 40, y + 167, 0, 166, 184, 88);
        if (!this.menu.slots.get(ResearchTableMenu.SLOT_RESEARCH_NOTE).getItem().isEmpty()) {
            this.drawSheet(graphics, x, y, mouseX, mouseY);
        }

        this.drawPlayerAspects(graphics, x + 10, y + 40, mouseX, mouseY);
        this.drawCombinationArea(graphics, x, y, mouseX, mouseY);
        this.drawDraggingOrb(graphics, mouseX, mouseY);
        if (this.page < this.lastPage) {
            graphics.blit(GUI_TEX, x + 51, y + 121, 208, 208, 24, 8);
        }
        if (this.page > 0) {
            graphics.blit(GUI_TEX, x + 27, y + 121, 184, 208, 24, 8);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    private void drawSheet(GuiGraphics graphics, int x, int y, int mx, int my) {
        graphics.blit(PAPER_TEX, x + 94, y + 8, 0, 0, 150, 150, 256, 256);
        long time = System.currentTimeMillis();
        updateRuneGenerator(time);
        for (Rune rune : runes.values()) {
            CubeCoordinateHelper.ScreenPos pix = rune.hex.toPixel(9.0f);
            float progress = (float) (time - rune.start) / (float) (rune.decay - rune.start);
            float alpha = 0.5F;
            if (progress < 0.25F) {
                alpha = progress * 2.0F;
            } else if (progress > 0.5F) {
                alpha = 1.0F - progress;
            }
            double renderX = (double) (x + 169) + pix.x();
            double renderY = (double) (y + 83) - pix.y();
            this.drawRune(graphics, renderX, renderY, rune.rune, alpha * 0.66F);
        }

        drawHexGrid(graphics, x + 169, y + 83, mx, my);
    }

    private void drawHexGrid(GuiGraphics graphics, int centerX, int centerY, int mx, int my) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0);
        ItemStack researchNote = menu.getResearchNotes();
        ResearchNoteComponent researchNoteComponent = researchNote.get(DataComponentRegistry.RESEARCH_NOTE);
        if (Objects.isNull(researchNoteComponent)) {
            graphics.pose().popPose();
            return;
        }
        Map<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> decodedHexes = researchNoteComponent.getDecodedHexes();
        List<CubeCoordinateHelper.CubeHex> disconnectedHexes = getDisconnectedFullHexes(decodedHexes);
        CubeCoordinateHelper.CubeHex hoveredHex = CubeCoordinateHelper.pixelToCube(mx - centerX, my - centerY, 9.0f);
        if (decodedHexes.containsKey(hoveredHex)) {
            if (decodedHexes.get(hoveredHex).type() != ResearchNoteComponent.HexEntry.ROOT) {
                this.drawHexHighlight(graphics, hoveredHex);
            }
        }
        for (Map.Entry<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> entry : decodedHexes.entrySet()) {
            CubeCoordinateHelper.CubeHex hex = entry.getKey();
            ResearchNoteComponent.HexEntry hexEntry = entry.getValue();
            if (hexEntry.type() != ResearchNoteComponent.HexEntry.ROOT) {
                if (!researchNoteComponent.complete()) {
                    this.drawHex(graphics, hex);
                }
            }
        }
        Set<HexLink> allLinks = getAllLinks(decodedHexes);
        for (HexLink link : allLinks) {
            CubeCoordinateHelper.ScreenPos p1 = link.a().toPixel(9.0F);
            CubeCoordinateHelper.ScreenPos p2 = link.b().toPixel(9.0F);
            this.drawConnectionLine(graphics, (float) (p1.x()), (float) (p1.y()), (float) (p2.x()), (float) (p2.y()));
        }

        for (Map.Entry<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> entry : decodedHexes.entrySet()) {
            CubeCoordinateHelper.CubeHex hex = entry.getKey();
            ResearchNoteComponent.HexEntry hexEntry = entry.getValue();
            if (hexEntry.type() == ResearchNoteComponent.HexEntry.ROOT) {
                this.drawOrb(graphics, hex);
            }
            if (Arrays.asList(ResearchNoteComponent.HexEntry.FULL, ResearchNoteComponent.HexEntry.ROOT).contains(hexEntry.type())) {
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(hexEntry.aspect());
                if (Objects.isNull(aspect)) {
                    continue;
                }
                this.drawAspectIcon(graphics, hex, aspect, disconnectedHexes.contains(hex));
            }
        }
        graphics.pose().popPose();
    }

    private void drawConnectionLine(GuiGraphics graphics, double x, double y, double x2, double y2) {
        float thickness = 1F;
        float ticks = (float) (System.currentTimeMillis() / 50.0);
        float alpha = 0.3F + Mth.sin(ticks * 0.2F + (float) x) * 0.3F + 0.3F;
        float dx = (float) (x2 - x);
        float dy = (float) (y2 - y);
        float len = Mth.sqrt(dx * dx + dy * dy);
        if (len <= 0) {
            return;
        }
        float nx = -dy / len * (thickness / 2.0F);
        float ny = dx / len * (thickness / 2.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = graphics.pose().last().pose();
        buffer.addVertex(matrix, (float) (x + nx), (float) (y + ny), 0.0F).setColor(0.0F, 0.6F, 0.8F, alpha);
        buffer.addVertex(matrix, (float) (x2 + nx), (float) (y2 + ny), 0.0F).setColor(0.0F, 0.6F, 0.8F, alpha);
        buffer.addVertex(matrix, (float) (x2 - nx), (float) (y2 - ny), 0.0F).setColor(0.0F, 0.6F, 0.8F, alpha);
        buffer.addVertex(matrix, (float) (x - nx), (float) (y - ny), 0.0F).setColor(0.0F, 0.6F, 0.8F, alpha);
        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void drawCombinationArea(GuiGraphics graphics, int x, int y, int mx, int my) {
        if (Objects.nonNull(this.left) && Objects.nonNull(this.right)) {
            if (this.buttonCombineTime < System.nanoTime()) {
                graphics.blit(GUI_TEX, x + 35, y + 139, 184, 184, 32, 16);
                this.drawOrb(graphics, x + 43 + 8, y + 139 + 8);
            } else {
                graphics.blit(GUI_TEX, x + 35, y + 139, 184, 184, 32, 16);
                RenderSystem.enableBlend();
                graphics.blit(GUI_TEX, x + 35, y + 139, 184, 168, 32, 16);
                RenderSystem.disableBlend();
            }
        }
        if (Objects.nonNull(this.left)) {
            this.drawAspectTag(graphics, x + 13, y + 139, this.left, 1, mx, my, false);
        }
        if (Objects.nonNull(this.right)) {
            this.drawAspectTag(graphics, x + 71, y + 139, this.right, 1, mx, my, false);
        }
    }

    @Nullable
    private ResourceLocation getClickedAspect(double mouseX, double mouseY) {
        if (Objects.isNull(this.minecraft) || Objects.isNull(this.minecraft.player)) {
            return null;
        }
        ResearchPoint researchPoint = this.minecraft.player.getData(AttachmentRegistry.RESEARCH_POINT);
        AspectList points = researchPoint.points().copy();
        List<ResourceLocation> sortedKeys = points.keySet().stream().sorted().toList();
        int count = 0;
        int drawn = 0;
        for (ResourceLocation rl : sortedKeys) {
            count++;
            if (count - 1 >= this.page * 5 && drawn < 25) {
                int offsetX = (drawn / 5) * 16;
                int offsetY = (drawn % 5) * 16;
                int slotX = 10 + offsetX;
                int slotY = 40 + offsetY;
                if (isHovering(slotX, slotY, 16, 16, mouseX, mouseY)) {
                    int amount = points.get(rl);
                    if (amount > 0) {
                        return rl;
                    }
                    return null;
                }
                drawn++;
            }
        }

        return null;
    }

    private boolean isCoordinateInNote(CubeCoordinateHelper.CubeHex coordinate) {
        ItemStack researchNote = menu.getResearchNotes();
        ResearchNoteComponent researchNoteComponent = researchNote.get(DataComponentRegistry.RESEARCH_NOTE);
        if (Objects.isNull(researchNoteComponent)) {
            return false;
        }
        Map<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> decodedHexes = researchNoteComponent.getDecodedHexes();
        return decodedHexes.containsKey(coordinate);
    }

    private void drawPlayerAspects(GuiGraphics graphics, int x, int y, int mx, int my) {
        if (Objects.isNull(this.minecraft) || Objects.isNull(this.minecraft.player)) {
            return;
        }
        ResearchPoint researchPoint = this.minecraft.player.getData(AttachmentRegistry.RESEARCH_POINT);
        AspectList points = researchPoint.points().copy();
        List<ResourceLocation> sortedKeys = points.keySet().stream().sorted().toList();
        int count = sortedKeys.size();
        this.lastPage = (count - 20) / 5;
        count = 0;
        int drawn = 0;
        for (ResourceLocation rl : sortedKeys) {
            ++count;
            if (count - 1 >= this.page * 5 && drawn < 25) {
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
                if (Objects.isNull(aspect)) {
                    continue;
                }
                int amount = points.get(rl);
                int xx = x + (drawn / 5) * 16;
                int yy = y + (drawn % 5) * 16;
                this.drawAspectTag(graphics, xx, yy, aspect, amount, mx, my, true);
                ++drawn;
            }
        }
    }

    private void drawAspectTag(GuiGraphics graphics, int x, int y, Aspect aspect, int amount, int mx, int my, boolean renderAmount) {
        boolean faded = amount <= 0;
        float alpha = faded ? 0.33F : 1.0F;
        graphics.pose().pushPose();
        graphics.pose().translate(x + 8, y + 8, 0.1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int color = aspect.getARGBColor();
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        RenderSystem.setShaderColor(r, g, b, alpha);
        graphics.blit(aspect.getImage(), -8, -8, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();
        if (amount > 0 && renderAmount) {
            graphics.pose().pushPose();
            graphics.pose().translate(x + 16, y + 12, 0.5f);
            String s = String.valueOf(amount);
            graphics.drawString(this.font, s, -this.font.width(s), -3, 0xFFFFFF, true);
            graphics.pose().popPose();
        }
        if (mx >= x && mx < x + 16 && my >= y && my < y + 16) {
            MutableComponent title = Component.translatable(aspect.getNameTranslationKey()).withStyle(ChatFormatting.AQUA);
            MutableComponent lore = Component.translatable(aspect.getLoreTranslationKey()).withStyle(ChatFormatting.GRAY);
            graphics.renderComponentTooltip(this.font, Arrays.asList(title, lore), mx, my - 8);
        }
    }

    private void drawHex(GuiGraphics graphics, CubeCoordinateHelper.CubeHex hex) {
        CubeCoordinateHelper.ScreenPos pix = hex.toPixel(9.0f);
        graphics.pose().pushPose();
        graphics.pose().translate(0 + pix.x(), 0 + pix.y(), 0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.25F);
        graphics.blit(HEX_TEX, -8, -8, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();
    }

    private void drawHexHighlight(GuiGraphics graphics, CubeCoordinateHelper.CubeHex hex) {
        CubeCoordinateHelper.ScreenPos pix = hex.toPixel(9.0f);
        graphics.pose().pushPose();
        graphics.pose().translate(0 + pix.x(), 0 + pix.y(), 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(HEX_LIGHT_TEX, -8, -8, 0, 0, 16, 16, 16, 16);
        RenderSystem.defaultBlendFunc();
        graphics.pose().popPose();
    }

    private void drawAspectIcon(GuiGraphics graphics, CubeCoordinateHelper.CubeHex hex, Aspect aspect, boolean disconnect) {
        CubeCoordinateHelper.ScreenPos pix = hex.toPixel(9.0f);
        graphics.pose().pushPose();
        graphics.pose().translate(0 + pix.x(), 0 + pix.y(), 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (disconnect) {
            RenderSystem.setShaderColor(0.1F, 0.1F, 0.1F, 0.5F);
        } else {
            int argb = aspect.getARGBColor();
            float r = (float) (argb >> 16 & 255) / 255.0F;
            float g = (float) (argb >> 8 & 255) / 255.0F;
            float b = (float) (argb & 255) / 255.0F;
            RenderSystem.setShaderColor(r, g, b, 1F);
        }
        graphics.blit(aspect.getImage(), -8, -8, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        graphics.pose().popPose();
    }

    private void drawRune(GuiGraphics graphics, double x, double y, int rune, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.pose().pushPose();
        graphics.pose().translate((float) x, (float) y, 0.0F);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, alpha);
        int textureWidth = 256;
        int textureHeight = 16;
        int runeSize = 10;
        float uStart = (float) rune * 0.0625F * textureWidth;
        graphics.blit(RUNE_TEX, -5, -5, runeSize, runeSize, uStart, 0.0F, 16, 16, textureWidth, textureHeight);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();
        RenderSystem.disableBlend();
    }

    private void drawDraggingOrb(GuiGraphics graphics, int mx, int my) {
        if (this.dragging && Objects.nonNull(this.draggingAspect)) {
            this.drawOrbXY(graphics, mx, my, this.draggingAspect.getARGBColor());
        }
    }

    private void drawOrb(GuiGraphics graphics, double x, double y) {
        float ticks = (float) (System.currentTimeMillis() / 50.0);
        float red = 0.7F + Mth.sin((ticks + (float) x) / 10.0F) * 0.15F + 0.15F;
        float green = 0.7F + Mth.sin((ticks + (float) x + (float) y) / 11.0F) * 0.15F + 0.15F;
        float blue = 0.7F + Mth.sin((ticks + (float) y) / 12.0F) * 0.15F + 0.15F;
        int dynamicColor = ((int) (red * 255) << 16) | ((int) (green * 255) << 8) | (int) (blue * 255);
        drawOrbXY(graphics, x, y, dynamicColor);
    }

    private void drawOrb(GuiGraphics graphics, CubeCoordinateHelper.CubeHex hex) {
        CubeCoordinateHelper.ScreenPos pix = hex.toPixel(9.0f);
        float ticks = (float) (System.currentTimeMillis() / 50.0);
        float red = 0.7F + Mth.sin((ticks + (float) pix.x()) / 10.0F) * 0.15F + 0.15F;
        float green = 0.7F + Mth.sin((ticks + (float) pix.x() + (float) pix.y()) / 11.0F) * 0.15F + 0.15F;
        float blue = 0.7F + Mth.sin((ticks + (float) pix.y()) / 12.0F) * 0.15F + 0.15F;
        int dynamicColor = ((int) (red * 255) << 16) | ((int) (green * 255) << 8) | (int) (blue * 255);
        drawOrb(graphics, hex, dynamicColor);
    }

    private void drawOrb(GuiGraphics graphics, CubeCoordinateHelper.CubeHex hex, int color) {
        CubeCoordinateHelper.ScreenPos pix = hex.toPixel(9.0f);
        this.drawOrbXY(graphics, pix.x(), pix.y(), color);
    }

    private void drawOrbXY(GuiGraphics graphics, double x, double y, int color) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        long ticks = System.currentTimeMillis() / 50;
        int part = (int) (ticks % 8);
        float u = (0.5F + (float) part / 8.0F) * 256.0F;
        float v = 0.5F * 256.0F;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderColor(r, g, b, 1.0F);
        graphics.blit(PARTICLES_TEX, -8, -8, u, v, 16, 16, 256, 256);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        graphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            ResourceLocation rl = this.getClickedAspect(mx, my);
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
            if (Objects.nonNull(aspect)) {
                this.draggingAspect = aspect;
                this.dragging = true;
                this.playButtonAspect();
                return true;
            }
        }

        if (Objects.nonNull(this.left) && isHovering(11, 137, 16, 16, mx, my)) {
            this.left = null;
            this.playButtonAspect();
            return true;
        }
        if (Objects.nonNull(this.right) && isHovering(71, 137, 16, 16, mx, my)) {
            this.right = null;
            this.playButtonAspect();
            return true;
        }
        if (isHovering(35, 139, 32, 16, mx, my)) {
            if (Objects.nonNull(this.left) && Objects.nonNull(this.right) && this.buttonCombineTime < System.nanoTime()) {
                this.buttonCombineTime = System.nanoTime() + 200000000L;
                this.playButtonClick();
                this.playButtonCombine();
                ResourceLocation leftAspect = AspectRegistry.ASPECT_REGISTRY.getKey(left);
                ResourceLocation rightAspect = AspectRegistry.ASPECT_REGISTRY.getKey(right);
                PacketDistributor.sendToServer(new PlayerCombineAspectPayload(this.menu.getBlockEntityPos(), leftAspect, rightAspect));
                return true;
            }
        }
        if (this.page > 0 && isHovering(27, 121, 24, 8, mx, my)) {
            --this.page;
        }
        if (this.page < this.lastPage && isHovering(51, 121, 24, 8, mx, my)) {
            ++this.page;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == 0 && this.dragging) {
            this.handleMouseDraggingEnd(mx, my);
            this.dragging = false;
            this.draggingAspect = null;
            return true;
        }
        if (button == 0) {
            this.handleEraseNote(mx, my);
        }
        return super.mouseReleased(mx, my, button);
    }

    private void handleEraseNote(double mx, double my) {
        if (!this.dragging && Objects.isNull(this.draggingAspect)) {
            ItemStack note = this.menu.getResearchNotes();
            ResearchNoteComponent researchNoteComponent = note.get(DataComponentRegistry.RESEARCH_NOTE);
            if (!note.isEmpty() && Objects.nonNull(researchNoteComponent)) {
                CubeCoordinateHelper.CubeHex hex = CubeCoordinateHelper.pixelToCube(mx - this.leftPos - 169, my - this.topPos - 83, 9.0F);
                if (researchNoteComponent.hexes().containsKey(hex.toKey()) && researchNoteComponent.hexes()
                        .get(hex.toKey())
                        .type() == ResearchNoteComponent.HexEntry.FULL) {
                    this.playButtonCombine();
                    this.playButtonErase();
                    PacketDistributor.sendToServer(new PlayerEraseNotePayload(menu.getBlockEntityPos(), hex.toKey()));
                }
            }
        }
    }

    private void handleMouseDraggingEnd(double mx, double my) {
        if (this.dragging && Objects.nonNull(this.draggingAspect)) {
            ItemStack note = this.menu.getResearchNotes();
            ResearchNoteComponent researchNoteComponent = note.get(DataComponentRegistry.RESEARCH_NOTE);
            if (!note.isEmpty() && Objects.nonNull(researchNoteComponent)) {
                CubeCoordinateHelper.CubeHex hex = CubeCoordinateHelper.pixelToCube(mx - this.leftPos - 169, my - this.topPos - 83, 9.0F);
                if (researchNoteComponent.hexes().containsKey(hex.toKey()) && researchNoteComponent.hexes()
                        .get(hex.toKey())
                        .type() == ResearchNoteComponent.HexEntry.EMPTY) {
                    this.playButtonCombine();
                    this.playButtonWrite();
                    ResourceLocation rl = AspectRegistry.ASPECT_REGISTRY.getKey(this.draggingAspect);
                    if (Objects.nonNull(rl)) {
                        PacketDistributor.sendToServer(new PlayerWriteNotePayload(menu.getBlockEntityPos(), hex.toKey(), rl));
                    }
                    this.draggingAspect = null;
                }
            }
        }
        if (Objects.nonNull(this.draggingAspect)) {
            boolean skip = false;
            if (isHovering(11, 137, 16, 16, mx, my)) {
                this.playButtonAspect();
                this.left = this.draggingAspect;
                skip = true;
            }
            if (!skip && isHovering(71, 137, 16, 16, mx, my)) {
                this.playButtonAspect();
                this.right = this.draggingAspect;
                skip = true;
            }
            if (!skip) {
                ResourceLocation rl = this.getClickedAspect(mx, my);
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
                if (Objects.nonNull(aspect)) {
                    if (aspect == this.draggingAspect) {
                        if (Objects.isNull(this.left)) {
                            this.left = this.draggingAspect;
                        } else if (Objects.isNull(this.right)) {
                            this.right = this.draggingAspect;
                        }
                    }
                }
            }
        }
        this.dragging = false;
        this.draggingAspect = null;
    }

    private void playButtonClick() {
        if (Objects.nonNull(this.minecraft) && Objects.nonNull(this.minecraft.player)) {
            this.minecraft.player.playSound(SoundRegistry.BUTTON_CLICK.get(), 0.4F, 1.0F);
        }
    }

    private void playButtonCombine() {
        if (Objects.nonNull(this.minecraft) && Objects.nonNull(this.minecraft.player)) {
            this.minecraft.player.playSound(SoundRegistry.HHON.get(), 0.3F, 1.0F);
        }
    }

    private void playButtonAspect() {
        if (Objects.nonNull(this.minecraft) && Objects.nonNull(this.minecraft.player)) {
            this.minecraft.player.playSound(SoundRegistry.HHOFF.get(), 0.4F, 1.0F);
        }
    }

    private void playButtonWrite() {
        if (Objects.nonNull(this.minecraft) && Objects.nonNull(this.minecraft.player)) {
            this.minecraft.player.playSound(SoundRegistry.WRITE.get(), 0.2F, 1.0F);
        }
    }

    private void playButtonErase() {
        if (Objects.nonNull(this.minecraft) && Objects.nonNull(this.minecraft.player)) {
            this.minecraft.player.playSound(SoundRegistry.ERASE.get(), 0.2F, 1.0F);
        }
    }

    private void updateRuneGenerator(long time) {
        if (this.lastRuneCheck < time) {
            this.lastRuneCheck = time + 250L;
            var random = this.minecraft.level.random;
            int k = random.nextInt(120) - 60;
            int l = random.nextInt(120) - 60;
            CubeCoordinateHelper.CubeHex hex = CubeCoordinateHelper.pixelToCube(k, -l, 9.0f);
            String hexKey = hex.toString();
            if (!this.runes.containsKey(hexKey) && !isCoordinateInNote(hex)) {
                long decayTime = this.lastRuneCheck + 15000L + (long) random.nextInt(10000);
                int runeType = random.nextInt(16);

                this.runes.put(hexKey, new Rune(hex, time, decayTime, runeType));
            }
        }
        this.runes.entrySet().removeIf(entry -> entry.getValue().decay < time);
    }

    public List<CubeCoordinateHelper.CubeHex> getDisconnectedFullHexes(Map<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> hexes) {
        Set<CubeCoordinateHelper.CubeHex> connected = new HashSet<>();
        Queue<CubeCoordinateHelper.CubeHex> queue = new LinkedList<>();

        hexes.forEach((pos, entry) -> {
            if (entry.type() == ResearchNoteComponent.HexEntry.ROOT) {
                queue.add(pos);
                connected.add(pos);
            }
        });

        while (!queue.isEmpty()) {
            CubeCoordinateHelper.CubeHex current = queue.poll();
            for (int i = 0; i < 6; i++) {
                CubeCoordinateHelper.CubeHex neighbor = current.getNeighbor(i);
                if (hexes.containsKey(neighbor)) {
                    ResearchNoteComponent.HexEntry neighborEntry = hexes.get(neighbor);
                    if (neighborEntry.type() == ResearchNoteComponent.HexEntry.FULL && !connected.contains(neighbor)) {
                        Aspect currentAspect = AspectRegistry.ASPECT_REGISTRY.get(hexes.get(current).aspect());
                        Aspect neighborAspect = AspectRegistry.ASPECT_REGISTRY.get(hexes.get(neighbor).aspect());
                        if (Objects.nonNull(currentAspect) && Objects.nonNull(neighborAspect)) {
                            if (currentAspect.isRelatedTo(neighborAspect)) {
                                connected.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
        List<CubeCoordinateHelper.CubeHex> disconnected = new ArrayList<>();
        hexes.forEach((pos, entry) -> {
            if (entry.type() == ResearchNoteComponent.HexEntry.FULL && !connected.contains(pos)) {
                disconnected.add(pos);
            }
        });
        return disconnected;
    }

    public Set<HexLink> getAllLinks(Map<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> hexes) {
        Set<HexLink> links = new HashSet<>();
        for (Map.Entry<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> entry : hexes.entrySet()) {
            CubeCoordinateHelper.CubeHex pos = entry.getKey();
            ResearchNoteComponent.HexEntry current = entry.getValue();
            if (current.type() == ResearchNoteComponent.HexEntry.EMPTY) {
                continue;
            }
            for (int i = 0; i < 6; i++) {
                CubeCoordinateHelper.CubeHex neighborPos = pos.getNeighbor(i);
                if (hexes.containsKey(neighborPos)) {
                    ResearchNoteComponent.HexEntry neighborEntry = hexes.get(neighborPos);
                    if (neighborEntry.type() != ResearchNoteComponent.HexEntry.EMPTY) {
                        Aspect currentAspect = AspectRegistry.ASPECT_REGISTRY.get(current.aspect());
                        Aspect neighborAspect = AspectRegistry.ASPECT_REGISTRY.get(neighborEntry.aspect());
                        if (Objects.nonNull(currentAspect) && Objects.nonNull(neighborAspect)) {
                            if (currentAspect.isRelatedTo(neighborAspect)) {
                                links.add(new HexLink(pos, neighborPos));
                            }
                        }
                    }
                }
            }
        }
        return links;
    }

    public record HexLink(CubeCoordinateHelper.CubeHex a, CubeCoordinateHelper.CubeHex b) {
        public HexLink {
            if (a.hashCode() > b.hashCode()) {
                CubeCoordinateHelper.CubeHex temp = a;
                a = b;
                b = temp;
            }
        }
    }

    private record Rune(CubeCoordinateHelper.CubeHex hex, long start, long decay, int rune) {
    }
}