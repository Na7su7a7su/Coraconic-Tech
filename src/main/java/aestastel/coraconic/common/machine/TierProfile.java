/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.machine;

import java.util.Objects;

/**
 * 等级级通用规格基准。
 *
 * <p>本记录只提供等级元数据和参考倍率，不保存任何机器的绝对容量。绝对
 * 容量由 {@link MachineProfile} 按机器家族定义，避免把 Refined Energy
 * Storage 的容量错误套用到 Alloying Machine Refined 上。
 *
 * <p>Tier-level reference profile. It holds tier metadata and reference
 * multipliers only, never absolute machine capacities. Absolute values belong
 * to {@link MachineProfile} implementations, so different machine families can
 * share a tier without sharing the same storage or transfer values.
 */
public record TierProfile(
  MachineTier tier,
  double processingSpeedMultiplier,
  double energyCapacityMultiplier,
  double energyTransferMultiplier,
  double processingCapacityMultiplier
) {

  public static final TierProfile PRIMITIVE = new TierProfile(
    MachineTier.PRIMITIVE,
    1.0,
    1.0,
    1.0,
    1.0
  );

  public static final TierProfile REFINED = new TierProfile(
    MachineTier.REFINED,
    1.0,
    2.0,
    2.0,
    2.0
  );

  public static final TierProfile ENHANCED = new TierProfile(
    MachineTier.ENHANCED,
    1.2,
    4.0,
    4.0,
    4.0
  );

  public static final TierProfile SUPERIOR = new TierProfile(
    MachineTier.SUPERIOR,
    1.5,
    8.0,
    8.0,
    8.0
  );

  public TierProfile {
    Objects.requireNonNull(tier, "tier");
    requirePositive(processingSpeedMultiplier, "processingSpeedMultiplier");
    requirePositive(energyCapacityMultiplier, "energyCapacityMultiplier");
    requirePositive(energyTransferMultiplier, "energyTransferMultiplier");
    requirePositive(
      processingCapacityMultiplier,
      "processingCapacityMultiplier"
    );
  }

  /** {@return 指定等级的参考规格} */
  public static TierProfile forTier(MachineTier tier) {
    return switch (tier) {
      case PRIMITIVE -> PRIMITIVE;
      case REFINED -> REFINED;
      case ENHANCED -> ENHANCED;
      case SUPERIOR -> SUPERIOR;
    };
  }

  private static void requirePositive(double value, String name) {
    if (!Double.isFinite(value) || value <= 0.0D) {
      throw new IllegalArgumentException(
        name + " must be finite and positive"
      );
    }
  }
}
