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
 * 热力发电机规格。
 *
 * <p>热量容量、热量转化率和熔岩被动发电属于热力发电机家族参数。
 *
 * <p>Heat generator profile. Heat capacity, fuel conversion, and lava passive
 * generation are family-specific parameters.
 */
public record HeatGeneratorProfile(
  MachineTier tier,
  long energyCapacity,
  long maxTransfer,
  int itemChargeRate,
  int heatCapacity,
  int heatPerTick,
  int fePerHeat,
  int passiveGenerationPerLava,
  int mixBonusPerLava
) implements MachineProfile {

  public HeatGeneratorProfile {
    Objects.requireNonNull(tier, "tier");
    requireNonNegative(energyCapacity, "energyCapacity");
    requireNonNegative(maxTransfer, "maxTransfer");
    requireNonNegative(itemChargeRate, "itemChargeRate");
    requireNonNegative(heatCapacity, "heatCapacity");
    requireNonNegative(heatPerTick, "heatPerTick");
    requireNonNegative(fePerHeat, "fePerHeat");
    requireNonNegative(
      passiveGenerationPerLava,
      "passiveGenerationPerLava"
    );
    requireNonNegative(mixBonusPerLava, "mixBonusPerLava");
  }

  @Override
  public MachineType type() {
    return MachineType.HEAT_GENERATOR;
  }

  @Override
  public long maxEnergyInput() {
    return this.maxTransfer;
  }

  @Override
  public long maxEnergyOutput() {
    return this.maxTransfer;
  }

  @Override
  public double processingSpeed() {
    return 0.0D;
  }

  @Override
  public int processingCapacity() {
    return 0;
  }

  /** {@return 主动模式下每 tick 的满额发电量} */
  public int generationPerTick() {
    return this.heatPerTick * this.fePerHeat;
  }

  private static void requireNonNegative(long value, String name) {
    if (value < 0L) {
      throw new IllegalArgumentException(name + " must not be negative");
    }
  }

  private static void requireNonNegative(int value, String name) {
    if (value < 0) {
      throw new IllegalArgumentException(name + " must not be negative");
    }
  }
}
