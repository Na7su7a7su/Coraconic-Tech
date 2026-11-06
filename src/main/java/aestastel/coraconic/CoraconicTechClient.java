/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic;

import aestastel.coraconic.client.AlloyingMachineScreen;
import aestastel.coraconic.client.EnergyStorageUnitScreen;
import aestastel.coraconic.client.ItemStorageUnitScreen;
import aestastel.coraconic.client.SolarGeneratorScreen;
import aestastel.coraconic.menu.ModMenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * 客户端专用入口：保留客户端事件总线与初始化钩子。
 *
 * <p>Client-only entry point: keeps the client event bus and setup hook.
 */
@Mod(value = CoraconicTech.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CoraconicTech.MODID, value = Dist.CLIENT)
public class CoraconicTechClient {

  /**
   * 客户端初始化钩子。当前只输出初始化日志，后续可在此挂载渲染或界面逻辑。
   *
   * <p>Client setup hook. Currently only logs initialization; rendering or UI
   * logic can be attached here later.
   */
  @SubscribeEvent
  static void onClientSetup(FMLClientSetupEvent event) {
    CoraconicTech.LOGGER.info("Coraconic Tech client initialized");
  }

  /**
   * 把菜单类型与客户端界面绑定。
   *
   * <p>菜单类型在两端是同一个注册表条目，但只有客户端知道该用哪个
   * {@code Screen} 来渲染它，因此界面注册必须放在客户端事件中。
   *
   * <p>Binds the menu type to its client screen. Only the client knows how to
   * render a menu, so screen registration happens on the client event bus.
   */
  @SubscribeEvent
  static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(ModMenuType.ITEM_STORAGE_UNIT.get(), ItemStorageUnitScreen::new);
    event.register(
      ModMenuType.ENERGY_STORAGE_UNIT.get(),
      EnergyStorageUnitScreen::new
    );
    event.register(
      ModMenuType.SOLAR_GENERATOR.get(),
      SolarGeneratorScreen::new
    );
    event.register(
      ModMenuType.ALLOYING_MACHINE.get(),
      AlloyingMachineScreen::new
    );
  }
}
