package team.torka.thaumicrecords.block.part;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ThaumatoriumPart implements StringRepresentable {
    BOTTOM("bottom"),
    TOP("top");

    private final String name;

    ThaumatoriumPart(String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return this.name;
    }
}
