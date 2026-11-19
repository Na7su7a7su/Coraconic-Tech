/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.block;

import aestastel.coraconic.common.machine.MachineProfile;
import aestastel.coraconic.common.machine.MachineTier;
import aestastel.coraconic.common.machine.MachineType;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * 太阳能发电机等级及发电规则。
 *
 * <p>缓存容量、每面输出和物品充能速率集中在这里声明；发电条件由
 * {@link #getGeneration(Level, BlockPos, BooleanSupplier)} 根据等级、维度、
 * 天气、时间和天空柱遮挡返回本 tick 的发电量。SUPERIOR 在高海拔时优先进入
 * Radiation 模式。
 *
 * <p>Solar generator tiers and generation rules. Capacity, per-face output, and
 * item charge rate live here; the generation method evaluates dimension,
 * weather, time, sky light, and sky-column obstruction. SUPERIOR enters Radiation
 * mode first at high altitude.
 */
public enum SolarGeneratorTier implements MachineProfile {
  PRIMITIVE(MachineTier.PRIMITIVE, 96_000, 600, 600),
  REFINED(MachineTier.REFINED, 192_000, 1_200, 1_200),
  ENHANCED(MachineTier.ENHANCED, 480_000, 2_400, 2_400),
  SUPERIOR(MachineTier.SUPERIOR, 1_920_000, 9_600, 9_600);

  private static final int OVERWORLD_RADIATION_MIN_Y = 315;
  private static final int OTHER_DIMENSION_RADIATION_MIN_Y = 250;

  private final MachineTier machineTier;
  private final int capacity;
  private final int outputPerSide;
  private final int itemChargeRate;

  SolarGeneratorTier(
    MachineTier machineTier,
    int capacity,
    int outputPerSide,
    int itemChargeRate
  ) {
    this.machineTier = machineTier;
    this.capacity = capacity;
    this.outputPerSide = outputPerSide;
    this.itemChargeRate = itemChargeRate;
  }

  @Override
  public MachineType type() {
    return MachineType.SOLAR_GENERATOR;
  }

  @Override
  public MachineTier tier() {
    return this.machineTier;
  }

  @Override
  public long energyCapacity() {
    return this.capacity;
  }

  @Override
  public long maxEnergyInput() {
    return 0L;
  }

  @Override
  public long maxEnergyOutput() {
    return this.outputPerSide;
  }

  @Override
  public double processingSpeed() {
    return 0.0D;
  }

  @Override
  public int processingCapacity() {
    return 0;
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

  /** {@return 按网络/数据序号还原等级；非法值回退到 PRIMITIVE} */
  public static SolarGeneratorTier byOrdinal(int ordinal) {
    SolarGeneratorTier[] tiers = values();
    return ordinal >= 0 && ordinal < tiers.length
      ? tiers[ordinal]
      : PRIMITIVE;
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
        case PRIMITIVE -> 30;
        case REFINED -> 120;
        case ENHANCED -> 240;
        case SUPERIOR -> 960;
      };
      case LUNAR -> switch (this) {
        case PRIMITIVE, REFINED -> 0;
        case ENHANCED -> 120;
        case SUPERIOR -> 240;
      };
      case REFLECTION, NETHER -> this == SUPERIOR ? 80 : 0;
      case THUNDER -> switch (this) {
        case PRIMITIVE, REFINED -> 0;
        case ENHANCED -> 120;
        case SUPERIOR -> 360;
      };
      case RADIATION -> this == SUPERIOR ? 1_440 : 0;
      case NULL -> 0;
    };
  }

  /**
   * 根据环境条件选择运行模式。
   *
   * <p>优先级：高海拔 Radiation（仅 SUPERIOR）→ 下界 → 天空柱遮挡 → 末地 →
   * 雷暴 → 白天/黑夜。末地在无遮挡时固定为 Solar；PRIMITIVE/REFINED 白天天空光不足
   * 时返回 Null。
   *
   * <p>Selects the runtime mode. Priority: high-altitude Radiation (SUPERIOR
   * only), Nether, obstruction, the End, thunder, then daytime/night. The End
   * is always Solar when unobstructed; PRIMITIVE/REFINED report Null during daytime with
   * insufficient sky light.
   */
  public SolarGeneratorMode determineMode(
    Level level,
    BlockPos pos,
    BooleanSupplier obstructed
  ) {
    if (isInRadiationZone(level, pos)) {
      return SolarGeneratorMode.RADIATION;
    }

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
        (this == PRIMITIVE || this == REFINED) &&
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

  private boolean isInRadiationZone(Level level, BlockPos pos) {
    if (this != SUPERIOR) {
      return false;
    }

    int minimumY = level.dimension().equals(Level.OVERWORLD)
      ? OVERWORLD_RADIATION_MIN_Y
      : OTHER_DIMENSION_RADIATION_MIN_Y;
    return pos.getY() >= minimumY;
  }

  private static boolean hasEnoughSkyLight(Level level, BlockPos pos) {
    return level.getBrightness(LightLayer.SKY, pos.above()) > 10;
  }
}
