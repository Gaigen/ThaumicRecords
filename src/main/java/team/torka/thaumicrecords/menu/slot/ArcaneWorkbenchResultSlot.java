package team.torka.thaumicrecords.menu.slot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.data.component.WandItemComponent;
import team.torka.thaumicrecords.menu.ArcaneWorkbenchMenu;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.WandCapRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;

public class ArcaneWorkbenchResultSlot extends SlotItemHandler {
    private final ArcaneWorkbenchMenu menu;

    public ArcaneWorkbenchResultSlot(ArcaneWorkbenchMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @NotNull
    @Override
    public ItemStack remove(int amount) {
        return super.remove(amount);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(player, stack);
        if (player.level().isClientSide) {
            return;
        }
        AspectList cachedAspect = this.menu.getCachedAspect();
        if (Objects.isNull(cachedAspect)) {
            this.menu.consumeCraftingMaterials(1);
            this.menu.updateResultSlot();
            return;
        }
        ItemStack wand = this.menu.getWandStack();
        WandItemComponent data = wand.get(DataComponentRegistry.WAND_ITEM_DATA.get());
        if (data != null) {
            WandCap cap = WandCapRegistry.WAND_CAP_REGISTRY.get(data.getCap());
            WandItemComponent currentData = data;
            for (Map.Entry<ResourceLocation, Integer> entry : cachedAspect.entrySet()) {
                ResourceLocation aspectRl = entry.getKey();
                int actualCost = cachedAspect.getWithModifier(aspectRl, Objects.nonNull(cap) ? cap.getAspectCostModifier(aspectRl) : 1);
                currentData = currentData.consumeVis(aspectRl, actualCost);
            }
            wand.set(DataComponentRegistry.WAND_ITEM_DATA.get(), currentData);
        }
        this.menu.consumeCraftingMaterials(1);
        this.menu.updateResultSlot();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean mayPickup(Player player) {
        return !this.menu.isVisInsufficient();
    }
}
