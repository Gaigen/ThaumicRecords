package team.torka.thaumicrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.menu.ArcaneWorkbenchMenu;
import team.torka.thaumicrecords.recipe.ArcaneCraftingShapedRecipe;
import team.torka.thaumicrecords.registry.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class ArcaneWorkbenchBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot != ArcaneWorkbenchMenu.SLOT_CRAFT_RESULT) {
                updateRecipeOutput();
            }
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public ArcaneWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ARCANE_WORKBENCH.get(), pos, state);
    }

    public void updateRecipeOutput() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(this.inventory.getStackInSlot(i));
        }
        CraftingInput input = CraftingInput.of(3, 3, stacks);

        Optional<RecipeHolder<ArcaneCraftingShapedRecipe>> arcaneRecipe = this.level.getRecipeManager().getRecipeFor(
                RecipeTypeRegistry.ARCANE_CRAFTING_SHAPED.get(), input, this.level);
        if (arcaneRecipe.isPresent()) {
            ArcaneCraftingShapedRecipe recipe = arcaneRecipe.get().value();
            ItemStack wand = this.inventory.getStackInSlot(10);
            if (canCraftArcane(recipe, wand)) {
                ItemStack result = recipe.assemble(input, this.level.registryAccess());
                this.inventory.setStackInSlot(ArcaneWorkbenchMenu.SLOT_CRAFT_RESULT, result);
                return;
            }
        }
        Optional<RecipeHolder<CraftingRecipe>> vanillaRecipe = this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, this.level);
        if (vanillaRecipe.isPresent()) {
            ItemStack result = vanillaRecipe.get().value().assemble(input, this.level.registryAccess());
            this.inventory.setStackInSlot(ArcaneWorkbenchMenu.SLOT_CRAFT_RESULT, result);
        } else {
            this.inventory.setStackInSlot(ArcaneWorkbenchMenu.SLOT_CRAFT_RESULT, ItemStack.EMPTY);
        }
    }

    private boolean canCraftArcane(ArcaneCraftingShapedRecipe recipe, ItemStack wand) {
        if (wand.isEmpty() || (wand.getItem() != ItemRegistry.WAND.asItem())) {
            return false;
        }
        // TODO 检查研究
//        recipe.requiredResearch();
        WandItemComponent data = wand.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (Objects.isNull(data)) {
            return false;
        }
        AspectList wandStorage = data.getAspects();
        WandCap wandCap = WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap());
        if (Objects.isNull(wandCap)) {
            return false;
        }
        AspectList cost = recipe.baseVisCost();
        for (Map.Entry<ResourceLocation, Integer> entry : cost.entrySet()) {
            ResourceLocation rl = entry.getKey();
            Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
            if (Objects.isNull(aspect) || !aspect.isPrimal()) {
                continue;
            }
            Integer wandVis = wandStorage.getOrDefault(entry.getKey(), 0);
            Integer baseCostVis = cost.get(entry.getKey());
            double modifier = wandCap.getAspectCostModifier(rl);
            if (wandVis < (int) (baseCostVis * modifier)) {
                return false;
            }
        }
        return true;
    }


    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable(ThaumicRecords.createTranslationKey("container", "arcane_workbench"));
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ArcaneWorkbenchMenu(id, inventory, this);
    }


    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
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
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }
}