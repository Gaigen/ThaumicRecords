package team.torka.thaumicrecords.api.research;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.torka.thaumicrecords.api.aspect.AspectList;

import javax.annotation.Nullable;
import java.util.List;

public class Research {
    public final String nameTranslationKey;
    public final String descTranslationKey;

    public final ResourceLocation category;

    public final AspectList aspects;

    /**
     * 图标
     */
    @Nullable
    public final ResourceLocation icon;

    /**
     * 图标，优先级大于icon
     */
    @Nullable
    public final ItemStack iconItem;

    /**
     * 前置研究
     */
    public final ResourceLocation[] parents;

    public final int row;

    public final int col;

    /**
     * 数值->研究笔记的格子圈数为 1+gridSize
     */
    public final int gridSize;

    /**
     * 禁忌研究,0为无害,正数为增加的扭曲值
     */
    public final int warp;

    public final RenderStrategy renderStrategy;
    public final UnlockStrategy unlockStrategy;
    public final List<DiscoveryStrategy> discoveryStrategy;

    public Research(String nameTranslationKey, String descTranslationKey, ResourceLocation category, AspectList aspects, @Nullable ResourceLocation icon,
                    @Nullable ItemStack iconItem, ResourceLocation[] parents, int row, int col, int gridSize, RenderStrategy renderStrategy,
                    UnlockStrategy unlockStrategy, List<DiscoveryStrategy> discoveryStrategy, int warp) {
        this.nameTranslationKey = nameTranslationKey;
        this.descTranslationKey = descTranslationKey;
        this.category = category;
        this.aspects = aspects;
        this.icon = icon;
        this.iconItem = iconItem;
        this.parents = parents;
        this.row = row;
        this.col = col;
        this.gridSize = gridSize;
        this.renderStrategy = renderStrategy;
        this.unlockStrategy = unlockStrategy;
        this.discoveryStrategy = discoveryStrategy;
        this.warp = warp;
    }

    public static Research createNormal(String nameTranslationKey, String descTranslationKey, ResourceLocation category, AspectList aspects,
                                        @Nullable ResourceLocation icon, @Nullable ItemStack iconItem, ResourceLocation[] parents, int row, int col,
                                        int gridSize) {
        return new Research(nameTranslationKey, descTranslationKey, category, aspects, icon, iconItem, parents, row, col, gridSize, RenderStrategy.NORMAL,
                UnlockStrategy.RESEARCH, List.of(DiscoveryStrategy.PARENT), 0);
    }

    public enum RenderStrategy implements StringRepresentable {
        /**
         * 正方形图标
         */
        NORMAL("NORMAL"),
        /**
         * 圆形
         */
        ROUND("ROUND"),
        /**
         * 六边形
         */
        HEXAGON("HEXAGON"),
        /**
         * 带刺的圆形
         */
        SPIKY("SPIKY");

        private final String name;

        RenderStrategy(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public enum UnlockStrategy implements StringRepresentable {
        /**
         * 初始解锁
         */
        INITIAL("INITIAL"),
        /**
         * 研究解锁
         */
        RESEARCH("RESEARCH"),
        /**
         * 用研究点直接解锁
         */
        POINTS("POINTS"),
        ;

        private final String name;

        UnlockStrategy(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public enum DiscoveryStrategy implements StringRepresentable {
        /**
         * 总是
         */
        ALWAYS("ALWAYS"),
        /**
         * 当前置研究完成时发现
         */
        PARENT("PARENT"),
        /**
         * 扫描特定物品/生物/要素时发现
         */
        SCAN("SCAN"),
        /**
         * 从知识碎片拼成的笔记 或者特定方式解锁
         */
        SPECIAL("SPECIAL"),
        ;

        private final String name;

        DiscoveryStrategy(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}