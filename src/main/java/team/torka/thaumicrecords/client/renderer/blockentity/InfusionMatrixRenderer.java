package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.entity.InfusionMatrixBlockEntity;

public class InfusionMatrixRenderer implements BlockEntityRenderer<InfusionMatrixBlockEntity> {

    private static final ResourceLocation TEXTURE = ThaumicRecords.createRl("textures/block/infuser.png");

    public InfusionMatrixRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull InfusionMatrixBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        float ticks = mc.player != null ? mc.player.tickCount + partialTick : 0;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);

        if (be.active) {
            poseStack.mulPose(Axis.YP.rotationDegrees(ticks % 360.0F * be.startUp));
            poseStack.mulPose(Axis.XP.rotationDegrees(35.0F * be.startUp));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F * be.startUp));
        }

        float instability = Math.min(6.0F, 1.0F + be.instability * 0.66F * Math.min(be.craftCount, 50) / 50.0F);

        // Pass 1: Base cubes
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    float b1 = 0, b2 = 0, b3 = 0;
                    if (be.active) {
                        b1 = Mth.sin((ticks + a * 10) / (15.0F - instability / 2.0F)) * 0.01F * be.startUp * instability;
                        b2 = Mth.sin((ticks + b * 10) / (14.0F - instability / 2.0F)) * 0.01F * be.startUp * instability;
                        b3 = Mth.sin((ticks + c * 10) / (13.0F - instability / 2.0F)) * 0.01F * be.startUp * instability;
                    }
                    int aa = (a == 0) ? -1 : 1;
                    int bb = (b == 0) ? -1 : 1;
                    int cc = (c == 0) ? -1 : 1;

                    poseStack.pushPose();
                    poseStack.translate(b1 + aa * 0.25F, b2 + bb * 0.25F, b3 + cc * 0.25F);
                    if (a > 0) poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * a));
                    if (b > 0) poseStack.mulPose(Axis.YP.rotationDegrees(90.0F * b));
                    if (c > 0) poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F * c));
                    poseStack.scale(0.45F, 0.45F, 0.45F);

                    renderCube(poseStack, vc, packedLight, 0);

                    poseStack.popPose();
                }
            }
        }

        // Pass 2: Glow overlay
        if (be.active) {
            for (int a = 0; a < 2; a++) {
                for (int b = 0; b < 2; b++) {
                    for (int c = 0; c < 2; c++) {
                        float b1 = Mth.sin((ticks + a * 10) / (15.0F - instability / 2.0F)) * 0.01F * be.startUp * instability;
                        float b2 = Mth.sin((ticks + b * 10) / (14.0F - instability / 2.0F)) * 0.01F * be.startUp * instability;
                        float b3 = Mth.sin((ticks + c * 10) / (13.0F - instability / 2.0F)) * 0.01F * be.startUp * instability;
                        int aa = (a == 0) ? -1 : 1;
                        int bb = (b == 0) ? -1 : 1;
                        int cc = (c == 0) ? -1 : 1;

                        poseStack.pushPose();
                        poseStack.translate(b1 + aa * 0.25F, b2 + bb * 0.25F, b3 + cc * 0.25F);
                        if (a > 0) poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * a));
                        if (b > 0) poseStack.mulPose(Axis.YP.rotationDegrees(90.0F * b));
                        if (c > 0) poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F * c));
                        poseStack.scale(0.45F, 0.45F, 0.45F);

                        float alpha = (Mth.sin((ticks + a * 2 + b * 3 + c * 4) / 4.0F) * 0.1F + 0.2F) * be.startUp;
                        VertexConsumer vcGlow = bufferSource.getBuffer(RenderType.entityTranslucentCull(TEXTURE));
                        renderCubeGlow(poseStack, vcGlow, 15728880, alpha);

                        poseStack.popPose();
                    }
                }
            }
        }

        poseStack.popPose();
    }

    /**
     * Renders a cube with correct CCW winding order for each face.
     * UV follows TC4's ModelCube layout on a 64x64 texture.
     */
    private void renderCube(PoseStack poseStack, VertexConsumer vc, int light, int vOffset) {
        PoseStack.Pose pose = poseStack.last();
        float s = 0.5F;
        float t = 64.0F;

        // UV for each face region in the 64x64 texture
        // Top:    (16, v) → (32, v+16)
        // Bottom: (32, v) → (48, v+16)
        // Front:  (16, v+16) → (32, v+32)
        // Back:   (32, v+16) → (48, v+32)
        // Right:  (0, v+16) → (16, v+32)
        // Left:   (48, v+16) → (64, v+32)

        // Top face (+Y) — CCW from above: SW → SE → NE → NW
        addVert(pose, vc, -s, s,  s, 16/t, (vOffset+16)/t, light, 0, 1, 0);
        addVert(pose, vc,  s, s,  s, 32/t, (vOffset+16)/t, light, 0, 1, 0);
        addVert(pose, vc,  s, s, -s, 32/t, vOffset/t,      light, 0, 1, 0);
        addVert(pose, vc, -s, s, -s, 16/t, vOffset/t,      light, 0, 1, 0);

        // Bottom face (-Y) — CCW from below: NW → NE → SE → SW
        addVert(pose, vc, -s, -s, -s, 32/t, vOffset/t,      light, 0, -1, 0);
        addVert(pose, vc,  s, -s, -s, 48/t, vOffset/t,      light, 0, -1, 0);
        addVert(pose, vc,  s, -s,  s, 48/t, (vOffset+16)/t, light, 0, -1, 0);
        addVert(pose, vc, -s, -s,  s, 32/t, (vOffset+16)/t, light, 0, -1, 0);

        // Front face (+Z) — CCW from front: BL → BR → TR → TL
        addVert(pose, vc, -s, -s, s, 16/t, (vOffset+32)/t, light, 0, 0, 1);
        addVert(pose, vc,  s, -s, s, 32/t, (vOffset+32)/t, light, 0, 0, 1);
        addVert(pose, vc,  s,  s, s, 32/t, (vOffset+16)/t, light, 0, 0, 1);
        addVert(pose, vc, -s,  s, s, 16/t, (vOffset+16)/t, light, 0, 0, 1);

        // Back face (-Z) — CCW from back: BL → BR → TR → TL
        addVert(pose, vc,  s, -s, -s, 32/t, (vOffset+32)/t, light, 0, 0, -1);
        addVert(pose, vc, -s, -s, -s, 48/t, (vOffset+32)/t, light, 0, 0, -1);
        addVert(pose, vc, -s,  s, -s, 48/t, (vOffset+16)/t, light, 0, 0, -1);
        addVert(pose, vc,  s,  s, -s, 32/t, (vOffset+16)/t, light, 0, 0, -1);

        // Right face (+X) — CCW from right: BL → BR → TR → TL
        addVert(pose, vc, s, -s,  s,  0/t, (vOffset+32)/t, light, 1, 0, 0);
        addVert(pose, vc, s, -s, -s, 16/t, (vOffset+32)/t, light, 1, 0, 0);
        addVert(pose, vc, s,  s, -s, 16/t, (vOffset+16)/t, light, 1, 0, 0);
        addVert(pose, vc, s,  s,  s,  0/t, (vOffset+16)/t, light, 1, 0, 0);

        // Left face (-X) — CCW from left: BL → BR → TR → TL
        addVert(pose, vc, -s, -s, -s, 48/t, (vOffset+32)/t, light, -1, 0, 0);
        addVert(pose, vc, -s, -s,  s, 64/t, (vOffset+32)/t, light, -1, 0, 0);
        addVert(pose, vc, -s,  s,  s, 64/t, (vOffset+16)/t, light, -1, 0, 0);
        addVert(pose, vc, -s,  s, -s, 48/t, (vOffset+16)/t, light, -1, 0, 0);
    }

    private void renderCubeGlow(PoseStack poseStack, VertexConsumer vc, int light, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        float s = 0.5F;
        float t = 64.0F;
        int vOffset = 32;
        int r = (int)(0.8F * 255), g = (int)(0.1F * 255), b = (int)(1.0F * 255), a = (int)(alpha * 255);

        addVertCol(pose, vc, -s, s,  s, 16/t, (vOffset+16)/t, light, r, g, b, a, 0, 1, 0);
        addVertCol(pose, vc,  s, s,  s, 32/t, (vOffset+16)/t, light, r, g, b, a, 0, 1, 0);
        addVertCol(pose, vc,  s, s, -s, 32/t, vOffset/t,      light, r, g, b, a, 0, 1, 0);
        addVertCol(pose, vc, -s, s, -s, 16/t, vOffset/t,      light, r, g, b, a, 0, 1, 0);

        addVertCol(pose, vc, -s, -s, -s, 32/t, vOffset/t,      light, r, g, b, a, 0, -1, 0);
        addVertCol(pose, vc,  s, -s, -s, 48/t, vOffset/t,      light, r, g, b, a, 0, -1, 0);
        addVertCol(pose, vc,  s, -s,  s, 48/t, (vOffset+16)/t, light, r, g, b, a, 0, -1, 0);
        addVertCol(pose, vc, -s, -s,  s, 32/t, (vOffset+16)/t, light, r, g, b, a, 0, -1, 0);

        addVertCol(pose, vc, -s, -s, s, 16/t, (vOffset+32)/t, light, r, g, b, a, 0, 0, 1);
        addVertCol(pose, vc,  s, -s, s, 32/t, (vOffset+32)/t, light, r, g, b, a, 0, 0, 1);
        addVertCol(pose, vc,  s,  s, s, 32/t, (vOffset+16)/t, light, r, g, b, a, 0, 0, 1);
        addVertCol(pose, vc, -s,  s, s, 16/t, (vOffset+16)/t, light, r, g, b, a, 0, 0, 1);

        addVertCol(pose, vc,  s, -s, -s, 32/t, (vOffset+32)/t, light, r, g, b, a, 0, 0, -1);
        addVertCol(pose, vc, -s, -s, -s, 48/t, (vOffset+32)/t, light, r, g, b, a, 0, 0, -1);
        addVertCol(pose, vc, -s,  s, -s, 48/t, (vOffset+16)/t, light, r, g, b, a, 0, 0, -1);
        addVertCol(pose, vc,  s,  s, -s, 32/t, (vOffset+16)/t, light, r, g, b, a, 0, 0, -1);

        addVertCol(pose, vc, s, -s,  s,  0/t, (vOffset+32)/t, light, r, g, b, a, 1, 0, 0);
        addVertCol(pose, vc, s, -s, -s, 16/t, (vOffset+32)/t, light, r, g, b, a, 1, 0, 0);
        addVertCol(pose, vc, s,  s, -s, 16/t, (vOffset+16)/t, light, r, g, b, a, 1, 0, 0);
        addVertCol(pose, vc, s,  s,  s,  0/t, (vOffset+16)/t, light, r, g, b, a, 1, 0, 0);

        addVertCol(pose, vc, -s, -s, -s, 48/t, (vOffset+32)/t, light, r, g, b, a, -1, 0, 0);
        addVertCol(pose, vc, -s, -s,  s, 64/t, (vOffset+32)/t, light, r, g, b, a, -1, 0, 0);
        addVertCol(pose, vc, -s,  s,  s, 64/t, (vOffset+16)/t, light, r, g, b, a, -1, 0, 0);
        addVertCol(pose, vc, -s,  s, -s, 48/t, (vOffset+16)/t, light, r, g, b, a, -1, 0, 0);
    }

    private void addVert(PoseStack.Pose pose, VertexConsumer vc, float x, float y, float z,
                         float u, float v, int light, float nx, float ny, float nz) {
        vc.addVertex(pose, x, y, z)
          .setColor(255, 255, 255, 255)
          .setUv(u, v)
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(light)
          .setNormal(pose, nx, ny, nz);
    }

    private void addVertCol(PoseStack.Pose pose, VertexConsumer vc, float x, float y, float z,
                            float u, float v, int light, int r, int g, int b, int a,
                            float nx, float ny, float nz) {
        vc.addVertex(pose, x, y, z)
          .setColor(r, g, b, a)
          .setUv(u, v)
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(light)
          .setNormal(pose, nx, ny, nz);
    }
}
