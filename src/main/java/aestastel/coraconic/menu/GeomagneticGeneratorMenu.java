/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.block.SolarGeneratorTier;
import aestastel.coraconic.blockentity.GeomagneticGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;

/**
 * 地磁发电机 MK0–MK3 的专用菜单。
 *
 * <p>保留太阳能发电机的通用缓存、输出槽、面配置和同步逻辑，但使用独立的
 * 菜单类型与客户端界面，便于单独显示地磁 CLI。
 *
 * <p>Dedicated menu for Geomagnetic Generators MK0–MK3. It reuses the solar
 * generator's buffer, output slot, face configuration, and synchronization
 * logic while using its own menu type and client screen.
 */
public class GeomagneticGeneratorMenu extends SolarGeneratorMenu {

  public GeomagneticGeneratorMenu(
    int containerId,
    Inventory playerInventory,
    GeomagneticGeneratorBlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity,
      blockEntity.getCapacity(),
      blockEntity.getTier()
    );
  }

  public GeomagneticGeneratorMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    GeomagneticGeneratorBlockEntity blockEntity,
    int capacity,
    SolarGeneratorTier tier
  ) {
    super(
      ModMenuType.GEOMAGNETIC_GENERATOR.get(),
      containerId,
      playerInventory,
      container,
      blockEntity,
      capacity,
      tier,
      true
    );
  }

  public static GeomagneticGeneratorMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    int capacity = buffer.readVarInt();
    SolarGeneratorTier tier = SolarGeneratorTier.byOrdinal(
      buffer.readVarInt()
    );
    buffer.readBoolean();
    GeomagneticGeneratorBlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof GeomagneticGeneratorBlockEntity generator
        ? generator
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new GeomagneticGeneratorMenu(
      containerId,
      playerInventory,
      container,
      blockEntity,
      capacity,
      tier
    );
  }
}
