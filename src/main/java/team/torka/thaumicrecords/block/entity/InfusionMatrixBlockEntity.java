package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.block.InfusionPillarBlock;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class InfusionMatrixBlockEntity extends BlockEntity {

    public boolean active = false;
    public boolean crafting = false;
    public int craftCount = 0;
    public float startUp = 0.0F;
    public int instability = 0;

    public InfusionMatrixBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.INFUSION_MATRIX.get(), pos, blockState);
    }

    /**
     * Checks if the Mystical Construct structure is valid:
     * Level 1 (y-2): 3x3 grid, corners = Arcane Stone Brick, center = Arcane Pedestal
     * Level 2 (y-1): 4 Arcane Stone Block on top of the bricks (at corners)
     * Level 3 (y):   Runic Matrix at center (this block)
     */
    public boolean checkStructure() {
        if (level == null) return false;

        BlockPos center = worldPosition;

        // Level 1 (y-2): corners should be Arcane Stone Brick
        BlockPos level1Center = center.below(2);
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                BlockPos corner = level1Center.offset(dx, 0, dz);
                BlockState state = level.getBlockState(corner);
                if (!state.is(BlockRegistry.ARCANE_STONE_BRICK.get())) {
                    return false;
                }
            }
        }

        // Level 1 center (y-2): should be Arcane Pedestal
        BlockPos pedestalPos = level1Center;
        BlockEntity be = level.getBlockEntity(pedestalPos);
        if (!(be instanceof ArcanePedestalBlockEntity)) {
            return false;
        }

        // Level 2 (y-1): on top of each brick should be Arcane Stone Block
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                BlockPos stoneBlockPos = level1Center.offset(dx, 1, dz);
                BlockState state = level.getBlockState(stoneBlockPos);
                if (!state.is(BlockRegistry.ARCANE_STONE.get())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Activates the altar: transforms bricks into pillars, removes stone blocks, starts matrix animation.
     */
    public void activate() {
        if (active || level == null) return;

        BlockPos center = worldPosition;
        BlockPos level1Center = center.below(2);

        // Transform bricks at corners into pillars
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                BlockPos brickPos = level1Center.offset(dx, 0, dz);
                Direction facing = getFacingForPillar(dx, dz);
                BlockState pillarState = BlockRegistry.INFUSION_PILLAR.get().defaultBlockState()
                        .setValue(InfusionPillarBlock.FACING, facing);
                level.setBlock(brickPos, pillarState, 3);
            }
        }

        // Remove stone blocks on level 2 (they become air)
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                BlockPos stonePos = level1Center.offset(dx, 1, dz);
                level.removeBlock(stonePos, false);
            }
        }

        // Activate matrix
        active = true;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        level.playSound(null, worldPosition, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    private Direction getFacingForPillar(int dx, int dz) {
        // Based on TC4 blueprint orientations:
        // (-1,-1) → WEST (orientation 2, default)
        // (-1,+1) → SOUTH (orientation 3, 90°)
        // (+1,-1) → NORTH (orientation 4, 270°)
        // (+1,+1) → EAST (orientation 5, 180°)
        if (dx == -1 && dz == -1) return Direction.WEST;
        if (dx == -1 && dz == 1) return Direction.SOUTH;
        if (dx == 1 && dz == -1) return Direction.NORTH;
        if (dx == 1 && dz == 1) return Direction.EAST;
        return Direction.NORTH;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (blockEntity instanceof InfusionMatrixBlockEntity be) {
            if (be.active && be.startUp < 1.0F) {
                be.startUp = Math.min(1.0F, be.startUp + 0.02F);
                if (!level.isClientSide) {
                    be.setChanged();
                }
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("active", active);
        tag.putBoolean("crafting", crafting);
        tag.putInt("craftCount", craftCount);
        tag.putFloat("startUp", startUp);
        tag.putInt("instability", instability);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        active = tag.getBoolean("active");
        crafting = tag.getBoolean("crafting");
        craftCount = tag.getInt("craftCount");
        startUp = tag.getFloat("startUp");
        instability = tag.getInt("instability");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        super.onDataPacket(net, pkt, registries);
        if (Objects.nonNull(level) && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
