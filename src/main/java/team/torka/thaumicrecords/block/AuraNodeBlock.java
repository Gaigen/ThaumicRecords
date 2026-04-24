package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.data.component.AspectListComponent;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class AuraNodeBlock extends BaseEntityBlock {
    private static final VoxelShape NODE_SHAPE = Block.box(4.8, 4.8, 4.8, 11.2, 11.2, 11.2);

    public AuraNodeBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(AuraNodeBlock::new);
    }

    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AuraNodeBlockEntity(pos, state);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.AURA_NODE.get(), AuraNodeBlockEntity::tick);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return NODE_SHAPE;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof AuraNodeBlockEntity node) {
                AspectList aspects = node.getCurrentAspect();
                for (var entry : aspects.entrySet()) {
                    ResourceLocation aspect = entry.getKey();
                    int amount = entry.getValue();
                    if (amount >= 5) {
                        int dropCount = (amount / 10);
                        for (int i = 0; i <= dropCount; i++) {
                            ItemStack essence = new ItemStack(ItemRegistry.WISP_ESSENCE.get());
                            AspectList aspectList = new AspectList();
                            aspectList.put(aspect, 2);
                            essence.set(DataComponentRegistry.ASPECT_LIST, new AspectListComponent(aspectList));
                            Block.popResource(level, pos, essence);
                        }
                    }
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
        return state;
    }
}
