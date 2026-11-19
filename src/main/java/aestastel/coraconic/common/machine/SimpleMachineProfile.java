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
 * 通用机器规格实现。
 *
 * <p>用于没有额外家族参数的机器。没有某项能力时使用 {@code 0}。
 *
 * <p>Generic machine profile for machines without extra family-specific
 * parameters. Unsupported capabilities use {@code 0}.
 */
public record SimpleMachineProfile(
  MachineType type,
  MachineTier tier,
  long energyCapacity,
  long maxEnergyInput,
  long maxEnergyOutput,
  double processingSpeed,
  int processingCapacity
) implements MachineProfile {

  public SimpleMachineProfile {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(tier, "tier");
    requireNonNegative(energyCapacity, "energyCapacity");
    requireNonNegative(maxEnergyInput, "maxEnergyInput");
    requireNonNegative(maxEnergyOutput, "maxEnergyOutput");
    requireNonNegative(processingSpeed, "processingSpeed");
    requireNonNegative(processingCapacity, "processingCapacity");
  }

  private static void requireNonNegative(long value, String name) {
    if (value < 0L) {
      throw new IllegalArgumentException(name + " must not be negative");
    }
  }

  private static void requireNonNegative(double value, String name) {
    if (!Double.isFinite(value) || value < 0.0D) {
      throw new IllegalArgumentException(name + " must be finite and non-negative");
    }
  }
}
