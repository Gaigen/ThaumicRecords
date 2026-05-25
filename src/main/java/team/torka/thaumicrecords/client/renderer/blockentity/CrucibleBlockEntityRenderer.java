package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import team.torka.thaumicrecords.block.entity.CrucibleBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

public class CrucibleBlockEntityRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {

    public CrucibleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(CrucibleBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {

        if (!blockEntity.hasFluid()) return;

        float fluidHeight = blockEntity.getFluidHeight();

        TextureAtlasSprite waterSprite = Minecraft.getInstance()
                .getTextureAtlas(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)
                .apply(ResourceLocation.withDefaultNamespace("block/water_still"));

        IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(Fluids.WATER.defaultFluidState());
        int tintColor = fluidExt.getTintColor();
        int alpha = (tintColor >> 24) & 0xFF;
        float waterR = ((tintColor >> 16) & 0xFF) / 255f;
        float waterG = ((tintColor >> 8) & 0xFF) / 255f;
        float waterB = (tintColor & 0xFF) / 255f;
        float waterA = alpha == 0 ? 1.0f : alpha / 255f;

        float recolor = blockEntity.getRecolor();
        float r = waterR;
        float g = waterG * Math.max(0.0f, 1.0f - recolor / 3.0f);
        float b = waterB * Math.max(0.0f, 1.0f - recolor);
        float a = waterA * Math.max(0.0f, 1.0f - recolor / 2.0f);

        float minX = 2f / 16f;
        float maxX = 14f / 16f;
        float minZ = 2f / 16f;
        float maxZ = 14f / 16f;

        float u0 = waterSprite.getU0();
        float u1 = waterSprite.getU1();
        float v0 = waterSprite.getV0();
        float v1 = waterSprite.getV1();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());

        poseStack.pushPose();

        addVertex(consumer, poseStack, minX, fluidHeight, minZ, u0, v0, r, g, b, a, packedLight);
        addVertex(consumer, poseStack, minX, fluidHeight, maxZ, u0, v1, r, g, b, a, packedLight);
        addVertex(consumer, poseStack, maxX, fluidHeight, maxZ, u1, v1, r, g, b, a, packedLight);
        addVertex(consumer, poseStack, maxX, fluidHeight, minZ, u1, v0, r, g, b, a, packedLight);

        addVertex(consumer, poseStack, maxX, fluidHeight, minZ, u1, v0, r, g, b, a, packedLight);
        addVertex(consumer, poseStack, maxX, fluidHeight, maxZ, u1, v1, r, g, b, a, packedLight);
        addVertex(consumer, poseStack, minX, fluidHeight, maxZ, u0, v1, r, g, b, a, packedLight);
        addVertex(consumer, poseStack, minX, fluidHeight, minZ, u0, v0, r, g, b, a, packedLight);

        poseStack.popPose();
    }

    private void addVertex(VertexConsumer consumer, PoseStack poseStack,
                           float x, float y, float z,
                           float u, float v,
                           float r, float g, float b, float a,
                           int packedLight) {
        consumer.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setLight(packedLight)
                .setNormal(0, 1, 0);
    }
}
