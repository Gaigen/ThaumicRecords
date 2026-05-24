package team.torka.thaumicrecords.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.attachment.ScanHistory;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

public class ThaumometerItem extends Item {
    public ThaumometerItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 25;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            HitResult hitResult = performScanRayTrace(player, 10.0D);
            if (hitResult.getType() != HitResult.Type.MISS) {
                if (level.isClientSide && remainingUseDuration % 2 == 0) {
                    spawnScanParticles(level, hitResult);
                    level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundRegistry.CAMERA_TICKS.get(), SoundSource.PLAYERS, 0.2F,
                            0.45F + level.random.nextFloat() * 0.1F, false);
                }
                if (remainingUseDuration <= 5) {
                    player.stopUsingItem();
                    if (!level.isClientSide) {
                        handleScanComplete((ServerPlayer) player, hitResult);
                    }
                }
            } else {
                player.stopUsingItem();
            }
        }
    }

    private HitResult performScanRayTrace(Player player, double range) {
        Level level = player.level();
        HitResult entityHit = net.minecraft.client.Minecraft.getInstance().hitResult;
        var eyePos = player.getEyePosition();
        var viewVec = player.getViewVector(1.0F);
        var endPos = eyePos.add(viewVec.x * range, viewVec.y * range, viewVec.z * range);
        BlockHitResult blockHit = level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (level.isClientSide && entityHit instanceof EntityHitResult eHit) {
            if (eyePos.distanceToSqr(eHit.getLocation()) < eyePos.distanceToSqr(blockHit.getLocation())) {
                return eHit;
            }
        }
        return blockHit;
    }


    private void handleScanComplete(ServerPlayer player, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            Level level = player.level();
            BlockState blockState = level.getBlockState(pos);
            ScanHistory scanned = player.getData(AttachmentRegistry.SCAN_HISTORY);
            // TODO 判断物品(方块 掉落物) 实体 节点 - 生成唯一key - 检查是否扫描过 - 发布事件
            ThaumicRecords.LOGGER.debug("blockPos{}, {}", pos, blockState.getBlock());
            if (level.getBlockEntity(pos) instanceof AuraNodeBlockEntity nodeBE) {
                ThaumicRecords.LOGGER.debug("nodeBE{}", nodeBE.getCurrentAspect());
            }
        }
    }


    private void spawnScanParticles(Level level, HitResult hitResult) {
        var pos = hitResult.getLocation();
        // TODO 用TC4效果替换
        level.addParticle(ParticleTypes.ENCHANTED_HIT, pos.x, pos.y, pos.z, 0, 0.1, 0);
    }

}
