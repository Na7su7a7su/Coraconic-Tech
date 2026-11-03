/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic;

import aestastel.coraconic.block.ModBlockEntityType;
import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.item.ModCreativeModeTabs;
import aestastel.coraconic.item.ModItems;
import aestastel.coraconic.loot.ModLootConditions;
import aestastel.coraconic.loot.ModLootModifiers;
import aestastel.coraconic.menu.ModMenuType;
import aestastel.coraconic.network.ModNetwork;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

/**
 * 核龙科技主类：挂载注册器与模组生命周期事件。
 *
 * <p>物品、方块与创造栏的注册分别由 {@link ModItems}、{@link ModBlocks} 和
 * {@link ModCreativeModeTabs} 负责；本类只负责把它们的延迟注册器挂到模组事件
 * 总线，并保留公共初始化与服务端启动钩子。
 *
 * <p>Main mod class: attaches the deferred registers to the mod event bus and
 * keeps the common setup and server-starting hooks.
 */
@Mod(CoraconicTech.MODID)
public class CoraconicTech {

  /** 模组 ID，必须与 {@code neoforge.mods.toml} 中的 modId 一致。 */
  public static final String MODID = "coraconic";

  /** 模组共享日志器。 */
  public static final Logger LOGGER = LogUtils.getLogger();

  public CoraconicTech(IEventBus modEventBus) {
    ModItems.register(modEventBus);
    ModBlocks.register(modEventBus);
    ModBlockEntityType.register(modEventBus);
    ModMenuType.register(modEventBus);
    ModLootConditions.register(modEventBus);
    ModLootModifiers.register(modEventBus);
    ModCreativeModeTabs.register(modEventBus);

    modEventBus.addListener(ModNetwork::register);
    modEventBus.addListener(this::commonSetup);
    NeoForge.EVENT_BUS.register(this);
  }

  /**
   * 公共初始化钩子。当前只输出初始化日志，后续可在此挂载通用逻辑。
   *
   * <p>Common setup hook. Currently only logs initialization; shared logic can
   * be attached here later.
   */
  private void commonSetup(FMLCommonSetupEvent event) {
    LOGGER.info("Coraconic Tech common setup initialized");
  }

  /**
   * 服务端启动钩子，保留用于后续服务端逻辑。
   *
   * <p>Server-starting hook, reserved for future server-side logic.
   */
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    LOGGER.info("Coraconic Tech server starting");
  }
}
