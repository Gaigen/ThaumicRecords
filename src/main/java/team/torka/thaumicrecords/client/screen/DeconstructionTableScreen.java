package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.menu.DeconstructionTableMenu;
import team.torka.thaumicrecords.registry.AspectRegistry;

public class DeconstructionTableScreen extends AbstractContainerScreen<DeconstructionTableMenu> {

    private static final ResourceLocation GUI_TEXTURE = ThaumicRecords.createRl("textures/gui/deconstruction_table.png");

    public DeconstructionTableScreen(DeconstructionTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = -10;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, k, l, 0, 0, this.imageWidth, this.imageHeight);

        // Progress bar — vertical, original: k+93, l+15, texture(176, 46-i1), 9px wide, i1 tall
        int breaktime = menu.getBreaktime();
        if (breaktime > 0) {
            int scaled = breaktime * 46 / 40;
            guiGraphics.blit(GUI_TEXTURE, k + 93, l + 15 + 46 - scaled, 176, 46 - scaled, 9, scaled);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Aspect icon — original: k+64, l+48
        ResourceLocation currentAspect = menu.getCurrentAspect();
        if (currentAspect != null) {
            var aspect = AspectRegistry.ASPECT_REGISTRY.get(currentAspect);
            if (aspect != null) {
                int k = (this.width - this.imageWidth) / 2;
                int l = (this.height - this.imageHeight) / 2;

                ResourceLocation aspectTex = aspect.getImage();
                if (aspectTex != null) {
                    int aspectColor = aspect.getARGBColor();
                    float r = ((aspectColor >> 16) & 0xFF) / 255.0F;
                    float g = ((aspectColor >> 8) & 0xFF) / 255.0F;
                    float b = (aspectColor & 0xFF) / 255.0F;

                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(r, g, b, 1.0F);
                    guiGraphics.blit(aspectTex, k + 64, l + 48, 0, 0, 16, 16, 16, 16);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                }

                // Tooltip on hover
                int relX = mouseX - (k + 64);
                int relY = mouseY - (l + 48);
                if (relX >= 0 && relY >= 0 && relX < 16 && relY < 16) {
                    Component name = Component.translatable(aspect.getNameTranslationKey());
                    guiGraphics.renderTooltip(font, name, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;

        // Click on aspect icon — original: k+64, l+48, 16x16, sends button 1
        int relX = (int) mouseX - (k + 64);
        int relY = (int) mouseY - (l + 48);
        if (relX >= 0 && relY >= 0 && relX < 16 && relY < 16 && menu.getCurrentAspect() != null) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Title hidden — original TC4 has no visible title
    }
}
