package team.torka.thaumicrecords.client.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

public abstract class RenderThaumicVisionEvent extends Event {

    private final Player player;
    private final Level level;
    private boolean visible;

    protected RenderThaumicVisionEvent(Player player, Level level, boolean defaultVisible) {
        this.player = player;
        this.level = level;
        this.visible = defaultVisible;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Level getLevel() {
        return this.level;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * 是否完整渲染节点
     */
    public static class Node extends RenderThaumicVisionEvent {
        public Node(Player player, Level level) {
            super(player, level, false);
        }
    }

    /**
     * 是否渲染方块要素悬浮 如节点，坩埚
     */
    public static class Aspect extends RenderThaumicVisionEvent {
        public Aspect(Player player, Level level) {
            super(player, level, false);
        }
    }

    /**
     * 是否渲染魔力网络连线
     */
    public static class VisNet extends RenderThaumicVisionEvent {
        public VisNet(Player player, Level level) {
            super(player, level, false);
        }
    }
}
