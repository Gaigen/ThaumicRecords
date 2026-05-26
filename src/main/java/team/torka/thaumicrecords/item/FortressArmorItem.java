package team.torka.thaumicrecords.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.IRunicArmor;
import team.torka.thaumicrecords.client.model.FortressArmorModel;
import team.torka.thaumicrecords.client.renderer.CustomModelLayer;
import team.torka.thaumicrecords.registry.DataComponentRegistry;

public class FortressArmorItem extends ArmorItem implements IRunicArmor {

    public FortressArmorItem(Type type, Holder<ArmorMaterial> material) {
        super(material, type, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(getMaxDurability(type)));
    }

    public static IClientItemExtensions getExtensions() {
        return new IClientItemExtensions() {
            private FortressArmorModel model;

            @Override
            @NotNull
            public HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity living, @NotNull ItemStack stack, @NotNull EquipmentSlot slot,
                                                          @NotNull HumanoidModel<?> original) {
                if (model == null) {
                    EntityModelSet models = Minecraft.getInstance().getEntityModels();
                    ModelPart part = models.bakeLayer(CustomModelLayer.FORTRESS_ARMOR);
                    model = new FortressArmorModel(part);
                }
                model.setupVisibility(living, slot);
                model.young = original.young;
                model.crouching = original.crouching;
                model.riding = original.riding;
                model.rightArmPose = original.rightArmPose;
                model.leftArmPose = original.leftArmPose;
                return model;
            }
        };
    }

    @Override
    public int getRunicCharge(@NotNull ItemStack itemstack) {
        return 0;
    }

    // Mask & Goggles helpers
    public static boolean hasGoggles(ItemStack stack) {
        return stack.has(DataComponentRegistry.FORTRESS_GOGGLES);
    }

    public static int getMask(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.FORTRESS_MASK, -1);
    }

    public static ItemStack withGoggles(ItemStack stack) {
        stack.set(DataComponentRegistry.FORTRESS_GOGGLES, true);
        return stack;
    }

    public static ItemStack withMask(ItemStack stack, int maskType) {
        stack.set(DataComponentRegistry.FORTRESS_MASK, maskType);
        return stack;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        return repair.is(net.minecraft.world.item.Items.NETHERITE_INGOT) || super.isValidRepairItem(toRepair, repair);
    }

    private static int getMaxDurability(Type type) {
        return switch (type) {
            case HELMET -> 462;
            case CHESTPLATE -> 672;
            case LEGGINGS -> 630;
            default -> 546;
        };
    }
}