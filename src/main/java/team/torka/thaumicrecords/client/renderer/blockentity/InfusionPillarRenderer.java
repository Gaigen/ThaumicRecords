package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.entity.InfusionPillarBlockEntity;

public class InfusionPillarRenderer implements BlockEntityRenderer<InfusionPillarBlockEntity> {

    private static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(ThaumicRecords.createRl("block/pillar_obj"));

    public InfusionPillarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull InfusionPillarBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();

        poseStack.pushPose();

        // center on block, rotate OBJ upright
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));

        // Rotate based on facing
        switch (be.getFacing()) {
            case NORTH -> poseStack.mulPose(Axis.ZP.rotationDegrees(270.0F));
            case SOUTH -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            // WEST = default (no rotation)
        }

        BakedModel objModel = mc.getModelManager().getModel(MODEL);
        var vc = bufferSource.getBuffer(RenderType.cutout());
        mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), vc, mc.level != null ? be.getBlockState() : null, objModel, 1.0F, 1.0F, 1.0F,
                packedLight, packedOverlay);

        poseStack.popPose();
    }
}
