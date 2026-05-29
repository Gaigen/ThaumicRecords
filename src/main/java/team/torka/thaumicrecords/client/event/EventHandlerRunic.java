package team.torka.thaumicrecords.client.event;

import team.torka.thaumicrecords.event.ServerRunicHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Client-side runic shield data holder.
 * Receives synced data from ServerRunicHandler via RunicShieldPayload.
 * HUD reads charge/max from here.
 */
public class EventHandlerRunic {

    private static final Map<UUID, Integer> clientRunicCharge = new HashMap<>();
    private static final Map<UUID, Integer> clientMaxRunicCharge = new HashMap<>();

    public static void setClientShieldData(UUID uuid, int current, int max) {
        clientRunicCharge.put(uuid, current);
        clientMaxRunicCharge.put(uuid, max);
    }

    public static int getRunicCharge(UUID uuid) {
        return clientRunicCharge.getOrDefault(uuid, 0);
    }

    public static int getMaxRunicCharge(UUID uuid) {
        return clientMaxRunicCharge.getOrDefault(uuid, 0);
    }

    public static void onPlayerDisconnect(UUID uuid) {
        clientRunicCharge.remove(uuid);
        clientMaxRunicCharge.remove(uuid);
        ServerRunicHandler.onPlayerDisconnect(uuid);
    }
}
