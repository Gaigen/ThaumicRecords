package team.torka.thaumicrecords.client.renderer.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class SpecialItemRenderer extends EntityRenderer<ItemEntity> {
    private final ItemRenderer itemRenderer;

    public SpecialItemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F;
    }

    @Override
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float ageInTicks = (float) entity.tickCount + partialTicks;
        float bobOffset = Mth.sin(ageInTicks / 10.0F + entity.bobOffs) * 0.1F + 0.1F;
        poseStack.translate(0.0D, bobOffset + 0.15F, 0.0D);

        VertexConsumer buffer = bufferSource.getBuffer(RAY);
        int beamCount = Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FAST ? 5 : 10;
        float timeFactor = ageInTicks / 500.0F;

        Random random = new Random(245L);

        for (int i = 0; i < beamCount; i++) {
            poseStack.pushPose();
            poseStack.translate(0, 0.15, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + timeFactor * 360.0F));


            float fa = random.nextFloat() * 20.0F + 5.0F;
            float f4 = random.nextFloat() * 2.0F + 1.0F;

            float ageScale = Math.min((float) entity.tickCount + partialTicks, 10.0F) / 10.0F;
            fa /= 30.0F / ageScale;
            f4 /= 30.0F / ageScale;

            Matrix4f matrix = poseStack.last().pose();

            float cx = 0, cy = 0, cz = 0;
            float p1x = -0.866F * f4, p1y = fa, p1z = -0.5F * f4;
            float p2x = 0.866F * f4, p2y = fa, p2z = -0.5F * f4;
            float p3x = 0.0F, p3y = fa, p3z = f4;


            addVertex(buffer, matrix, cx, cy, cz, 255, 255, 255, 255);
            addVertex(buffer, matrix, p1x, p1y, p1z, 255, 0, 255, 0);
            addVertex(buffer, matrix, p2x, p2y, p2z, 255, 0, 255, 0);

            addVertex(buffer, matrix, cx, cy, cz, 255, 255, 255, 255);
            addVertex(buffer, matrix, p2x, p2y, p2z, 255, 0, 255, 0);
            addVertex(buffer, matrix, p3x, p3y, p3z, 255, 0, 255, 0);

            addVertex(buffer, matrix, cx, cy, cz, 255, 255, 255, 255);
            addVertex(buffer, matrix, p3x, p3y, p3z, 255, 0, 255, 0);
            addVertex(buffer, matrix, p1x, p1y, p1z, 255, 0, 255, 0);

            poseStack.popPose();
        }
        if (bufferSource instanceof MultiBufferSource.BufferSource impl) {
            impl.endBatch(RenderType.lightning());
        }
        ItemStack itemStack = entity.getItem();
        if (!itemStack.isEmpty()) {
            poseStack.pushPose();
            float rotation = (ageInTicks / 20.0F) * (180F / (float) Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            BakedModel bakedModel = this.itemRenderer.getModel(itemStack, entity.level(), null, entity.getId());
            this.itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedModel);
            poseStack.popPose();
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, int r, int g, int b, int a) {
        builder.addVertex(matrix, x, y, z).setColor(r, g, b, a);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public ResourceLocation getTextureLocation(ItemEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    private static final RenderType RAY = RenderType.create("ray", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false));
}