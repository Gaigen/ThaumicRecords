package team.torka.thaumicrecords.registry;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.attachment.AspectDiscovery;
import team.torka.thaumicrecords.attachment.ResearchPoint;
import team.torka.thaumicrecords.attachment.ResearchUnlocked;

public class AttachmentRegistry {

    public static final DeferredRegister<AttachmentType<?>> REGISTRAR = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
            ThaumicRecords.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AspectDiscovery>> ASPECT_DISCOVERY = REGISTRAR.register("aspect_discovery",
            () -> AttachmentType.builder(() -> AspectDiscovery.DEFAULT)
                    .serialize(AspectDiscovery.CODEC)
                    .sync(AspectDiscovery.STREAM_CODEC)
                    .copyOnDeath()
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ResearchPoint>> RESEARCH_POINT = REGISTRAR.register("research_point",
            () -> AttachmentType.builder(() -> ResearchPoint.DEFAULT).serialize(ResearchPoint.CODEC).sync(ResearchPoint.STREAM_CODEC).copyOnDeath().build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ResearchUnlocked>> RESEARCH_UNLOCKED = REGISTRAR.register("research_unlocked",
            () -> AttachmentType.builder(() -> ResearchUnlocked.DEFAULT)
                    .serialize(ResearchUnlocked.CODEC)
                    .sync(ResearchUnlocked.STREAM_CODEC)
                    .copyOnDeath()
                    .build());
}
