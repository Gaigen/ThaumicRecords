package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.helper.HexHelper;
import team.torka.thaumicrecords.menu.ResearchTableMenu;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResearchTableScreen extends AbstractContainerScreen<ResearchTableMenu> {
    private static final ResourceLocation GUI_TEX = ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID, "textures/gui/research_table.png");
    private static final ResourceLocation PAPER_TEX = ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID, "textures/gui/research_table_parchment.png");
    private static final ResourceLocation HEX_TEX = ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID, "textures/gui/research_table_hex_dark.png");
    private static final ResourceLocation HEX_LIGHT_TEX = ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID,
            "textures/gui/research_table_hex_light.png");
    private static final ResourceLocation RUNE_TEX = ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID, "textures/misc/runes.png");

    private final Map<String, Rune> runes = new ConcurrentHashMap<>();
    private long lastRuneCheck = 0L;

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

        this.drawSheet(graphics, x, y, mouseX, mouseY);

//        if (this.menu.page > 0) {
        graphics.blit(GUI_TEX, x + 27, y + 121, 184, 208, 24, 8);
//        }
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
            HexHelper.Pixel pix = (new HexHelper.Hex(rune.q, rune.r)).toPixel(9);
            float progress = (float) (time - rune.start) / (float) (rune.decay - rune.start);
            float alpha = 0.5F;
            if (progress < 0.25F) {
                alpha = progress * 2.0F;
            } else if (progress > 0.5F) {
                alpha = 1.0F - progress;
            }
            this.drawRune(graphics, (double) (x + 169) + pix.x, (double) (y + 83) + pix.y, rune.rune, alpha * 0.66F);
        }

        renderHexGrid(graphics, x + 169, y + 83, mx, my);
    }

    private void renderHexGrid(GuiGraphics graphics, int centerX, int centerY, int mx, int my) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0);

        // 渲染连接线 (仿照 drawLine)
        // 现代做法：使用 VertexConsumer 在屏幕上画色块线，或者直接 blit 拉伸的小像素
        // 这里简化为逻辑：
        // for (Hex line : lines) drawConnectionLine(graphics, ...);

        // 渲染六边形格子
        RenderSystem.setShaderTexture(0, HEX_TEX);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.25f);

        // 示例：渲染一个格子
        // graphics.blit(HEX_TEX, localX - 8, localY - 8, 0, 0, 16, 16, 16, 16);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
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

    private void updateRuneGenerator(long time) {
        if (this.lastRuneCheck < time) {
            this.lastRuneCheck = time + 250L;

            var random = this.minecraft.level.random;
            int k = random.nextInt(120) - 60;
            int l = random.nextInt(120) - 60;

            HexHelper.Hex hp = (new HexHelper.Pixel(k, l)).toHex(9);
            String hexKey = hp.toString();

            if (!this.runes.containsKey(hexKey) /* TODO 不是格子*/) {
                long decayTime = this.lastRuneCheck + 15000L + (long) random.nextInt(10000);
                int runeType = random.nextInt(16);

                this.runes.put(hexKey, new Rune(hp.q, hp.r, time, decayTime, runeType));
            }
        }
        this.runes.entrySet().removeIf(entry -> entry.getValue().decay < time);
    }

    private static class Rune {
        public int q, r, rune;
        public long start, decay;

        public Rune(int q, int r, long start, long decay, int rune) {
            this.q = q;
            this.r = r;
            this.start = start;
            this.decay = decay;
            this.rune = rune;
        }
    }
}