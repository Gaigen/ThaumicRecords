package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.helper.EssentiaHandler;
import team.torka.thaumicrecords.block.InfusionPillarBlock;
import team.torka.thaumicrecords.recipe.InfusionRecipe;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final LinkedHashMap<Aspect, Integer> recipeEssentia = new LinkedHashMap<>();

    // Tick counter and delay — mirrors TC4's count / countDelay
    private int count = 0;
    private int countDelay = 10;

    // Recipe instability — used for instability chance calculations
    private int recipeInstability = 0;

    // Current recipe tracking
    private InfusionRecipe currentRecipe = null;
    private List<BlockPos> pedestalPositions = new ArrayList<>();
    private boolean[] consumedComponents = null;

    private static final int DRAIN_RANGE = 12;
    private static final int PEDESTAL_SCAN_RANGE = 5;

    public InfusionMatrixBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.INFUSION_MATRIX.get(), pos, blockState);
    }

    // --- Recipe matching ---

    /**
     * Scan pedestals around the matrix and find a matching InfusionRecipe.
     * Returns null if no recipe matches.
     */
    public InfusionRecipe findMatchingRecipe() {
        if (level == null) {
            return null;
        }

        // Scan for pedestals with items
        List<BlockPos> foundPedestals = new ArrayList<>();
        List<ItemStack> pedestalItems = new ArrayList<>();
        BlockPos centralPos = worldPosition.below(2);
        ItemStack centralItem = ItemStack.EMPTY;

        // Get central pedestal item
        BlockEntity centralBE = level.getBlockEntity(centralPos);
        if (centralBE instanceof ArcanePedestalBlockEntity centralPedestal) {
            centralItem = centralPedestal.getItem();
        }

        // Scan for surrounding pedestals (within range, excluding center)
        for (int dx = -PEDESTAL_SCAN_RANGE; dx <= PEDESTAL_SCAN_RANGE; dx++) {
            for (int dz = -PEDESTAL_SCAN_RANGE; dz <= PEDESTAL_SCAN_RANGE; dz++) {
                for (int dy = -PEDESTAL_SCAN_RANGE; dy <= 0; dy++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue; // skip center
                    }
                    BlockPos checkPos = worldPosition.offset(dx, dy, dz);
                    BlockEntity be = level.getBlockEntity(checkPos);
                    if (be instanceof ArcanePedestalBlockEntity pedestal && pedestal.hasItem()) {
                        foundPedestals.add(checkPos);
                        pedestalItems.add(pedestal.getItem());
                    }
                }
            }
        }

        // Try to match recipes
        var recipeManager = level.getRecipeManager();
        var allRecipes = recipeManager.getAllRecipesFor(RecipeTypeRegistry.INFUSION.get());

        for (var recipeHolder : allRecipes) {
            InfusionRecipe recipe = recipeHolder.value();
            if (recipe.matches(pedestalItems, centralItem)) {
                // Store matched pedestals for later use
                this.pedestalPositions = foundPedestals;
                return recipe;
            }
        }

        return null;
    }

    /**
     * Start infusion crafting with the given recipe.
     * Called from WandItem after recipe matching succeeds.
     */
    public void startInfusion(InfusionRecipe recipe) {
        if (level == null || crafting) {
            return;
        }

        this.currentRecipe = recipe;
        this.crafting = true;
        this.craftCount = 0;
        this.consumedComponents = new boolean[recipe.components().size()];
        this.recipeInstability = recipe.instability();

        // Set up essentia requirements
        recipeEssentia.clear();
        for (var entry : recipe.aspects().entrySet()) {
            Aspect aspect = AspectRegistry.getByName(entry.getKey().getPath());
            if (aspect != null) {
                recipeEssentia.put(aspect, entry.getValue());
            }
        }

        this.checkSurroundings = true;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        level.playSound(null, worldPosition, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    // --- Craft cycle ---

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
            be.craftCount++;
            be.craftCycle();
            be.setChanged();
        }
    }

    /**
     * Core craft cycle — mirrors TC4's craftCycle().
     * Phase 1: drain essentia from jars
     * Phase 2: consume ingredients from pedestals
     * Phase 3: place result on central pedestal
     */
    private void craftCycle() {
        // Phase 1: Essentia drain
        if (recipeEssentiaVisSize() > 0) {
            for (Map.Entry<Aspect, Integer> entry : recipeEssentia.entrySet()) {
                Aspect aspect = entry.getKey();
                int remaining = entry.getValue();

                if (remaining > 0) {
                    if (EssentiaHandler.drainEssentia(level, worldPosition, aspect, DRAIN_RANGE)) {
                        entry.setValue(remaining - 1);

                        if (isEssentiaComplete()) {
                            // Essentia phase done — will transition to ingredient phase next cycle
                        }

                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                        setChanged();
                        return;
                    }

                    // Failed drain — instability chance (TC4 line 441-442)
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

            // All aspects tried, none drained — rescan
            this.checkSurroundings = true;
            return;
        }

        // Phase 2: Consume ingredients from pedestals
        if (currentRecipe != null && !allComponentsConsumed()) {
            consumeNextIngredient();
            return;
        }

        // Phase 3: All ingredients consumed — finish crafting
        if (currentRecipe != null && allComponentsConsumed()) {
            finishCrafting();
        }
    }

    /**
     * Consume one ingredient from a pedestal per cycle (TC4 behavior).
     * Checks ALL remaining components.
     */
    private void consumeNextIngredient() {
        if (level == null || currentRecipe == null || consumedComponents == null) {
            return;
        }

        for (BlockPos pedestalPos : pedestalPositions) {
            BlockEntity be = level.getBlockEntity(pedestalPos);
            if (be instanceof ArcanePedestalBlockEntity pedestal && pedestal.hasItem()) {
                ItemStack pedestalItem = pedestal.getItem();

                for (int i = 0; i < currentRecipe.components().size(); i++) {
                    if (!consumedComponents[i] && currentRecipe.components().get(i).test(pedestalItem)) {
                        pedestal.setItem(ItemStack.EMPTY);
                        consumedComponents[i] = true;

                        if (level.random.nextInt(100 - recipeInstability * 3) == 0) {
                            this.instability++;
                        }
                        if (this.instability > 25) {
                            this.instability = 25;
                        }

                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                        setChanged();
                        return;
                    }
                }
            }
        }
    }

    private boolean allComponentsConsumed() {
        if (consumedComponents == null) {
            return false;
        }
        for (boolean c : consumedComponents) {
            if (!c) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finish crafting — place result on central pedestal.
     */
    private void finishCrafting() {
        if (level == null || currentRecipe == null) {
            return;
        }

        BlockPos centralPos = worldPosition.below(2);
        BlockEntity centralBE = level.getBlockEntity(centralPos);

        if (centralBE instanceof ArcanePedestalBlockEntity centralPedestal) {
            // Replace central item with result
            ItemStack result = currentRecipe.result().copy();
            centralPedestal.setItem(result);
        }

        // Reset crafting state
        this.crafting = false;
        this.currentRecipe = null;
        this.consumedComponents = null;
        this.pedestalPositions.clear();
        this.instability = 0;
        this.recipeInstability = 0;

        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        level.playSound(null, worldPosition, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
        setChanged();
    }

    // --- Essentia helpers (for debug/manual setup) ---

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

    public void addRecipeEssentia(Aspect aspect, int amount) {
        this.recipeEssentia.merge(aspect, amount, Integer::sum);
    }

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

    public Map<Aspect, Integer> getRecipeEssentia() {
        return java.util.Collections.unmodifiableMap(recipeEssentia);
    }

    public boolean isEssentiaComplete() {
        for (int amount : recipeEssentia.values()) {
            if (amount > 0) {
                return false;
            }
        }
        return true;
    }

    private int recipeEssentiaVisSize() {
        int total = 0;
        for (int amount : recipeEssentia.values()) {
            total += amount;
        }
        return total;
    }

    // --- Accessors ---

    public InfusionRecipe getCurrentRecipe() {
        return currentRecipe;
    }

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
