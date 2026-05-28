package team.torka.thaumicrecords.event.listener;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import team.torka.thaumicrecords.item.ElementalPickaxeItem;
import team.torka.thaumicrecords.item.PrimalCrusherItem;
import team.torka.thaumicrecords.registry.OreClusterRegistry;

@EventBusSubscriber
public class OreClusterEventListener {

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) return;

        ItemStack tool = event.getTool();
        if (!(tool.getItem() instanceof ElementalPickaxeItem) && !(tool.getItem() instanceof PrimalCrusherItem)) {
            return;
        }

        // Fortune: 0.2 base + 0.075 per level (matching original TC4)
        Holder<Enchantment> fortuneEnchant = player.level().registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.FORTUNE);
        int fortune = EnchantmentHelper.getItemEnchantmentLevel(fortuneEnchant, tool);
        float chance = 0.2F + fortune * 0.075F;

        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack drop = dropEntity.getItem();
            Block sourceBlock = event.getState().getBlock();

            OreClusterRegistry.ClusterMapping mapping = OreClusterRegistry.getMapping(sourceBlock);
            if (mapping == null) continue;

            float roll = player.level().random.nextFloat();
            if (roll <= chance * mapping.baseChance()) {
                ItemStack cluster = mapping.output().get().copy();
                cluster.setCount(drop.getCount());
                dropEntity.setItem(cluster);

                event.getLevel().playSound(null, event.getPos(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                        0.2F, 0.7F + player.level().random.nextFloat() * 0.2F);
            }
        }
    }
}
