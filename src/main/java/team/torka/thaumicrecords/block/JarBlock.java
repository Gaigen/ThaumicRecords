package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerEntity;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;
import team.torka.thaumicrecords.block.entity.JarBlockEntity;

import javax.annotation.Nullable;

public class JarBlock extends BaseEntityBlock {

    public static final MapCodec<JarBlock> CODEC = simpleCodec(JarBlock::new);

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (stack.getItem() instanceof IEssentiaContainerItem itemContainer) {
            if (!itemContainer.isLiquid()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IEssentiaContainerEntity jarBlockEntity) {
                    AspectList aspectList = itemContainer.getAspects(stack);
                    ResourceLocation aspectResourceKey = aspectList.firstEntry().getKey();
                    int itemContainerAmount = aspectList.get(aspectList.firstEntry().getKey());
                    if (itemContainer.canBePartiallyPoured()) {
                        int pouringAmount = Math.min(itemContainerAmount, itemContainer.poursBy()); //that thing
                        if (jarBlockEntity.addAspect(aspectResourceKey, pouringAmount)) {
//                            aspectList.put(aspectResourceKey, itemContainerAmount - pouringAmount);
                            itemContainer.wasPoured(stack, player, pouringAmount);
                            return ItemInteractionResult.SUCCESS;
                        } else {
                            return ItemInteractionResult.FAIL;
                        }
                    } else {
                        if (itemContainer.poursBy() != itemContainerAmount) {
                            return ItemInteractionResult.FAIL;
                        }
                        int pouringAmount = itemContainer.poursBy();
                        if (jarBlockEntity.addAspect(aspectResourceKey, pouringAmount)) {
//                            aspectList.put(aspectResourceKey, itemContainerAmount - pouringAmount);
                            itemContainer.onEmpty(stack, player);
                            return ItemInteractionResult.SUCCESS;
                        } else {
                            return ItemInteractionResult.FAIL;
                        }
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /*
     * Minecraft voxel coordinates are 0-16
     */
    private static final VoxelShape SHAPE = Shapes.or(

            // main body
            box(3, 0, 3, 13, 12, 13),
            // lid
            box(5, 12, 5, 11, 14, 11));

    public JarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new JarBlockEntity(pos, state);
    }


}
