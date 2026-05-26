package team.torka.thaumicrecords.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.menu.ThaumatoriumMenu;
import team.torka.thaumicrecords.recipe.CrucibleRecipe;
import team.torka.thaumicrecords.registry.AspectRegistry;

public class ThaumatoriumScreen extends AbstractContainerScreen<ThaumatoriumMenu> {

    private static final ResourceLocation GUI_TEXTURE = ThaumicRecords.createRl("textures/gui/gui_thaumatorium.png");

    private int index = 0;
    private int lastSize = 0;
    private int startAspect = 0;

    public ThaumatoriumScreen(ThaumatoriumMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = -10;
        refreshIndex();
    }

    private void refreshIndex() {
        if (menu.blockEntity.recipeIds != null && !menu.recipes.isEmpty()) {
            for (int a = 0; a < menu.recipes.size(); a++) {
                if (a < menu.recipeIds.size() && menu.blockEntity.recipeIds.contains(menu.recipeIds.get(a))) {
                    index = a;
                    break;
                }
            }
        }
        startAspect = 0;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (index >= menu.recipes.size()) {
            index = menu.recipes.size() - 1;
        }

        if (menu.recipes.isEmpty()) {
            return;
        }

        if (lastSize != menu.recipes.size()) {
            lastSize = menu.recipes.size();
            refreshIndex();
        }

        if (index < 0) {
            index = 0;
        }

        CrucibleRecipe currentRecipe = menu.recipes.get(index);

        // === Recipe navigation arrows (up/down) ===
        if (menu.recipes.size() > 1) {
            // Up arrow
            if (index > 0) {
                guiGraphics.blit(GUI_TEXTURE, x + 128, y + 16, 192, 16, 16, 8);
            } else {
                guiGraphics.blit(GUI_TEXTURE, x + 128, y + 16, 176, 16, 16, 8);
            }
            // Down arrow
            if (index < menu.recipes.size() - 1) {
                guiGraphics.blit(GUI_TEXTURE, x + 128, y + 24, 192, 24, 16, 8);
            } else {
                guiGraphics.blit(GUI_TEXTURE, x + 128, y + 24, 176, 24, 16, 8);
            }
        }

        // === Aspect scroll arrows ===
        AspectList recipeAspects = currentRecipe.aspects();
        if (recipeAspects.size() > 6) {
            // Left scroll
            if (startAspect > 0) {
                guiGraphics.blit(GUI_TEXTURE, x + 32, y + 40, 192, 32, 8, 16);
            } else {
                guiGraphics.blit(GUI_TEXTURE, x + 32, y + 40, 176, 32, 8, 16);
            }
            // Right scroll
            if (startAspect < recipeAspects.size() - 1) {
                guiGraphics.blit(GUI_TEXTURE, x + 136, y + 40, 200, 32, 8, 16);
            } else {
                guiGraphics.blit(GUI_TEXTURE, x + 136, y + 40, 184, 32, 8, 16);
            }
        } else {
            startAspect = 0;
        }

        // === Assign recipe button ===
        boolean isAssigned = index < menu.recipeIds.size() && menu.blockEntity.recipeIds.contains(menu.recipeIds.get(index));
        boolean canAssign = menu.blockEntity.recipeIds.size() < menu.blockEntity.maxRecipes || isAssigned;

        if (canAssign) {
            // Hover area + assigned indicator
            int btnX = mouseX - x;
            int btnY = mouseY - y;
            if ((btnX >= 112 && btnX < 128 && btnY >= 16 && btnY < 32) || isAssigned) {
                RenderSystem.enableBlend();
                guiGraphics.blit(GUI_TEXTURE, x + 104, y + 8, 176, 96, 48, 48);
                RenderSystem.disableBlend();
            }

            // Gear animation (pulsing)
            RenderSystem.enableBlend();
            float alpha = 0.6F + Mth.sin(Minecraft.getInstance().player.tickCount / 5.0F) * 0.4F + 0.4F;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            guiGraphics.blit(GUI_TEXTURE, x + 88, y + 16, 176, 56, 24, 24);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
        }

        // === Max recipes counter ===
        if (menu.blockEntity.maxRecipes > 1) {
            String text = menu.blockEntity.recipeIds.size() + "/" + menu.blockEntity.maxRecipes;
            int tw = font.width(text) / 2;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x + 136, y + 33, 0);
            guiGraphics.pose().scale(0.5F, 0.5F, 0);
            guiGraphics.drawString(font, text, -tw, 0, 0xFFFFFF, false);
            guiGraphics.pose().popPose();
        }

