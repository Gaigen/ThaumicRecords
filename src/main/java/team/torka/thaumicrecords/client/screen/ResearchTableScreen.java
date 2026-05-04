package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.CubeCoordinateHelper;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.data.component.ResearchNoteComponent;
import team.torka.thaumicrecords.menu.ResearchTableMenu;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        graphics.blit(GUI_TEX, x + 27, y + 121, 184, 208, 24, 8);

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

        renderHexGrid(graphics, x + 169, y + 83, mx, my);
    }

    private void renderHexGrid(GuiGraphics graphics, int centerX, int centerY, int mx, int my) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0);
        ItemStack researchNote = menu.getResearchNote();
        ResearchNoteComponent researchNoteComponent = researchNote.get(DataComponentRegistry.RESEARCH_NOTE);
        if (Objects.isNull(researchNoteComponent)) {
            graphics.pose().popPose();
            return;
        }
        Map<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> decodedHexes = researchNoteComponent.getDecodedHexes();
        for (Map.Entry<CubeCoordinateHelper.CubeHex, ResearchNoteComponent.HexEntry> entry : decodedHexes.entrySet()) {
            CubeCoordinateHelper.CubeHex hex = entry.getKey();
            ResearchNoteComponent.HexEntry hexEntry = entry.getValue();
            if (hexEntry.type() != ResearchNoteComponent.HexEntry.ROOT) {
                if (!researchNoteComponent.complete()) {
                    this.drawHex(graphics, hex);
                }
            }
            if (hexEntry.type() == ResearchNoteComponent.HexEntry.ROOT) {
                this.drawOrb(graphics, hex);
            }
            if (Arrays.asList(ResearchNoteComponent.HexEntry.FULL, ResearchNoteComponent.HexEntry.ROOT).contains(hexEntry.type())) {
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(hexEntry.aspect());
                if (Objects.isNull(aspect)) {
                    continue;
                }
                this.drawAspectIcon(graphics, hex, aspect);
            }
        }
        CubeCoordinateHelper.CubeHex hoveredHex = CubeCoordinateHelper.pixelToCube(mx - centerX, -my + centerY, 9.0f);
        if (decodedHexes.containsKey(hoveredHex)) {
            this.drawHexHighlight(graphics, hoveredHex);
        }
        graphics.pose().popPose();
    }

    private boolean isCoordinateInNote(CubeCoordinateHelper.CubeHex coordinate) {
        ItemStack researchNote = menu.getResearchNote();
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
        this.lastPage = Math.max(0, (count - 1) / 25);
        int drawn = 0;
        int startIndex = this.page * 25;
        for (int i = startIndex; i < sortedKeys.size() && drawn < 25; i++) {
            ResourceLocation aspectId = sortedKeys.get(i);
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(aspectId);
            if (aspect == null) {
                continue;
            }
            int amount = points.get(aspectId);
            int xx = x + (drawn / 5) * 16;
            int yy = y + (drawn % 5) * 16;
            this.drawAspectTag(graphics, xx, yy, aspect, amount, mx, my);
            drawn++;
        }
    }

    private void drawAspectTag(GuiGraphics graphics, int x, int y, Aspect aspect, int amount, int mx, int my) {
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
        if (amount > 0) {
            graphics.pose().pushPose();
            graphics.pose().translate(x + 16, y + 12, 0.5f);
            String s = String.valueOf(amount);
            graphics.drawString(this.font, s, -this.font.width(s), -3, 0xFFFFFF, true);
            graphics.pose().popPose();
        }
        if (mx >= x && mx < x + 16 && my >= y && my < y + 16) {
            MutableComponent title = Component.translatable(aspect.getNameTranslationKey()).withStyle(ChatFormatting.AQUA);
            MutableComponent lore = Component.translatable(aspect.getLoreTranslationKey()).withStyle(ChatFormatting.GRAY);
            graphics.renderComponentTooltip(this.font, Arrays.asList(title, lore), mx, my);
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

    private void drawAspectIcon(GuiGraphics graphics, CubeCoordinateHelper.CubeHex hex, Aspect aspect) {
        CubeCoordinateHelper.ScreenPos pix = hex.toPixel(9.0f);
        graphics.pose().pushPose();
        graphics.pose().translate(0 + pix.x(), 0 + pix.y(), 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int argb = aspect.getARGBColor();
        float r = (float) (argb >> 16 & 255) / 255.0F;
        float g = (float) (argb >> 8 & 255) / 255.0F;
        float b = (float) (argb & 255) / 255.0F;
        RenderSystem.setShaderColor(r, g, b, 1.0F);
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
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        long ticks = System.currentTimeMillis() / 50;
        int part = (int) (ticks % 8);
        float u = (0.5F + (float) part / 8.0F) * 256.0F;
        float v = 0.5F * 256.0F;
        graphics.pose().pushPose();
        graphics.pose().translate(pix.x(), pix.y(), 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderColor(r, g, b, 1.0F);
        graphics.blit(PARTICLES_TEX, -8, -8, u, v, 16, 16, 256, 256);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        graphics.pose().popPose();
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

    private record Rune(CubeCoordinateHelper.CubeHex hex, long start, long decay, int rune) {
    }
}