package team.torka.thaumicrecords.node.type;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.node.NodeType;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;
import team.torka.thaumicrecords.client.renderer.CustomRenderType;

import java.util.ArrayList;
import java.util.List;

public class HungryNodeType extends NodeType {
    private static final String KEY = "hungry";

    private static final int BLOCK_RADIUS = 5;
    private static final int ENTITY_RADIUS = 8;

    public HungryNodeType() {
        super(ThaumicRecords.createTranslationKey("node_type", KEY), ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png"),
                CustomRenderType.additiveTransparencyNoDepth(ThaumicRecords.createRl("textures/misc/node/" + KEY + ".png")), 600, 0.75F, true);
    }

    @Override
    public void onTick(Level level, BlockPos pos, BlockState state, AuraNodeBlockEntity be) {
        super.onTick(level, pos, state, be);

        if (level.isClientSide) {
            return;
        }

        Vec3 center = Vec3.atCenterOf(pos);

        pullAndDestroyBlocks(level, pos);
        pullEntities(level, center);
    }

    private void pullAndDestroyBlocks(Level level, BlockPos centerPos) {

        if (level.getGameTime() % 20 != 0) {
            return;
        }

        List<BlockPos> validBlocks = new ArrayList<>();
        BlockPos.betweenClosedStream(centerPos.offset(-BLOCK_RADIUS, -BLOCK_RADIUS, -BLOCK_RADIUS), centerPos.offset(BLOCK_RADIUS, BLOCK_RADIUS, BLOCK_RADIUS))
                .forEach(pos -> {
                    if (pos.equals(centerPos)) {
                        return;
                    }
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) {
                        return;
                    }
                    if (state.getDestroySpeed(level, pos) < 0) {
                        return;
                    }
                    validBlocks.add(pos.immutable());
                });

        if (validBlocks.isEmpty()) {
            return;
        }
        BlockPos target = validBlocks.get(level.random.nextInt(validBlocks.size()));
        level.destroyBlock(target, true);
    }

    private void pullEntities(Level level, Vec3 center) {
        AABB area = new AABB(center.x - ENTITY_RADIUS, center.y - ENTITY_RADIUS, center.z - ENTITY_RADIUS, center.x + ENTITY_RADIUS, center.y + ENTITY_RADIUS,
                center.z + ENTITY_RADIUS);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, area, entity -> entity instanceof ItemEntity || entity instanceof Player);
        for (Entity entity : entities) {
            Vec3 entityPos = entity.position();
            double distance = entityPos.distanceTo(center);
            if (distance < 0.001D) {
                continue;
            }
            double strength = 1.0D - (distance / ENTITY_RADIUS);
            if (strength <= 0) {
                continue;
            }
            Vec3 motion = center.subtract(entityPos).normalize().scale(0.5D * strength);
            if (entity instanceof Player player) {
                if (player.isCreative() || player.isSpectator()) {
                    break;
                }
                entity.setDeltaMovement(entity.getDeltaMovement().add(motion));
            } else {
                entity.setDeltaMovement(entity.getDeltaMovement().add(motion));
            }
            entity.hurtMarked = true;
            if (distance <= 1.5D) {
                if (entity instanceof ItemEntity itemEntity) {
                    itemEntity.discard();
                }
                if (entity instanceof Player player) {

                    player.hurt(level.damageSources().magic(), 4.0F);
                }
            }
        }
    }
}
