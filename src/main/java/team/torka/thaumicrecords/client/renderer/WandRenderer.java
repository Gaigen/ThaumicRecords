package team.torka.thaumicrecords.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;
import team.torka.thaumicrecords.client.model.WandModel;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;
import team.torka.thaumicrecords.registry.WandRodRegistry;

import java.util.Optional;

public class WandRenderer extends BlockEntityWithoutLevelRenderer {
    public static final WandRenderer INSTANCE = new WandRenderer();

    private static final WandModel model = new WandModel();

    public WandRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        WandItemComponent data = stack.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (data == null) return;
        WandRod wandRod = Optional.ofNullable(WandRodRegistry.WAND_ROD_REGISTRY.get(ResourceLocation.parse(data.getRodKey()))).orElse
                (WandRodRegistry.WAND_ROD_REGISTRY.get(ResourceLocation.fromNamespaceAndPath(
                ThaumicRecords.MOD_ID,"wood")));
        WandCap wandCap = Optional.ofNullable(WandCapRegistry.WAND_CAP_REGISTRY.get(ResourceLocation.parse(data.getCapKey()))).orElse
                (WandCapRegistry.WAND_CAP_REGISTRY.get(ResourceLocation.fromNamespaceAndPath(
                        ThaumicRecords.MOD_ID,"iron")));

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // 翻转 Y 轴

        ResourceLocation rodTex = wandRod.getModelTexture();
        VertexConsumer rodBuf = buffer.getBuffer(RenderType.entityCutout(rodTex));

        poseStack.pushPose();
        model.rod.render(poseStack, rodBuf, packedLight, packedOverlay);
        poseStack.popPose();

        ResourceLocation capTex = wandCap.getModelTexture();
        VertexConsumer capBuf = buffer.getBuffer(RenderType.entityTranslucent(capTex));

        poseStack.pushPose();
        poseStack.scale(1.2f, 1.0f, 1.2f);
        model.cap.render(poseStack, capBuf, LightTexture.FULL_BRIGHT, packedOverlay );

        poseStack.pushPose();
        model.capBottom.render(poseStack, capBuf, LightTexture.FULL_BRIGHT, packedOverlay );
        poseStack.popPose();

        poseStack.popPose();
    }
}