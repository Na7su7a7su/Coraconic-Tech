/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.machine;

/**
 * 已迁移机器的固定规格常量。
 *
 * <p>这些值是迁移前硬编码参数的唯一来源，机器方块与方块实体应从这里读取。
 *
 * <p>Fixed profiles for migrated machines. These values are the single source of
 * truth for the parameters that were previously hard-coded in machine blocks
 * and block entities.
 */
public final class MachineProfiles {

  public static final AlloyingMachineProfile ALLOYING_REFINED =
    new AlloyingMachineProfile(
      MachineTier.REFINED,
      320_000L,
      10_000L,
      1,
      1,
      1
    );

  public static final AlloyingMachineProfile ALLOYING_ENHANCED =
    new AlloyingMachineProfile(
      MachineTier.ENHANCED,
      1_040_000L,
      40_000L,
      3,
      6,
      5
    );

  public static final SimpleMachineProfile RESONANCE_CRYSTALLIZING_REFINED =
    new SimpleMachineProfile(
      MachineType.RESONANCE_CRYSTALLIZING,
      MachineTier.REFINED,
      640_000L,
      1_000L,
      0L,
      1.0D,
      1
    );

  public static final SmeltingMetallurgyMachineProfile
    SMELTING_METALLURGY_REFINED = new SmeltingMetallurgyMachineProfile(
      MachineTier.REFINED,
      320_000L,
      10_000L,
      2
    );

  public static final EnergyStorageUnitProfile ENERGY_STORAGE_REFINED =
    new EnergyStorageUnitProfile(
      MachineTier.REFINED,
      32_000_000L,
      4_000_000L
    );

  public static final EnergyStorageUnitProfile ENERGY_STORAGE_ENHANCED =
    new EnergyStorageUnitProfile(
      MachineTier.ENHANCED,
      320_000_000L,
      16_000_000L
    );

  public static final EnergyStorageUnitProfile ENERGY_STORAGE_SUPERIOR =
    new EnergyStorageUnitProfile(
      MachineTier.SUPERIOR,
      3_200_000_000L,
      64_000_000L
    );

  public static final HeatGeneratorProfile HEAT_GENERATOR_PRIMITIVE =
    new HeatGeneratorProfile(
      MachineTier.PRIMITIVE,
      96_000L,
      600L,
      600,
      640_000,
      40,
      2,
      10,
      5
    );

  private MachineProfiles() {}
}
