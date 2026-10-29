/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/**
 * 相邻可配置能量方块之间的主动传输仲裁。
 *
 * <p>如果两个相邻方块都支持逐面配置，只让位置较小的一方处理这一对面，
 * 避免双方在同一 tick 互相抽取/发送。相邻方块不是本模组可配置能量方块时
 * 不做限制。
 *
 * <p>Arbitrates active transfer between two adjacent configurable energy
 * blocks. Only the block with the lower position handles the pair, preventing
 * both sides from moving energy in the same tick. Non-configurable neighbours
 * are not restricted.
 */
public final class EnergyFaceTransfer {

  private EnergyFaceTransfer() {}

  /** {@return 当前方块是否应处理该方向的主动传输} */
  public static boolean ownsTransfer(
    Level level,
    BlockPos pos,
    Direction side
  ) {
    BlockPos neighbourPos = pos.relative(side);
    if (!(level.getBlockEntity(neighbourPos) instanceof EnergyFaceConfigurable)) {
      return true;
    }
    return pos.asLong() < neighbourPos.asLong();
  }
}
