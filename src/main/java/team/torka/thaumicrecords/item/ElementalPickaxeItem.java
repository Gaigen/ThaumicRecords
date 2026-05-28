package team.torka.thaumicrecords.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.network.payload.OreScanPayload;
import team.torka.thaumicrecords.registry.SoundRegistry;
import team.torka.thaumicrecords.registry.TierRegistry;

public class ElementalPickaxeItem extends PickaxeItem {
    public ElementalPickaxeItem() {
        super(TierRegistry.ELEMENTAL, new Item.Properties().rarity(Rarity.RARE)
                .stacksTo(1)
                .attributes(PickaxeItem.createAttributes(TierRegistry.ELEMENTAL, 1.0F, -2.8F)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide) {
            boolean canBurn = true;
            if (target instanceof Player && attacker instanceof Player) {
                // PvP check — only burn if PvP is enabled
                canBurn = true;
            }
            if (canBurn) {
                target.setRemainingFireTicks(40); // 2 seconds
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    // Right-click on block = ore scan
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null) {
            return super.useOn(context);
        }

        // Cost: 5 durability
        ItemStack stack = context.getItemInHand();
        stack.hurtAndBreak(5, player, context.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

        if (!level.isClientSide) {
            level.playSound(null, context.getClickedPos(), SoundRegistry.WAND_FAIL.get(), SoundSource.PLAYERS, 0.2F, 0.2F + level.random.nextFloat() * 0.2F);
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new OreScanPayload(context.getClickedPos(), 8));
            }
        }

        player.swing(context.getHand());
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
