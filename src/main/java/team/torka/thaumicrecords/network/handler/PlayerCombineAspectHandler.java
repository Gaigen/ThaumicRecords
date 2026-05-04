package team.torka.thaumicrecords.network.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.torka.thaumicrecords.api.aspect.Aspect;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.api.helper.AspectHelper;
import team.torka.thaumicrecords.api.helper.ResearchHelper;
import team.torka.thaumicrecords.block.entity.ResearchTableBlockEntity;
import team.torka.thaumicrecords.network.payload.PlayerCombineAspectPayload;
import team.torka.thaumicrecords.registry.AspectRegistry;

import java.util.Objects;


public class PlayerCombineAspectHandler {

    public static void handle(PlayerCombineAspectPayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        context.enqueueWork(() -> {
            BlockEntity be = player.level().getBlockEntity(payload.blockPos());
            if (be instanceof ResearchTableBlockEntity) {
                Aspect left = AspectRegistry.ASPECT_REGISTRY.get(payload.left());
                Aspect right = AspectRegistry.ASPECT_REGISTRY.get(payload.right());
                if (Objects.isNull(left) || Objects.isNull(right)) {
                    return;
                }
                AspectList modify = new AspectList();
                modify.put(payload.left(), -1);
                modify.put(payload.right(), -1);
                ResourceLocation rl = AspectHelper.getAspectCombined(left, right);
                if (Objects.isNull(rl)) {
                    return;
                }
                Aspect aspect = AspectRegistry.ASPECT_REGISTRY.get(rl);
                if (Objects.isNull(aspect)) {
                    return;
                }

                boolean discovered = AspectHelper.isAspectDiscovered(player, rl);
                if (!discovered) {
                    modify.put(rl, 3);
                } else {
                    modify.put(rl, 1);
                }
                AspectHelper.discoverAspect(player, rl);
                ResearchHelper.modifyResearchPoint(player, modify);
            }
        });
    }
}