package team.torka.thaumicrecords.api.entity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.registry.AspectRegistry;

/**
 * Реестр аспектов сущностей — аналог TC4 {@code ThaumcraftApi.scanEntities}. Аспекты извлекаются в котёл при варке моба.
 */
public class EntityAspectRegistry {

    private static final Map<EntityType<?>, AspectList> ENTITY_ASPECTS = new HashMap<>();

    public static void init() {
        register(EntityType.ZOMBIE,
                new AspectList().add(AspectRegistry.MORTUUS.getId(), 2).add(AspectRegistry.HUMANUS.getId(), 1).add(AspectRegistry.TERRA.getId(), 1));

        register(EntityType.SKELETON,
                new AspectList().add(AspectRegistry.MORTUUS.getId(), 3).add(AspectRegistry.HUMANUS.getId(), 1).add(AspectRegistry.TERRA.getId(), 1));

        register(EntityType.CREEPER, new AspectList().add(AspectRegistry.HERBA.getId(), 2).add(AspectRegistry.IGNIS.getId(), 2));

        register(EntityType.SPIDER, new AspectList().add(AspectRegistry.BESTIA.getId(), 3).add(AspectRegistry.PERDITIO.getId(), 2));

        // Passive mobs
        register(EntityType.COW, new AspectList().add(AspectRegistry.BESTIA.getId(), 3).add(AspectRegistry.TERRA.getId(), 3));

        register(EntityType.PIG, new AspectList().add(AspectRegistry.BESTIA.getId(), 2).add(AspectRegistry.TERRA.getId(), 2));

        register(EntityType.SHEEP, new AspectList().add(AspectRegistry.BESTIA.getId(), 2).add(AspectRegistry.TERRA.getId(), 2));

        register(EntityType.CHICKEN,
                new AspectList().add(AspectRegistry.BESTIA.getId(), 2).add(AspectRegistry.VOLATUS.getId(), 2).add(AspectRegistry.AER.getId(), 1));
    }

    private static void register(EntityType<?> type, AspectList aspects) {
        ENTITY_ASPECTS.put(type, aspects);
    }

    /** Получить аспекты сущности */
    public static AspectList getAspects(Entity entity) {
        AspectList aspects = ENTITY_ASPECTS.get(entity.getType());
        return aspects != null ? aspects.copy() : null;
    }

    /** Проверить есть ли аспекты у сущности */
    public static boolean hasAspects(Entity entity) {
        return ENTITY_ASPECTS.containsKey(entity.getType());
    }
}
