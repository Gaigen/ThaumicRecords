package team.torka.thaumicrecords.block.part;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ResearchTablePart implements StringRepresentable {
    LEFT("left"),
    RIGHT("right");

    private final String name;

    ResearchTablePart(String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return this.name;
    }
}
