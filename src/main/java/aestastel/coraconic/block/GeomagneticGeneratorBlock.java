/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 地磁发电机 MK0–MK3 方块。
 *
 * <p>复用太阳能发电机的朝向、ticker、界面打开和掉落逻辑，仅创建地磁
 * 方块实体以替换发电规则。
 *
 * <p>Geomagnetic Generator MK0–MK3 block. Reuses the solar generator's
 * facing, ticker, menu opening, and drop handling, while creating the
 * geomagnetic block entity to supply the different generation rules.
 */
public class GeomagneticGeneratorBlock extends SolarGeneratorBlock {

  private final GeomagneticGeneratorTier geomagneticTier;

  public GeomagneticGeneratorBlock(
    Properties properties,
    Supplier<BlockEntityType<?>> typeSupplier,
    GeomagneticGeneratorTier tier
  ) {
    super(properties, typeSupplier, tier.getSolarTier());
    this.geomagneticTier = tier;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new GeomagneticGeneratorBlockEntity(
      getBlockEntityType(),
      pos,
      state,
      this.geomagneticTier
    );
  }

  /** {@return 本地磁发电机等级} */
  public GeomagneticGeneratorTier getGeomagneticTier() {
    return this.geomagneticTier;
  }
}
