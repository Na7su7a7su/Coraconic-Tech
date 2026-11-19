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
 * 合金仪机器规格。
 *
 * <p>生产线数量与处理速度属于合金仪家族参数，不放入通用 TierProfile。
 * 处理速度使用整数分子/分母，保持现有 1.2x 加速的精确 tick 行为。
 *
 * <p>Alloying machine profile. Production-line count and processing speed are
 * family-specific parameters and stay out of the generic TierProfile. The speed
 * is stored as an integer numerator/denominator to preserve the exact 1.2x tick
 * behavior.
 */
public record AlloyingMachineProfile(
  MachineTier tier,
  long energyCapacity,
  long maxEnergyInput,
  int lineCount,
  int speedNumerator,
  int speedDenominator
) implements MachineProfile {

  public AlloyingMachineProfile {
    Objects.requireNonNull(tier, "tier");
    requireNonNegative(energyCapacity, "energyCapacity");
    requireNonNegative(maxEnergyInput, "maxEnergyInput");
    if (lineCount < 1) {
      throw new IllegalArgumentException("lineCount must be positive");
    }
    if (speedNumerator < 1 || speedDenominator < 1) {
      throw new IllegalArgumentException(
        "speed numerator and denominator must be positive"
      );
    }
  }

  @Override
  public MachineType type() {
    return MachineType.ALLOYING;
  }

  @Override
  public long maxEnergyOutput() {
    return 0L;
  }

  @Override
  public double processingSpeed() {
    return (double) this.speedNumerator / (double) this.speedDenominator;
  }

  @Override
  public int processingCapacity() {
    return this.lineCount;
  }

  private static void requireNonNegative(long value, String name) {
    if (value < 0L) {
      throw new IllegalArgumentException(name + " must not be negative");
    }
  }
}
