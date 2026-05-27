package team.torka.thaumicrecords.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public enum TierRegistry implements Tier {
    THAUMIUM(400, 7.0F, 2.0F, 22, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.THAUMIUM_INGOT.get())),
    VOID(150, 8.0F, 3.0F, 10, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.VOID_INGOT.get())),
    PRIMAL_VOID(500, 8.0F, 3.0F, 20, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.VOID_INGOT.get())),
    CRIMSON_VOID(200, 8.0F, 3.5F, 20, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.VOID_INGOT.get()));

    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final TagKey<Block> incorrectBlocksForDrops;
    private final Supplier<Ingredient> repairIngredient;

    TierRegistry(int uses, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Block> incorrectBlocksForDrops,
                 Supplier<Ingredient> repairIngredient) {
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
