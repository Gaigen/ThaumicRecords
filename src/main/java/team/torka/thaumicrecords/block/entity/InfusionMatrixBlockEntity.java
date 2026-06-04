package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.block.AspectRenderable;
import team.torka.thaumicrecords.api.helper.EssentiaHandler;
import team.torka.thaumicrecords.block.InfusionPillarBlock;
import team.torka.thaumicrecords.network.payload.InfusionSourcePayload;
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

public class InfusionMatrixBlockEntity extends BlockEntity implements AspectRenderable {

    public boolean active = false;
    public boolean crafting = false;
    public int craftCount = 0;
    public float startUp = 0.0F;
    public int instability = 0;
    public int symmetry = 0; // calculated from pedestal/stabilizer mirror pairs
    public int stabilizerCount = 0; // number of stabilizers found in last scan
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
    private ResourceLocation currentRecipeId = null; // persisted for chunk reload
    private ItemStack currentRecipeInput = ItemStack.EMPTY; // persisted — central item pattern
    private ItemStack currentRecipeOutput = ItemStack.EMPTY; // persisted — result
    private List<BlockPos> pedestalPositions = new ArrayList<>();
    private boolean[] consumedComponents = null;
    private List<ItemStack> syncedComponents = new ArrayList<>(); // synced to client for rendering
    private int itemCount = 0; // delay before consuming ingredient (TC4: 5 ticks)
    private BlockPos absorbingPedestal = null; // pedestal being absorbed from
    private boolean recipeRestored = false; // false until we restore from RecipeManager after load

