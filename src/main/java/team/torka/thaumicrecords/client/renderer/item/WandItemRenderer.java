package team.torka.thaumicrecords.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.client.model.WandModel;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

public class WandItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final WandItemRenderer INSTANCE = new WandItemRenderer();

    private static final WandModel model = new WandModel();
    private static final ResourceLocation RUNE_TEXTURE = ThaumicRecords.createRl("textures/misc/script.png");

    public WandItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        WandItemComponent data = stack.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (Objects.isNull(data)) {
            data = new WandItemComponent(WandRodRegistry.WAND_ROD_WOOD.getId(), WandCapRegistry.WAND_CAP_IRON.getId(), AspectList.empty());
        }
        WandRod wandRod = Optional.ofNullable(WandRodRegistry.WAND_ROD_REGISTRY.get(data.getRod())).orElse(WandRodRegistry.WAND_ROD_WOOD.get());
        WandCap wandCap = Optional.ofNullable(WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap())).orElse(WandCapRegistry.WAND_CAP_IRON.get());
        boolean sceptre = data.sceptre();

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        // Rod
        ResourceLocation rodTex = wandRod.getModelTexture();
        VertexConsumer rodBuf = buffer.getBuffer(RenderType.entityCutout(rodTex));
        poseStack.pushPose();
        model.rod.render(poseStack, rodBuf, packedLight, packedOverlay);
        poseStack.popPose();

        // Cap
        ResourceLocation capTex = wandCap.getModelTexture();
        VertexConsumer capBuf = buffer.getBuffer(RenderType.entityTranslucent(capTex));

        // Cap rendering (same coordinate system as TC4)
        poseStack.pushPose();
        // Base scale: 1.2x for normal wand
        poseStack.scale(1.2f, 1.0f, 1.2f);

        if (sceptre) {
            // Sceptre: bigger cap (1.3x on top of 1.2x base)
            poseStack.pushPose();
            poseStack.scale(1.3f, 1.3f, 1.3f);
            model.cap.render(poseStack, capBuf, packedLight, packedOverlay);
            poseStack.popPose();

            // Extra squished cap part, 0.3 above
            poseStack.pushPose();
            poseStack.translate(0.0, 0.3, 0.0);
            poseStack.scale(1.0f, 0.66f, 1.0f);
            model.cap.render(poseStack, capBuf, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            model.cap.render(poseStack, capBuf, packedLight, packedOverlay);
        }

        // Cap bottom
        poseStack.pushPose();
        model.capBottom.render(poseStack, capBuf, packedLight, packedOverlay);
        poseStack.popPose();
        poseStack.popPose();

        // Sceptre runes (in base coordinate system, like TC4)
        if (sceptre) {
            renderSceptreRunes(poseStack, buffer, packedLight);
        }
    }

    private void renderSceptreRunes(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        int ticks = mc.player != null ? mc.player.tickCount : 0;

        // Get rune buffer ONCE
        VertexConsumer runeBuf = buffer.getBuffer(RenderType.entityTranslucent(RUNE_TEXTURE));

        for (int i = 0; i < 10; i++) {
            poseStack.pushPose();
            // Rotate around Y axis (like TC4: 36 * rot + ticks)
            poseStack.mulPose(Axis.YP.rotationDegrees(36 * i + ticks));

            // drawRune: rotate 90 around Z, then translate
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            poseStack.translate(0.16, -0.01, -0.125);

            // Animated color
            float r = Mth.sin((ticks + i * 5) / 5.0f) * 0.1f + 0.88f;
            float g = Mth.sin((ticks + i * 5) / 7.0f) * 0.1f + 0.63f;
            float b = 0.2f;
            float alpha = Mth.sin((ticks + i * 5) / 10.0f) * 0.2f + 0.6f;

            // Pulsing size (like TC4: alpha / 40)
            float pulse = alpha / 40.0f;
            float size = 0.06f + pulse;

            // UV: horizontal strip of 16 runes, each 1/16 wide
            float u0 = i * 0.0625f;
            float u1 = u0 + 0.0625f;
            float v0 = 0.0f;
            float v1 = 1.0f;

            PoseStack.Pose entry = poseStack.last();
            runeBuf.addVertex(entry, -size, size, 0).setColor(r, g, b, alpha).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(
                    entry, 0, 0, 1);
            runeBuf.addVertex(entry, size, size, 0).setColor(r, g, b, alpha).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(
                    entry, 0, 0, 1);
            runeBuf.addVertex(entry, size, -size, 0).setColor(r, g, b, alpha).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(
                    entry, 0, 0, 1);
            runeBuf.addVertex(entry, -size, -size, 0).setColor(r, g, b, alpha).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(
                    entry, 0, 0, 1);

            poseStack.popPose();
        }
    }

    public IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            @NotNull
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return WandItemRenderer.this;
            }
        };
    }
}
