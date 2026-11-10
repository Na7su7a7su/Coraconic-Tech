/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.creativetab;

import aestastel.coraconic.CoraconicTech;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创造模式物品栏统一注册器。
 *
 * <p>负责持有创造栏延迟注册器，并挂载两个独立的创造栏类。具体图标与
 * 展示条目分别由 {@link CoraconicTechCreativeTab} 和
 * {@link CoraconicTechToolCreativeTab} 定义。
 *
 * <p>Unified creative tab registry. It owns the deferred register and attaches
 * the two tab classes; each tab class defines its own icon and display items.
 */
public final class ModCreativeModeTabs {

  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
    DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoraconicTech.MODID);

  public static final Supplier<CreativeModeTab> CORACONIC_TECH =
    CoraconicTechCreativeTab.register(CREATIVE_MODE_TABS);

  public static final Supplier<CreativeModeTab> CORACONIC_TECH_TOOL =
    CoraconicTechToolCreativeTab.register(CREATIVE_MODE_TABS);

  private ModCreativeModeTabs() {}

  /**
   * 将创造栏延迟注册器挂到模组事件总线。
   *
   * <p>Attaches the deferred creative tab register to the mod event bus.
   */
  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);
  }
}
