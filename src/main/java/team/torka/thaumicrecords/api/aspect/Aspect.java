package team.torka.thaumicrecords.api.aspect;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.registry.AspectRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Aspect implements Comparable<Aspect> {
    public static final ResourceLocation UNKNOWN_TEX = ThaumicRecords.createRl("textures/aspects/_unknown.png");

    private final String name;

    private final @Nullable Aspect[] components;

    private final int color;

    private final ResourceLocation image;

    private final boolean blend;

    private final ChatFormatting textColor;

    private final String nameTranslationKey;

    private final String loreTranslationKey;

    @Override
    public int compareTo(@NotNull Aspect o) {
        return this.getName().compareTo(o.getName());
    }

    public Aspect(String name, int color, Aspect[] components, ResourceLocation image, boolean blend, ChatFormatting textColor, String nameTranslationKey,
                  String loreTranslationKey) {
        if (Objects.nonNull(components) && components.length != 2) {
            throw new IllegalArgumentException("Component amount of aspect {" + name + "} expected 2 but got " + components.length);
        }
        this.name = name;
        this.color = color;
        this.components = components;
        this.image = image;
        this.blend = blend;
        this.textColor = textColor;
        this.nameTranslationKey = nameTranslationKey;
        this.loreTranslationKey = loreTranslationKey;
    }

    private Aspect(String name, int color, Aspect[] components) {
        this(name, color, components, ThaumicRecords.createRl("textures/aspects/" + name.toLowerCase() + ".png"), false, ChatFormatting.WHITE,
                ThaumicRecords.createTranslationKey("aspect", "name." + name), ThaumicRecords.createTranslationKey("aspect", "lore." + name));
    }

    private Aspect(String name, int color, Aspect[] components, boolean blend) {
        this(name, color, components, ThaumicRecords.createRl("textures/aspects/" + name.toLowerCase() + ".png"), blend, ChatFormatting.WHITE,
                ThaumicRecords.createTranslationKey("aspect", "name." + name), ThaumicRecords.createTranslationKey("aspect", "lore." + name));
    }

    private Aspect(String name, int color, Aspect[] components, boolean blend, ChatFormatting textColor) {
        this(name, color, components, ThaumicRecords.createRl("textures/aspects/" + name.toLowerCase() + ".png"), blend, textColor,
                ThaumicRecords.createTranslationKey("aspect", "name." + name), ThaumicRecords.createTranslationKey("aspect", "lore." + name));
    }

    private Aspect(String name, int color, ChatFormatting textColor) {
        this(name, color, null, false, textColor);
    }

    private Aspect(String name, int color, boolean blend, ChatFormatting textColor) {
        this(name, color, null, blend, textColor);
    }

    public boolean isPrimal() {
        return Objects.isNull(this.components);
    }

    public String getName() {
        return this.name;
    }

    public int getARGBColor() {
        return 0xFF000000 | color;
    }

    public ResourceLocation getImage() {
        return this.image;
    }

    public boolean getBlend() {
        return blend;
    }

    public @Nullable Aspect[] getComponents() {
        return components;
    }

    public static List<ResourceLocation> getPrimalList() {
        return Arrays.asList(AspectRegistry.AER.getId(), AspectRegistry.IGNIS.getId(), AspectRegistry.AQUA.getId(), AspectRegistry.TERRA.getId(),
                AspectRegistry.ORDO.getId(), AspectRegistry.PERDITIO.getId());
    }

    public boolean isRelatedTo(Aspect other) {
        if (Objects.nonNull(this.components)) {
            for (Aspect component : this.components) {
                if (component == other) {
                    return true;
                }
            }
        }
        if (Objects.nonNull(other.components)) {
            for (Aspect component : other.components) {
                if (component == this) {
                    return true;
                }
            }
        }
        return false;
    }

    /*@formatter:off*/
    public static final Aspect AER = new Aspect("aer",0xFFFF7E,ChatFormatting.YELLOW);
    public static final Aspect TERRA = new Aspect("terra",0x56C000,ChatFormatting.DARK_GREEN);
    public static final Aspect IGNIS = new Aspect("ignis",0xFF5A01,ChatFormatting.RED);
    public static final Aspect AQUA = new Aspect("aqua",0x3CD4FC,ChatFormatting.DARK_AQUA);
    public static final Aspect ORDO = new Aspect("ordo",0xD5D4EC,ChatFormatting.GRAY);
    public static final Aspect PERDITIO = new Aspect("perditio",0x404040,true,ChatFormatting.DARK_GRAY);
    public static final Aspect VACUOS=new Aspect("vacuos",0x888888,new Aspect[]{AER,PERDITIO},true);
    public static final Aspect LUX=new Aspect("lux",0xFFF663,new Aspect[]{AER,IGNIS});
    public static final Aspect TEMPESTAS=new Aspect("tempestas",0xFFFFFF,new Aspect[]{AER,AQUA});
    public static final Aspect MOTUS=new Aspect("motus",0xCDCCF4,new Aspect[]{AER,ORDO});
    public static final Aspect GELUM=new Aspect("gelum",0xE1FFFF,new Aspect[]{IGNIS,PERDITIO});
    public static final Aspect VITREUS=new Aspect("vitreus",0x80FFFF,new Aspect[]{TERRA,ORDO});
    public static final Aspect VICTUS=new Aspect("victus",0xDE0005,new Aspect[]{AQUA,TERRA});
    public static final Aspect VENENUM = new Aspect("venenum", 0x89F000, new Aspect[]{AQUA, PERDITIO});
    public static final Aspect POTENTIA = new Aspect("potentia", 0xC0FFFF, new Aspect[]{ORDO, IGNIS});
    public static final Aspect PERMUTATIO = new Aspect("permutatio", 0x578357, new Aspect[]{PERDITIO, ORDO});
    public static final Aspect METALLUM = new Aspect("metallum", 0xB5B5CD, new Aspect[]{TERRA, VITREUS});
    public static final Aspect MORTUUS = new Aspect("mortuus", 0x887788, new Aspect[]{VICTUS, PERDITIO});
    public static final Aspect VOLATUS = new Aspect("volatus", 0xE7E7D7, new Aspect[]{AER, MOTUS});
    public static final Aspect TENEBRAE = new Aspect("tenebrae", 0x222222, new Aspect[]{ VACUOS, LUX});
    public static final Aspect SPIRITUS = new Aspect("spiritus", 0xEBEBFB, new Aspect[]{VICTUS, MORTUUS});
    public static final Aspect SANO = new Aspect("sano", 0xFF2F34, new Aspect[]{VICTUS, ORDO});
    public static final Aspect ITER = new Aspect("iter", 0xE0585B, new Aspect[]{MOTUS, TERRA});
    public static final Aspect ALIENIS = new Aspect("alienis", 0x805080, new Aspect[]{ VACUOS, TENEBRAE});
    public static final Aspect PRAECANTATIO = new Aspect("praecantatio", 0x9700C0, new Aspect[]{ VACUOS, POTENTIA});
    public static final Aspect AURAM = new Aspect("auram", 0xFFC0FF, new Aspect[]{PRAECANTATIO, AER});
    public static final Aspect VITIUM = new Aspect("vitium", 0x800080, new Aspect[]{PRAECANTATIO, PERDITIO});
    public static final Aspect LIMUS = new Aspect("limus", 0x01F800, new Aspect[]{VICTUS, AQUA});
    public static final Aspect HERBA = new Aspect("herba", 0x01AC00, new Aspect[]{VICTUS, TERRA});
    public static final Aspect ARBOR = new Aspect("arbor", 0x876531, new Aspect[]{AER, HERBA});
    public static final Aspect BESTIA = new Aspect("bestia", 0x9F6409, new Aspect[]{MOTUS, VICTUS});
    public static final Aspect CORPUS = new Aspect("corpus", 0xEE478D, new Aspect[]{MORTUUS, BESTIA});
    public static final Aspect EXANIMIS = new Aspect("exanimis", 0x3A4000, new Aspect[]{MOTUS, MORTUUS});
    public static final Aspect COGNITIO = new Aspect("cognitio", 0xFFC2B3, new Aspect[]{IGNIS, SPIRITUS});
    public static final Aspect SENSUS = new Aspect("sensus", 0x0FD9FF, new Aspect[]{AER, SPIRITUS});
    public static final Aspect HUMANUS = new Aspect("humanus", 0xFFD7C0, new Aspect[]{BESTIA, COGNITIO});
    public static final Aspect MESSIS = new Aspect("messis", 0xE1B371, new Aspect[]{HERBA, HUMANUS});
    public static final Aspect PERFODIO = new Aspect("perfodio", 0xDCD2D8, new Aspect[]{HUMANUS, TERRA});
    public static final Aspect INSTRUMENTUM = new Aspect("instrumentum", 0x4040EE, new Aspect[]{HUMANUS, ORDO});
    public static final Aspect METO = new Aspect("meto", 0xEEAD82, new Aspect[]{MESSIS, INSTRUMENTUM});
    public static final Aspect TELUM = new Aspect("telum", 0xC05050, new Aspect[]{INSTRUMENTUM, IGNIS});
    public static final Aspect TUTAMEN = new Aspect("tutamen", 0x00C0C0, new Aspect[]{INSTRUMENTUM, TERRA});
    public static final Aspect FAMES = new Aspect("fames", 0x9A0305, new Aspect[]{VICTUS,  VACUOS});
    public static final Aspect LUCRUM = new Aspect("lucrum", 0xE6BE44, new Aspect[]{HUMANUS, FAMES});
    public static final Aspect FABRICO = new Aspect("fabrico", 0x809D80, new Aspect[]{HUMANUS, INSTRUMENTUM});
    public static final Aspect PANNUS = new Aspect("pannus", 0xEAEAC2, new Aspect[]{INSTRUMENTUM, BESTIA});
    public static final Aspect MACHINA = new Aspect("machina", 0x8080A0, new Aspect[]{MOTUS, INSTRUMENTUM});
    public static final Aspect VINCULUM = new Aspect("vinculum", 0x9A8080, new Aspect[]{MOTUS, PERDITIO});
    /*@formatter:on*/
    public ChatFormatting getTextColor() {
        return textColor;
    }

    public String getNameTranslationKey() {
        return nameTranslationKey;
    }

    public String getLoreTranslationKey() {
        return loreTranslationKey;
    }
}
