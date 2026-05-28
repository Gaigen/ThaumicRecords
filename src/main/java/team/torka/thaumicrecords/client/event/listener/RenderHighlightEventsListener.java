package team.torka.thaumicrecords.client.event.listener;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.block.AspectRenderable;
import team.torka.thaumicrecords.api.helper.AspectHelper;
import team.torka.thaumicrecords.block.AuraNodeBlock;
import team.torka.thaumicrecords.client.event.RenderThaumicVisionEvent;
import team.torka.thaumicrecords.registry.AspectRegistry;

import java.awt.Color;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
public class RenderHighlightEventsListener {
    @SubscribeEvent
    public static void onBlockEvent(RenderHighlightEvent.Block event) {
        BlockPos blockPos = event.getTarget().getBlockPos();
        BlockState state = event.getCamera().getEntity().level().getBlockState(blockPos);
        if (state.getBlock() instanceof AuraNodeBlock) {
            event.setCanceled(true);
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = player.level();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof AspectRenderable renderable) {
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            Camera camera = event.getCamera();
            var renderThaumicVisionEvent = new RenderThaumicVisionEvent.Aspect(player, player.level());
            NeoForge.EVENT_BUS.post(renderThaumicVisionEvent);
            if (renderThaumicVisionEvent.isVisible()) {
                renderAspects(player, event.getTarget(), renderable, poseStack, bufferSource, camera);
            }
        }
    }

    private static void renderAspects(Player player, BlockHitResult hitResult, AspectRenderable renderable, PoseStack poseStack,
                                      MultiBufferSource bufferSource, Camera camera) {
        BlockPos pos = hitResult.getBlockPos();
        boolean spaceAbove = player.level().isEmptyBlock(pos.above());
        Direction renderDir = spaceAbove ? Direction.UP : hitResult.getDirection();
        drawTagsOnContainer(player, poseStack, bufferSource, camera, pos.getX(), pos.getY() + (spaceAbove ? 0.4F : 0.0F) + renderable.getRenderYOffset(),
                pos.getZ(), renderable.getAspectRendered(), 15728880, renderDir);
    }

    public static void drawTagsOnContainer(Player player, PoseStack poseStack, MultiBufferSource bufferSource, Camera camera, double x, double y, double z,
                                           AspectList aspectList, int bright, Direction dir) {
        float tagscale = 0.3F;
        if (aspectList == null || aspectList.isEmpty()) {
            return;
        }
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        int rowsize = 5;
        int current = 0;
        float shifty = 0.0F;
        int left = aspectList.size();

        for (var aspectRl : aspectList.keySet()) {
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(aspectRl);
            Integer aspectColor = Optional.ofNullable(aspect).map(Aspect::getARGBColor).orElse(Color.GRAY.getRGB() | 0xFF000000);
            ResourceLocation aspectTex = Optional.ofNullable(aspect).map(Aspect::getImage).orElse(Aspect.UNKNOWN_TEX);
            int div = Math.min(left, rowsize);
            if (current >= rowsize) {
                current = 0;
                shifty -= tagscale * 1.05F;
                left -= rowsize;
                if (left < rowsize) {
                    div = left % rowsize;
                }
            }

            float shift = ((float) current - (float) div / 2.0F + 0.5F) * tagscale * 4.0F;
            shift *= tagscale;
            Color color = new Color(aspectColor);

            poseStack.pushPose();
            double renderX = x - camX + 0.5D + (tagscale * 2.0F * (float) dir.getStepX());
            double renderY = y - camY - shifty + 0.5D + (tagscale * 2.0F * (float) dir.getStepY());
            double renderZ = z - camZ + 0.5D + (tagscale * 2.0F * (float) dir.getStepZ());
            poseStack.translate(renderX, renderY, renderZ);
            float xd = (float) (camX - (x + 0.5D));
            float zd = (float) (camZ - (z + 0.5D));
            float rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotYaw + 180.0F));
            poseStack.translate(shift, 0.0F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.scale(tagscale, tagscale, tagscale);


            ResourceLocation texture = Aspect.UNKNOWN_TEX;
            if (AspectHelper.isAspectDiscovered(player, aspectRl)) {
                texture = aspectTex;
            }

            VertexConsumer iconConsumer = bufferSource.getBuffer(getOverlayRenderType(texture));
            renderCenteredQuad(poseStack, iconConsumer, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.75F, bright);

            if (aspectList.getOrZero(aspectRl) >= 0) {
                String am = String.valueOf(aspectList.getOrZero(aspectRl));
                Font font = Minecraft.getInstance().font;
                int sw = font.width(am);

                poseStack.pushPose();
                poseStack.scale(0.04F, 0.04F, 0.04F);
                poseStack.translate(0.0F, 6.0F, 0.0F);
                font.drawInBatch(am, 14.0F - sw, 1.0F, 0x111111, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, bright);
                poseStack.translate(0.0F, 0.0F, -0.5F);
                font.drawInBatch(am, 13.0F - sw, 0.0F, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, bright);
                poseStack.popPose();
            }

            poseStack.popPose();
            current++;
        }
    }

    private static void renderCenteredQuad(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, int packedLight) {
        Matrix4f matrix = poseStack.last().pose();
        consumer.addVertex(matrix, -0.5F, -0.5F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setLight(packedLight);
        consumer.addVertex(matrix, 0.5F, -0.5F, 0.0F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setLight(packedLight);
        consumer.addVertex(matrix, 0.5F, 0.5F, 0.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setLight(packedLight);
        consumer.addVertex(matrix, -0.5F, 0.5F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setLight(packedLight);
    }

    private static RenderType getOverlayRenderType(ResourceLocation texture) {
        return RenderType.create("aspect_overlay_icon", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .setCullState(RenderStateShard.NO_CULL)
                        .createCompositeState(false));
    }
}
