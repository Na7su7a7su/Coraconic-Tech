/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

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
   * <p>先由条件判定模式，再从模式功率表取值。
   *
   * <p>Determines the mode first, then reads its generation rate from the
   * tier's mode table.
   */
  public int getGeneration(
    Level level,
    BlockPos pos,
    BooleanSupplier obstructed
  ) {
    return getGeneration(determineMode(level, pos, obstructed));
  }

  /** {@return 指定模式在本等级的发电速率} */
  public int getGeneration(SolarGeneratorMode mode) {
    return switch (mode) {
      case SOLAR -> switch (this) {
        case MK0 -> 30;
        case MK1 -> 120;
        case MK2 -> 240;
        case MK3 -> 960;
      };
      case LUNAR -> switch (this) {
        case MK0, MK1 -> 0;
        case MK2 -> 120;
        case MK3 -> 240;
      };
      case REFLECTION, NETHER -> this == MK3 ? 80 : 0;
      case THUNDER -> switch (this) {
        case MK0, MK1 -> 0;
        case MK2 -> 120;
        case MK3 -> 360;
      };
      case NULL -> 0;
    };
  }

  /**
   * 根据环境条件选择运行模式。
   *
   * <p>优先级：下界 → 天空柱遮挡 → 末地 → 雷暴 → 白天/黑夜。末地在
   * 无遮挡时固定为 Solar；MK0/MK1 白天天空光不足时返回 Null。
   *
   * <p>Selects the runtime mode. Priority: Nether, obstruction, the End,
   * thunder, then daytime/night. The End is always Solar when unobstructed;
   * MK0/MK1 report Null during daytime with insufficient sky light.
   */
  public SolarGeneratorMode determineMode(
    Level level,
    BlockPos pos,
    BooleanSupplier obstructed
  ) {
    if (level.dimension().equals(Level.NETHER)) {
      return SolarGeneratorMode.NETHER;
    }

    if (obstructed.getAsBoolean()) {
      return SolarGeneratorMode.REFLECTION;
    }

    if (level.dimension().equals(Level.END)) {
      return SolarGeneratorMode.SOLAR;
    }

    if (level.isThundering()) {
      return SolarGeneratorMode.THUNDER;
    }

    if (level.isDay()) {
      if (
        (this == MK0 || this == MK1) &&
        !hasEnoughSkyLight(level, pos)
      ) {
        return SolarGeneratorMode.NULL;
      }
      return SolarGeneratorMode.SOLAR;
    }

    return SolarGeneratorMode.LUNAR;
  }

  /**
   * 计算当前速率与运行模式。
   *
   * <p>模式由 {@link #determineMode(Level, BlockPos, BooleanSupplier)}
   * 选择，功率由 {@link #getGeneration(SolarGeneratorMode)} 绑定。
   *
   * <p>Calculates the current rate and mode. Conditions select the mode, and
   * generation is bound to that mode.
   */
  public SolarGeneratorStatus getStatus(
    Level level,
    BlockPos pos,
    BooleanSupplier obstructed
  ) {
    SolarGeneratorMode mode = determineMode(level, pos, obstructed);
    return new SolarGeneratorStatus(getGeneration(mode), mode);
  }

  private static boolean hasEnoughSkyLight(Level level, BlockPos pos) {
    return level.getBrightness(LightLayer.SKY, pos.above()) > 10;
  }
}
