package team.torka.thaumicrecords.item;

import net.minecraft.world.item.Item;
import team.torka.thaumicrecords.api.item.ScribingTool;

public class ScribingToolsItem extends Item implements ScribingTool {

    public ScribingToolsItem() {
        super(new Properties().stacksTo(1).durability(100));
    }
}
