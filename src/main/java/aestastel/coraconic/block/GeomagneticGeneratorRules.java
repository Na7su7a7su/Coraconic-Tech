/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 地磁发电机 MK0 的发电规则。
 *
 * <p>主世界要求 Y&lt;-59 且正下方为基岩；下界和其他模组维度要求 Y&lt;6
 * 且正下方为基岩；末地要求 Y&lt;6 且正下方没有方块。条件只选择模式，
 * 发电功率由 {@link GeomagneticGeneratorTier} 提供；不符合条件时模式为
 * Null。
 *
 * <p>Geomagnetic generator rules. The Overworld requires Y &lt; -59 with
 * bedrock below; the Nether and modded dimensions require Y &lt; 6 with bedrock
 * below; the End requires Y &lt; 6 with empty space below. Conditions select
 * the mode, and {@link GeomagneticGeneratorTier} supplies its generation rate;
 * any other case reports Null.
 */
public final class GeomagneticGeneratorRules {

  private static final int OVERWORLD_MAX_Y = -59;
  private static final int NETHER_MAX_Y = 6;
  private static final int END_MAX_Y = 6;

  private GeomagneticGeneratorRules() {}

  /**
   * 根据环境条件选择运行模式。
   *
   * <p>Selects the runtime mode from Y level, dimension, and the block directly
   * below.
   */
  public static GeomagneticGeneratorMode determineMode(
    Level level,
    BlockPos pos
  ) {
    BlockState below = level.getBlockState(pos.below());
    if (level.dimension().equals(Level.END)) {
      if (pos.getY() >= END_MAX_Y) {
        return GeomagneticGeneratorMode.NULL;
      }
      return below.isAir()
        ? GeomagneticGeneratorMode.END
        : GeomagneticGeneratorMode.NULL;
    }

    if (level.dimension().equals(Level.OVERWORLD)) {
      if (pos.getY() >= OVERWORLD_MAX_Y) {
        return GeomagneticGeneratorMode.NULL;
      }
      return below.is(Blocks.BEDROCK)
        ? GeomagneticGeneratorMode.OVERWORLD
        : GeomagneticGeneratorMode.NULL;
    }

    // 下界与其他模组维度统一套用下界规则。
    // The Nether and other modded dimensions share the Nether rules.
    if (pos.getY() >= NETHER_MAX_Y) {
      return GeomagneticGeneratorMode.NULL;
    }
    return below.is(Blocks.BEDROCK)
      ? GeomagneticGeneratorMode.NETHER
      : GeomagneticGeneratorMode.NULL;
  }

  /**
   * 计算当前 tick 的发电速率与模式。
   *
   * <p>Conditions select the mode; the tier supplies the mode's generation
   * rate.
   */
  public static SolarGeneratorStatus getStatus(
    Level level,
    BlockPos pos,
    GeomagneticGeneratorTier tier
  ) {
    GeomagneticGeneratorMode mode = determineMode(level, pos);
    return new SolarGeneratorStatus(tier.getGeneration(mode), mode);
  }
}
