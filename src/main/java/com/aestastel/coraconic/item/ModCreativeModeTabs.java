package com.aestastel.coraconic.item;

import java.util.function.Supplier;

import com.aestastel.coraconic.CoraconicTech;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoraconicTech.MODID);

    public static final Supplier<CreativeModeTab> CORACONIC_TECH = CREATIVE_MODE_TABS.register("coraconic_tech", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CORACONIC_INGOT.get())).title(Component.translatable("itemGroup.coraconic_tech")).displayItems((parameter, output) -> {
        output.accept(ModItems.HCV2);
        output.accept(ModItems.TIN_INGOT);
        output.accept(ModItems.LEAD_INGOT);
        output.accept(ModItems.CORACONIC_INGOT);
        output.accept(ModItems.AVIRACONIC_INGOT);
        output.accept(ModItems.COMMONMETAL_CORE);
        output.accept(ModItems.CORACONIC_CORE);
        output.accept(ModItems.AVIRACONIC_CORE);
        output.accept(ModItems.RAW_LEAD);
        output.accept(ModItems.RAW_TIN);
        output.accept(ModItems.CORACONIC_ELEMENT);
        output.accept(ModItems.AVIRACONIC_ELEMENT);
    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
