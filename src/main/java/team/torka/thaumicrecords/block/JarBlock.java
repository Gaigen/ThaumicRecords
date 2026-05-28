package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;
import team.torka.thaumicrecords.block.entity.JarBlockEntity;
import team.torka.thaumicrecords.item.JarBlockItem;
import team.torka.thaumicrecords.registry.ItemRegistry;

import javax.annotation.Nullable;

public class JarBlock extends BaseEntityBlock {

    public static final MapCodec<JarBlock> CODEC = simpleCodec(JarBlock::new);

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide()) {
            if (stack.getItem() instanceof JarBlockItem item) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof JarBlockEntity jarBlockEntity) {
                    AspectList itemAspectList = item.getAspects(stack);
                    jarBlockEntity.setAspects(itemAspectList);
                }
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
//      if (!level.isClientSide()) { //if i uncomment this - block cloning to default state
        if (level.getBlockEntity(pos) instanceof JarBlockEntity jar) {
            ItemStack stack = new ItemStack(ItemRegistry.JAR.get());
            stack.setCount(1);
            JarBlockItem jarBlockItem = (JarBlockItem) ItemRegistry.JAR.get();
            jarBlockItem.setAspects(stack, jar.getAspects());
            return stack;
        } else {
            return super.getCloneItemStack(state, target, level, pos, player);
        }
//      }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (!(stack.getItem() instanceof IEssentiaContainerItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (stack.getItem() instanceof IEssentiaContainerItem itemContainer) {
                if (!itemContainer.isLiquid()) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }

                BlockEntity be = level.getBlockEntity(pos);

                if (be instanceof JarBlockEntity jarBlockEntity) {

                    int itemContainerAmount = itemContainer.storedAmount(stack);
                    ResourceLocation aspectResourceKey = itemContainer.getStoredAspectResource(stack);

                    AspectList itemContainerAspectList = itemContainer.getAspects(stack);
                    if (itemContainerAspectList.isEmpty() && !jarBlockEntity.getAspects().isEmpty()) {
                        if (!itemContainer.canBePartiallyPoured()) {
                            int storedAmount = jarBlockEntity.storedAmount();
                            ResourceLocation storedAspect = jarBlockEntity.getStoredAspectResource();
                            int amount = itemContainer.poursBy();
                            if (storedAmount < amount) {
                                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                            }
                            if (itemContainer.addAspect(player, stack, storedAspect, amount)) {
                                jarBlockEntity.wasPoured(storedAspect, amount);
                            }
                        } else {
                            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                        }
                    }
                    int spaceLeft = jarBlockEntity.capacity() - jarBlockEntity.storedAmount();

                    if (itemContainer.canBePartiallyPoured()) {
                        int pouringAmount = Math.min(Math.min(itemContainerAmount, itemContainer.poursBy()), spaceLeft); //that thing
                        if (jarBlockEntity.addAspect(aspectResourceKey, pouringAmount)) {
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
                            itemContainer.wasPoured(stack, player, pouringAmount);
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
