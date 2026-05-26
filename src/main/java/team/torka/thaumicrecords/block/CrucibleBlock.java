package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.block.entity.CrucibleBlockEntity;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class CrucibleBlock extends BaseEntityBlock {

    public static final MapCodec<CrucibleBlock> CODEC = simpleCodec(CrucibleBlock::new);

    private int delay = 0;

    private static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 4, 0, 2, 16, 16), Block.box(14, 4, 0, 16, 16, 16),
            Block.box(2, 4, 0, 14, 16, 2), Block.box(2, 4, 14, 14, 16, 16));

    public CrucibleBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return createTickerHelper(blockEntityType, BlockEntityRegistry.CRUCIBLE.get(), CrucibleBlockEntity::clientTick);
        }
        return createTickerHelper(blockEntityType, BlockEntityRegistry.CRUCIBLE.get(), CrucibleBlockEntity::serverTick);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int type) {
        super.triggerEvent(state, level, pos, id, type);
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, type);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     *
     */
    @Override
    @ParametersAreNonnullByDefault
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible) {
            crucible.spillRemnants();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    // =========================================================================
    // =========================================================================
    @Override
    @ParametersAreNonnullByDefault
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)) {
            return;
        }

        if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.getPersistentData().getBoolean("thaumicrecords:craft_result")) {
                return;
            }

            if (!crucible.isBoiling()) {
                return;
            }

            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) {
                return;
            }

            CrucibleBlockEntity.SmeltResult result = crucible.attemptSmelt(stack);

            if (result.bounced) {
                itemEntity.setDeltaMovement((level.random.nextFloat() - level.random.nextFloat()) * 0.2, 0.35,
                        (level.random.nextFloat() - level.random.nextFloat()) * 0.2);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.2F,
                        ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F) + 1.0F);
                return;
            }

            if (result.remainingOut.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setItem(result.remainingOut);
            }

            if (!result.craftOut.isEmpty()) {
                ejectItem(level, pos, result.craftOut);
                level.playSound(null, pos, team.torka.thaumicrecords.registry.SoundRegistry.BUBBLE.get(), SoundSource.BLOCKS, 0.2F,
                        1.0F + level.random.nextFloat() * 0.4F);
                level.blockEvent(pos, state.getBlock(), 2, 5);
            } else {
                level.playSound(null, pos, team.torka.thaumicrecords.registry.SoundRegistry.BUBBLE.get(), SoundSource.BLOCKS, 0.2F,
                        1.0F + level.random.nextFloat() * 0.4F);
                level.blockEvent(pos, state.getBlock(), 2, 1);
            }
            return;
        }

        delay++;
        if (delay < 10) {
            return;
        }
        delay = 0;
        if (crucible.isBoiling()) {
            entity.hurt(level.damageSources().inFire(), 1.0F);
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 2.0F + level.random.nextFloat() * 0.4F);
        }
    }

    // =========================================================================
    // =========================================================================
    public static void ejectItem(Level level, BlockPos pos, ItemStack items) {
        boolean first = true;
        ItemStack remaining = items.copy();
        while (!remaining.isEmpty()) {
            ItemStack spitout = remaining.copy();
            if (spitout.getCount() > spitout.getMaxStackSize()) {
                spitout.setCount(spitout.getMaxStackSize());
            }
            remaining.shrink(spitout.getCount());

            ItemEntity entityitem = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.71, pos.getZ() + 0.5, spitout);
            entityitem.setDeltaMovement(first ? 0 : (level.random.nextFloat() - level.random.nextFloat()) * 0.01, 0.10000000149011612,
                    first ? 0 : (level.random.nextFloat() - level.random.nextFloat()) * 0.01);

            entityitem.getPersistentData().putBoolean("thaumicrecords:craft_result", true);

            level.addFreshEntity(entityitem);
            first = false;
        }
    }

    // =========================================================================
    // =========================================================================

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (heldItem.isEmpty() && !level.isClientSide) {
            int fluid = crucible.getFluidLevel();
            int tags = crucible.tagAmount();
            int heat = crucible.getHeat();
            player.sendSystemMessage(Component.literal(
                    "[Crucible] Heat: " + heat + "/" + CrucibleBlockEntity.HEAT_BOILING + " | Fluid: " + fluid + "/" + CrucibleBlockEntity.MAX_FLUID + " | " + "Aspects: " + tags));
            crucible.getAspects().forEach((key, amount) -> player.sendSystemMessage(Component.literal("  " + key + " x" + amount)));
            return ItemInteractionResult.SUCCESS;
        }

        if (heldItem.is(Items.WATER_BUCKET)) {
            if (!level.isClientSide && crucible.fillWithWater()) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.CONSUME;
        }

        if (heldItem.is(Items.BUCKET)) {
            if (!level.isClientSide && crucible.drainToBucket()) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
                }
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.CONSUME;
        }

        if (heldItem.is(Items.POTION)) {
            PotionContents contents = heldItem.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.is(Potions.WATER)) {
                if (!level.isClientSide && crucible.fillWithBottle()) {
                    if (!player.getAbilities().instabuild) {
                        player.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                    }
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.CONSUME;
            }
        }


        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}