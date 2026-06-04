package team.torka.thaumicrecords.data.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.data.AspectRegistrationData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AspectRegistrationManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    private static final Map<AspectRegistrationData.TargetType, Map<ResourceLocation, AspectList>> ASPECTS = new EnumMap<>(
            AspectRegistrationData.TargetType.class);

    static {
        for (AspectRegistrationData.TargetType type : AspectRegistrationData.TargetType.values()) {
            ASPECTS.put(type, new HashMap<>());
        }
    }

    public AspectRegistrationManager() {
        super(GSON, "aspect_registration");
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        ASPECTS.values().forEach(Map::clear);

        object.forEach((fileLocation, element) -> AspectRegistrationData.Container.CODEC.parse(JsonOps.INSTANCE, element)
                .resultOrPartial(error -> ThaumicRecords.LOGGER.error("deserialize file {} error: {}", fileLocation, error))
                .ifPresent(container -> {
                    for (AspectRegistrationData data : container.values()) {
                        AspectRegistrationData.KeyEntry key = data.key();
                        ResourceLocation targetRl = key.getLoc();

                        if (targetRl == null) {
                            ThaumicRecords.LOGGER.warn("file {} contains unsupported key", fileLocation);
                            continue;
                        }

                        if (key.isTag()) {
                            resolveAndRegisterTag(data.type(), targetRl, data.aspects(), data.replace());
                        } else {
                            if (validateId(data.type(), targetRl)) {
                                commitToRegistry(data.type(), targetRl, data.aspects(), data.replace());
                            } else {
                                ThaumicRecords.LOGGER.warn("unknown key in file {} -> type: {}, id: {}", fileLocation, data.type(), targetRl);
                            }
                        }
                    }
                }));

        ThaumicRecords.LOGGER.info("load aspect registration ended.");
        ASPECTS.forEach((k, v) -> {
            ThaumicRecords.LOGGER.info("{} -> {}", k.getSerializedName(), v.size());
        });
    }

    private static void commitToRegistry(AspectRegistrationData.TargetType type, ResourceLocation id, Map<ResourceLocation, Integer> newAspects,
                                         boolean replace) {
        Map<ResourceLocation, Integer> currentAspects = ASPECTS.get(type).computeIfAbsent(id, k -> new AspectList());
        if (replace) {
            currentAspects.clear();
        }
        currentAspects.putAll(newAspects);
    }

    private void resolveAndRegisterTag(AspectRegistrationData.TargetType type, ResourceLocation tagRl, Map<ResourceLocation, Integer> aspects,
                                       boolean replace) {
        switch (type) {
            case ITEM -> {
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagRl);
                Optional<HolderSet.Named<Item>> tagElements = BuiltInRegistries.ITEM.getTag(tagKey);
                tagElements.ifPresentOrElse(holders -> holders.forEach(holder -> {
                    ResourceLocation itemRl = BuiltInRegistries.ITEM.getKey(holder.value());
                    commitToRegistry(type, itemRl, aspects, replace);
                }), () -> ThaumicRecords.LOGGER.warn("no item tagged with #{}", tagRl));
            }
            case BLOCK -> {
                TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tagRl);
                Optional<HolderSet.Named<Block>> tagElements = BuiltInRegistries.BLOCK.getTag(tagKey);
                tagElements.ifPresentOrElse(holders -> holders.forEach(holder -> {
                    ResourceLocation blockRl = BuiltInRegistries.BLOCK.getKey(holder.value());
                    commitToRegistry(type, blockRl, aspects, replace);
                }), () -> ThaumicRecords.LOGGER.warn("no block tagged with #{}", tagRl));
            }
            case ENTITY -> {
                TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tagRl);
                Optional<HolderSet.Named<EntityType<?>>> tagElements = BuiltInRegistries.ENTITY_TYPE.getTag(tagKey);
                tagElements.ifPresentOrElse(holders -> holders.forEach(holder -> {
                    ResourceLocation entityRl = BuiltInRegistries.ENTITY_TYPE.getKey(holder.value());
                    commitToRegistry(type, entityRl, aspects, replace);
                }), () -> ThaumicRecords.LOGGER.warn("no entity tagged with #{}", tagRl));
            }
        }
    }

    private boolean validateId(AspectRegistrationData.TargetType type, ResourceLocation id) {
        return switch (type) {
            case ITEM -> BuiltInRegistries.ITEM.containsKey(id);
            case BLOCK -> BuiltInRegistries.BLOCK.containsKey(id);
            case ENTITY -> BuiltInRegistries.ENTITY_TYPE.containsKey(id);
            case POTION -> BuiltInRegistries.POTION.containsKey(id);
        };
    }

    public static AspectList getAspectsFor(AspectRegistrationData.TargetType type, ResourceLocation id) {
        return ASPECTS.get(type).getOrDefault(id, AspectList.empty());
    }
}
