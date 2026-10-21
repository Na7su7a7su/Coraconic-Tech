package com.aestastel.coraconic;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

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
}
