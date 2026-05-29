package team.torka.thaumicrecords.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.IRunicArmor;
import team.torka.thaumicrecords.network.payload.RunicShieldPayload;
import team.torka.thaumicrecords.network.payload.ShieldEffectPayload;
import team.torka.thaumicrecords.registry.DataComponentRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side runic shield logic.
 * Handles charge calculation, recharge, damage absorption, and ability triggers.
 * In event/ package (not client/) so loaded on both sides.
 */
@EventBusSubscriber(modid = ThaumicRecords.MOD_ID)
public class ServerRunicHandler {

    private static final Map<UUID, Integer> runicCharge = new HashMap<>();
    private static final Map<UUID, Integer[]> runicInfo = new HashMap<>(); // [max, charged, kinetic, healing, emergency]
    private static final Map<UUID, Long> nextCycle = new HashMap<>();
    private static final Map<UUID, Integer> lastCharge = new HashMap<>();
    private static final Map<String, Long> upgradeCooldown = new HashMap<>();

    private static final int SHIELD_WAIT = 80;
    private static final long SHIELD_RECHARGE = 2000;
    private static final int RECALC_INTERVAL = 40;
    private static int tickCounter = 0;
    private static int rechargeDelay = 0;

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        UUID uuid = player.getUUID();
        tickCounter++;

        if (tickCounter % RECALC_INTERVAL == 0) {
            recalculateMaxCharge(player, uuid);
        }

        if (rechargeDelay > 0) {
            rechargeDelay--;
            return;
        }

        if (runicInfo.containsKey(uuid)) {
            if (!lastCharge.containsKey(uuid)) {
                lastCharge.put(uuid, -1);
            }
            if (!runicCharge.containsKey(uuid)) {
                runicCharge.put(uuid, 0);
            }
            if (!nextCycle.containsKey(uuid)) {
                nextCycle.put(uuid, 0L);
            }

            long time = System.currentTimeMillis();
            int charge = runicCharge.get(uuid);
            int max = runicInfo.get(uuid)[0];

            if (charge > max) {
                charge = max;
            } else if (charge < max && nextCycle.get(uuid) < time) {
                int chargedCount = runicInfo.get(uuid)[1];
                long interval = SHIELD_RECHARGE - (chargedCount * 500L);
                nextCycle.put(uuid, time + interval);
                charge++;
                runicCharge.put(uuid, charge);
            }

            if (lastCharge.get(uuid) != charge) {
                syncShieldData(player, uuid);
                lastCharge.put(uuid, charge);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        UUID uuid = player.getUUID();
        long time = System.currentTimeMillis();
        DamageSource source = event.getSource();

        if (isBypassingDamage(source)) {
            return;
        }

        if (runicInfo.containsKey(uuid) && runicCharge.containsKey(uuid) && runicCharge.get(uuid) > 0) {
            float damage = event.getNewDamage();
            int charge = runicCharge.get(uuid);

            int target = -1;
            if (source.getEntity() != null) {
                target = source.getEntity().getId();
            }
            if (source.is(DamageTypes.FALL)) {
                target = -2;
            }
            if (source.is(DamageTypes.FALLING_BLOCK)) {
                target = -3;
            }

            ShieldEffectPayload effectPayload = new ShieldEffectPayload(player.getId(), target);
            PacketDistributor.sendToPlayer(player, effectPayload);
            PacketDistributor.sendToPlayersNear(player.serverLevel(), player, player.getX(), player.getY(), player.getZ(), 64.0, effectPayload);

            if (charge > damage) {
                charge = (int) (charge - damage);
                event.setNewDamage(0);
            } else {
                event.setNewDamage(damage - charge);
                charge = 0;
            }

            // Kinetic
            String key = uuid + ":" + (char) 2;
            if (charge <= 0 && runicInfo.get(uuid)[2] > 0 && (!upgradeCooldown.containsKey(key) || upgradeCooldown.get(key) < time)) {
                upgradeCooldown.put(key, time + 20000L);
                player.level().explode(player, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 1.5F + runicInfo.get(uuid)[2] * 0.5F,
                        false, net.minecraft.world.level.Level.ExplosionInteraction.NONE);
            }

            // Healing
            key = uuid + ":" + (char) 3;
            if (charge <= 0 && runicInfo.get(uuid)[3] > 0 && (!upgradeCooldown.containsKey(key) || upgradeCooldown.get(key) < time)) {
                upgradeCooldown.put(key, time + 20000L);
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 240, runicInfo.get(uuid)[3]));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.RUNIC_SHIELD_EFFECT.get(), SoundSource.PLAYERS, 1.0f,
                        1.0f);
            }

