package team.torka.thaumicrecords.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import team.torka.thaumicrecords.ThaumicRecords;

import javax.annotation.ParametersAreNonnullByDefault;

public class ThaumometerItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static ThaumometerItemRenderer INSTANCE = new ThaumometerItemRenderer();

    private static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(ThaumicRecords.createRl("item/thaumometer_obj"));
    private static final ResourceLocation GLASS_TEXTURE = ThaumicRecords.createRl("textures/item/thaumometer_glass.png");

    public ThaumometerItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                             int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        BakedModel objModel = mc.getModelManager().getModel(MODEL);
        poseStack.pushPose();
        mc.getItemRenderer().render(stack, displayContext, false, poseStack, bufferSource, packedLight, packedOverlay, objModel);
        float ticks = mc.level != null ? (float) mc.level.getGameTime() : 0.0F;
        float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);
        float renderTime = ticks + partialTicks;
        float alpha = Mth.sin(renderTime / 8.0F) * 0.1F + 0.75F;
        poseStack.pushPose();
        poseStack.translate(-0.5F, -0.475F, -0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(30));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        VertexConsumer screenConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(GLASS_TEXTURE));
        drawScreenQuad(poseStack, screenConsumer, 1.3F, alpha, packedOverlay);
        poseStack.popPose();
        poseStack.popPose();
    }

    private void drawScreenQuad(PoseStack poseStack, VertexConsumer consumer, float size, float alpha, int packedOverlay) {
        Matrix4f matrix = poseStack.last().pose();
        int maxLight = 15728880;
        consumer.addVertex(matrix, -size, -size, 0.0F)
                .setColor(1.0F, 1.0F, 1.0F, alpha)
                .setUv(0.0F, 0.0F)
                .setOverlay(packedOverlay)
                .setLight(maxLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, -size, size, 0.0F)
                .setColor(1.0F, 1.0F, 1.0F, alpha)
                .setUv(0.0F, 1.0F)
                .setOverlay(packedOverlay)
                .setLight(maxLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, size, size, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(1.0F, 1.0F).setOverlay(packedOverlay).setLight(maxLight).setNormal(
                0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, size, -size, 0.0F)
                .setColor(1.0F, 1.0F, 1.0F, alpha)
                .setUv(1.0F, 0.0F)
                .setOverlay(packedOverlay)
                .setLight(maxLight)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    public IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            @NotNull
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ThaumometerItemRenderer.this;
            }
        };
    }
}
