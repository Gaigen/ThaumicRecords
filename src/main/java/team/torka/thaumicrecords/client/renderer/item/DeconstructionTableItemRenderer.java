package team.torka.thaumicrecords.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.block.entity.DeconstructionTableBlockEntity;
import team.torka.thaumicrecords.registry.BlockRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class DeconstructionTableItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final DeconstructionTableItemRenderer INSTANCE = new DeconstructionTableItemRenderer();
    private final DeconstructionTableBlockEntity fakeBE = new DeconstructionTableBlockEntity(BlockPos.ZERO, BlockRegistry.DECONSTRUCTION_TABLE.get().defaultBlockState());

    public DeconstructionTableItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int combinedLight,
                             int combinedOverlay) {
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(fakeBE, poseStack, buffer, combinedLight, combinedOverlay);
    }

    public IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            @NotNull
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return DeconstructionTableItemRenderer.this;
            }
        };
    }
}
