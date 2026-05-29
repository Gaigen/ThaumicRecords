package team.torka.thaumicrecords.client;

import net.minecraft.client.Minecraft;
import team.torka.thaumicrecords.client.screen.ThaumonomiconScreen;

public class ClientProxy {
    public static void openThaumonomiconScreen() {
        Minecraft.getInstance().setScreen(new ThaumonomiconScreen());
    }
}
