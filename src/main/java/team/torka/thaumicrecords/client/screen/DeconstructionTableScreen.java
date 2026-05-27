package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.menu.DeconstructionTableMenu;
import team.torka.thaumicrecords.registry.AspectRegistry;

public class DeconstructionTableScreen extends AbstractContainerScreen<DeconstructionTableMenu> {

    private static final ResourceLocation GUI_TEXTURE = ThaumicRecords.createRl("textures/gui/deconstruction_table.png");
    private static final int BREAK_BAR_X = 89;
    private static final int BREAK_BAR_Y = 19;
    private static final int BREAK_BAR_WIDTH = 24;
    private static final int BREAK_BAR_HEIGHT = 16;
    private static final int ASPECT_ICON_X = 116;
    private static final int ASPECT_ICON_Y = 19;

    private Button collectButton;

    public DeconstructionTableScreen(DeconstructionTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

        // Collect button — visible only when aspect is present
        collectButton = Button.builder(Component.translatable("button.thaumicrecords.collect"), button -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
            }
        }).bounds(this.leftPos + ASPECT_ICON_X - 6, this.topPos + ASPECT_ICON_Y + 18, 40, 14).build();
        this.addRenderableWidget(collectButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Break progress bar (like furnace burn time)
        int breaktime = menu.getBreaktime();
        if (breaktime > 0) {
            int progress = (int) ((float) breaktime / 40.0F * BREAK_BAR_WIDTH);
            progress = Math.min(progress, BREAK_BAR_WIDTH);
            guiGraphics.blit(GUI_TEXTURE, x + BREAK_BAR_X, y + BREAK_BAR_Y,
                    176, 0, progress, BREAK_BAR_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Show aspect icon if present
        ResourceLocation currentAspect = menu.getCurrentAspect();
        if (currentAspect != null) {
            var aspect = AspectRegistry.ASPECT_REGISTRY.get(currentAspect);
            if (aspect != null) {
                int x = (this.width - this.imageWidth) / 2;
                int y = (this.height - this.imageHeight) / 2;

                // Draw aspect icon with color tinting
                ResourceLocation aspectTex = aspect.getImage();
                if (aspectTex != null) {
                    int aspectColor = aspect.getARGBColor();
                    float r = ((aspectColor >> 16) & 0xFF) / 255.0F;
                    float g = ((aspectColor >> 8) & 0xFF) / 255.0F;
                    float b = (aspectColor & 0xFF) / 255.0F;

                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(r, g, b, 1.0F);
                    guiGraphics.blit(aspectTex, x + ASPECT_ICON_X, y + ASPECT_ICON_Y,
                            0, 0, 16, 16, 16, 16);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                }

                // Draw aspect name below icon
                Component name = Component.translatable(aspect.getNameTranslationKey());
                int nameWidth = font.width(name);
                guiGraphics.drawString(font, name, x + ASPECT_ICON_X + 8 - nameWidth / 2,
                        y + ASPECT_ICON_Y + 17, aspect.getARGBColor(), true);

                // Tooltip on hover over aspect icon
                if (mouseX >= x + ASPECT_ICON_X && mouseX < x + ASPECT_ICON_X + 16
                        && mouseY >= y + ASPECT_ICON_Y && mouseY < y + ASPECT_ICON_Y + 16) {
                    guiGraphics.renderTooltip(font, name, mouseX, mouseY);
                }
            }
        }

        // Show/hide collect button based on aspect presence
        if (collectButton != null) {
            collectButton.visible = currentAspect != null;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
