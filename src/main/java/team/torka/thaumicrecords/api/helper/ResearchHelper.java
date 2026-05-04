package team.torka.thaumicrecords.api.helper;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import team.torka.thaumicrecords.api.aspect.AspectList;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.network.payload.SyncResearchPointPayload;
import team.torka.thaumicrecords.registry.AttachmentRegistry;

public class ResearchHelper {
    public static void modifyResearchPoint(ServerPlayer player, AspectList add) {
        ResearchPoint oldData = player.getData(AttachmentRegistry.RESEARCH_POINT);
        AspectList points = oldData.points().copy();
        points.merge(add);
        points.forEach((k, v) -> {
            if (v < 0) {
                points.put(k, 0);
            }
        });
        ResearchPoint newData = new ResearchPoint(points);
        player.setData(AttachmentRegistry.RESEARCH_POINT, newData);
        PacketDistributor.sendToPlayer(player, new SyncResearchPointPayload(newData));
    }
}
