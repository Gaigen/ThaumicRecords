package team.torka.thaumicrecords.api.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.attachment.AspectDiscovery;
import team.torka.thaumicrecords.recipe.AspectRecipe;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.RecipeTypeRegistry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AspectHelper {

    private static final Map<Item, AspectList> ITEM_ASPECTS_CACHE = new ConcurrentHashMap<>();

    public static AspectList getAspects(ItemStack stack) {
        if (stack.isEmpty()) {
            return AspectList.empty();
        }
        Item item = stack.getItem();
        AspectList cached = ITEM_ASPECTS_CACHE.get(item);
        if (Objects.nonNull(cached)) {
            return cached;
        }
        return AspectList.empty();
    }

    public static void rebuildAspectRegistrationRecipeCache(RecipeManager recipeManager) {
        ITEM_ASPECTS_CACHE.clear();
        List<RecipeHolder<AspectRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeTypeRegistry.ASPECT_REGISTRATION.get());
        for (RecipeHolder<AspectRecipe> holder : recipes) {
            AspectRecipe recipe = holder.value();
            if (recipe.replace()) {
                for (Ingredient.Value itemHolder : recipe.ingredient().getValues()) {
                    Collection<ItemStack> itemStacks = itemHolder.getItems();
                    for (ItemStack itemStack : itemStacks) {
                        Item item = itemStack.getItem();
                        ITEM_ASPECTS_CACHE.put(item, recipe.aspects().copy());
                    }
                }
            } else {
                for (Ingredient.Value itemHolder : recipe.ingredient().getValues()) {
                    Collection<ItemStack> itemStacks = itemHolder.getItems();
                    for (ItemStack itemStack : itemStacks) {
                        Item item = itemStack.getItem();
                        if (ITEM_ASPECTS_CACHE.containsKey(item)) {
                            ITEM_ASPECTS_CACHE.get(item).merge(recipe.aspects().copy());
                        } else {
                            ITEM_ASPECTS_CACHE.put(item, recipe.aspects().copy());
                        }
                    }
                }
            }
        }
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
