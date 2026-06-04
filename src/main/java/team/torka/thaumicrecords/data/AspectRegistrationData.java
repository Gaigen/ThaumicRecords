package team.torka.thaumicrecords.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record AspectRegistrationData(TargetType type, KeyEntry key, Map<ResourceLocation, Integer> aspects, boolean replace) {

    public record KeyEntry(Optional<ResourceLocation> tag, Optional<ResourceLocation> item, Optional<ResourceLocation> block, Optional<ResourceLocation> entity,
                           Optional<ResourceLocation> potion) {
        public static final Codec<KeyEntry> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("tag").forGetter(KeyEntry::tag),
                        ResourceLocation.CODEC.optionalFieldOf("item").forGetter(KeyEntry::item),
                        ResourceLocation.CODEC.optionalFieldOf("block").forGetter(KeyEntry::block),
                        ResourceLocation.CODEC.optionalFieldOf("entity").forGetter(KeyEntry::entity),
                        ResourceLocation.CODEC.optionalFieldOf("potion").forGetter(KeyEntry::potion)).apply(instance, KeyEntry::new));

        public boolean isTag() {
            return tag.isPresent();
        }

        public ResourceLocation getLoc() {
            return tag.orElseGet(() -> item.orElseGet(() -> block.orElseGet(() -> entity.orElseGet(() -> potion.orElse(null)))));
        }
    }

    public static final Codec<AspectRegistrationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(StringRepresentable.fromEnum(
                    TargetType::values).fieldOf("type").forGetter(AspectRegistrationData::type),
            KeyEntry.CODEC.fieldOf("key").forGetter(AspectRegistrationData::key),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("aspects").forGetter(AspectRegistrationData::aspects),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(AspectRegistrationData::replace)).apply(instance, AspectRegistrationData::new));

    public enum TargetType implements StringRepresentable {
        ITEM("item"),
        BLOCK("block"),
        ENTITY("entity"),
        POTION("potion");

        private final String name;

        TargetType(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public record Container(List<AspectRegistrationData> values) {
        public static final Codec<Container> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(AspectRegistrationData.CODEC.listOf().fieldOf("values").forGetter(Container::values))
                        .apply(instance, Container::new));
    }
}