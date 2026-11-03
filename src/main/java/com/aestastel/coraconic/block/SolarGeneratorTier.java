/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.block;

import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * 太阳能发电机等级及发电规则。
 *
 * <p>缓存容量、每面输出和物品充能速率集中在这里声明；发电条件由
 * {@link #getGeneration(Level, BlockPos, BooleanSupplier)} 根据等级、维度、
 * 天气、时间和天空柱遮挡返回本 tick 的发电量。
 *
 * <p>Solar generator tiers and generation rules. Capacity, per-face output, and
 * item charge rate live here; the generation method evaluates dimension,
 * weather, time, sky light, and sky-column obstruction.
 */
public enum SolarGeneratorTier {
  MK0(96_000, 600, 600),
  MK1(192_000, 1_200, 1_200),
  MK2(480_000, 2_400, 2_400),
  MK3(1_920_000, 9_600, 9_600);

  private final int capacity;
  private final int outputPerSide;
  private final int itemChargeRate;

  SolarGeneratorTier(
    int capacity,
    int outputPerSide,
    int itemChargeRate
  ) {
    this.capacity = capacity;
    this.outputPerSide = outputPerSide;
    this.itemChargeRate = itemChargeRate;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getOutputPerSide() {
    return outputPerSide;
  }

  public int getItemChargeRate() {
    return itemChargeRate;
  }

  /**
   * 计算当前 tick 的发电量。
   *
   * <p>{@code obstructed} 只在确实需要检查天空柱时调用，避免每个 tick 都做
   * 无用的高度柱扫描。
   *
   * <p>Returns this tick's generation. The obstruction supplier is only called
   * when the tier's rule actually needs a sky-column check.
   */
  public int getGeneration(
    Level level,
    BlockPos pos,
    BooleanSupplier obstructed
  ) {
    return switch (this) {
      case MK0 -> {
        if (
          level.dimension().equals(Level.NETHER) ||
          !level.isDay() ||
          level.getBrightness(LightLayer.SKY, pos.above()) <= 10
        ) {
          yield 0;
        }
        yield obstructed.getAsBoolean() ? 0 : 30;
      }
      case MK1 -> {
        if (
          level.dimension().equals(Level.NETHER) ||
          !level.isDay() ||
          level.getBrightness(LightLayer.SKY, pos.above()) <= 10
        ) {
          yield 0;
        }
        yield obstructed.getAsBoolean() ? 0 : 120;
      }
      case MK2 -> {
        if (
          level.dimension().equals(Level.NETHER) ||
          obstructed.getAsBoolean()
        ) {
          yield 0;
        }
        if (level.isThundering()) {
          yield 120;
        }
        yield level.isDay() ? 240 : 120;
      }
      case MK3 -> {
        if (level.dimension().equals(Level.NETHER)) {
          yield 80;
        }
        if (obstructed.getAsBoolean()) {
          yield 80;
        }
        if (level.isThundering()) {
          yield 360;
        }
        yield level.isDay() ? 960 : 240;
      }
    };
  }
}
