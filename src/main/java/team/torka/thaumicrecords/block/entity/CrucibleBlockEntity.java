package team.torka.thaumicrecords.block.entity;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.ModTags;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.AspectHelper;
import team.torka.thaumicrecords.client.particle.CrucibleBubbleParticle;
import team.torka.thaumicrecords.recipe.CrucibleRecipe;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.BlockEntityRegistry;
import team.torka.thaumicrecords.registry.ParticleRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.Color;
import java.util.Objects;

public class CrucibleBlockEntity extends BlockEntity {

    public static final int MAX_FLUID = 1000;
    public static final int BOTTLE_AMOUNT = 500;
    public static final int RAIN_AMOUNT = 1;
    public static final short HEAT_MAX = 200;
    public static final short HEAT_BOILING = 150;

    private int fluidLevel = 0;
    private short heat = 0;
    private final AspectList aspects = new AspectList();
    public long counter = -100;

    public CrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.CRUCIBLE.get(), pos, blockState);
    }


    public int getFluidLevel() {
        return fluidLevel;
    }

    public short getHeat() {
        return heat;
    }

    public AspectList getAspects() {
        return aspects;
    }

    public boolean hasFluid() {
        return fluidLevel > 0;
    }

    public boolean isBoiling() {
        return heat >= HEAT_BOILING && hasFluid();
    }

    /**
     *
     */
    public int tagAmount() {
        int total = 0;
        for (int amount : aspects.values()) {
            total += amount;
        }
        return total;
    }

    /**
     *
     */
    public float getRecolor() {
        float recolor = tagAmount() / 100.0f;
        if (recolor > 0.0f) {
            recolor = 0.5f + recolor / 2.0f;
        }
        return Math.min(recolor, 1.9f);
    }

    /**
     * base = 0.3 + 0.5 * fluidAmount/capacity
     * out  = base + tagAmount/100 * (1 - base)
     * out == 1.0  → 0.9999
     * out >  1.0  → 1.001
     */
    public float getFluidHeight() {
        float base = 0.3f + 0.5f * fluidLevel / (float) MAX_FLUID;
        float out = base + tagAmount() / 100.0f * (1.0f - base);
        if (out > 1.0f) {
            out = 1.001f;
        }
        if (out == 1.0f) {
            out = 0.9999f;
        }
        return out;
    }


    public boolean fillWithWater() {
        if (fluidLevel >= MAX_FLUID) {
            return false;
        }
        fluidLevel = MAX_FLUID;
        setChanged();
        syncToClient();
        playSound(SoundEvents.BUCKET_EMPTY);
        return true;
    }

    public boolean fillWithBottle() {
        if (fluidLevel >= MAX_FLUID) {
            return false;
        }
        fluidLevel = Math.min(fluidLevel + BOTTLE_AMOUNT, MAX_FLUID);
        setChanged();
        syncToClient();
        playSound(SoundEvents.BOTTLE_EMPTY);
        return true;
    }

    public boolean drainToBucket() {
        if (fluidLevel <= 0) {
            return false;
        }
        fluidLevel = 0;
        aspects.clear();
        setChanged();
        syncToClient();
        playSound(SoundEvents.BUCKET_FILL);
        return true;
    }

    /**
     *
     * this.tank.setFluid(null);
     * for (int a = 0; a < this.aspects.visSize() / 2; a++) spill();
     * this.aspects = new AspectList();
     *
     */
    public void spillRemnants() {
        if (fluidLevel <= 0 && tagAmount() <= 0) {
            return;
        }
        fluidLevel = 0;
        int spillCount = tagAmount() / 2;

        ResourceLocation[] aspectKeys = aspects.keySet().toArray(new ResourceLocation[0]);

        aspects.clear();

        if (level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, SoundRegistry.SPILL.get(), SoundSource.BLOCKS, 0.2f, 1.0f);

            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 10; i++) {
                    float bx = (float) (worldPosition.getX() + 0.2 + level.random.nextFloat() * 0.6);
                    float by = (float) (worldPosition.getY() + 1.0);
                    float bz = (float) (worldPosition.getZ() + 0.2 + level.random.nextFloat() * 0.6);

                    serverLevel.sendParticles(ParticleRegistry.CRUCIBLE_BUBBLE.get(), bx, by, bz, 1, 0.0, 0.0, 0.0, 0.01);
                }
            }
        }

        setChanged();
        syncToClient();
    }

    public void addRain() {
        if (fluidLevel >= MAX_FLUID) {
            return;
        }
        fluidLevel = Math.min(fluidLevel + RAIN_AMOUNT, MAX_FLUID);
        setChanged();
        syncToClient();
    }


    public void addAspects(AspectList newAspects) {
        aspects.merge(newAspects);
        setChanged();
        syncToClient();
    }

    /**
     *
     */
    public AspectList takeRandomFromSource() {
        AspectList output = new AspectList();
        if (!aspects.isEmpty()) {
            ResourceLocation[] keys = aspects.keySet().toArray(new ResourceLocation[0]);
            ResourceLocation randomKey = keys[level.random.nextInt(keys.length)];
            output.put(randomKey, 1);
            int current = aspects.getOrDefault(randomKey, 0);
            if (current <= 1) {
                aspects.remove(randomKey);
            } else {
                aspects.put(randomKey, current - 1);
            }
        }
        setChanged();
        syncToClient();
        return output;
    }

    /**
     *
     */
    public void removeAspect(ResourceLocation aspectKey) {
        removeAspect(aspectKey, true);
    }

    public void removeAspect(ResourceLocation aspectKey, boolean sync) {
        int current = aspects.getOrDefault(aspectKey, 0);
        if (current <= 1) {
            aspects.remove(aspectKey);
        } else {
            aspects.put(aspectKey, current - 1);
        }
        if (sync) {
            setChanged();
            syncToClient();
        }
    }


    /**
     *
     */
    public static class SmeltResult {
        public ItemStack remainingOut = ItemStack.EMPTY;
        public ItemStack craftOut = ItemStack.EMPTY;
        public boolean bounced = false;
    }

    /**
     *
     */
    public SmeltResult attemptSmelt(ItemStack itemStack) {
        SmeltResult result = new SmeltResult();

        int stacksize = itemStack.getCount();
        int remaining = stacksize;

        boolean hadCraft = false;
        boolean hadDissolve = false;

        for (int a = 0; a < stacksize; a++) {
            CrucibleRecipe recipe = findMatchingRecipe(itemStack);

            if (recipe != null && fluidLevel > 0) {
                for (var entry : recipe.requiredAspects().entrySet()) {
                    ResourceLocation aspectKey = entry.getKey();
                    int cost = entry.getValue();
                    int current = aspects.getOrDefault(aspectKey, 0);
                    if (current <= cost) {
                        aspects.remove(aspectKey);
                    } else {
                        aspects.put(aspectKey, current - cost);
                    }
                }
                fluidLevel = Math.max(fluidLevel - 50, 0);
                if (fluidLevel <= 0) {
                    aspects.clear();
                }

                result.craftOut = recipe.getResultItem(level.registryAccess()).copy();
                remaining--;
                hadCraft = true;

            } else {
                AspectList itemAspects = AspectHelper.getAspects(itemStack);

                if (itemAspects == null || itemAspects.isEmpty()) {
                    result.bounced = true;
                    result.remainingOut = itemStack.copy();
                    result.remainingOut.setCount(remaining);
                    setChanged();
                    syncToClient();
                }

                aspects.merge(itemAspects);
                remaining--;
                hadDissolve = true;
            }
        }

        if (hadCraft) {

        } else if (hadDissolve) {

        }

        if (remaining <= 0) {
            result.remainingOut = ItemStack.EMPTY;
        } else {
            result.remainingOut = itemStack.copy();
            result.remainingOut.setCount(remaining);
        }

        setChanged();
        syncToClient();
        return result;
    }

    /**
     *
     */
    @Nullable
    private CrucibleRecipe findMatchingRecipe(ItemStack catalyst) {
        if (level == null) {
            return null;
        }

        var recipes = level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.CRUCIBLE.get());
        for (var holder : recipes) {
            CrucibleRecipe recipe = holder.value();
            if (!recipe.catalyst().test(catalyst)) {
                continue;
            }

            boolean aspectsMatch = true;
            for (var entry : recipe.requiredAspects().entrySet()) {
                int available = aspects.getOrDefault(entry.getKey(), 0);
                if (available < entry.getValue()) {
                    aspectsMatch = false;
                    break;
                }
            }
            if (aspectsMatch) {
                return recipe;
            }
        }
        return null;
    }


    private boolean hasHeatSource() {
        if (level == null) {
            return false;
        }
        BlockState below = level.getBlockState(worldPosition.below());
        return below.is(ModTags.CRUCIBLE_HEAT_SOURCE);
    }


    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        if (level.isClientSide) {
            return;
        }

        crucible.counter++;

        if (level.isRainingAt(pos.above())) {
            crucible.addRain();
        }

        short prevHeat = crucible.heat;
        boolean hasHeat = crucible.hasHeatSource();

        if (crucible.hasFluid()) {
            if (hasHeat) {
                if (crucible.heat < HEAT_MAX) {
                    crucible.heat++;
                }
            } else if (crucible.heat > 0) {
                crucible.heat--;
            }
        } else if (crucible.heat > 0) {
            crucible.heat--;
        }

        if ((prevHeat < HEAT_BOILING && crucible.heat >= HEAT_BOILING) || (prevHeat >= HEAT_BOILING && crucible.heat < HEAT_BOILING)) {
            crucible.setChanged();
            crucible.syncToClient();
        }

        if (crucible.tagAmount() > 100 && crucible.counter % 5L == 0L) {
            crucible.takeRandomFromSource();
            // TODO
        }

        if (crucible.counter > 100L && crucible.heat > HEAT_BOILING) {
            crucible.counter = 0L;

            if (crucible.tagAmount() > 0) {
                ResourceLocation[] keys = crucible.aspects.keySet().toArray(new ResourceLocation[0]);
                int s = keys.length;

                ResourceLocation randomKey = keys[level.random.nextInt(s)];
                Aspect a = AspectRegistry.ASPECT_REGISTRY.get(randomKey);

                if (a != null && a.isPrimal()) {
                    randomKey = keys[level.random.nextInt(s)];
                    a = AspectRegistry.ASPECT_REGISTRY.get(randomKey);
                }

                if (a != null) {
                    crucible.fluidLevel = Math.max(crucible.fluidLevel - 2, 0);
                    if (crucible.fluidLevel <= 0) {
                        crucible.aspects.clear();
                    }

                    crucible.removeAspect(randomKey, false);

                    if (!a.isPrimal()) {
                        Aspect[] comps = a.getComponents();
                        if (comps != null && comps.length == 2) {
                            Aspect chosen = level.random.nextBoolean() ? comps[0] : comps[1];
                            ResourceLocation compKey = AspectRegistry.ASPECT_REGISTRY.getResourceKey(chosen).map(k -> k.location()).orElse(null);
                            if (compKey != null) {
                                crucible.aspects.merge(compKey, 1, Integer::sum);
                            }
                        }
                    } else {
                        // TODO
                    }
                }
            }

            crucible.setChanged();
            crucible.syncToClient();
        }
    }


    public static void clientTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        if (!level.isClientSide) {
            return;
        }
        if (!(level instanceof ClientLevel clientLevel)) {
            return;
        }
        if (!crucible.hasFluid()) {
            return;
        }

        if (crucible.heat > HEAT_BOILING) {
            CrucibleBubbleParticle.create(clientLevel, pos.getX() + 0.2 + level.random.nextFloat() * 0.6, pos.getY() + crucible.getFluidHeight(),
                    pos.getZ() + 0.2 + level.random.nextFloat() * 0.6, -4).setFroth().setRGB(0.5f, 0.5f, 0.7f);

            if (crucible.tagAmount() > 100) {
                for (int a = 0; a < 2; a++) {
                    CrucibleBubbleParticle.create(clientLevel, pos.getX(), pos.getY() + 1, pos.getZ() + level.random.nextFloat(), -4).setFroth2().setRGB(0.5f,
                            0.5f, 0.7f);
                    CrucibleBubbleParticle.create(clientLevel, pos.getX() + 1, pos.getY() + 1, pos.getZ() + level.random.nextFloat(), -4).setFroth2().setRGB(
                            0.5f, 0.5f, 0.7f);
                    CrucibleBubbleParticle.create(clientLevel, pos.getX() + level.random.nextFloat(), pos.getY() + 1, pos.getZ(), -4).setFroth2().setRGB(0.5f,
                            0.5f, 0.7f);
                    CrucibleBubbleParticle.create(clientLevel, pos.getX() + level.random.nextFloat(), pos.getY() + 1, pos.getZ() + 1, -4).setFroth2().setRGB(
                            0.5f, 0.5f, 0.7f);
                }
            }
        }

        if (level.random.nextInt(6) == 0 && !crucible.aspects.isEmpty()) {
            ResourceLocation[] keys = crucible.aspects.keySet().toArray(new ResourceLocation[0]);
            ResourceLocation randomKey = keys[level.random.nextInt(keys.length)];
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(randomKey);
            if (aspect != null) {
                Color c = new Color(aspect.getARGBColor());
                float r = c.getRed() / 255.0f;
                float g = c.getGreen() / 255.0f;
                float b = c.getBlue() / 255.0f;

                int px = 5 + level.random.nextInt(22);
                int pz = 5 + level.random.nextInt(22);

                CrucibleBubbleParticle.create(clientLevel, pos.getX() + px / 32.0f + 0.015625f, pos.getY() + 0.05f + crucible.getFluidHeight(),
                        pos.getZ() + pz / 32.0f + 0.015625f, 1).setRGB(r, g, b);
            }
        }
    }


    // === NBT ===

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("FluidLevel", fluidLevel);
        tag.putShort("Heat", heat);
        if (!aspects.isEmpty()) {
            tag.put("Aspects", aspects.writeToNBT());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidLevel = tag.getInt("FluidLevel");
        heat = tag.getShort("Heat");
        if (tag.contains("Aspects", Tag.TAG_COMPOUND)) {
            aspects.readFromNBT(tag.getCompound("Aspects"));
        } else {
            aspects.clear();
        }
    }


    @Override
    @ParametersAreNonnullByDefault
    public boolean triggerEvent(int id, int type) {
        if (id == 2 && level != null && level.isClientSide) {

            level.playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, SoundRegistry.SPILL.get(),
                    SoundSource.BLOCKS, 0.2f, 1.0f, false);

            for (int q = 0; q < 10; q++) {
                float bx = worldPosition.getX() + 0.2f + level.random.nextFloat() * 0.6f;
                float by = worldPosition.getY() + 0.1f + getFluidHeight();
                float bz = worldPosition.getZ() + 0.2f + level.random.nextFloat() * 0.6f;

                CrucibleBubbleParticle bubble = CrucibleBubbleParticle.create((ClientLevel) level, bx, by, bz, 3).setBubbleSpeed(0.003 * type);

                if (aspects.isEmpty()) {
                    bubble.setRGB(1.0f, 1.0f, 1.0f);
                } else {
                    ResourceLocation[] keys = aspects.keySet().toArray(new ResourceLocation[0]);
                    ResourceLocation randomKey = keys[level.random.nextInt(keys.length)];
                    Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(randomKey);
                    if (aspect != null) {
                        java.awt.Color c = new java.awt.Color(aspect.getARGBColor());
                        bubble.setRGB(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
                    }
                }
            }
            return true;
        }
        return false;
    }


    private void syncToClient() {
        if (Objects.nonNull(level) && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void playSound(SoundEvent sound) {
        if (Objects.nonNull(level) && !level.isClientSide) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
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


    /**
     *
     */
    public boolean onWandRightClick(ItemStack wandStack, Player player) {
        System.out.println("[CRUCIBLE] onWandRightClick called, sneaking=" + player.isShiftKeyDown());
        if (player.isShiftKeyDown()) {
            spillRemnants();
            if (Objects.nonNull(level)) {
                level.playSound(null, worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            return true;
        }
        return false;
    }
}