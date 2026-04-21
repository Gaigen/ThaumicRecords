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

    public static final DeferredRegister<Aspect> REGISTRAR = DeferredRegister.create(RegistryKeys.ASPECTS, ThaumicRecords.MOD_ID);

    public static Registry<Aspect> ASPECT_REGISTRY = null;

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        ASPECT_REGISTRY = event.create(new RegistryBuilder<>(RegistryKeys.ASPECTS));
    }

    /*@formatter:off*/
    public static final DeferredHolder<Aspect,Aspect> AER=REGISTRAR.register(Aspect.AER.getName(),()->Aspect.AER);
    public static final DeferredHolder<Aspect,Aspect> TERRA=REGISTRAR.register(Aspect.TERRA.getName(), ()->Aspect.TERRA);
    public static final DeferredHolder<Aspect,Aspect> IGNIS=REGISTRAR.register(Aspect.IGNIS.getName(),()->Aspect.IGNIS);
    public static final DeferredHolder<Aspect,Aspect> AQUA=REGISTRAR.register(Aspect.AQUA.getName(),()->Aspect.AQUA);
    public static final DeferredHolder<Aspect,Aspect> ORDO=REGISTRAR.register(Aspect.ORDO.getName(),()->Aspect.ORDO);
    public static final DeferredHolder<Aspect,Aspect> PERDITIO=REGISTRAR.register(Aspect.PERDITIO.getName(),()->Aspect.PERDITIO);
    public static final DeferredHolder<Aspect,Aspect> VACUOS=REGISTRAR.register(Aspect.VACUOS.getName(),()->Aspect.VACUOS);
    public static final DeferredHolder<Aspect,Aspect> LUX=REGISTRAR.register(Aspect.LUX.getName(),()->Aspect.LUX);
    public static final DeferredHolder<Aspect,Aspect> TEMPESTAS=REGISTRAR.register(Aspect.TEMPESTAS.getName(),()->Aspect.TEMPESTAS);
    public static final DeferredHolder<Aspect,Aspect> MOTUS=REGISTRAR.register(Aspect.MOTUS.getName(),()->Aspect.MOTUS);
    public static final DeferredHolder<Aspect,Aspect> GELUM=REGISTRAR.register(Aspect.GELUM.getName(),()->Aspect.GELUM);
    public static final DeferredHolder<Aspect,Aspect> VITREUS=REGISTRAR.register(Aspect.VITREUS.getName(),()->Aspect.VITREUS);
    public static final DeferredHolder<Aspect,Aspect> VICTUS=REGISTRAR.register(Aspect.VICTUS.getName(),()->Aspect.VICTUS);
    public static final DeferredHolder<Aspect,Aspect> VENENUM=REGISTRAR.register(Aspect.VENENUM.getName(),()->Aspect.VENENUM);
    public static final DeferredHolder<Aspect,Aspect> POTENTIA=REGISTRAR.register(Aspect.POTENTIA.getName(),()->Aspect.POTENTIA);
    public static final DeferredHolder<Aspect,Aspect> PERMUTATIO=REGISTRAR.register(Aspect.PERMUTATIO.getName(),()->Aspect.PERMUTATIO);
    public static final DeferredHolder<Aspect,Aspect> METALLUM=REGISTRAR.register(Aspect.METALLUM.getName(),()->Aspect.METALLUM);
    public static final DeferredHolder<Aspect,Aspect> MORTUUS=REGISTRAR.register(Aspect.MORTUUS.getName(),()->Aspect.MORTUUS);
    public static final DeferredHolder<Aspect,Aspect> VOLATUS=REGISTRAR.register(Aspect.VOLATUS.getName(),()->Aspect.VOLATUS);
    public static final DeferredHolder<Aspect,Aspect> TENEBRAE=REGISTRAR.register(Aspect.TENEBRAE.getName(),()->Aspect.TENEBRAE);
    public static final DeferredHolder<Aspect,Aspect> SPIRITUS=REGISTRAR.register(Aspect.SPIRITUS.getName(),()->Aspect.SPIRITUS);
    public static final DeferredHolder<Aspect,Aspect> SANO=REGISTRAR.register(Aspect.SANO.getName(),()->Aspect.SANO);
    public static final DeferredHolder<Aspect,Aspect> ITER=REGISTRAR.register(Aspect.ITER.getName(),()->Aspect.ITER);
    public static final DeferredHolder<Aspect,Aspect> ALIENIS=REGISTRAR.register(Aspect.ALIENIS.getName(),()->Aspect.ALIENIS);
    public static final DeferredHolder<Aspect,Aspect> PRAECANTATIO=REGISTRAR.register(Aspect.PRAECANTATIO.getName(),()->Aspect.PRAECANTATIO);
    public static final DeferredHolder<Aspect,Aspect> AURAM=REGISTRAR.register(Aspect.AURAM.getName(),()->Aspect.AURAM);
    public static final DeferredHolder<Aspect,Aspect> VITIUM=REGISTRAR.register(Aspect.VITIUM.getName(),()->Aspect.VITIUM);
    public static final DeferredHolder<Aspect,Aspect> LIMUS=REGISTRAR.register(Aspect.LIMUS.getName(),()->Aspect.LIMUS);
    public static final DeferredHolder<Aspect,Aspect> HERBA=REGISTRAR.register(Aspect.HERBA.getName(),()->Aspect.HERBA);
    public static final DeferredHolder<Aspect,Aspect> ARBOR=REGISTRAR.register(Aspect.ARBOR.getName(),()->Aspect.ARBOR);
    public static final DeferredHolder<Aspect,Aspect> BESTIA=REGISTRAR.register(Aspect.BESTIA.getName(),()->Aspect.BESTIA);
    public static final DeferredHolder<Aspect,Aspect> CORPUS=REGISTRAR.register(Aspect.CORPUS.getName(),()->Aspect.CORPUS);
    public static final DeferredHolder<Aspect,Aspect> EXANIMIS=REGISTRAR.register(Aspect.EXANIMIS.getName(),()->Aspect.EXANIMIS);
    public static final DeferredHolder<Aspect,Aspect> COGNITIO=REGISTRAR.register(Aspect.COGNITIO.getName(),()->Aspect.COGNITIO);
    public static final DeferredHolder<Aspect,Aspect> SENSUS=REGISTRAR.register(Aspect.SENSUS.getName(),()->Aspect.SENSUS);
    public static final DeferredHolder<Aspect,Aspect> HUMANUS=REGISTRAR.register(Aspect.HUMANUS.getName(),()->Aspect.HUMANUS);
    public static final DeferredHolder<Aspect,Aspect> MESSIS=REGISTRAR.register(Aspect.MESSIS.getName(),()->Aspect.MESSIS);
    public static final DeferredHolder<Aspect,Aspect> PERFODIO=REGISTRAR.register(Aspect.PERFODIO.getName(),()->Aspect.PERFODIO);
    public static final DeferredHolder<Aspect,Aspect> INSTRUMENTUM=REGISTRAR.register(Aspect.INSTRUMENTUM.getName(),()->Aspect.INSTRUMENTUM);
    public static final DeferredHolder<Aspect,Aspect> METO=REGISTRAR.register(Aspect.METO.getName(),()->Aspect.METO);
    public static final DeferredHolder<Aspect,Aspect> TELUM=REGISTRAR.register(Aspect.TELUM.getName(),()->Aspect.TELUM);
    public static final DeferredHolder<Aspect,Aspect> TUTAMEN=REGISTRAR.register(Aspect.TUTAMEN.getName(),()->Aspect.TUTAMEN);
    public static final DeferredHolder<Aspect,Aspect> FAMES=REGISTRAR.register(Aspect.FAMES.getName(),()->Aspect.FAMES);
    public static final DeferredHolder<Aspect,Aspect> LUCRUM=REGISTRAR.register(Aspect.LUCRUM.getName(),()->Aspect.LUCRUM);
    public static final DeferredHolder<Aspect,Aspect> FABRICO=REGISTRAR.register(Aspect.FABRICO.getName(),()->Aspect.FABRICO);
    public static final DeferredHolder<Aspect,Aspect> PANNUS=REGISTRAR.register(Aspect.PANNUS.getName(),()->Aspect.PANNUS);
    public static final DeferredHolder<Aspect,Aspect> MACHINA=REGISTRAR.register(Aspect.MACHINA.getName(),()->Aspect.MACHINA);
    public static final DeferredHolder<Aspect,Aspect> VINCULUM=REGISTRAR.register(Aspect.VINCULUM.getName(),()->Aspect.VINCULUM);
}
