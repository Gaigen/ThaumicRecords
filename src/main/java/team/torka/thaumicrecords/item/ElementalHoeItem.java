package team.torka.thaumicrecords.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.registry.TierRegistry;

public class ElementalHoeItem extends HoeItem {
    public ElementalHoeItem() {
        super(TierRegistry.ELEMENTAL, new Item.Properties().rarity(Rarity.RARE)
                .stacksTo(1)
                .attributes(HoeItem.createAttributes(TierRegistry.ELEMENTAL, -3.0F, 0.0F)));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return super.useOn(context);
        }

        // Shift = normal single hoe
        if (player.isShiftKeyDown()) {
            return super.useOn(context);
        }

        boolean didSomething = false;

        // 3x3 tilling
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = clickedPos.offset(dx, 0, dz);
                UseOnContext copyContext = new UseOnContext(level, player, context.getHand(), stack,
                        new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, context.isInside()));
                InteractionResult result = super.useOn(copyContext);
                if (result.consumesAction()) {
                    didSomething = true;
                }
            }
        }

        // If nothing was tilled, try bone meal effect
        if (!didSomething) {
            BlockState state = level.getBlockState(clickedPos);
            if (state.getBlock() instanceof BonemealableBlock bonemealable && bonemealable.isValidBonemealTarget(level, clickedPos, state)) {

                // Grow on server
                if (level instanceof ServerLevel serverLevel) {
                    if (bonemealable.isBonemealSuccess(serverLevel, serverLevel.random, clickedPos, state)) {
                        bonemealable.performBonemeal(serverLevel, serverLevel.random, clickedPos, state);
                    }
                    // Send bone meal particles to all nearby players
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, clickedPos.getX() + 0.5, clickedPos.getY() + 0.5, clickedPos.getZ() + 0.5, 15, 0.25,
                            0.25, 0.25, 0.0);
                }

                stack.hurtAndBreak(1, player, context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                didSomething = true;
            }
        }

        if (didSomething) {
            level.playSound(player, clickedPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 0.75F, 0.9F + level.random.nextFloat() * 0.2F);
            player.swing(context.getHand());
        }

        return didSomething ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }
}
