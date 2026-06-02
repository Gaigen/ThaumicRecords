package team.torka.thaumicrecords.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.IAspectSource;
import team.torka.thaumicrecords.block.entity.InfusionMatrixBlockEntity;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

import java.util.LinkedHashMap;

public class InfusionMatrixBlock extends BaseEntityBlock {

    public static final MapCodec<InfusionMatrixBlock> CODEC = simpleCodec(InfusionMatrixBlock::new);

    public InfusionMatrixBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new InfusionMatrixBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> blockEntityType) {
        if (blockEntityType == BlockEntityRegistry.INFUSION_MATRIX.get()) {
            return InfusionMatrixBlockEntity::tick;
        }
        return null;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    }

    // DEBUG: right-click with empty hand
    // - 1st click: scan jars and start drain (8 AER + 4 TERRA + 2 IGNIS)
    // - 2nd click: show remaining essentia status
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                                        @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof InfusionMatrixBlockEntity matrix) {
            int range = 12;

            if (!matrix.crafting) {
                // Scan for jars
                int jarCount = 0;
                for (int dx = -range; dx <= range; dx++) {
                    for (int dz = -range; dz <= range; dz++) {
                        for (int dy = -range; dy <= range; dy++) {
                            if (dx == 0 && dy == 0 && dz == 0) {
                                continue;
                            }
                            BlockPos checkPos = pos.offset(dx, dy, dz);
                            BlockEntity be = level.getBlockEntity(checkPos);
                            if (be instanceof IAspectSource source) {
                                jarCount++;
                                Aspect stored = source.getStoredAspect();
                                int amount = source.storedAmount();
                                if (stored != null) {
                                    player.sendSystemMessage(
                                            Component.literal("§a[DEBUG] §fJar at " + checkPos.toShortString() + " → " + stored.getName() + " x" + amount));
                                } else {
                                    player.sendSystemMessage(Component.literal("§a[DEBUG] §7Jar at " + checkPos.toShortString() + " → empty"));
                                }
                            }
                        }
                    }
                }

                player.sendSystemMessage(Component.literal("§a[DEBUG] §eFound " + jarCount + " jars in range " + range));

                // Set up recipe: 8 AER + 4 TERRA + 2 IGNIS 
                LinkedHashMap<Aspect, Integer> essentia = new LinkedHashMap<>();
                essentia.put(Aspect.AER, 8);
                essentia.put(Aspect.TERRA, 4);
                essentia.put(Aspect.IGNIS, 2);

                matrix.setRecipeEssentia(essentia, 0);
                matrix.startCrafting();

                player.sendSystemMessage(Component.literal("§a[DEBUG] §eStarted drain: 8 AER + 4 TERRA + 2 IGNIS"));

            } else {
                // Show remaining essentia
                player.sendSystemMessage(Component.literal("§a[DEBUG] §bRemaining essentia:"));
                for (var entry : matrix.getRecipeEssentia().entrySet()) {
                    String status = entry.getValue() > 0 ? "§e" + entry.getValue() : "§a✓";
                    player.sendSystemMessage(Component.literal("§a[DEBUG] §f  " + entry.getKey().getName() + " = " + status));
                }

                if (matrix.isEssentiaComplete()) {
                    player.sendSystemMessage(Component.literal("§a[DEBUG] §aAll essentia drained!"));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
