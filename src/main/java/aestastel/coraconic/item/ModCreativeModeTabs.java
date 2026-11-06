/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.item;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.block.ModBlocks;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创造模式物品栏注册器：声明本模组的自定义创造栏。
 *
 * <p>本类只负责创造栏本身的注册与条目展示顺序，不负责物品或方块的注册。
 * 物品是否存在于游戏中由 {@code ModItems} 和 {@code ModBlocks} 决定；即使
 * 某个物品已经注册，如果没有在这里调用 {@code output.accept(...)}，它也不会
 * 出现在本模组的创造栏中。
 *
 * <p>Creative tab registry: declares the mod's custom creative tab. This class
 * does not register items or blocks; it only controls the tab itself and the
 * order in which already-registered entries are displayed.
 */
public class ModCreativeModeTabs {

  /**
   * 创造模式物品栏延迟注册器。
   *
   * <p>创造栏属于游戏注册表 {@link Registries#CREATIVE_MODE_TAB} 中的条目，
   * 因此同样需要通过 {@link DeferredRegister} 延迟注册。构造注册器时传入
   * {@code CoraconicTech.MODID}，使创造栏 ID 固定为
   * {@code coraconic:<name>}。
   *
   * <p>与物品、方块注册器不同，这里的泛型类型是 {@link CreativeModeTab}。
   * 真正的创造栏实例由 {@link CreativeModeTab.Builder} 构建，注册器只负责
   * 把构建结果写入注册表；在 {@link #register(IEventBus)} 执行之前，注册器
   * 不会触碰游戏注册表。
   *
   * <p>Deferred creative tab register. The tab is an entry in
   * {@link Registries#CREATIVE_MODE_TAB}; the namespace is fixed to
   * {@code coraconic}, and the register stays inactive until attached to the mod
   * event bus.
   */
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
    DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoraconicTech.MODID);
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS_TOOL =
    DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoraconicTech.MODID);

  public static final Supplier<CreativeModeTab> CORACONIC_TECH =
    CREATIVE_MODE_TABS.register("coraconic_tech", () ->
      CreativeModeTab.builder()
        .icon(() -> new ItemStack(ModItems.CORACONIC_METAL_INGOT.get()))
        .title(Component.translatable("itemGroup.coraconic_tech"))
        .displayItems((parameter, output) -> {
          output.accept(ModItems.TIN_INGOT);
          output.accept(ModItems.LEAD_INGOT);
          output.accept(ModItems.CORACONIC_METAL_INGOT);
          output.accept(ModItems.AVIRACONIC_METAL_INGOT);
          output.accept(ModItems.TIN_NUGGET);
          output.accept(ModItems.LEAD_NUGGET);
          output.accept(ModItems.CORACONIC_NUGGET);
          output.accept(ModItems.AVIRACONIC_NUGGET);
          output.accept(ModItems.COMMONMETAL_CORE);
          output.accept(ModItems.CORACONIC_CORE);
          output.accept(ModItems.AVIRACONIC_CORE);
          output.accept(ModItems.EFFECT_AMPLIFICATION_CORE);
          output.accept(ModItems.SPATIAL_SCALING_CORE);
          output.accept(ModItems.PHASE_SYNCHRONIZATION_CORE);
          output.accept(ModItems.MASS_ENERGY_COMPENSATION_CORE);
          output.accept(ModItems.CENTRAL_COMPUTATION_CORE);
          output.accept(ModItems.RAW_LEAD);
          output.accept(ModItems.RAW_TIN);
          output.accept(ModItems.CORACONIC_ELEMENT);
          output.accept(ModItems.AVIRACONIC_ELEMENT);
          output.accept(ModItems.CORACONIC_DIAMOND_STRUCTURED_CRYSTAL);
          output.accept(ModItems.AVIRACONIC_DIAMOND_STRUCTURED_CRYSTAL);
          output.accept(ModBlocks.TIN_BLOCK.get());
          output.accept(ModBlocks.LEAD_BLOCK.get());
          output.accept(ModBlocks.CORACONIC_BLOCK.get());
          output.accept(ModBlocks.AVIRACONIC_BLOCK.get());
          output.accept(ModBlocks.TIN_ORE.get());
          output.accept(ModBlocks.LEAD_ORE.get());
          output.accept(ModBlocks.DEEPSLAT_TIN_ORE.get());
          output.accept(ModBlocks.DEEPSLAT_LEAD_ORE.get());
          output.accept(ModBlocks.ITEM_STORAGE_UNIT_MK1.get());
          output.accept(ModBlocks.ITEM_STORAGE_UNIT_MK2.get());
          output.accept(ModBlocks.ITEM_STORAGE_UNIT_MK3.get());
          output.accept(ModBlocks.ENERGY_STORAGE_UNIT_MK1.get());
          output.accept(ModBlocks.ENERGY_STORAGE_UNIT_MK2.get());
          output.accept(ModBlocks.ENERGY_STORAGE_UNIT_MK3.get());
          output.accept(ModBlocks.ALLOYING_MACHINE_MK1.get());
          output.accept(ModBlocks.SOLAR_GENERATOR_MK0.get());
          output.accept(ModBlocks.GEOMAGNETIC_GENERATOR_MK0.get());
          output.accept(ModBlocks.GEOMAGNETIC_GENERATOR_MK1.get());
          output.accept(ModBlocks.GEOMAGNETIC_GENERATOR_MK2.get());
          output.accept(ModBlocks.GEOMAGNETIC_GENERATOR_MK3.get());
          output.accept(ModBlocks.SOLAR_GENERATOR_MK1.get());
          output.accept(ModBlocks.SOLAR_GENERATOR_MK2.get());
          output.accept(ModBlocks.SOLAR_GENERATOR_MK3.get());
        })
        .build()
    );

  public static final Supplier<CreativeModeTab> CORACONIC_TECH_TOOL =
    CREATIVE_MODE_TABS_TOOL.register("coraconic_tech_tool", () ->
      CreativeModeTab.builder()
        .icon(() -> new ItemStack(ModItems.CORACONIC_PICKAXE.get()))
        .title(Component.translatable("itemGroup.coraconic_tech_tool"))
        .displayItems((parameter, output) -> {
          // 锡质工具 / Tin tools
          output.accept(ModItems.TIN_SWORD);
          output.accept(ModItems.TIN_PICKAXE);
          output.accept(ModItems.TIN_AXE);
          output.accept(ModItems.TIN_SHOVEL);
          output.accept(ModItems.TIN_HOE);

          // 铅质工具 / Lead tools
          output.accept(ModItems.LEAD_SWORD);
          output.accept(ModItems.LEAD_PICKAXE);
          output.accept(ModItems.LEAD_AXE);
          output.accept(ModItems.LEAD_SHOVEL);
          output.accept(ModItems.LEAD_HOE);

          // 核龙金属工具 / Coraconic tools
          output.accept(ModItems.CORACONIC_SWORD);
          output.accept(ModItems.CORACONIC_PICKAXE);
          output.accept(ModItems.CORACONIC_AXE);
          output.accept(ModItems.CORACONIC_SHOVEL);
          output.accept(ModItems.CORACONIC_HOE);

          // 腾龙金属工具 / Aviraconic tools
          output.accept(ModItems.AVIRACONIC_SWORD);
          output.accept(ModItems.AVIRACONIC_PICKAXE);
          output.accept(ModItems.AVIRACONIC_AXE);
          output.accept(ModItems.AVIRACONIC_SHOVEL);
          output.accept(ModItems.AVIRACONIC_HOE);
          output.accept(ModItems.SPANNER);
        })
        .build()
    );

  /**
   * 将创造模式物品栏延迟注册器挂到模组事件总线。
   *
   * <p>必须在模组构造期间调用。{@link #CREATIVE_MODE_TABS} 与
   * {@link #CREATIVE_MODE_TABS_TOOL} 在声明阶段只保存待注册的创造栏，调用
   * {@link DeferredRegister#register(IEventBus)} 后才会在对应注册事件阶段把
   * 创造栏写入游戏注册表；若遗漏此调用，创造栏不会出现，其中的展示条目自然
   * 也无法被玩家看到。
   *
   * <p>Attaches both deferred creative tab registers to the mod event bus. Must
   * be called during mod construction; otherwise the creative tabs will not
   * exist.
   *
   * @param eventBus 模组专用事件总线，通常由主模组类传入
   */
  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);
    CREATIVE_MODE_TABS_TOOL.register(eventBus);
  }
}
