package team.torka.thaumicrecords.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.block.entity.ArcanePedestalBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestalBlockEntity> {

    private final ItemRenderer itemRenderer;

    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(ArcanePedestalBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        ItemStack stack = blockEntity.getItem();
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();

        float hoverOffset = 0.0F;
        float rotationAngle = 0.0F;

        if (Objects.nonNull(blockEntity.getLevel())) {
            float gameTicks = blockEntity.getLevel().getGameTime() + partialTick;
            hoverOffset = Mth.sin(gameTicks / 16.0F) * 0.05F;
            rotationAngle = gameTicks * 1.5F;
        }
        poseStack.translate(0.5, 1.12 + hoverOffset, 0.5);
        if (rotationAngle != 0.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        }
        BakedModel bakedModel = this.itemRenderer.getModel(stack, blockEntity.getLevel(), null, 0);
        this.itemRenderer.render(stack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight, packedOverlay, bakedModel);

        poseStack.popPose();
    }
}
