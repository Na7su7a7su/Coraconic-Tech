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
 * 具体机器的绝对规格。
 *
 * <p>实现类描述某一台机器在特定 Tier 下的真实参数。没有某项能力的机器
 * 使用 {@code 0} 表示不适用；例如纯发电机没有处理速度时可以返回 0。
 *
 * <p>Absolute specification for a concrete machine. Implementations describe a
 * machine at a specific tier. Unsupported capabilities use {@code 0}; for
 * example, a generator may return 0 processing speed.
 */
public interface MachineProfile {

  /** {@return 机器类型} */
  MachineType type();

  /** {@return 机器等级} */
  MachineTier tier();

  /** {@return 能量缓存容量，单位 FE} */
  long energyCapacity();

  /** {@return 最大能量输入速率，单位 FE/t} */
  long maxEnergyInput();

  /** {@return 最大能量输出速率，单位 FE/t} */
  long maxEnergyOutput();

  /** {@return 基础处理速度倍率；不适用时返回 0} */
  double processingSpeed();

  /** {@return 基础处理能力；不适用时返回 0} */
  int processingCapacity();

  /** {@return 该机器的等级级参考规格} */
  default TierProfile tierProfile() {
    return TierProfile.forTier(tier());
  }
}
