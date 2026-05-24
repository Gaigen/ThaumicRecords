package team.torka.thaumicrecords.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.block.entity.AuraNodeBlockEntity;

public abstract class PlayerScanEvent extends Event implements ICancellableEvent {
    private final ServerPlayer player;
    /**
     * 唯一标识，也是在ScanHistory作为key的值
     */
    private String scanIdentifier;
    /**
     * 本次扫描获取的研究点
     */
    private AspectList gainedAspects;

    protected PlayerScanEvent(ServerPlayer player, String scanIdentifier, AspectList baseAspects) {
        this.player = player;
        this.scanIdentifier = scanIdentifier;
        this.gainedAspects = baseAspects.copy();
    }

    public static class Item extends PlayerScanEvent {
        private final ItemStack itemStack;

        public Item(ServerPlayer player, String defaultIdentifier, ItemStack itemStack, AspectList baseAspects) {
            super(player, defaultIdentifier, baseAspects);
            this.itemStack = itemStack.copy();
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }
    }

    public static class ScanEntity extends PlayerScanEvent {
        private final Entity targetEntity;

        public ScanEntity(ServerPlayer player, String defaultIdentifier, Entity targetEntity, AspectList baseAspects) {
            super(player, defaultIdentifier, baseAspects);
            this.targetEntity = targetEntity;
        }

        public Entity getTargetEntity() {
            return this.targetEntity;
        }
    }

    public static class Node extends PlayerScanEvent {
        private final BlockPos pos;
        private final AuraNodeBlockEntity blockEntity;

        public Node(ServerPlayer player, String defaultIdentifier, AuraNodeBlockEntity blockEntity, AspectList baseAspects) {
            super(player, defaultIdentifier, baseAspects);
            this.pos = blockEntity.getBlockPos();
            this.blockEntity = blockEntity;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public AuraNodeBlockEntity getBlockEntity() {
            return this.blockEntity;
        }
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public String getScanIdentifier() {
        return scanIdentifier;
    }

    public void setScanIdentifier(String scanIdentifier) {
        this.scanIdentifier = scanIdentifier;
    }

    public AspectList getGainedAspects() {
        return gainedAspects;
    }

}
