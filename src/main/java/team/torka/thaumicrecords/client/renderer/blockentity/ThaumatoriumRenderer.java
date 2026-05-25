package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.block.entity.ThaumatoriumBlockEntity;

public class ThaumatoriumRenderer implements BlockEntityRenderer<ThaumatoriumBlockEntity> {

    private static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
            ThaumicRecords.createRl("block/thaumatorium_obj"));

    private final ItemRenderer itemRenderer;

    public ThaumatoriumRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull ThaumatoriumBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();

        poseStack.pushPose();

        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));

        switch (be.getFacing()) {
            case NORTH -> poseStack.mulPose(Axis.ZP.rotationDegrees(270.0F));
            case SOUTH -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            case EAST  -> poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            // WEST = default (no rotation)
        }

        BakedModel objModel = mc.getModelManager().getModel(MODEL);
        var vc = bufferSource.getBuffer(RenderType.cutout());
        mc.getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), vc, mc.level != null ? be.getBlockState() : null, objModel,
                1.0F, 1.0F, 1.0F, packedLight, packedOverlay);

        poseStack.popPose();

        if (be.getRecipeHashSize() > 0) {
            int stackIdx = (int) ((mc.level != null ? mc.level.getGameTime() : 0) / 40
                    % Math.max(1, be.getRecipeHashSize()));
            ItemStack output = be.getOutputForCycle(stackIdx);
            if (!output.isEmpty()) {
                poseStack.pushPose();
                float fx = be.getFacing().getStepX() / 1.99F;
                float fz = be.getFacing().getStepZ() / 1.99F;
                poseStack.translate(0.5D + fx, 1.325D, 0.5D + fz);

                switch (be.getFacing()) {
                    case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    case EAST  -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                    case WEST  -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
                    // SOUTH = default (no rotation)
                }

                poseStack.scale(0.75F, 0.75F, 0.75F);
                this.itemRenderer.renderStatic(output, ItemDisplayContext.GROUND,
                        packedLight, packedOverlay, poseStack, bufferSource, be.getLevel(), 0);
                poseStack.popPose();
            }
        }
    }
}
