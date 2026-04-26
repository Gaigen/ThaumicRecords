package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.TableBlock;
import team.torka.thaumicrecords.block.entity.TableBlockEntity;
import team.torka.thaumicrecords.client.model.TableModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;

public class TableRenderer implements BlockEntityRenderer<TableBlockEntity> {
    private static final ResourceLocation TEXTURE = ThaumicRecords.createRl("textures/block/table.png");
    private final TableModel model;

    public TableRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new TableModel(context.bakeLayer(CustomModelLayer.TABLE));
    }

    @Override
    public void render(TableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float f = blockEntity.getBlockState().getValue(TableBlock.FACING).toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(f));
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, packedOverlay, -1);
        poseStack.popPose();
    }
}