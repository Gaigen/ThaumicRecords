package team.torka.thaumicrecords.item;

import net.minecraft.world.item.ItemStack;
import team.torka.thaumicrecords.api.IRunicArmor;

public class RunicCurioItem extends CurioItem implements IRunicArmor {

    private final int runicCharge;

    public RunicCurioItem(String curioSlot, int runicCharge) {
        super(curioSlot);
        this.runicCharge = runicCharge;
    }

    @Override
    public int getRunicCharge(ItemStack stack) {
        return runicCharge;
    }
}
