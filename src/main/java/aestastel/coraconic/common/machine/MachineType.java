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
 * 机器类型。
 *
 * <p>机器类型决定机器能做什么；等级只决定机器的技术规格。两者必须分开，
 * 不能让某个等级自动赋予所有同等级机器相同功能。
 *
 * <p>Machine type. The type decides what a machine can do; the tier only
 * describes its technical level. The two must remain separate.
 */
public enum MachineType {
  ALLOYING,
  RESONANCE_CRYSTALLIZING,
  SMELTING_METALLURGY,
  INSTRUMENT_ASSEMBLING,
  ENERGY_STORAGE,
  SOLAR_GENERATOR,
  GEOMAGNETIC_GENERATOR,
  HEAT_GENERATOR
}
