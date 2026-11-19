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

/**
 * 地磁发电机等级与模式功率表。
 *
 * <p>缓存、每面输出和物品充能复用对应太阳能等级；发电功率由本枚举按
 * 模式提供。
 *
 * <p>Geomagnetic generator tiers and mode power table. Capacity, per-face
 * output, and item charge rate reuse the matching solar tier; generation is
 * supplied per mode by this enum.
 */
public enum GeomagneticGeneratorTier implements MachineProfile {
  PRIMITIVE(SolarGeneratorTier.PRIMITIVE, 2, 3, 6),
  REFINED(SolarGeneratorTier.REFINED, 6, 16, 32),
  ENHANCED(SolarGeneratorTier.ENHANCED, 32, 56, 112),
  SUPERIOR(SolarGeneratorTier.SUPERIOR, 112, 320, 640);

  private final SolarGeneratorTier solarTier;
  private final int overworldGeneration;
  private final int netherGeneration;
  private final int endGeneration;

  GeomagneticGeneratorTier(
    SolarGeneratorTier solarTier,
    int overworldGeneration,
    int netherGeneration,
    int endGeneration
  ) {
    this.solarTier = solarTier;
    this.overworldGeneration = overworldGeneration;
    this.netherGeneration = netherGeneration;
    this.endGeneration = endGeneration;
  }

  @Override
  public MachineType type() {
    return MachineType.GEOMAGNETIC_GENERATOR;
  }

  @Override
  public MachineTier tier() {
    return this.solarTier.tier();
  }

  @Override
  public long energyCapacity() {
    return this.solarTier.energyCapacity();
  }

  @Override
  public long maxEnergyInput() {
    return 0L;
  }

  @Override
  public long maxEnergyOutput() {
    return this.solarTier.maxEnergyOutput();
  }

  @Override
  public double processingSpeed() {
    return 0.0D;
  }

  @Override
  public int processingCapacity() {
    return 0;
  }

  /** {@return 对应的太阳能缓存/输出等级} */
  public SolarGeneratorTier getSolarTier() {
    return solarTier;
  }

  /** {@return 指定模式在本等级的发电速率} */
  public int getGeneration(GeomagneticGeneratorMode mode) {
    return switch (mode) {
      case OVERWORLD -> overworldGeneration;
      case NETHER -> netherGeneration;
      case END -> endGeneration;
      case NULL -> 0;
    };
  }

  /** {@return 按序号还原等级；非法值回退到 PRIMITIVE} */
  public static GeomagneticGeneratorTier byOrdinal(int ordinal) {
    GeomagneticGeneratorTier[] tiers = values();
    return ordinal >= 0 && ordinal < tiers.length
      ? tiers[ordinal]
      : PRIMITIVE;
  }
}
