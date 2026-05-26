package team.torka.thaumicrecords.client.event.listener;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import team.torka.thaumicrecords.client.renderer.item.ArcaneWorkbenchItemRenderer;
import team.torka.thaumicrecords.client.renderer.item.AuraNodeItemRenderer;
import team.torka.thaumicrecords.client.renderer.item.TableItemRenderer;
import team.torka.thaumicrecords.client.renderer.item.ThaumometerItemRenderer;
import team.torka.thaumicrecords.client.renderer.item.WandItemRenderer;
import team.torka.thaumicrecords.item.FortressArmorItem;
import team.torka.thaumicrecords.registry.BlockRegistry;
import team.torka.thaumicrecords.registry.ItemRegistry;
import team.torka.thaumicrecords.registry.ParticleRegistry;
import team.torka.thaumicrecords.registry.SoundRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterClientExtensionsEventListener {

    @SubscribeEvent
    public static void onEvent(RegisterClientExtensionsEvent event) {
        registerItemExtensions(event);
        registerBlockExtensions(event);
    }

    private static void registerItemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(WandItemRenderer.INSTANCE.getExtensions(), ItemRegistry.WAND.get());
        event.registerItem(AuraNodeItemRenderer.INSTANCE.getExtensions(), ItemRegistry.AURA_NODE.get());
        event.registerItem(ArcaneWorkbenchItemRenderer.INSTANCE.getExtensions(), ItemRegistry.ARCANE_WORKBENCH.get());
        event.registerItem(TableItemRenderer.INSTANCE.getExtensions(), ItemRegistry.TABLE.get());
        event.registerItem(ThaumometerItemRenderer.INSTANCE.getExtensions(), ItemRegistry.THAUMOMETER.get());
        event.registerItem(FortressArmorItem.getExtensions(), ItemRegistry.FORTRESS_HELMET.get());
        event.registerItem(FortressArmorItem.getExtensions(), ItemRegistry.FORTRESS_CHESTPLATE.get());
        event.registerItem(FortressArmorItem.getExtensions(), ItemRegistry.FORTRESS_LEGGINGS.get());
    }

    private static void registerBlockExtensions(RegisterClientExtensionsEvent event) {
        event.registerBlock(new IClientBlockExtensions() { // 去除节点方块的粒子
            @Override
            @ParametersAreNonnullByDefault
            public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
                return true;
            }

            @Override
            @ParametersAreNonnullByDefault
            public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundRegistry.NODE_BREAK.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
                manager.createParticle(ParticleRegistry.NODE_BREAK.get(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
                return true;
            }

        }, BlockRegistry.AURA_NODE.get());
    }
}
