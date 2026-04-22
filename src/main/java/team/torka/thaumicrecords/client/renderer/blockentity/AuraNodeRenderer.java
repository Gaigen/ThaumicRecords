package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;
import team.torka.thaumicrecords.registry.AspectRegistry;

import java.util.Objects;

public class AuraNodeRenderer implements BlockEntityRenderer<AuraNodeBlockEntity> {


    public static final ResourceLocation AURA = ThaumicRecords.createRl("textures/misc/node/aura.png");

    public AuraNodeRenderer(BlockEntityRendererProvider.Context context) {
    }


    @Override
    public void render(@NotNull AuraNodeBlockEntity blockEntity, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
        NodeType nodeType = blockEntity.getNodeType().isBound() ? blockEntity.getNodeType().value() : null;
        if (nodeType == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

        renderFullAuraNode(blockEntity, partialTicks, poseStack, bufferSource, nodeType);

        poseStack.popPose();
    }

    private void renderFullAuraNode(@NotNull AuraNodeBlockEntity blockEntity, float partialTicks, @NotNull PoseStack poseStack,
                                    @NotNull MultiBufferSource bufferSource, NodeType nodeType) {
        if (Objects.isNull(Minecraft.getInstance().player)) {
            return;
        }
        AspectList currentAspect = blockEntity.getCurrentAspect();
        long nt = System.nanoTime();
        float ticks = Minecraft.getInstance().player.tickCount + partialTicks;
        int count = 0;
        int totalAmount = currentAspect.values().stream().mapToInt(Integer::intValue).sum();
        float averageAmount = currentAspect.isEmpty() ? 0 : (float) totalAmount / currentAspect.size();
        float angle = 0F;
        float countFactor = Math.max(1.0F, (float) currentAspect.size() / 2.0F);
        float baseAlpha = 0.7F;
        for (var entry : currentAspect.entrySet()) {
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(entry.getKey());
            if (Objects.isNull(aspect)) {
                count++;
                continue;
            }
            float size = calculateRenderSize(ticks, count, entry.getValue(), 1.0F);
            angle = calculateRenderAngle(nt, count);
            int color = aspect.getARGBColor();
            int a = (color >> 24) & 0xFF;
            if (a == 0) a = 255;
            int finalAlpha = (int) ((a * baseAlpha) / countFactor);
            int finalArgb = (finalAlpha << 24) | (color & 0xFFFFFF);

            renderLayer(poseStack, bufferSource, size, angle, finalArgb, CustomRenderType.additiveTransparency(AURA), count);
            count++;
        }
        renderLayer(poseStack, bufferSource, calculateCoreSize(averageAmount, 1.0F), angle, 0xFFFFFFFF, nodeType.getRenderType(), 0);
    }

    private void renderLayer(PoseStack poseStack, MultiBufferSource bufferSource, float size, float angle, int argb, RenderType renderType, int indexOffset) {
        poseStack.pushPose();
        if (angle != 0) {
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(angle));
        }
        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        Matrix4f matrix = poseStack.last().pose();
        float time = (float) net.minecraft.Util.getMillis() / 100.0F;
        int totalFrames = 32;
        int currentFrame = (int) (time * 2.0F + indexOffset) % 32;
        float frameWidth = 1.0F / totalFrames;
        float minU = currentFrame * frameWidth;
        float maxU = (currentFrame + 1) * frameWidth;
        int light = 15728880;

        addVertex(buffer, matrix, -size, -size, 0.0F, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF, minU, 1.0F, light);
        addVertex(buffer, matrix, size, -size, 0.0F, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF, maxU, 1.0F, light);
        addVertex(buffer, matrix, size, size, 0.0F, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF, maxU, 0.0F, light);
        addVertex(buffer, matrix, -size, size, 0.0F, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF, minU, 0.0F, light);

        poseStack.popPose();
    }

    public static float calculateRenderSize(float ticks, int count, float amount, float baseSize) {
        float bscale = 0.25F;
        float wave = (float) Math.sin(ticks / (14.0F - ((float) count) % 13.0)) * bscale + bscale * 2.0F;
        float scale = 0.2F + wave * (amount / 50.0F);
        return scale * baseSize;
    }

    public static float calculateRenderAngle(long nanoTime, int count) {
        long time = nanoTime / 4000000L;
        float rad = (float) (Math.PI * 2.0);
        float angleRad = (float) (time % (5000 + 500L * count)) / (5000.0F + (float) (500 * count)) * rad;
        return angleRad * (180.0F / (float) Math.PI);
    }

    public static float calculateCoreSize(float averageAmount, float baseSize) {
        float scale = 0.1F + averageAmount / 150.0F;
        return scale * baseSize;
    }

    private void addVertex(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, int r, int g, int b, int a, float u, float v, int light) {
        buffer.addVertex(matrix, x, y, z).setColor(r, g, b, a).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
    }

}