    private static final int DRAIN_RANGE = 12;
    private static final int PEDESTAL_SCAN_RANGE = 5;
    private static final TagKey<Block> STABILIZERS_TAG = TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("thaumicrecords", "infusion_stabilizers"));

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
        this.currentRecipeId = level.getRecipeManager()
                .getAllRecipesFor(RecipeTypeRegistry.INFUSION.get())
                .stream()
                .filter(h -> h.value() == recipe)
                .findFirst()
                .map(RecipeHolder::id)
                .orElse(null);
        this.currentRecipeInput = recipe.input().getItems().length > 0 ? recipe.input().getItems()[0].copy() : ItemStack.EMPTY;
        this.currentRecipeOutput = recipe.result().copy();
        this.recipeRestored = true;
        this.crafting = true;
        this.craftCount = 0;
        this.consumedComponents = new boolean[recipe.components().size()];
        this.recipeInstability = recipe.instability();
        this.countDelay = 10;

        // Calculate initial instability: symmetry + recipeInstability (TC4)
        this.checkSurroundings = true; // triggers getSurroundings() on next tick
        this.instability = this.symmetry + this.recipeInstability;

        // Sync component items to client for rendering
        this.syncedComponents = new ArrayList<>();
        for (var component : recipe.components()) {
            var items = component.getItems();
            this.syncedComponents.add(items.length > 0 ? items[0].copy() : ItemStack.EMPTY);
        }

        // Set up essentia requirements
        recipeEssentia.clear();
        for (var entry : recipe.aspects().entrySet()) {
            Aspect aspect = AspectRegistry.getByName(entry.getKey().getPath());
            if (aspect != null) {
                recipeEssentia.put(aspect, entry.getValue());
            }
        }

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

        // Restore recipe from RecipeManager after chunk reload
        if (be.crafting && !be.recipeRestored && be.currentRecipeId != null) {
            be.restoreRecipe(level);
        }

        // Structure validation — TC4: every 20 ticks when crafting, 100 when idle
        if (be.active && be.count % (be.crafting ? 20 : 100) == 0) {
            if (!be.validLocation()) {
                be.active = false;
                if (be.crafting) {
                    be.cancelCrafting("Structure broken");
                }
                be.setChanged();
                level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
                return;
            }
        }

        // checkSurroundings — triggers full surroundings scan (TC4 line 208-211)
        if (be.checkSurroundings) {
            be.checkSurroundings = false;
            EssentiaHandler.refreshSources(pos);
            be.getSurroundings();
        }

        // Craft cycle — every countDelay ticks (TC4 line 223-225)
        if (be.active && be.crafting && be.count % be.countDelay == 0) {
            // Cancel if central item was removed
            BlockPos centralPos = pos.below(2);
            BlockEntity centralBE = level.getBlockEntity(centralPos);
            if (!(centralBE instanceof ArcanePedestalBlockEntity centralPed) || !centralPed.hasItem()) {
                be.cancelCrafting("Central item removed");
                return;
            }

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
        // TODO: Instability event check (TC4 line 366-395)
        // Each cycle: chance = instability / 500 of triggering a random bad event
        // 21 possible outcomes via switch(random.nextInt(21)):
        //   case 0,2,10,13 (19%):  inEvEjectItem(0) — drop item from random pedestal
        //   case 6,17     (9.5%):  inEvEjectItem(1) — drop item + flux goo
        //   case 1,11     (9.5%):  inEvEjectItem(2) — drop item + flux gas
        //   case 3,8,14   (14.3%): inEvZap(false) — zap ONE entity (4-7 magic damage)
        //   case 5,16     (9.5%):  inEvHarm(false) — harm ONE entity (taint/vis exhaustion)
        //   case 12       (4.8%):  inEvZap(true) — zap ALL entities
        //   case 19       (4.8%):  inEvEjectItem(3) — DESTROY item + flux goo
        //   case 7        (4.8%):  inEvEjectItem(4) — DESTROY item + flux gas
        //   case 4,15     (9.5%):  inEvEjectItem(5) — drop item + explosion at pedestal
        //   case 18       (4.8%):  inEvHarm(true) — harm ALL entities
        //   case 9        (4.8%):  explosion at matrix center (1.5 + rand radius)
        //   case 20       (4.8%):  inEvWarp() — give warp to random nearby player
        //
        // If event fires AND central item is still valid → return (continue crafting)
        // If event fires AND central item is gone → cancel crafting

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
            this.countDelay = 20; // TC4: slower during ingredient phase
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
     * Phase 1: Send particle effect, start delay (itemCount = 5)
     * Phase 2: After delay, actually remove the item
     */
    private void consumeNextIngredient() {
        if (level == null || currentRecipe == null || consumedComponents == null) {
            return;
        }

        // Phase 2: delay expired — actually consume the item
        if (itemCount > 0) {
            itemCount--;
            if (itemCount <= 0 && absorbingPedestal != null) {
                BlockEntity be = level.getBlockEntity(absorbingPedestal);
                if (be instanceof ArcanePedestalBlockEntity pedestal && pedestal.hasItem()) {
                    ItemStack pedestalItem = pedestal.getItem();
                    for (int i = 0; i < currentRecipe.components().size(); i++) {
                        if (!consumedComponents[i] && currentRecipe.components().get(i).test(pedestalItem)) {
                            pedestal.setItem(ItemStack.EMPTY);
                            consumedComponents[i] = true;
                            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                            setChanged();
                            break;
                        }
                    }
                }
                absorbingPedestal = null;
            }
            return;
        }

        // Phase 1: find next ingredient to absorb — send animation, start delay
        for (BlockPos pedestalPos : pedestalPositions) {
            BlockEntity be = level.getBlockEntity(pedestalPos);
            if (be instanceof ArcanePedestalBlockEntity pedestal && pedestal.hasItem()) {
                ItemStack pedestalItem = pedestal.getItem();

                for (int i = 0; i < currentRecipe.components().size(); i++) {
                    if (!consumedComponents[i] && currentRecipe.components().get(i).test(pedestalItem)) {
                        // Start absorption animation
                        sendPedestalConsumeFX(pedestalPos);
                        this.itemCount = 5; // TC4: 5 tick delay
                        this.absorbingPedestal = pedestalPos;

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
     * Send sparkle/item particle effect from pedestal to matrix when consuming an ingredient.
     * Uses TC4's drawInfusionParticles1/3 (33% purple sparkle, 67% item texture).
     */
    private void sendPedestalConsumeFX(BlockPos pedestalPos) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }

        // Get the item on the pedestal for texture particles
        ItemStack itemStack = ItemStack.EMPTY;
        BlockEntity be = level.getBlockEntity(pedestalPos);
        if (be instanceof ArcanePedestalBlockEntity pedestal) {
            itemStack = pedestal.getItem();
        }

        InfusionSourcePayload payload = new InfusionSourcePayload(worldPosition, pedestalPos, itemStack.copy());
        for (ServerPlayer player : serverLevel.players()) {
            if (player.blockPosition().closerThan(worldPosition, 32)) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }
    }

    /**
     * Cancel the current craft — reset all state.
     */
    public void cancelCrafting(String reason) {
        this.crafting = false;
        this.currentRecipe = null;
        this.currentRecipeId = null;
        this.currentRecipeInput = ItemStack.EMPTY;
        this.currentRecipeOutput = ItemStack.EMPTY;
        this.recipeRestored = false;
        this.consumedComponents = null;
        this.syncedComponents.clear();
        this.pedestalPositions.clear();
        this.itemCount = 0;
        this.absorbingPedestal = null;
        this.recipeEssentia.clear();
        this.instability = 0;
        this.recipeInstability = 0;
        this.countDelay = 10;

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            level.playSound(null, worldPosition, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.25F, 0.5F);
        }
        setChanged();
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
        this.currentRecipeId = null;
        this.currentRecipeInput = ItemStack.EMPTY;
        this.currentRecipeOutput = ItemStack.EMPTY;
        this.recipeRestored = false;
        this.consumedComponents = null;
        this.syncedComponents.clear();
        this.pedestalPositions.clear();
        this.itemCount = 0;
        this.absorbingPedestal = null;
        this.instability = 0;
        this.recipeInstability = 0;
        this.countDelay = 10;

        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        level.playSound(null, worldPosition, SoundRegistry.WAND.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
        setChanged();
    }

    // --- Instability events (TODO: implement) ---

    /**
     * Eject item from a random pedestal.
     *
     * @param type 0=drop, 1=drop+goo, 2=drop+gas, 3=destroy+goo, 4=destroy+gas, 5=drop+explosion
     */
    private void inEvEjectItem(int type) {
        // TODO: Pick random pedestal from pedestalPositions
        // type 0: drop item as entity (InventoryUtils.dropItems equivalent)
        // type 1: drop + place flux goo block above pedestal (level 7)
        // type 2: drop + place flux gas block above pedestal (level 7)
        // type 3: destroy item (setItem EMPTY) + flux goo
        // type 4: destroy item + flux gas
        // type 5: drop + explosion at pedestal (radius 1.0)
        // Visual: zap effect from matrix to pedestal
    }

    /**
     * Zap entities with magic damage (4-7 per target).
     *
     * @param all true=hit ALL entities in 10-block radius, false=hit only one
     */
    private void inEvZap(boolean all) {
        // TODO: Find EntityLivingBase within 10 blocks of matrix
        // Deal 4 + rand(4) magic damage
        // If !all, only hit first entity
        // Visual: lightning bolt FX from matrix to target
    }

    /**
     * Apply harmful potion effects to entities.
     *
     * @param all true=hit ALL, false=hit one
     */
    private void inEvHarm(boolean all) {
        // TODO: Find EntityLivingBase within 10 blocks
        // 50% chance: Taint Poison (6 seconds, no particles)
        // 50% chance: Vis Exhaustion (2 minutes, NOT curable by milk)
        // If !all, only hit first entity
    }

    /**
     * Give warp to a random nearby player.
     */
    private void inEvWarp() {
        // TODO: Find players within 10 blocks
        // Pick random player
        // 25% chance: 1 sticky warp
        // 75% chance: 1-5 temporary warp
        // (Requires warp system to be implemented first)
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

    public boolean[] getConsumedComponents() {
        return consumedComponents;
    }

    public List<ItemStack> getSyncedComponents() {
        return syncedComponents;
    }

    @Override
    public AspectList getAspectRendered() {
        // Show remaining recipe essentia when looking at the matrix
        if (!crafting || recipeEssentia.isEmpty()) {
            return AspectList.empty();
        }
        AspectList list = new AspectList();
        for (Map.Entry<Aspect, Integer> entry : recipeEssentia.entrySet()) {
            if (entry.getValue() > 0) {
                list.add(ResourceLocation.fromNamespaceAndPath("thaumicrecords", entry.getKey().getName()), entry.getValue());
            }
        }
        return list;
    }

    @Override
    public float getRenderYOffset() {
        return 0.5F;
    }

    public List<BlockPos> getPedestalPositions() {
        return pedestalPositions;
    }

    public int getStabilizerCount() {
        return stabilizerCount;
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

    // --- Chunk reload: restore recipe from RecipeManager ---

    /**
     * Restore currentRecipe from RecipeManager after chunk reload.
     * Called once on first tick after load when crafting=true.
     */
    private void restoreRecipe(Level level) {
        recipeRestored = true;
        if (currentRecipeId == null) {
            cancelCrafting("No recipe ID saved");
            return;
        }

        var holder = level.getRecipeManager()
                .getAllRecipesFor(RecipeTypeRegistry.INFUSION.get())
                .stream()
                .filter(h -> h.id().equals(currentRecipeId))
                .findFirst()
                .orElse(null);

        if (holder == null) {
            cancelCrafting("Recipe not found: " + currentRecipeId);
            return;
        }

        currentRecipe = holder.value();
        // Full rescan on next tick
        checkSurroundings = true;
        getSurroundings(); // immediate rescan for pedestals + symmetry
    }

    /**
     * Full surroundings scan — mirrors TC4's getSurroundings().
     * Scans for pedestals + stabilizers, calculates symmetry from mirror pairs.
     * Called when checkSurroundings triggers.
     */
    public void getSurroundings() {
        if (level == null) {
            return;
        }

        List<BlockPos> foundPedestals = new ArrayList<>();
        List<BlockPos> foundStabilizers = new ArrayList<>();

        // Scan range: X/Z ±12, Y from +5 to -10 relative to matrix
        for (int xx = -12; xx <= 12; xx++) {
            for (int zz = -12; zz <= 12; zz++) {
                boolean skipPedestal = false;
                for (int yy = -5; yy <= 10; yy++) {
                    if (xx == 0 && zz == 0) {
                        continue;
                    }

                    int x = worldPosition.getX() + xx;
                    int y = worldPosition.getY() - yy; // TC4: y - yy (scans downward)
                    int z = worldPosition.getZ() + zz;
                    BlockPos checkPos = new BlockPos(x, y, z);

                    BlockEntity te = level.getBlockEntity(checkPos);

                    // Pedestals: only within ±8 X/Z, below matrix (yy > 0), one per column
                    if (!skipPedestal && yy > 0 && Math.abs(xx) <= 8 && Math.abs(zz) <= 8 && te instanceof ArcanePedestalBlockEntity) {
                        foundPedestals.add(checkPos);
                        skipPedestal = true; // only first pedestal per column
                    } else {
                        // Stabilizers: check tag
                        BlockState state = level.getBlockState(checkPos);
                        if (state.is(STABILIZERS_TAG)) {
                            foundStabilizers.add(checkPos);
                        }
                    }
                }
            }
        }

        // Update pedestalPositions for ingredient consumption
        pedestalPositions.clear();
        pedestalPositions.addAll(foundPedestals);

        // Calculate symmetry
        symmetry = 0;

        // Pedestal symmetry (integer arithmetic)
        int pedSym = 0;
        for (BlockPos pedPos : foundPedestals) {
            int offsetX = worldPosition.getX() - pedPos.getX();
            int offsetZ = worldPosition.getZ() - pedPos.getZ();

            boolean hasItem = false;
            BlockEntity pedBE = level.getBlockEntity(pedPos);
            if (pedBE instanceof ArcanePedestalBlockEntity ped && ped.hasItem()) {
                hasItem = true;
            }

            // +2 for having a pedestal
            pedSym += 2;
            // +1 if pedestal has an item
            if (hasItem) {
                pedSym += 1;
            }

            // Check mirror position
            BlockPos mirrorPos = new BlockPos(worldPosition.getX() + offsetX, pedPos.getY(), worldPosition.getZ() + offsetZ);
            BlockEntity mirrorBE = level.getBlockEntity(mirrorPos);
            if (mirrorBE instanceof ArcanePedestalBlockEntity) {
                pedSym -= 2; // -2 if mirror also has pedestal
                if (((ArcanePedestalBlockEntity) mirrorBE).hasItem() && hasItem) {
                    pedSym -= 1; // -1 if BOTH have items
                }
            } else {
                // No mirror found — this is an unmatched pedestal
            }
        }

        // Stabilizer symmetry (float, cast to int at end)
        float stabSym = 0.0F;
        stabilizerCount = foundStabilizers.size();
        for (BlockPos stabPos : foundStabilizers) {
            int offsetX = worldPosition.getX() - stabPos.getX();
            int offsetZ = worldPosition.getZ() - stabPos.getZ();

            stabSym += 0.1F;

            // Check mirror position
            BlockPos mirrorPos = new BlockPos(worldPosition.getX() + offsetX, stabPos.getY(), worldPosition.getZ() + offsetZ);
            if (level.getBlockState(mirrorPos).is(STABILIZERS_TAG)) {
                stabSym -= 0.2F; // matched pair: net -0.1
            }
        }

        symmetry = (int) (pedSym + stabSym);
    }

    // --- Structure validation (TC4: validLocation) ---

    /**
     * Check if the altar structure is still intact:
     * - Central pedestal at y-2
     * - 4 pillars at corners of y-2
     */
    private boolean validLocation() {
        if (level == null) {
            return false;
        }

        BlockPos center = worldPosition;
        BlockPos level1Center = center.below(2);

        // Central pedestal
        BlockEntity te = level.getBlockEntity(level1Center);
        if (!(te instanceof ArcanePedestalBlockEntity)) {
            return false;
        }

        // 4 pillars at corners
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                te = level.getBlockEntity(level1Center.offset(dx, 0, dz));
                if (!(te instanceof InfusionPillarBlockEntity)) {
                    return false;
                }
            }
        }

        return true;
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
        tag.putInt("symmetry", symmetry);
        tag.putInt("recipeInstability", recipeInstability);
        tag.putInt("itemCount", itemCount);

        // Save consumedComponents
        if (consumedComponents != null) {
            int[] intArr = new int[consumedComponents.length];
            for (int i = 0; i < consumedComponents.length; i++) {
                intArr[i] = consumedComponents[i] ? 1 : 0;
            }
            tag.putIntArray("consumedComponents", intArr);
        }

        // Save syncedComponents (for client rendering)
        net.minecraft.nbt.ListTag componentsTag = new net.minecraft.nbt.ListTag();
        for (ItemStack stack : syncedComponents) {
            componentsTag.add(stack.saveOptional(registries));
        }
        tag.put("syncedComponents", componentsTag);

        // Save recipeEssentia
        CompoundTag essentiaTag = new CompoundTag();
        for (Map.Entry<Aspect, Integer> entry : recipeEssentia.entrySet()) {
            essentiaTag.putInt(entry.getKey().getName(), entry.getValue());
        }
        tag.put("recipeEssentia", essentiaTag);

        // Save recipe reference for chunk reload
        if (currentRecipeId != null) {
            tag.putString("recipeId", currentRecipeId.toString());
        }
        if (!currentRecipeInput.isEmpty()) {
            tag.put("recipeInput", currentRecipeInput.saveOptional(registries));
        }
        if (!currentRecipeOutput.isEmpty()) {
            tag.put("recipeOutput", currentRecipeOutput.saveOptional(registries));
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        active = tag.getBoolean("active");
        crafting = tag.getBoolean("crafting");
        craftCount = tag.getInt("craftCount");
        startUp = tag.getFloat("startUp");
        instability = tag.getInt("instability");
        symmetry = tag.getInt("symmetry");
        recipeInstability = tag.getInt("recipeInstability");
        itemCount = tag.getInt("itemCount");

        // Load consumedComponents
        if (tag.contains("consumedComponents")) {
            int[] intArr = tag.getIntArray("consumedComponents");
            consumedComponents = new boolean[intArr.length];
            for (int i = 0; i < intArr.length; i++) {
                consumedComponents[i] = intArr[i] != 0;
            }
        } else {
            consumedComponents = null;
        }

        // Load syncedComponents
        syncedComponents.clear();
        if (tag.contains("syncedComponents")) {
            net.minecraft.nbt.ListTag componentsTag = tag.getList("syncedComponents", 10);
            for (int i = 0; i < componentsTag.size(); i++) {
                syncedComponents.add(ItemStack.parseOptional(registries, componentsTag.getCompound(i)));
            }
        }

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

        // Load recipe reference — will be restored from RecipeManager on first tick
        recipeRestored = false;
        currentRecipe = null;
        if (tag.contains("recipeId")) {
            currentRecipeId = ResourceLocation.tryParse(tag.getString("recipeId"));
        } else {
            currentRecipeId = null;
        }
        currentRecipeInput = tag.contains("recipeInput") ? ItemStack.parseOptional(registries, tag.getCompound("recipeInput")) : ItemStack.EMPTY;
        currentRecipeOutput = tag.contains("recipeOutput") ? ItemStack.parseOptional(registries, tag.getCompound("recipeOutput")) : ItemStack.EMPTY;
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
