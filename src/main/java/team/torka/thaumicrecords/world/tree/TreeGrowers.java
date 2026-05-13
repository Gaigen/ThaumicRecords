package team.torka.thaumicrecords.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.world.feature.ConfiguredFeatures;

import java.util.Optional;

public class TreeGrowers {
    public static final TreeGrower SILVERWOOD_TREE = new TreeGrower(ThaumicRecords.createRl("silverwood").getPath(), Optional.empty(),
            Optional.of(ConfiguredFeatures.SILVERWOOD_TREE), Optional.empty());

    public static final TreeGrower GREATWOOD_TREE = new TreeGrower(ThaumicRecords.createRl("greatwood").getPath(), Optional.empty(),
            Optional.of(ConfiguredFeatures.GREATWOOD_TREE), Optional.empty());
}