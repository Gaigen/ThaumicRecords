package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.menu.ThaumatoriumMenu;
import team.torka.thaumicrecords.recipe.CrucibleRecipe;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThaumatoriumBlockEntity extends BlockEntity implements MenuProvider, Container {

    public ItemStack inputStack = ItemStack.EMPTY;
    public AspectList essentia = new AspectList();

    public final List<ResourceLocation> recipeIds = new ArrayList<>();
    public final List<AspectList> recipeEssentia = new ArrayList<>();
    public final List<String> recipePlayers = new ArrayList<>();

    public int currentCraft = -1;
    public int maxRecipes = 1;
    public Direction facing = Direction.NORTH;
    public ResourceLocation currentSuction = null;

    public CrucibleRecipe currentRecipe = null;
    int venting = 0;
    int counter = 0;
    boolean heated = false;

    // === Event handler for menu (changes → updateRecipes) ===
    @Nullable
    public ThaumatoriumMenu eventHandler;

    public ThaumatoriumBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.THAUMATORIUM.get(), pos, state);
    }

    // ========== TICK ==========

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThaumatoriumBlockEntity be) {
        if (level.isClientSide) {
            return;
        }

        // Every ~2 seconds: check heat + upgrades
        if (be.counter == 0 || be.counter % 40 == 0) {
            be.heated = be.checkHeat();
            be.getUpgrades();
        }
        be.counter++;

        // If heated, no redstone, has recipes, every ~0.25s
        if (be.heated && !be.gettingPower() && be.counter % 5 == 0 && !be.recipeIds.isEmpty()) {

            if (be.inputStack.isEmpty()) {
                be.currentSuction = null;
                return;
            }

            // Find matching recipe for input
            if (be.currentCraft < 0 || be.currentCraft >= be.recipeIds.size() || be.currentRecipe == null || !be.currentRecipe.catalyst().test(be.inputStack)) {
                be.findMatchingRecipe();
            }

            if (be.currentCraft < 0 || be.currentCraft >= be.recipeIds.size()) {
                return;
            }

            boolean canOutput = false;
            if (be.level != null) {
                BlockPos outputPos = be.worldPosition.relative(be.facing);
                // TC4: if inventory exists and can't hold result → wait
                // For now: skip this check since we don't have full inventory system
            }

            boolean done = true;
            be.currentSuction = null;
            AspectList needed = be.recipeEssentia.get(be.currentCraft);
            for (var entry : needed.entrySet()) {
                if (be.essentia.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                    be.currentSuction = entry.getKey();
                    done = false;
                    break;
                }
            }

            if (done) {
                be.completeRecipe();
            } else if (be.currentSuction != null) {
                be.fill();
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ThaumatoriumBlockEntity be) {
        if (!level.isClientSide) {
            return;
        }
        if (be.venting > 0) {
            be.venting--;
            float fx = 0.1F - level.random.nextFloat() * 0.2F;
            float fz = 0.1F - level.random.nextFloat() * 0.2F;
            float fy = 0.1F - level.random.nextFloat() * 0.2F;
            float fx2 = 0.1F - level.random.nextFloat() * 0.2F;
            float fz2 = 0.1F - level.random.nextFloat() * 0.2F;
            float fy2 = 0.1F - level.random.nextFloat() * 0.2F;
            int color = 16777215;
            // TODO: spawn vent particles when particle system is ready
        }
    }


    public boolean checkHeat() {
        if (level == null) {
            return false;
        }
        BlockPos below = worldPosition.below(2);
        BlockState belowState = level.getBlockState(below);
        return belowState.is(Blocks.LAVA) || belowState.is(Blocks.FIRE) || belowState.is(Blocks.SOUL_FIRE) || belowState.getFluidState().is(FluidTags.LAVA);
    }

    public boolean gettingPower() {
        if (level == null) {
            return false;
        }
        return level.hasNeighborSignal(worldPosition) || level.hasNeighborSignal(worldPosition.below()) || level.hasNeighborSignal(worldPosition.above());
    }

    public void getUpgrades() {
        if (level == null) {
            return;
        }
        int mr = 1;
        for (int yy = 0; yy <= 1; yy++) {
            for (Direction dir : Direction.values()) {
                if (dir == Direction.DOWN || dir == facing) {
                    continue;
                }
                BlockPos checkPos = worldPosition.offset(dir.getStepX(), yy + dir.getStepY(), dir.getStepZ());
                // TC4: check for Brainbox (TileBrainbox) — not yet implemented in TR
                // For now: check if it's a specific upgrade block
                // TODO: implement Brainbox logic
            }
        }
        if (mr != maxRecipes) {
            maxRecipes = mr;
            while (recipeIds.size() > maxRecipes) {
                recipeIds.remove(recipeIds.size() - 1);
            }
            syncToClient();
        }
    }

    public void findMatchingRecipe() {
        if (level == null || inputStack.isEmpty()) {
            return;
        }
        for (int a = 0; a < recipeIds.size(); a++) {
            CrucibleRecipe recipe = findRecipeById(recipeIds.get(a));
            if (recipe != null && recipe.catalystMatches(inputStack)) {
                currentCraft = a;
                currentRecipe = recipe;
                return;
            }
        }
        currentCraft = -1;
        currentRecipe = null;
    }

    public void completeRecipe() {
        if (currentRecipe == null || currentCraft < 0 || currentCraft >= recipeIds.size()) {
            return;
        }
        if (level == null || inputStack.isEmpty()) {
            return;
        }

        // Check recipe match
        if (!currentRecipe.matches(essentia, inputStack)) {
            return;
        }

        // Consume input
        inputStack = ItemStack.EMPTY;

        // Consume essentia
        // TC4: essentia = new AspectList() — full clear after craft
        essentia = new AspectList();

        // Get output
        ItemStack output = currentRecipe.getResultItem(level.registryAccess());
        if (output.isEmpty()) {
            return;
        }

        // Try to put into adjacent inventory (facing direction)
        // TC4: InventoryUtils.placeItemStackIntoInventory + eject remaining
        BlockPos outputPos = worldPosition.relative(facing);
        ItemStack remaining = output;

        // For now: eject directly in front
        if (!remaining.isEmpty()) {
            double ex = worldPosition.getX() + 0.5D + facing.getStepX() * 0.66D;
            double ey = worldPosition.getY() + 0.33D;
            double ez = worldPosition.getZ() + 0.5D + facing.getStepZ() * 0.66D;
            ItemEntity ei = new ItemEntity(level, ex, ey, ez, remaining.copy());
            ei.setDeltaMovement(0.075F * facing.getStepX(), 0.02500000037252903D, 0.075F * facing.getStepZ());
            level.addFreshEntity(ei);
            level.blockEvent(worldPosition, getBlockState().getBlock(), 0, 0);
        }

        // Sound — TC4: random.fizz
        level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, SoundEvents.FIRE_EXTINGUISH,
                SoundSource.BLOCKS, 0.25F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

        currentCraft = -1;
        syncToClient();
        setChanged();
    }

    public void fill() {
        if (level == null || currentSuction == null || currentCraft < 0 || currentCraft >= recipeEssentia.size()) {
            return;
        }

        // TC4: check adjacent for IEssentiaTransport at y and y+1
        // Since essentia transport isn't in TR yet, this does nothing
        // TODO: implement when IEssentiaTransport is available
    }

    public int addToContainer(ResourceLocation tt, int am) {
        if (currentRecipe == null || currentCraft < 0 || currentCraft >= recipeEssentia.size()) {
            return am;
        }
        int ce = recipeEssentia.get(currentCraft).getOrDefault(tt, 0) - essentia.getOrDefault(tt, 0);
        if (ce <= 0) {
            return am;
        }
        int add = Math.min(ce, am);
        essentia.add(tt, add);
        syncToClient();
        setChanged();
        return am - add;
    }

    public int containerContains(ResourceLocation tt) {
        return essentia.getOrDefault(tt, 0);
    }

    // ========== RECIPE MANAGEMENT ==========

    @Nullable
    private CrucibleRecipe findRecipeById(ResourceLocation id) {
        if (level == null) {
            return null;
        }
        var recipes = level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.CRUCIBLE.get());
        for (var holder : recipes) {
            if (holder.id().equals(id) && holder.value() instanceof CrucibleRecipe cr) {
                return cr;
            }
        }
        return null;
    }

    public void toggleRecipe(ResourceLocation recipeId, String playerName) {
        // Check if already assigned → remove
        int idx = recipeIds.indexOf(recipeId);
        if (idx >= 0) {
            recipeIds.remove(idx);
            recipeEssentia.remove(idx);
            recipePlayers.remove(idx);
            if (currentCraft >= recipeIds.size()) {
                currentCraft = -1;
            }
            syncToClient();
            setChanged();
            return;
        }

        // Check if we have room
        if (recipeIds.size() >= maxRecipes) {
            return;
        }

        // Add
        CrucibleRecipe recipe = findRecipeById(recipeId);
        if (recipe != null) {
            recipeIds.add(recipeId);
            recipeEssentia.add(recipe.aspects().copy());
            recipePlayers.add(playerName);
            syncToClient();
            setChanged();
        }
    }

    public int getRecipeHashSize() {
        return recipeIds.size();
    }

    @Nullable
    public ItemStack getOutputForCycle(int index) {
        if (index < 0 || index >= recipeIds.size()) {
            return ItemStack.EMPTY;
        }
        CrucibleRecipe recipe = findRecipeById(recipeIds.get(index));
        if (recipe == null) {
            return ItemStack.EMPTY;
        }
        ItemStack out = recipe.getResultItem(level != null ? level.registryAccess() : null);
        return out.isEmpty() ? ItemStack.EMPTY : out.copy();
    }

    public Direction getFacing() {
        return facing;
    }

    // ========== NBT ==========

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (!inputStack.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            inputStack.save(registries, itemTag);
            tag.put("Input", itemTag);
        }

        tag.put("Essentia", essentia.writeToNBT());

        tag.putByte("Facing", (byte) facing.get3DDataValue());
        tag.putByte("MaxRecipes", (byte) maxRecipes);

        int[] hashes = new int[recipeIds.size()];
        for (int i = 0; i < recipeIds.size(); i++) {
            // Store as string-based IDs instead of int hashes
        }
        ListTag recipeList = new ListTag();
        for (ResourceLocation rl : recipeIds) {
            CompoundTag entry = new CompoundTag();
            entry.putString("RecipeId", rl.toString());
            recipeList.add(entry);
        }
        tag.put("Recipes", recipeList);

        // Recipe players
        ListTag playerList = new ListTag();
        for (String p : recipePlayers) {
            if (p != null && !p.isEmpty()) {
                playerList.add(StringTag.valueOf(p));
            }
        }
        tag.put("RecipePlayers", playerList);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("Input")) {
            inputStack = ItemStack.parseOptional(registries, tag.getCompound("Input"));
        } else {
            inputStack = ItemStack.EMPTY;
        }

        if (tag.contains("Essentia", Tag.TAG_COMPOUND) || tag.contains("Essentia", net.minecraft.nbt.Tag.TAG_LIST)) {
            essentia.readFromNBT(tag.get("Essentia"));
        } else {
            essentia.clear();
        }

        facing = Direction.from3DDataValue(tag.getByte("Facing"));
        maxRecipes = tag.getByte("MaxRecipes");
        if (maxRecipes < 1) {
            maxRecipes = 1;
        }

        recipeIds.clear();
        recipeEssentia.clear();
        recipePlayers.clear();

        if (tag.contains("Recipes")) {
            ListTag recipeList = tag.getList("Recipes", Tag.TAG_COMPOUND);
            for (int i = 0; i < recipeList.size(); i++) {
                String idStr = recipeList.getCompound(i).getString("RecipeId");
                ResourceLocation rl = ResourceLocation.parse(idStr);
                CrucibleRecipe recipe = findRecipeById(rl);
                if (recipe != null) {
                    recipeIds.add(rl);
                    recipeEssentia.add(recipe.aspects().copy());
                }
            }
        }

        if (tag.contains("RecipePlayers")) {
            ListTag playerList = tag.getList("RecipePlayers", Tag.TAG_STRING);
            for (int i = 0; i < playerList.size(); i++) {
                if (recipePlayers.size() > i) {
                    recipePlayers.set(i, playerList.getString(i));
                }
            }
        }
    }

    // ========== SYNC ==========

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

    public void syncToClient() {
        if (Objects.nonNull(level) && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ========== MENU ==========

    public AspectList getEssentia() {
        return essentia;
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumatorium");
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ThaumatoriumMenu menu = new ThaumatoriumMenu(id, inv, this);
        this.eventHandler = menu;
        menu.updateRecipes();
        return menu;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (eventHandler != null) {
            eventHandler.slotsChanged(this);
        }
    }

    // ========== Container ==========

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inputStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? inputStack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot != 0 || inputStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = inputStack.split(amount);
        if (inputStack.isEmpty()) {
            inputStack = ItemStack.EMPTY;
        }
        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack result = inputStack;
        inputStack = ItemStack.EMPTY;
        setChanged();
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        inputStack = ItemStack.EMPTY;
        setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }
}
