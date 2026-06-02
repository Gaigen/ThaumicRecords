package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.helper.EssentiaHandler;
import team.torka.thaumicrecords.block.InfusionPillarBlock;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class InfusionMatrixBlockEntity extends BlockEntity {

    public boolean active = false;
    public boolean crafting = false;
    public int craftCount = 0;
    public float startUp = 0.0F;
    public int instability = 0;
    public boolean checkSurroundings = true;

    // Remaining essentia to drain — mirrors TC4's recipeEssentia
    // LinkedHashMap preserves insertion order for consistent iteration
    private final LinkedHashMap<Aspect, Integer> recipeEssentia = new LinkedHashMap<>();

    // Tick counter and delay — mirrors TC4's count / countDelay
    private int count = 0;
    private int countDelay = 10;

    // Recipe instability — used for instability chance calculations
    private int recipeInstability = 0;

    private static final int DRAIN_RANGE = 12;

    public InfusionMatrixBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.INFUSION_MATRIX.get(), pos, blockState);
    }

    /**
     * Set the essentia requirements for a craft session.
     * Call this when starting a craft (equivalent to TC4's recipe setup).
     */
    public void setRecipeEssentia(LinkedHashMap<Aspect, Integer> essentia, int recipeInstability) {
        this.recipeEssentia.clear();
        this.recipeEssentia.putAll(essentia);
        this.recipeInstability = recipeInstability;
        this.crafting = true;
        this.checkSurroundings = true;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * Add essentia requirements (for debug / incremental setup).
     */
    public void addRecipeEssentia(Aspect aspect, int amount) {
        this.recipeEssentia.merge(aspect, amount, Integer::sum);
    }

    /**
     * Start the essentia absorption phase — sets crafting=true.
     */
    public void startCrafting() {
        if (!recipeEssentia.isEmpty()) {
            this.crafting = true;
            this.checkSurroundings = true;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    /**
     * Get remaining recipe essentia (read-only view for debug/display).
     */
    public Map<Aspect, Integer> getRecipeEssentia() {
        return java.util.Collections.unmodifiableMap(recipeEssentia);
    }

    /**
     * Check if all essentia has been drained.
     */
    public boolean isEssentiaComplete() {
        for (int amount : recipeEssentia.values()) {
            if (amount > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the Mystical Construct structure is valid:
     * Level 1 (y-2): 3x3 grid, corners = Arcane Stone Brick, center = Arcane Pedestal
     * Level 2 (y-1): 4 Arcane Stone Block on top of the bricks (at corners)
     * Level 3 (y):   Runic Matrix at center (this block)
     */
    public boolean checkStructure() {
        if (level == null) {
            return false;
        }

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
        if (active || level == null) {
            return;
        }

        BlockPos center = worldPosition;
        BlockPos level1Center = center.below(2);

        // Transform bricks at corners into pillars
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                BlockPos brickPos = level1Center.offset(dx, 0, dz);
                Direction facing = getFacingForPillar(dx, dz);
                BlockState pillarState = BlockRegistry.INFUSION_PILLAR.get().defaultBlockState().setValue(InfusionPillarBlock.FACING, facing);
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
        if (dx == -1 && dz == -1) {
            return Direction.WEST;
        }
        if (dx == -1 && dz == 1) {
            return Direction.SOUTH;
        }
        if (dx == 1 && dz == -1) {
            return Direction.NORTH;
        }
        if (dx == 1 && dz == 1) {
            return Direction.EAST;
        }
        return Direction.NORTH;
    }

    // --- TC4-accurate tick logic ---

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!(blockEntity instanceof InfusionMatrixBlockEntity be)) {
            return;
        }

        // Startup animation — runs every tick client+server
        if (be.active && be.startUp < 1.0F) {
            be.startUp = Math.min(1.0F, be.startUp + 0.02F);
            if (!level.isClientSide) {
                be.setChanged();
            }
        }

        // Server-side crafting logic
        if (level.isClientSide) {
            return;
        }

        be.count++;

        // checkSurroundings — triggers source cache rescan (TC4 line 208-211)
        if (be.checkSurroundings) {
            be.checkSurroundings = false;
            EssentiaHandler.refreshSources(pos);
        }

        // Craft cycle — every countDelay ticks (TC4 line 223-225)
        if (be.active && be.crafting && be.count % be.countDelay == 0) {
            be.craftCycle();
            be.setChanged();
        }
    }

    /**
     * Core craft cycle — mirrors TC4's craftCycle() for essentia phase only.
     * Processes one essentia drain per call, then returns.
     */
    private void craftCycle() {
        // Essentia phase (TC4 lines 430-449)
        if (recipeEssentiaVisSize() > 0) {
            for (Map.Entry<Aspect, Integer> entry : recipeEssentia.entrySet()) {
                Aspect aspect = entry.getKey();
                int remaining = entry.getValue();

                if (remaining > 0) {
                    if (EssentiaHandler.drainEssentia(level, worldPosition, aspect, DRAIN_RANGE)) {
                        // Successfully drained 1 essentia
                        entry.setValue(remaining - 1);

                        // Check if all essentia done
                        if (isEssentiaComplete()) {
                            // TODO: transition to ingredient phase (TC4 line 451+)
                            // For now — stop crafting
                            this.crafting = false;
                        }

                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                        setChanged();
                        return; // Only 1 drain per cycle
                    }

                    // Failed to drain this aspect — chance for instability (TC4 line 441-442)
                    if (level.random.nextInt(100 - recipeInstability * 3) == 0) {
                        this.instability++;
                    }
                    if (this.instability > 25) {
                        this.instability = 25;
                    }
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                    setChanged();
                }
            }

            // All aspects tried, none drained — rescan sources (TC4 line 447)
            this.checkSurroundings = true;
            return;
        }
    }

    /**
     * Sum of all remaining essentia amounts — mirrors AspectList.visSize().
     */
    private int recipeEssentiaVisSize() {
        int total = 0;
        for (int amount : recipeEssentia.values()) {
            total += amount;
        }
        return total;
    }

    // --- NBT ---

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("active", active);
        tag.putBoolean("crafting", crafting);
        tag.putInt("craftCount", craftCount);
        tag.putFloat("startUp", startUp);
        tag.putInt("instability", instability);
        tag.putInt("recipeInstability", recipeInstability);

        // Save recipeEssentia
        CompoundTag essentiaTag = new CompoundTag();
        for (Map.Entry<Aspect, Integer> entry : recipeEssentia.entrySet()) {
            essentiaTag.putInt(entry.getKey().getName(), entry.getValue());
        }
        tag.put("recipeEssentia", essentiaTag);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        active = tag.getBoolean("active");
        crafting = tag.getBoolean("crafting");
        craftCount = tag.getInt("craftCount");
        startUp = tag.getFloat("startUp");
        instability = tag.getInt("instability");
        recipeInstability = tag.getInt("recipeInstability");

        // Load recipeEssentia
        recipeEssentia.clear();
        if (tag.contains("recipeEssentia")) {
            CompoundTag essentiaTag = tag.getCompound("recipeEssentia");
            for (String key : essentiaTag.getAllKeys()) {
                Aspect aspect = AspectRegistry.getByName(key);
                if (aspect != null) {
                    recipeEssentia.put(aspect, essentiaTag.getInt(key));
                }
            }
        }
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
