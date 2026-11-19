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
 * 能量存储单元规格。
 *
 * <p>容量与传输速率是储能设备家族参数，不放入通用 TierProfile。
 *
 * <p>Energy storage unit profile. Capacity and transfer rate are family-specific
 * parameters and stay out of the generic TierProfile.
 */
public record EnergyStorageUnitProfile(
  MachineTier tier,
  long energyCapacity,
  long transfer
) implements MachineProfile {

  public EnergyStorageUnitProfile {
    Objects.requireNonNull(tier, "tier");
    if (energyCapacity < 0L) {
      throw new IllegalArgumentException(
        "energyCapacity must not be negative"
      );
    }
    if (transfer < 0L) {
      throw new IllegalArgumentException("transfer must not be negative");
    }
  }

  @Override
  public MachineType type() {
    return MachineType.ENERGY_STORAGE;
  }

  @Override
  public long maxEnergyInput() {
    return this.transfer;
  }

  @Override
  public long maxEnergyOutput() {
    return this.transfer;
  }

  @Override
  public double processingSpeed() {
    return 0.0D;
  }

  @Override
  public int processingCapacity() {
    return 0;
  }
}
