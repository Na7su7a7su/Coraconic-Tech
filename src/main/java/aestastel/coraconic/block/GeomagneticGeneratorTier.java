/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

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
public enum GeomagneticGeneratorTier {
  MK0(SolarGeneratorTier.MK0, 2, 3, 6),
  MK1(SolarGeneratorTier.MK1, 6, 16, 32),
  MK2(SolarGeneratorTier.MK2, 32, 56, 112),
  MK3(SolarGeneratorTier.MK3, 112, 320, 640);

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

  /** {@return 按序号还原等级；非法值回退到 MK0} */
  public static GeomagneticGeneratorTier byOrdinal(int ordinal) {
    GeomagneticGeneratorTier[] tiers = values();
    return ordinal >= 0 && ordinal < tiers.length
      ? tiers[ordinal]
      : MK0;
  }
}
