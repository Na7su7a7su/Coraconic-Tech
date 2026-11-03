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

  /** {@return 按网络/数据序号还原等级；非法值回退到 MK0} */
  public static SolarGeneratorTier byOrdinal(int ordinal) {
    SolarGeneratorTier[] tiers = values();
    return ordinal >= 0 && ordinal < tiers.length
      ? tiers[ordinal]
      : MK0;
  }

  /**
   * 计算当前 tick 的规则发电速率。
   *
   * <p>Delegates to {@link #getStatus(Level, BlockPos, BooleanSupplier)}.
   */
  public int getGeneration(
    Level level,
    BlockPos pos,
    BooleanSupplier obstructed
  ) {
    return getStatus(level, pos, obstructed).generation();
  }

  /**
   * 计算当前速率与运行模式。
   *
   * <p>优先级：下界 → 天空柱遮挡 → 雷暴 → 白天/黑夜。雷暴没有单独速率
   * 以外的模式优先级，界面显示 Thunder。
   *
   * <p>Calculates the current rate and mode. Priority: Nether, obstruction,
   * thunder, then daytime/night.
   */
  public SolarGeneratorStatus getStatus(
    Level level,
    BlockPos pos,
    BooleanSupplier obstructed
  ) {
    if (level.dimension().equals(Level.NETHER)) {
      return new SolarGeneratorStatus(
        this == MK3 ? 80 : 0,
        SolarGeneratorMode.NETHER
      );
    }

    if (obstructed.getAsBoolean()) {
      return new SolarGeneratorStatus(
        this == MK3 ? 80 : 0,
        SolarGeneratorMode.REFLECTION
      );
    }

    if (level.isThundering()) {
      int thunderRate = switch (this) {
        case MK0, MK1 -> 0;
        case MK2 -> 120;
        case MK3 -> 360;
      };
      return new SolarGeneratorStatus(
        thunderRate,
        SolarGeneratorMode.THUNDER
      );
    }

    if (level.isDay()) {
      int dayRate = switch (this) {
        case MK0 -> hasEnoughSkyLight(level, pos) ? 30 : 0;
        case MK1 -> hasEnoughSkyLight(level, pos) ? 120 : 0;
        case MK2 -> 240;
        case MK3 -> 960;
      };
      return new SolarGeneratorStatus(
        dayRate,
        SolarGeneratorMode.SOLAR
      );
    }

    int nightRate = switch (this) {
      case MK0, MK1 -> 0;
      case MK2 -> 120;
      case MK3 -> 240;
    };
    return new SolarGeneratorStatus(
      nightRate,
      SolarGeneratorMode.LUNAR
    );
  }

  private static boolean hasEnoughSkyLight(Level level, BlockPos pos) {
    return level.getBrightness(LightLayer.SKY, pos.above()) > 10;
  }
}
