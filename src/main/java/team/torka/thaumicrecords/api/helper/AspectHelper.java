package team.torka.thaumicrecords.api.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.aspect.IEssentiaContainerItem;
import team.torka.thaumicrecords.attachment.AspectDiscovery;
import team.torka.thaumicrecords.data.AspectRegistrationData;
import team.torka.thaumicrecords.data.manager.AspectRegistrationManager;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.AttachmentRegistry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AspectHelper {

    public static AspectList getAspects(ItemStack stack) {
        if (stack.isEmpty()) {
            return AspectList.empty();
        }
        Item item = stack.getItem();
        ResourceLocation itemRl = BuiltInRegistries.ITEM.getKey(item);
        AspectList aspects = AspectRegistrationManager.getAspectsFor(AspectRegistrationData.TargetType.ITEM, itemRl);
        if (item instanceof IEssentiaContainerItem essentiaContainer) {
            AspectList readed = essentiaContainer.getAspects(stack);
            aspects.merge(readed);
        }
        return aspects;
    }

    public static void discoverAspect(ServerPlayer player, ResourceLocation aspect) {
        AspectDiscovery oldData = player.getData(AttachmentRegistry.ASPECT_DISCOVERY);
        if (!oldData.discovered().contains(aspect)) {
            Set<ResourceLocation> newSet = new HashSet<>(oldData.discovered());
            newSet.add(aspect);
            AspectDiscovery newData = new AspectDiscovery(newSet);
            player.setData(AttachmentRegistry.ASPECT_DISCOVERY, newData);
        }
    }

    public static boolean isAspectDiscovered(Player player, ResourceLocation aspect) {
        AspectDiscovery oldData = player.getData(AttachmentRegistry.ASPECT_DISCOVERY);
        return oldData.discovered().contains(aspect);
    }

    @Nullable
    public static ResourceLocation getAspectCombined(Aspect a1, Aspect a2) {
        Aspect aspect = AspectRegistry.ASPECT_REGISTRY.stream().filter(a -> !a.isPrimal() && Objects.nonNull(a.getComponents())).filter(
                a -> a.getComponents().length >= 2).filter(a -> {
            Aspect[] components = a.getComponents();
            return (components[0] == a1 && components[1] == a2) || (components[0] == a2 && components[1] == a1);
        }).findFirst().orElse(null);
        if (Objects.isNull(aspect)) {
            return null;
        }
        return AspectRegistry.ASPECT_REGISTRY.getKey(aspect);
    }
}
