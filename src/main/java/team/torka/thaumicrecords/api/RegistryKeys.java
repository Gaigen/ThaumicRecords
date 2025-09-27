package team.torka.thaumicrecords.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.item.WandCap;
import team.torka.thaumicrecords.api.item.WandRod;

public interface RegistryKeys {
    ResourceKey<Registry<Aspect>> ASPECTS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID, "aspects"));
    ResourceKey<Registry<WandCap>> WAND_CAPS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID, "wand_caps"));
    ResourceKey<Registry<WandRod>> WAND_RODS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ThaumicRecords.MOD_ID, "wand_rods"));
}
