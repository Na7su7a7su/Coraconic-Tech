/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

import aestastel.coraconic.menu.SolarGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 地磁发电机 MK0–MK3 方块实体。
 *
 * <p>缓存、输出槽、六面能量配置、主动传输和持久化完全复用
 * {@link SolarGeneratorBlockEntity}；只替换发电状态计算与 CLI 来源标识。
 *
 * <p>Geomagnetic Generator MK0–MK3 block entity. Buffer, output slot, face
 * configuration, active transfer, and persistence are reused from
 * {@link SolarGeneratorBlockEntity}; only generation and the CLI source label
 * differ.
 */
public class GeomagneticGeneratorBlockEntity
  extends SolarGeneratorBlockEntity
{

  private final GeomagneticGeneratorTier geomagneticTier;

  public GeomagneticGeneratorBlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state,
    GeomagneticGeneratorTier tier
  ) {
    super(type, pos, state, tier.getSolarTier());
    this.geomagneticTier = tier;
    setCurrentMode(GeomagneticGeneratorMode.NULL);
  }

  @Override
  protected SolarGeneratorStatus computeStatus() {
    Level level = getLevel();
    if (level == null) {
      return new SolarGeneratorStatus(0, GeomagneticGeneratorMode.NULL);
    }
    return GeomagneticGeneratorRules.getStatus(
      level,
      getBlockPos(),
      this.geomagneticTier
    );
  }

  @Override
  public boolean isGeomagnetic() {
    return true;
  }

  @Override
  public SolarGeneratorMenu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new SolarGeneratorMenu(
      containerId,
      playerInventory,
      this,
      true
    );
  }
}