            // Emergency
            key = uuid + ":" + (char) 4;
            if (charge <= 0 && runicInfo.get(uuid)[4] > 0 && (!upgradeCooldown.containsKey(key) || upgradeCooldown.get(key) < time)) {
                upgradeCooldown.put(key, time + 60000L);
                charge = Math.min(runicInfo.get(uuid)[0], 8 * runicInfo.get(uuid)[4]);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.RUNIC_SHIELD_CHARGE.get(), SoundSource.PLAYERS, 1.0f,
                        1.0f);
            }

            if (charge <= 0) {
                rechargeDelay = SHIELD_WAIT;
            }

            runicCharge.put(uuid, charge);
            syncShieldData(player, uuid);
        }
    }

    private static void recalculateMaxCharge(ServerPlayer player, UUID uuid) {
        int max = 0;
        int charged = 0;
        int kinetic = 0;
        int healing = 0;
        int emergency = 0;

        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof IRunicArmor) {
                max += getFinalCharge(armor);
            }
        }

        var curiosInventory = CuriosApi.getCuriosInventory(player);
        if (curiosInventory.isPresent()) {
            var handler = curiosInventory.get();
            for (var entry : handler.getCurios().entrySet()) {
                var stacksHandler = entry.getValue();
                for (int i = 0; i < stacksHandler.getSlots(); i++) {
                    ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                    if (stack.isEmpty()) {
                        continue;
                    }

                    if (stack.getItem() instanceof IRunicArmor) {
                        max += getFinalCharge(stack);

                        if (stack.is(ItemRegistry.RUNIC_RING_CHARGED.get())) {
                            charged++;
                        } else if (stack.is(ItemRegistry.RUNIC_RING_REGEN.get())) {
                            healing++;
                        } else if (stack.is(ItemRegistry.RUNIC_GIRDLE_KINETIC.get())) {
                            kinetic++;
                        } else if (stack.is(ItemRegistry.RUNIC_AMULET_EMERGENCY.get())) {
                            emergency++;
                        }
                    }
                }
            }
        }

        if (max > 0) {
            runicInfo.put(uuid, new Integer[]{max, charged, kinetic, healing, emergency});
            if (runicCharge.containsKey(uuid) && runicCharge.get(uuid) > max) {
                runicCharge.put(uuid, max);
                syncShieldData(player, uuid);
            }
        } else {
            runicInfo.remove(uuid);
            runicCharge.put(uuid, 0);
            syncShieldData(player, uuid);
        }
    }

    private static boolean isBypassingDamage(DamageSource source) {
        return source.is(DamageTypes.DROWN) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.STARVE);
    }

    private static void syncShieldData(ServerPlayer player, UUID uuid) {
        Integer current = runicCharge.get(uuid);
        Integer max = runicInfo.containsKey(uuid) ? runicInfo.get(uuid)[0] : 0;
        if (current != null) {
            PacketDistributor.sendToPlayer(player, new RunicShieldPayload(current, max));
        }
    }

    public static int getFinalCharge(ItemStack stack) {
        if (!(stack.getItem() instanceof IRunicArmor)) {
            return 0;
        }
        IRunicArmor armor = (IRunicArmor) stack.getItem();
        int base = armor.getRunicCharge(stack);
        base += stack.getOrDefault(DataComponentRegistry.RUNIC_HARDEN.get(), 0);
        return base;
    }

    public static void onPlayerDisconnect(UUID uuid) {
        runicCharge.remove(uuid);
        runicInfo.remove(uuid);
        nextCycle.remove(uuid);
        lastCharge.remove(uuid);
    }
}