        // === Aspect bars ===
        drawAspectBars(guiGraphics, x, y, currentRecipe);

        // === Result item ===
        drawOutput(guiGraphics, x, y, mouseX, mouseY, currentRecipe);
    }

    private void drawAspectBars(GuiGraphics guiGraphics, int x, int y, CrucibleRecipe recipe) {
        if (recipe == null) {
            return;
        }
        AspectList needed = recipe.aspects();
        if (needed == null || needed.size() == 0) {
            return;
        }

        boolean isAssigned = false;
        for (int i = 0; i < menu.recipes.size() && i < menu.recipeIds.size(); i++) {
            if (menu.recipes.get(i) == recipe && menu.blockEntity.recipeIds.contains(menu.recipeIds.get(i))) {
                isAssigned = true;
                break;
            }
        }
        int count = 0;
        int pos = 0;

        if (isAssigned) {
            for (var entry : needed.entrySet()) {
                if (count >= startAspect) {
                    // Bar background
                    guiGraphics.blit(GUI_TEXTURE, x + 41 + 16 * pos, y + 57, 176, 8, 14, 6);

                    // Bar fill
                    int have = menu.blockEntity.essentia.getOrDefault(entry.getKey(), 0);
                    int need = entry.getValue();
                    int fillWidth = (int) ((float) have / Math.max(1, need) * 12.0F);
                    fillWidth = Mth.clamp(fillWidth, 0, 12);

                    var aspect = AspectRegistry.ASPECT_REGISTRY.get(entry.getKey());
                    if (aspect != null) {
                        int aspectColor = aspect.getARGBColor();
                        float r = ((aspectColor >> 16) & 0xFF) / 255.0F;
                        float g = ((aspectColor >> 8) & 0xFF) / 255.0F;
                        float b = (aspectColor & 0xFF) / 255.0F;
                        RenderSystem.setShaderColor(r, g, b, 1.0F);
                    }
                    guiGraphics.blit(GUI_TEXTURE, x + 42 + 16 * pos, y + 58, 176, 0, fillWidth, 4);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                    pos++;
                }
                count++;
                if (count >= 6 + startAspect) {
                    break;
                }
            }
        }

        // === Aspect icons ===
        count = 0;
        pos = 0;
        for (var entry : needed.entrySet()) {
            if (count >= startAspect) {
                // Draw aspect tag (icon + number)
                var aspect = AspectRegistry.ASPECT_REGISTRY.get(entry.getKey());
                drawAspectTag(guiGraphics, x + 40 + 16 * pos, y + 40, aspect, entry.getValue());
                pos++;
            }
            count++;
            if (count >= 6 + startAspect) {
                break;
            }
        }
    }

    private void drawAspectTag(GuiGraphics guiGraphics, int x, int y, team.torka.thaumicrecords.api.aspect.Aspect aspect, int amount) {
        if (aspect == null) {
            return;
        }
        ResourceLocation aspectTex = aspect.getImage();
        if (aspectTex != null) {

            int aspectColor = aspect.getARGBColor();
            float r = ((aspectColor >> 16) & 0xFF) / 255.0F;
            float g = ((aspectColor >> 8) & 0xFF) / 255.0F;
            float b = (aspectColor & 0xFF) / 255.0F;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(r, g, b, 1.0F);
            guiGraphics.blit(aspectTex, x, y, 0, 0, 16, 16, 16, 16);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (amount > 0) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(x, y, 0);
                guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
                String amtStr = String.valueOf(amount);
                int sw = font.width(amtStr);
                guiGraphics.drawString(font, amtStr, 32 - sw, 32 - font.lineHeight, 0xFFFFFF, true);
                guiGraphics.pose().popPose();
            }
        }
    }

    private void drawOutput(GuiGraphics guiGraphics, int guiLeft, int guiTop, int mouseX, int mouseY, CrucibleRecipe recipe) {
        if (recipe == null) {
            return;
        }
        ItemStack output = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        if (output.isEmpty()) {
            return;
        }

        int slotX = guiLeft + 112;
        int slotY = guiTop + 16;

        boolean isAssigned = false;
        for (int i = 0; i < menu.recipes.size() && i < menu.recipeIds.size(); i++) {
            if (menu.recipes.get(i) == recipe && menu.blockEntity.recipeIds.contains(menu.recipeIds.get(i))) {
                isAssigned = true;
                break;
            }
        }
        boolean canAssign = menu.blockEntity.recipeIds.size() < menu.blockEntity.maxRecipes || isAssigned;

        if (!canAssign) {
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.3F + Mth.sin(Minecraft.getInstance().player.tickCount / 4.0F) * 0.3F + 0.3F);
        }

        guiGraphics.renderItem(output, slotX, slotY);
        guiGraphics.renderItemDecorations(font, output, slotX, slotY);

        if (!canAssign) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        // Tooltip on hover (relative to GUI)
        int relX = mouseX - guiLeft;
        int relY = mouseY - guiTop;
        if (relX >= 112 && relX < 128 && relY >= 16 && relY < 32) {
            guiGraphics.renderTooltip(font, output, mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        menu.updateRecipes();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // No labels
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        int gx = (this.width - this.imageWidth) / 2;
        int gy = (this.height - this.imageHeight) / 2;

        if (menu.recipes.isEmpty() || index < 0 || index >= menu.recipes.size()) {
            return false;
        }

        int rx = (int) (mouseX - gx);
        int ry = (int) (mouseY - gy);

        boolean isAssigned = index < menu.recipeIds.size() && menu.blockEntity.recipeIds.contains(menu.recipeIds.get(index));
        boolean canAssign = menu.blockEntity.recipeIds.size() < menu.blockEntity.maxRecipes || isAssigned;
        if (rx >= 112 && rx < 128 && ry >= 16 && ry < 32 && canAssign) {
            if (this.menu != null && this.minecraft != null) {

                ResourceLocation rl = menu.recipeIds.get(index);
                if (menu.blockEntity.recipeIds.contains(rl)) {
                    menu.blockEntity.recipeIds.remove(rl);
                } else {
                    menu.blockEntity.recipeIds.add(rl);
                }
                menu.updateRecipes();

                // Send to server
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, this.index);
            }
            return true;
        }

        // === Recipe navigation arrows ===
        if (menu.recipes.size() > 1) {
            // Up arrow at (128, 16) 16x8
            if (index > 0 && rx >= 128 && rx < 144 && ry >= 16 && ry < 24) {
                index--;
                return true;
            }
            // Down arrow at (128, 24) 16x8
            if (index < menu.recipes.size() - 1 && rx >= 128 && rx < 144 && ry >= 24 && ry < 32) {
                index++;
                return true;
            }
        }

        // === Aspect scroll arrows ===
        CrucibleRecipe currentRecipe = menu.recipes.get(index);
        if (currentRecipe.aspects().size() > 6) {
            // Left scroll at (32, 40) 8x16
            if (startAspect > 0 && rx >= 32 && rx < 40 && ry >= 40 && ry < 56) {
                startAspect--;
                return true;
            }
            // Right scroll at (136, 40) 8x16
            if (startAspect < currentRecipe.aspects().size() - 1 && rx >= 136 && rx < 144 && ry >= 40 && ry < 56) {
                startAspect++;
                return true;
            }
        }

        return false;
    }
}
