package team.torka.thaumicrecords.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.AspectHelper;
import team.torka.thaumicrecords.api.helper.ResearchHelper;
import team.torka.thaumicrecords.attachment.ScanHistory;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.event.PlayerScanEvent;
import team.torka.thaumicrecords.registry.AspectRegistry;
import team.torka.thaumicrecords.registry.AttachmentRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

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
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(viewVec.x * range, viewVec.y * range, viewVec.z * range);

        BlockHitResult blockHit = level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        double actualRange = blockHit.getType() != HitResult.Type.MISS ? eyePos.distanceTo(blockHit.getLocation()) : range;
        Vec3 entityEndPos = eyePos.add(viewVec.x * actualRange, viewVec.y * actualRange, viewVec.z * actualRange);
        AABB box = player.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0D);

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, eyePos, entityEndPos, box, entity -> !entity.isSpectator());

        if (entityHit != null) {
            return entityHit;
        }
        return blockHit;
    }


    private void handleScanComplete(ServerPlayer player, HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof ItemEntity itemEntity) { // ItemEntity fallback to item
                ItemStack itemStack = itemEntity.getItem();
                ResourceLocation itemRl = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                ThaumicRecords.LOGGER.debug("scan itemEntity {}", itemRl);
                ScanHistory scanHistory = player.getData(AttachmentRegistry.SCAN_HISTORY);
                if (scanHistory.hasScannedItem(itemRl)) {
                    ThaumicRecords.LOGGER.debug("{} already scanned", itemRl);
                    return;
                }
                AspectList aspects = AspectHelper.getAspects(itemStack);
                PlayerScanEvent.Item event = new PlayerScanEvent.Item(player, itemRl.toString(), itemStack, aspects);
                NeoForge.EVENT_BUS.post(event);
                AspectList gainedAspects = event.getGainedAspects();
                for (var aspectRl : gainedAspects.keySet()) {
                    Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(aspectRl);
                    if (Objects.isNull(aspect)) {
                        continue;
                    }
                    if (aspect.isPrimal() || Objects.isNull(aspect.getComponents())) {
                        continue;
                    }
                    Aspect component0 = aspect.getComponents()[0];
                    Aspect component1 = aspect.getComponents()[1];
                    if (Objects.isNull(component0) || Objects.isNull(component1)) {
                        continue;
                    }
                    if (!AspectHelper.isAspectDiscovered(player, (AspectRegistry.ASPECT_REGISTRY.getKey(component0)))) {
                        ThaumicRecords.LOGGER.debug("player {} aspect {} not discovered", player.getName().getString(), component0);
                        // TODO scan fail hint
                        return;
                    }
                    if (!AspectHelper.isAspectDiscovered(player, AspectRegistry.ASPECT_REGISTRY.getKey(component1))) {
                        ThaumicRecords.LOGGER.debug("player {} aspect {} not discovered", player.getName().getString(), component1);
                        // TODO scan fail hint
                        return;
                    }
                }
                AspectList researchPointGained = new AspectList();
                gainedAspects.forEach((key, val) -> {
                    int gainVal = val;
                    if (!AspectHelper.isAspectDiscovered(player, key)) {
                        AspectHelper.discoverAspect(player, key);
                        gainVal += 3;
                    }
                    researchPointGained.add(key, gainVal);
                });
                ResearchHelper.modifyResearchPoint(player, researchPointGained);
                ResearchHelper.addScannedItem(player, itemRl);
            } else { // normal entity
                EntityType<?> entityType = entityHit.getEntity().getType();
                ResourceLocation entityRl = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
                ThaumicRecords.LOGGER.debug("scan entity {}", entityRl);
                ScanHistory scanHistory = player.getData(AttachmentRegistry.SCAN_HISTORY);
                if (scanHistory.hasScannedEntity(entityRl)) {
                    return;
                }
                // TODO entity aspect注册
            }
        }
        if (hitResult instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            Level level = player.level();
            BlockState blockState = level.getBlockState(pos);
            ScanHistory scanned = player.getData(AttachmentRegistry.SCAN_HISTORY);
            // TODO 节点放在前面  先把Block看有没有blockitem，没有的话查block
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
