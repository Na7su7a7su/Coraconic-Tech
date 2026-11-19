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
 * 熔炼冶金仪机器规格。
 *
 * <p>每 tick 能耗属于熔炼冶金仪家族参数，不放入通用 TierProfile。
 *
 * <p>Smelting-metallurgy machine profile. Energy per tick is a family-specific
 * parameter and stays out of the generic TierProfile.
 */
public record SmeltingMetallurgyMachineProfile(
  MachineTier tier,
  long energyCapacity,
  long maxEnergyInput,
  int energyPerTick
) implements MachineProfile {

  public SmeltingMetallurgyMachineProfile {
    Objects.requireNonNull(tier, "tier");
    if (energyCapacity < 0L) {
      throw new IllegalArgumentException(
        "energyCapacity must not be negative"
      );
    }
    if (maxEnergyInput < 0L) {
      throw new IllegalArgumentException(
        "maxEnergyInput must not be negative"
      );
    }
    if (energyPerTick < 0) {
      throw new IllegalArgumentException(
        "energyPerTick must not be negative"
      );
    }
  }

  @Override
  public MachineType type() {
    return MachineType.SMELTING_METALLURGY;
  }

  @Override
  public long maxEnergyOutput() {
    return 0L;
  }

  @Override
  public double processingSpeed() {
    return 1.0D;
  }

  @Override
  public int processingCapacity() {
    return 1;
  }
}
