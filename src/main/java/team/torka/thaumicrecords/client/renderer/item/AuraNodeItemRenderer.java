package team.torka.thaumicrecords.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;
import team.torka.thaumicrecords.client.renderer.blockentity.AuraNodeRenderer;
import team.torka.thaumicrecords.registry.AspectRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class AuraNodeItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final AuraNodeItemRenderer INSTANCE = new AuraNodeItemRenderer();
    private static final AspectList DEFAULT_ASPECT_LIST = AspectList.empty().add(AspectRegistry.AER.getId(), 40).add(AspectRegistry.IGNIS.getId(), 40).add(
            AspectRegistry.TERRA.getId(), 40).add(AspectRegistry.AQUA.getId(), 40);

    public AuraNodeItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        AspectList currentAspect = getAspectsFromStack(stack);
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        renderItemAuraNode(currentAspect, poseStack, buffer);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        renderItemAuraNode(currentAspect, poseStack, buffer);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        renderItemAuraNode(currentAspect, poseStack, buffer);
        poseStack.popPose();
    }

    private void renderItemAuraNode(AspectList currentAspect, PoseStack poseStack, MultiBufferSource bufferSource) {
        long nt = System.nanoTime();
        float ticks = (float) Util.getMillis() / 50.0F;

        int totalAmount = currentAspect.values().stream().mapToInt(Integer::intValue).sum();
        float averageAmount = currentAspect.isEmpty() ? 0 : (float) totalAmount / currentAspect.size();
        float countFactor = Math.max(1.0F, (float) currentAspect.size() / 2.0F);
        float baseAlpha = 0.5F;
        int count = 0;

        for (var entry : currentAspect.entrySet()) {
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(entry.getKey());
            if (Objects.isNull(aspect)) {
                count++;
                continue;
            }
            float size = AuraNodeRenderer.calculateRenderSize(ticks, count, entry.getValue(), 1.0F);
            float angle = AuraNodeRenderer.calculateRenderAngle(nt, count);
            int color = aspect.getARGBColor();
            int a = (color >> 24) & 0xFF;
            if (a == 0) {
                a = 255;
            }
            int finalAlpha = (int) ((a * baseAlpha) / countFactor);
            int finalArgb = (finalAlpha << 24) | (color & 0xFFFFFF);

            renderLayerInternal(poseStack, bufferSource, size, angle, finalArgb, CustomRenderType.additiveTransparency(AuraNodeRenderer.AURA), count);
            count++;
        }

        float coreAngle = AuraNodeRenderer.calculateRenderAngle(nt, 0);
        float coreSize = AuraNodeRenderer.calculateCoreSize(averageAmount, 1.0F);
        renderLayerInternal(poseStack, bufferSource, coreSize, coreAngle, 0xAAFFFFFF, CustomRenderType.additiveTransparency(AuraNodeRenderer.NORMAL_CORE), 0);
    }

    private void renderLayerInternal(PoseStack poseStack, MultiBufferSource bufferSource, float size, float angle, int argb,
                                     net.minecraft.client.renderer.RenderType renderType, int indexOffset) {
        poseStack.pushPose();
        if (angle != 0) {
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(angle));
        }
        var buffer = bufferSource.getBuffer(renderType);
        var matrix = poseStack.last().pose();
        float time = (float) net.minecraft.Util.getMillis() / 100.0F;
        int currentFrame = (int) (time * 2.0F + indexOffset) % 32;
        float minU = currentFrame * (1.0F / 32.0F);
        float maxU = (currentFrame + 1) * (1.0F / 32.0F);
        addVertex(buffer, matrix, -size, -size, 0, argb, minU, 1.0F);
        addVertex(buffer, matrix, size, -size, 0, argb, maxU, 1.0F);
        addVertex(buffer, matrix, size, size, 0, argb, maxU, 0.0F);
        addVertex(buffer, matrix, -size, size, 0, argb, minU, 0.0F);
        poseStack.popPose();
    }

    private void addVertex(com.mojang.blaze3d.vertex.VertexConsumer buffer, org.joml.Matrix4f matrix, float x, float y, float z, int argb, float u, float v) {
        buffer.addVertex(matrix, x, y, z).setColor((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF).setUv(u, v).setOverlay(
                OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 0.0F, 1.0F);
    }

    private AspectList getAspectsFromStack(ItemStack stack) {
        return DEFAULT_ASPECT_LIST;
    }


    public IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            @NotNull
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return AuraNodeItemRenderer.this;
            }
        };
    }
}