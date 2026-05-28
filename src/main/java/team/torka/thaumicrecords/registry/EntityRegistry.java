package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.entity.FollowingItemEntity;
import team.torka.thaumicrecords.entity.SpecialItemEntity;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> REGISTRAR = DeferredRegister.create(Registries.ENTITY_TYPE, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<FollowingItemEntity>> FOLLOWING_ITEM = REGISTRAR.register("following_item", () -> {
        @SuppressWarnings("unchecked") EntityType<FollowingItemEntity> type = (EntityType<FollowingItemEntity>) (EntityType<?>) EntityType.Builder.of(
                FollowingItemEntity::create, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(2).build("following_item");
        return type;
    });

    public static final DeferredHolder<EntityType<?>, EntityType<SpecialItemEntity>> SPECIAL_ITEM = REGISTRAR.register("special_item", () -> {
        @SuppressWarnings("unchecked") EntityType<SpecialItemEntity> type = (EntityType<SpecialItemEntity>) (EntityType<?>) EntityType.Builder.of(
                SpecialItemEntity::create, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(2).build("special_item");
        return type;
    });
}
