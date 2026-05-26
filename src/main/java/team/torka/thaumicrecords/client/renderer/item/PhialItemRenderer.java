package team.torka.thaumicrecords.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;

public class PhialItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final PhialItemRenderer INSTANCE = new PhialItemRenderer();


    private BakedModel solidModel;
    private BakedModel animatedModel;


    public PhialItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
//        ModelManager mm = Minecraft.getInstance().getModelManager();
//        solidModel = mm.getModel(ModelResourceLocation.standalone(ThaumicRecords.createRl("item/phial_empty")));
//        animatedModel = mm.getModel(ModelResourceLocation.standalone(ThaumicRecords.createRl("item/phial_filled")));

    }

    private void loadModels() {
        if (solidModel == null) {
            ModelManager mm = Minecraft.getInstance().getModelManager();

            ResourceLocation solidLoc = ThaumicRecords.createRl("item/phial_empty");
            ResourceLocation animatedLoc = ThaumicRecords.createRl("item/phial_filled");

            solidModel = mm.getModel(ModelResourceLocation.standalone(solidLoc));
            animatedModel = mm.getModel(ModelResourceLocation.standalone(animatedLoc));
        }
    }

    private void applyTransformationsFromModel(PoseStack poseStack, ItemDisplayContext context, BakedModel model) {
        model.applyTransform(context, poseStack, false);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        loadModels();

        boolean isEmpty = true;
        if (stack.getItem() instanceof IEssentiaContainerItem item) {
            isEmpty = item.getAspects(stack).isEmpty();
        }

        poseStack.pushPose();
        applyTransformationsFromModel(poseStack, context, solidModel);

        // 1. Непрозрачный слой – всегда
        if (isEmpty) {
            Minecraft.getInstance().getItemRenderer().renderModelLists(solidModel, stack, packedLight, packedOverlay, poseStack,
                    buffer.getBuffer(RenderType.cutout()));
        }

        // 3. Анимированный слой – ТОЛЬКО если НЕ пустой
        if (!isEmpty) {
            Minecraft.getInstance().getItemRenderer().renderModelLists(animatedModel, stack, packedLight, packedOverlay, poseStack,
                    buffer.getBuffer(RenderType.cutout()));
        }

        poseStack.popPose();
    }

    public IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            @NotNull
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return PhialItemRenderer.this;
            }
        };
    }
}
