package team.torka.thaumicrecords.event.listener;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import team.torka.thaumicrecords.attachment.AspectDiscovery;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.network.packet.SyncAspectDiscoveryPacket;
import team.torka.thaumicrecords.network.packet.SyncResearchPointPacket;
import team.torka.thaumicrecords.registry.AttachmentRegistry;

@EventBusSubscriber()
public class PlayerEventsListener {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncAllData(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer newPlayer) {
            syncAllData(newPlayer);
        }
    }

    private static void syncAllData(ServerPlayer player) {
        AspectDiscovery discoveryData = player.getData(AttachmentRegistry.ASPECT_DISCOVERY);
        PacketDistributor.sendToPlayer(player, new SyncAspectDiscoveryPacket(discoveryData));
        ResearchPoint pointsData = player.getData(AttachmentRegistry.RESEARCH_POINT);
        PacketDistributor.sendToPlayer(player, new SyncResearchPointPacket(pointsData));
    }
}