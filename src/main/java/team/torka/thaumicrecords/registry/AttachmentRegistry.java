package team.torka.thaumicrecords.registry;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.attachment.AspectDiscovery;

public class AttachmentRegistry {

    public static final DeferredRegister<AttachmentType<?>> REGISTRAR = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
            ThaumicRecords.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AspectDiscovery>> ASPECT_DISCOVERY = REGISTRAR.register("aspect_discovery",
            () -> AttachmentType.builder(() -> AspectDiscovery.DEFAULT).serialize(AspectDiscovery.CODEC).copyOnDeath().build());

}
