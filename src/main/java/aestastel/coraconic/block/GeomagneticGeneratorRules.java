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
 * <p>方块必须位于 Y=5 或更低。末地要求正下方没有方块；主世界、下界和
 * 其他模组维度要求正下方为基岩。下界产出 3 FE/t，其余符合条件的维度
 * 产出 2 FE/t；不符合条件时模式为 Null。
 *
 * <p>Geomagnetic Generator MK0 generation rules. The block must be at Y=5 or
 * lower. The End requires empty space below; the Overworld, Nether, and modded
 * dimensions require bedrock below. The Nether produces 3 FE/t and every other
 * qualifying dimension produces 2 FE/t; any other case reports Null.
 */
public final class GeomagneticGeneratorRules {

  private static final int MAX_GENERATION_Y = 6;
  private static final int OVERWORLD_GENERATION = 2;
  private static final int NETHER_GENERATION = 3;
  private static final int END_GENERATION = 6;

  private GeomagneticGeneratorRules() {}

  /**
   * 计算当前 tick 的发电速率与模式。
   *
   * <p>Calculates the current generation rate and mode.
   */
  public static SolarGeneratorStatus getStatus(
    Level level,
    BlockPos pos
  ) {
    if (pos.getY() >= MAX_GENERATION_Y) {
      return new SolarGeneratorStatus(0, SolarGeneratorMode.NULL);
    }

    BlockState below = level.getBlockState(pos.below());
    if (level.dimension().equals(Level.END)) {
      return below.isAir()
        ? new SolarGeneratorStatus(
            END_GENERATION,
            SolarGeneratorMode.END
          )
        : new SolarGeneratorStatus(0, SolarGeneratorMode.NULL);
    }

    if (!below.is(Blocks.BEDROCK)) {
      return new SolarGeneratorStatus(0, SolarGeneratorMode.NULL);
    }

    if (level.dimension().equals(Level.NETHER)) {
      return new SolarGeneratorStatus(
        NETHER_GENERATION,
        SolarGeneratorMode.NETHER
      );
    }

    return new SolarGeneratorStatus(
      OVERWORLD_GENERATION,
      SolarGeneratorMode.OVERWORLD
    );
  }
}
