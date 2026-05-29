package team.torka.thaumicrecords.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
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
import team.torka.thaumicrecords.api.item.StaffRod;
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
        WandRod wandRod = WandRodRegistry.WAND_ROD_REGISTRY.get(data.getRod());
        if (wandRod == null) {
            wandRod = WandRodRegistry.WAND_ROD_WOOD.get();
        }
        WandCap wandCap = Optional.ofNullable(WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap())).orElse(WandCapRegistry.WAND_CAP_IRON.get());
        boolean sceptre = data.sceptre();
        boolean staff = wandRod instanceof StaffRod;
        boolean glowing = wandRod.isGlowing();

        int rodLight = glowing ? LightTexture.FULL_BRIGHT : packedLight;

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        // Scale down staffs in GUI to fit slot
        if (staff && context == ItemDisplayContext.GUI) {
            poseStack.translate(0.15, 0.6, 0.0);
            poseStack.scale(0.7f, 0.7f, 0.7f);
        }

        // First person: move staff forward for better visibility
        // Entity: move staff up when dropped on ground
        if (staff && context == ItemDisplayContext.GROUND) {
            poseStack.translate(0.0, -1.0, 0.0);
            poseStack.scale(0.9f, 0.9f, 0.9f);
        }

        if (staff && context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.translate(0.0, 0.0, -0.3);
        }

        // === OUTER PUSH (TC4 line 124) ===
        poseStack.pushPose();
        if (staff) {
            poseStack.translate(0.0, 0.2, 0.0); // TC4 line 125-126
        }

        // --- ROD (TC4 lines 127-145) ---
        ResourceLocation rodTex = wandRod.getModelTexture();
        VertexConsumer rodBuf = buffer.getBuffer(RenderType.entityCutout(rodTex));
        poseStack.pushPose(); // TC4 line 127
        if (staff) {
            poseStack.translate(0.0, -0.1, 0.0); // TC4 line 135
            poseStack.scale(1.2f, 2.0f, 1.2f);   // TC4 line 136
        }
        model.rod.render(poseStack, rodBuf, rodLight, packedOverlay); // TC4 line 138
        poseStack.popPose(); // TC4 line 145

        // --- CAP (TC4 lines 146-175) ---
        ResourceLocation capTex = wandCap.getModelTexture();
        VertexConsumer capBuf = buffer.getBuffer(RenderType.entityTranslucent(capTex));
        poseStack.pushPose(); // TC4 line 147
        if (staff) {
            poseStack.scale(1.3f, 1.1f, 1.3f); // TC4 line 149
        } else {
            poseStack.scale(1.2f, 1.0f, 1.2f); // TC4 line 151
        }

        if (sceptre) {
            // Sceptre cap (TC4 lines 153-162)
            poseStack.pushPose();
            poseStack.scale(1.3f, 1.3f, 1.3f);
            model.cap.render(poseStack, capBuf, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.0, 0.3, 0.0);
            poseStack.scale(1.0f, 0.66f, 1.0f);
            model.cap.render(poseStack, capBuf, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            model.cap.render(poseStack, capBuf, packedLight, packedOverlay); // TC4 line 164
        }

        if (staff) {
            // Staff extra cap part (TC4 lines 166-172)
            poseStack.translate(0.0, 0.225, 0.0); // TC4 line 167
            poseStack.pushPose();
            poseStack.scale(1.0f, 0.66f, 1.0f); // TC4 line 169
            model.cap.render(poseStack, capBuf, packedLight, packedOverlay); // TC4 line 170
            poseStack.popPose();
            poseStack.translate(0.0, 0.65, 0.0); // TC4 line 172
        }

        model.capBottom.render(poseStack, capBuf, packedLight, packedOverlay); // TC4 line 174


        poseStack.popPose(); // TC4 line 175 (cap pop)

        // === RUNES after cap pop (in outer push space) ===
        if (sceptre) {
            renderSceptreRunes(poseStack, buffer, packedLight);
        }
        if (staff && wandRod instanceof StaffRod staffRod && staffRod.hasRunes()) {
            renderStaffRunes(poseStack, buffer, packedLight);
        }

        poseStack.popPose(); // OUTER pop
    }

    private void renderSceptreRunes(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        int ticks = mc.player != null ? mc.player.tickCount : 0;

        VertexConsumer runeBuf = buffer.getBuffer(RenderType.entityTranslucent(RUNE_TEXTURE));

        for (int i = 0; i < 10; i++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(36 * i + ticks));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            poseStack.translate(0.16, -0.01, -0.125);

            float r = Mth.sin((ticks + i * 5) / 5.0f) * 0.1f + 0.88f;
            float g = Mth.sin((ticks + i * 5) / 7.0f) * 0.1f + 0.63f;
            float b = 0.2f;
            float alpha = Mth.sin((ticks + i * 5) / 10.0f) * 0.2f + 0.6f;

            float pulse = alpha / 40.0f;
            float size = 0.06f + pulse;

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

    private void renderStaffRunes(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        int ticks = mc.player != null ? mc.player.tickCount : 0;

        VertexConsumer runeBuf = buffer.getBuffer(RenderType.entityTranslucent(RUNE_TEXTURE));

        // TC4: 4 faces * 14 runes vertically on each face
        for (int face = 0; face < 4; face++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90 * face));

            for (int a = 0; a < 14; a++) {
                int rune = (a + face * 3) % 16;
                poseStack.pushPose();
                // TC4 position: (0.36 + a*0.14, -0.01, -0.08) + Z rotation 90°
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.translate(0.36 + a * 0.14, -0.01, -0.08);

                float r = Mth.sin((ticks + rune * 5) / 5.0f) * 0.1f + 0.88f;
                float g = Mth.sin((ticks + rune * 5) / 7.0f) * 0.1f + 0.63f;
                float b = 0.2f;
                float alpha = Mth.sin((ticks + rune * 5) / 10.0f) * 0.2f + 0.6f;

                float pulse = alpha / 40.0f;
                float size = 0.06f + pulse;

                float u0 = rune * 0.0625f;
                float u1 = u0 + 0.0625f;
                float v0 = 0.0f;
                float v1 = 1.0f;

                PoseStack.Pose entry = poseStack.last();
                runeBuf.addVertex(entry, -size, size, 0)
                        .setColor(r, g, b, alpha)
                        .setUv(u1, v1)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(15728880)
                        .setNormal(entry, 0, 0, 1);
                runeBuf.addVertex(entry, size, size, 0)
                        .setColor(r, g, b, alpha)
                        .setUv(u1, v0)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(15728880)
                        .setNormal(entry, 0, 0, 1);
                runeBuf.addVertex(entry, size, -size, 0)
                        .setColor(r, g, b, alpha)
                        .setUv(u0, v0)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(15728880)
                        .setNormal(entry, 0, 0, 1);
                runeBuf.addVertex(entry, -size, -size, 0)
                        .setColor(r, g, b, alpha)
                        .setUv(u0, v1)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(15728880)
                        .setNormal(entry, 0, 0, 1);

                poseStack.popPose();
            }

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
