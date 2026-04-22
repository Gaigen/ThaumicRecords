package team.torka.thaumicrecords.client.listener;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.client.renderer.WandRenderer;
import team.torka.thaumicrecords.client.renderer.blockentity.AuraNodeRenderer;
import team.torka.thaumicrecords.registry.*;

import javax.annotation.ParametersAreNonnullByDefault;

@EventBusSubscriber(value = Dist.CLIENT)
public class RegisterClientExtensionsEventListener {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        registerItemExtensions(event);
        registerBlockExtensions(event);
    }

    private static void registerItemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @NotNull
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return WandRenderer.INSTANCE;
            }
        }, ItemRegistry.WAND.get());
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

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.AURA_NODE.get(), AuraNodeRenderer::new);
    }
}
