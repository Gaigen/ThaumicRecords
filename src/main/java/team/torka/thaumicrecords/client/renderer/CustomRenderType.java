package team.torka.thaumicrecords.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CustomRenderType {

    private static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("additive_transparency",
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static RenderType additiveTransparency(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, true, false))
                .setCullState(RenderStateShard.CullStateShard.NO_CULL)
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .createCompositeState(false);
        return RenderType.create("additive_transparency", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, compositeState);
    }

    public static RenderType additiveTransparencyNoDepth(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, true, false))
                .setCullState(RenderStateShard.CullStateShard.NO_CULL)
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setDepthTestState(RenderStateShard.DepthTestStateShard.NO_DEPTH_TEST)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .createCompositeState(false);
        return RenderType.create("additive_transparency_no_depth", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, compositeState);
    }

    public static RenderType translucentNoDepth(ResourceLocation resourceLocation) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, true, false))
                .setCullState(RenderStateShard.CullStateShard.NO_CULL)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setDepthTestState(RenderStateShard.DepthTestStateShard.NO_DEPTH_TEST)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .createCompositeState(false);


        return RenderType.create("translucent_no_depth", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, compositeState);
    }

    public static RenderType translucentGlass(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)          // отключаем отсечение граней
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST) // проверка глубины
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)       // НЕ пишем в глубину
                .createCompositeState(true);                    // сортировка на GPU
        return RenderType.create("translucent_glass", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, state);
    }
}
