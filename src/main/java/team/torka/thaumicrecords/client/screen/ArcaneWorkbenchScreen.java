package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.menu.ArcaneWorkbenchMenu;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;


public class ArcaneWorkbenchScreen extends AbstractContainerScreen<ArcaneWorkbenchMenu> {
    private static final ResourceLocation GUI_TEXTURE = ThaumicRecords.createRl("textures/gui/arcane_workbench_gui.png");
    // AER, IGNIS, AQUA, TERRA, ORDO, PERDITIO
    private int[][] aspectLocations = new int[][]{{72, 21}, {24, 102}, {72, 124}, {24, 43}, {120, 102}, {120, 43}};

    public ArcaneWorkbenchScreen(ArcaneWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 190;
        this.imageHeight = 234;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        RenderSystem.enableBlend();
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        renderAspects(guiGraphics, x, y);
        RenderSystem.disableBlend();
    }

    private void renderAspects(GuiGraphics guiGraphics, int guiX, int guiY) {
        float ticks = (System.currentTimeMillis() % 10000) / 50.0F;
        AspectList cost = this.menu.getCachedAspect();
        if (Objects.isNull(cost) || cost.isEmpty()) {
            return;
        }
        ItemStack wand = this.menu.getWandStack();
        WandItemComponent data = wand.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        WandCap wandCap = Objects.nonNull(data) ? WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap()) : null;
        for (int i = 0; i < 6; i++) {
            ResourceLocation rl = Aspect.getPrimalList().get(i);
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
            int baseCost = cost.getOrZero(rl);
            if (baseCost <= 0) {
                continue;
            }
            int actualCost = cost.getWithModifier(rl, Objects.nonNull(wandCap) ? wandCap.getAspectCostModifier(rl) : 1);
            float alpha;
            int wandVis = Objects.isNull(data) ? AspectList.empty().getOrZero(rl) : data.getAspects().getOrZero(rl);
            if (wandVis >= actualCost) {
                alpha = 1.0F;
            } else {
                alpha = 0.5F + (Mth.sin((ticks + i * 10) / 2.0F) * 0.2F - 0.2F);
            }
            int drawX = guiX + aspectLocations[i][0] - 8;
            int drawY = guiY + aspectLocations[i][1] - 8;
            renderAspectIcon(guiGraphics, aspect, drawX, drawY, alpha);
            renderCostText(guiGraphics, drawX, drawY, AspectList.formatScaled(actualCost));
        }
        if (this.menu.isVisInsufficient() && !wand.isEmpty()) {
            renderInsufficientVisOverlay(guiGraphics, guiX, guiY);
        }
    }

    private void renderInsufficientVisOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 168, y + 46, 0);
        String text = Component.translatable(ThaumicRecords.createTranslationKey("text", "insufficient_vis")).getString();
        guiGraphics.drawString(this.font, text, -16, 0, 0xEE0000, false);
        guiGraphics.pose().popPose();
    }

    private void renderAspectIcon(GuiGraphics g, Aspect aspect, int x, int y, float alpha) {
        if (Objects.isNull(aspect)) {
            return;
        }
        int argb = aspect.getARGBColor();
        float red = (float) (argb >> 16 & 255) / 255.0F;
        float green = (float) (argb >> 8 & 255) / 255.0F;
        float blue = (float) (argb & 255) / 255.0F;
        ResourceLocation texture = aspect.getImage();
        g.setColor(red, green, blue, alpha);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        g.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
        g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
    }

    private void renderCostText(GuiGraphics g, int x, int y, String cost) {
        g.pose().pushPose();
        g.pose().translate(x + 8, y + 16, 300);
        g.drawString(this.font, cost, 4, -5, 0xFFFFFF, true);
        g.pose().popPose();
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        boolean isResultSlot = slot.index == ArcaneWorkbenchMenu.SLOT_CRAFT_RESULT;
        if (isResultSlot && !this.menu.getCachedAspect().isAspectEmpty() && slot.hasItem()) {
            int slotX = slot.x;
            int slotY = slot.y;
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty()) {
                if (this.menu.isVisInsufficient()) {
                    RenderSystem.setShaderColor(0.4F, 0.4F, 0.4F, 1.0F);
                }
                guiGraphics.renderItem(stack, slotX, slotY, slot.x + slot.y * this.imageWidth);
                guiGraphics.renderItemDecorations(this.font, stack, slotX, slotY);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        } else {
            super.renderSlot(guiGraphics, slot);
        }
    }
}