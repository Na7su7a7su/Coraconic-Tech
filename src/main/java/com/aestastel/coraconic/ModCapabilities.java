/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic;

import com.aestastel.coraconic.block.ModBlockEntityType;
import com.aestastel.coraconic.block.ModBlocks;
import com.aestastel.coraconic.item.EnergyStorageUnitItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * 能力注册器：暴露物品存储单元的物品栏与能量存储单元的 FE 存储。
 *
 * <p>NeoForge 的能力系统通过事件注册。物品存储单元注册
 * {@link Capabilities.ItemHandler#BLOCK}，能量存储单元方块注册
 * {@link Capabilities.EnergyStorage#BLOCK}，能量存储单元物品注册
 * {@link Capabilities.EnergyStorage#ITEM}，从而让漏斗、管道、线缆、机器
 * 以及其他能量单元直接访问对应数据。
 *
 * <p>Capability registration for item storage inventories, energy storage
 * blocks, and energy storage items. Hoppers, pipes, cables, machines, and other
 * energy units can access the corresponding data directly.
 */
@EventBusSubscriber(modid = CoraconicTech.MODID)
public final class ModCapabilities {

  private ModCapabilities() {}

  @SubscribeEvent
  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      ModBlockEntityType.ITEM_STORAGE_UNIT_MK1_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getItemHandler()
    );
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      ModBlockEntityType.ITEM_STORAGE_UNIT_MK2_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getItemHandler()
    );
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      ModBlockEntityType.ITEM_STORAGE_UNIT_MK3_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getItemHandler()
    );

    // 能量存储单元：向所有面暴露 FE 能量存储。
    // Energy storage units: expose their FE storage on every side.
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      ModBlockEntityType.ENERGY_STORAGE_UNIT_MK1_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getEnergyStorageForSide(side)
    );
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      ModBlockEntityType.ENERGY_STORAGE_UNIT_MK2_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getEnergyStorageForSide(side)
    );
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      ModBlockEntityType.ENERGY_STORAGE_UNIT_MK3_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getEnergyStorageForSide(side)
    );

    // 太阳能发电机：每个面都可以按面模式访问缓存能量。
    // Solar generators: expose face-mode-gated buffer access on every side.
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      ModBlockEntityType.SOLAR_GENERATOR_MK0_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getEnergyStorageForSide(side)
    );
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      ModBlockEntityType.SOLAR_GENERATOR_MK1_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getEnergyStorageForSide(side)
    );
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      ModBlockEntityType.SOLAR_GENERATOR_MK2_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getEnergyStorageForSide(side)
    );
    event.registerBlockEntity(
      Capabilities.EnergyStorage.BLOCK,
      ModBlockEntityType.SOLAR_GENERATOR_MK3_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getEnergyStorageForSide(side)
    );

    // 能量存储单元物品：让物品本身也能作为 FE 容器被充放电。
    // Energy storage unit items: expose their own FE storage for charging.
    registerEnergyItem(event, ModBlocks.ENERGY_STORAGE_UNIT_MK1.get());
    registerEnergyItem(event, ModBlocks.ENERGY_STORAGE_UNIT_MK2.get());
    registerEnergyItem(event, ModBlocks.ENERGY_STORAGE_UNIT_MK3.get());
  }

  private static void registerEnergyItem(
    RegisterCapabilitiesEvent event,
    Block block
  ) {
    if (!(block.asItem() instanceof EnergyStorageUnitItem item)) {
      throw new IllegalStateException(
        "Missing EnergyStorageUnitItem for " + block
      );
    }
    event.registerItem(
      Capabilities.EnergyStorage.ITEM,
      (stack, context) -> item.getEnergyStorage(stack),
      item
    );
  }
}
