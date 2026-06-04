package team.torka.thaumicrecords.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.client.event.EventHandlerRunic;

import java.util.UUID;

/**
 * Port of TC4's renderRunicArmorBar.
 * Renders runic shield bar above health hearts using mod's particle texture.
 * Shield icon base: (160, 16) size 9x9
 * Rune overlay:     (col*16, 96) size 16x16, rendered at 0.5x scale with gold tint
 */
@EventBusSubscriber(Dist.CLIENT)
public class RunicShieldHudOverlay {

    private static final ResourceLocation PARTICLE_TEXTURE = ThaumicRecords.createRl("textures/misc/particles.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        // Don't render in creative/spectator mode — hearts are hidden
        if (mc.player.getAbilities().instabuild || mc.player.isSpectator()) {
            return;
        }

        // Don't render during pause
        if (mc.isPaused()) {
            return;
        }

        UUID uuid = mc.player.getUUID();
        int maxCharge = EventHandlerRunic.getMaxRunicCharge(uuid);
        int currentCharge = EventHandlerRunic.getRunicCharge(uuid);

        if (maxCharge <= 0 || currentCharge <= 0) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int baseX = screenWidth / 2 - 91;
        int baseY = screenHeight - 39;

        int segments = Math.min((int) ((float) currentCharge / maxCharge * 10.0f), 10);
        int tickCount = mc.player.tickCount;

        // Match TC4 GL state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);

        for (int a = 0; a < segments; a++) {
            int sx = baseX + a * 8;

            // Shield icon background (9x9 from particles.png at UV 160,16)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            guiGraphics.blit(PARTICLE_TEXTURE, sx, baseY, 160, 16, 9, 9, 256, 256);

            // Animated rune overlay — gold-tinted, pulsing sin wave
            // TC4: glScaled(0.5) then draws full 16x16 -> GPU bilinear-filters to 8x8
            float alpha = Mth.sin(tickCount / 4.0f + a) * 0.4f + 0.6f;
            RenderSystem.setShaderColor(1.0f, 0.75f, 0.24f, alpha);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(sx, baseY, 0);
            guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
            guiGraphics.blit(PARTICLE_TEXTURE, 0, 0, a * 16, 96, 16, 16, 256, 256);
            guiGraphics.pose().popPose();
        }

        guiGraphics.pose().popPose();

        // Restore state
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableDepthTest();
    }
}
