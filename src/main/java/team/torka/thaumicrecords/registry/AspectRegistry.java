package team.torka.thaumicrecords.registry;

import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.api.RegistryKeys;
import team.torka.thaumicrecords.api.aspect.Aspect;

@EventBusSubscriber(modid = ThaumicRecords.MOD_ID)
public class AspectRegistry {

    public static final DeferredRegister<Aspect> ASPECTS =
            DeferredRegister.create(RegistryKeys.ASPECTS, ThaumicRecords.MOD_ID);

    public static Registry<Aspect> ASPECT_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        ASPECT_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.ASPECTS));
    }

    /*@formatter:off*/
    public static final DeferredHolder<Aspect,Aspect> AER=ASPECTS.register(Aspect.AER.getName(),()->Aspect.AER);
    public static final DeferredHolder<Aspect,Aspect> TERRA=ASPECTS.register(Aspect.TERRA.getName(), ()->Aspect.TERRA);
    public static final DeferredHolder<Aspect,Aspect> IGNIS=ASPECTS.register(Aspect.IGNIS.getName(),()->Aspect.IGNIS);
    public static final DeferredHolder<Aspect,Aspect> AQUA=ASPECTS.register(Aspect.AQUA.getName(),()->Aspect.AQUA);
    public static final DeferredHolder<Aspect,Aspect> ORDO=ASPECTS.register(Aspect.ORDO.getName(),()->Aspect.ORDO);
    public static final DeferredHolder<Aspect,Aspect> PERDITIO=ASPECTS.register(Aspect.PERDITIO.getName(),()->Aspect.PERDITIO);
    public static final DeferredHolder<Aspect,Aspect> VACUOS=ASPECTS.register(Aspect.VACUOS.getName(),()->Aspect.VACUOS);
    public static final DeferredHolder<Aspect,Aspect> LUX=ASPECTS.register(Aspect.LUX.getName(),()->Aspect.LUX);
    public static final DeferredHolder<Aspect,Aspect> TEMPESTAS=ASPECTS.register(Aspect.TEMPESTAS.getName(),()->Aspect.TEMPESTAS);
    public static final DeferredHolder<Aspect,Aspect> MOTUS=ASPECTS.register(Aspect.MOTUS.getName(),()->Aspect.MOTUS);
    public static final DeferredHolder<Aspect,Aspect> GELUM=ASPECTS.register(Aspect.GELUM.getName(),()->Aspect.GELUM);
    public static final DeferredHolder<Aspect,Aspect> VITREUS=ASPECTS.register(Aspect.VITREUS.getName(),()->Aspect.VITREUS);
    public static final DeferredHolder<Aspect,Aspect> VICTUS=ASPECTS.register(Aspect.VICTUS.getName(),()->Aspect.VICTUS);
    public static final DeferredHolder<Aspect,Aspect> VENENUM=ASPECTS.register(Aspect.VENENUM.getName(),()->Aspect.VENENUM);
    public static final DeferredHolder<Aspect,Aspect> POTENTIA=ASPECTS.register(Aspect.POTENTIA.getName(),()->Aspect.POTENTIA);
    public static final DeferredHolder<Aspect,Aspect> PERMUTATIO=ASPECTS.register(Aspect.PERMUTATIO.getName(),()->Aspect.PERMUTATIO);
    public static final DeferredHolder<Aspect,Aspect> METALLUM=ASPECTS.register(Aspect.METALLUM.getName(),()->Aspect.METALLUM);
    public static final DeferredHolder<Aspect,Aspect> MORTUUS=ASPECTS.register(Aspect.MORTUUS.getName(),()->Aspect.MORTUUS);
    public static final DeferredHolder<Aspect,Aspect> VOLATUS=ASPECTS.register(Aspect.VOLATUS.getName(),()->Aspect.VOLATUS);
    public static final DeferredHolder<Aspect,Aspect> TENEBRAE=ASPECTS.register(Aspect.TENEBRAE.getName(),()->Aspect.TENEBRAE);
    public static final DeferredHolder<Aspect,Aspect> SPIRITUS=ASPECTS.register(Aspect.SPIRITUS.getName(),()->Aspect.SPIRITUS);
    public static final DeferredHolder<Aspect,Aspect> SANO=ASPECTS.register(Aspect.SANO.getName(),()->Aspect.SANO);
    public static final DeferredHolder<Aspect,Aspect> ITER=ASPECTS.register(Aspect.ITER.getName(),()->Aspect.ITER);
    public static final DeferredHolder<Aspect,Aspect> ALIENIS=ASPECTS.register(Aspect.ALIENIS.getName(),()->Aspect.ALIENIS);
    public static final DeferredHolder<Aspect,Aspect> PRAECANTATIO=ASPECTS.register(Aspect.PRAECANTATIO.getName(),()->Aspect.PRAECANTATIO);
    public static final DeferredHolder<Aspect,Aspect> AURAM=ASPECTS.register(Aspect.AURAM.getName(),()->Aspect.AURAM);
    public static final DeferredHolder<Aspect,Aspect> VITIUM=ASPECTS.register(Aspect.VITIUM.getName(),()->Aspect.VITIUM);
    public static final DeferredHolder<Aspect,Aspect> LIMUS=ASPECTS.register(Aspect.LIMUS.getName(),()->Aspect.LIMUS);
    public static final DeferredHolder<Aspect,Aspect> HERBA=ASPECTS.register(Aspect.HERBA.getName(),()->Aspect.HERBA);
    public static final DeferredHolder<Aspect,Aspect> ARBOR=ASPECTS.register(Aspect.ARBOR.getName(),()->Aspect.ARBOR);
    public static final DeferredHolder<Aspect,Aspect> BESTIA=ASPECTS.register(Aspect.BESTIA.getName(),()->Aspect.BESTIA);
    public static final DeferredHolder<Aspect,Aspect> CORPUS=ASPECTS.register(Aspect.CORPUS.getName(),()->Aspect.CORPUS);
    public static final DeferredHolder<Aspect,Aspect> EXANIMIS=ASPECTS.register(Aspect.EXANIMIS.getName(),()->Aspect.EXANIMIS);
    public static final DeferredHolder<Aspect,Aspect> COGNITIO=ASPECTS.register(Aspect.COGNITIO.getName(),()->Aspect.COGNITIO);
    public static final DeferredHolder<Aspect,Aspect> SENSUS=ASPECTS.register(Aspect.SENSUS.getName(),()->Aspect.SENSUS);
    public static final DeferredHolder<Aspect,Aspect> HUMANUS=ASPECTS.register(Aspect.HUMANUS.getName(),()->Aspect.HUMANUS);
    public static final DeferredHolder<Aspect,Aspect> MESSIS=ASPECTS.register(Aspect.MESSIS.getName(),()->Aspect.MESSIS);
    public static final DeferredHolder<Aspect,Aspect> PERFODIO=ASPECTS.register(Aspect.PERFODIO.getName(),()->Aspect.PERFODIO);
    public static final DeferredHolder<Aspect,Aspect> INSTRUMENTUM=ASPECTS.register(Aspect.INSTRUMENTUM.getName(),()->Aspect.INSTRUMENTUM);
    public static final DeferredHolder<Aspect,Aspect> METO=ASPECTS.register(Aspect.METO.getName(),()->Aspect.METO);
    public static final DeferredHolder<Aspect,Aspect> TELUM=ASPECTS.register(Aspect.TELUM.getName(),()->Aspect.TELUM);
    public static final DeferredHolder<Aspect,Aspect> TUTAMEN=ASPECTS.register(Aspect.TUTAMEN.getName(),()->Aspect.TUTAMEN);
    public static final DeferredHolder<Aspect,Aspect> FAMES=ASPECTS.register(Aspect.FAMES.getName(),()->Aspect.FAMES);
    public static final DeferredHolder<Aspect,Aspect> LUCRUM=ASPECTS.register(Aspect.LUCRUM.getName(),()->Aspect.LUCRUM);
    public static final DeferredHolder<Aspect,Aspect> FABRICO=ASPECTS.register(Aspect.FABRICO.getName(),()->Aspect.FABRICO);
    public static final DeferredHolder<Aspect,Aspect> PANNUS=ASPECTS.register(Aspect.PANNUS.getName(),()->Aspect.PANNUS);
    public static final DeferredHolder<Aspect,Aspect> MACHINA=ASPECTS.register(Aspect.MACHINA.getName(),()->Aspect.MACHINA);
    public static final DeferredHolder<Aspect,Aspect> VINCULUM=ASPECTS.register(Aspect.VINCULUM.getName(),()->Aspect.VINCULUM);
}
