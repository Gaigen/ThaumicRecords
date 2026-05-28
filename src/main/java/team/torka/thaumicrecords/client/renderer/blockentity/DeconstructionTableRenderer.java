package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.entity.DeconstructionTableBlockEntity;
import team.torka.thaumicrecords.client.model.ArcaneWorkbenchModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;
import team.torka.thaumicrecords.registry.ItemRegistry;

public class DeconstructionTableRenderer implements BlockEntityRenderer<DeconstructionTableBlockEntity> {
    private static final ResourceLocation TEXTURE = ThaumicRecords.createRl("textures/block/deconstruction_table_model.png");

    private final ArcaneWorkbenchModel model;
    private final ItemRenderer itemRenderer;

    public DeconstructionTableRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ArcaneWorkbenchModel(context.bakeLayer(CustomModelLayer.ARCANE_WORKBENCH));
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull DeconstructionTableBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        poseStack.popPose();
        // Thaumometer — laying flat on table, centered
        ItemStack thaumometer = new ItemStack(ItemRegistry.THAUMOMETER.get());
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.05D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(0.8F, 0.8F, 0.8F);
        this.itemRenderer.renderStatic(thaumometer, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, be.getLevel(), 0);
        poseStack.popPose();
    }
}
