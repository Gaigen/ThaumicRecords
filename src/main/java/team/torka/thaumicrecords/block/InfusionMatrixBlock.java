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
import team.torka.thaumicrecords.block.entity.ArcanePedestalBlockEntity;
import team.torka.thaumicrecords.block.entity.InfusionMatrixBlockEntity;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;

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

    // DEBUG: right-click with empty hand — show recipe matching info
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                                        @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof InfusionMatrixBlockEntity matrix) {
            // Force refresh symmetry from current surroundings
            matrix.getSurroundings();

            // Always show stability info
            player.sendSystemMessage(Component.literal("§d[STABILITY] §7Symmetry: §e" + matrix.symmetry));
            player.sendSystemMessage(Component.literal("§d[STABILITY] §7Pedestals: §e" + matrix.getPedestalPositions().size()));
            player.sendSystemMessage(Component.literal("§d[STABILITY] §7Stabilizers: §e" + matrix.getStabilizerCount()));

            if (matrix.crafting) {
                // Show remaining essentia
                player.sendSystemMessage(Component.literal("§b[INFUSION] §eCrafting in progress..."));
                player.sendSystemMessage(Component.literal("§d[STABILITY] §7Instability: §c" + matrix.instability + "§7/25"));
                player.sendSystemMessage(Component.literal("§d[STABILITY] §7Recipe instability: §e" + matrix.getCurrentRecipe().instability()));
                player.sendSystemMessage(Component.literal("§d[STABILITY] §7Event chance: §e" + (matrix.instability * 100 / 500) + "%§7 per cycle"));
                player.sendSystemMessage(Component.literal("§b[INFUSION] §7Remaining essentia:"));
                for (var entry : matrix.getRecipeEssentia().entrySet()) {
                    String status = entry.getValue() > 0 ? "§e" + entry.getValue() : "§a✓";
                    player.sendSystemMessage(Component.literal("§b[INFUSION] §f  " + entry.getKey().getName() + " = " + status));
                }
                if (matrix.isEssentiaComplete()) {
                    player.sendSystemMessage(Component.literal("§b[INFUSION] §aAll essentia drained! Consuming ingredients..."));
                }
            } else {
                // Scan pedestals
                player.sendSystemMessage(Component.literal("§b[INFUSION] §7Scanning pedestals..."));

                // Central pedestal
                BlockPos centralPos = pos.below(2);
                BlockEntity centralBE = level.getBlockEntity(centralPos);
                if (centralBE instanceof ArcanePedestalBlockEntity centralPed) {
                    String itemName = centralPed.hasItem() ? centralPed.getItem().getHoverName().getString() : "§7(empty)";
                    player.sendSystemMessage(Component.literal("§b[INFUSION] §7Central: §f" + itemName));
                } else {
                    player.sendSystemMessage(Component.literal("§b[INFUSION] §cNo central pedestal found!"));
                }

                // Surrounding pedestals
                int pedCount = 0;
                for (int dx = -5; dx <= 5; dx++) {
                    for (int dz = -5; dz <= 5; dz++) {
                        for (int dy = -5; dy <= 0; dy++) {
                            if (dx == 0 && dy == 0 && dz == 0) {
                                continue;
                            }
                            BlockPos checkPos = pos.offset(dx, dy, dz);
                            BlockEntity be = level.getBlockEntity(checkPos);
                            if (be instanceof ArcanePedestalBlockEntity ped && ped.hasItem()) {
                                pedCount++;
                                player.sendSystemMessage(Component.literal(
                                        "§b[INFUSION] §7Pedestal " + checkPos.toShortString() + ": §f" + ped.getItem().getHoverName().getString()));
                            }
                        }
                    }
                }
                player.sendSystemMessage(Component.literal("§b[INFUSION] §7Found §e" + pedCount + " §7pedestals with items"));

                // Try recipe match
                var recipe = matrix.findMatchingRecipe();
                if (recipe != null) {
                    player.sendSystemMessage(Component.literal("§a[INFUSION] §aRecipe matched! §f" + recipe.result().getHoverName().getString()));
                    player.sendSystemMessage(Component.literal("§a[INFUSION] §7Recipe instability: §e" + recipe.instability()));
                    player.sendSystemMessage(Component.literal("§d[STABILITY] §7Predicted instability: §e" + (matrix.symmetry + recipe.instability())));
                    player.sendSystemMessage(Component.literal("§a[INFUSION] §7Required essentia:"));
                    for (var entry : recipe.aspects().entrySet()) {
                        player.sendSystemMessage(Component.literal("§a[INFUSION] §f  " + entry.getKey().getPath() + " x" + entry.getValue()));
                    }
                    player.sendSystemMessage(Component.literal("§a[INFUSION] §7Components: §e" + recipe.components().size()));
                } else {
                    player.sendSystemMessage(Component.literal("§c[INFUSION] §cNo matching recipe found"));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
