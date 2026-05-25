package team.torka.thaumicrecords.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.torka.thaumicrecords.ThaumicRecords;
import team.torka.thaumicrecords.menu.ArcaneWorkbenchMenu;
import team.torka.thaumicrecords.menu.ResearchTableMenu;
import team.torka.thaumicrecords.menu.ThaumatoriumMenu;

public class MenuRegistry {

    public static final DeferredRegister<MenuType<?>> REGISTRAR = DeferredRegister.create(Registries.MENU, ThaumicRecords.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ArcaneWorkbenchMenu>> ARCANE_WORKBENCH = REGISTRAR.register("arcane_workbench",
            () -> IMenuTypeExtension.create(ArcaneWorkbenchMenu::new));


    public static final DeferredHolder<MenuType<?>, MenuType<ResearchTableMenu>> RESEARCH_TABLE = REGISTRAR.register("research_table",
            () -> IMenuTypeExtension.create(ResearchTableMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ThaumatoriumMenu>> THAUMATORIUM = REGISTRAR.register("thaumatorium",
            () -> IMenuTypeExtension.create(ThaumatoriumMenu::new));
}
