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
 * 可声明自身机器规格的方块或机器组件。
 *
 * <p>实现该接口的对象必须返回固定的 {@link MachineProfile}。等级不通过
 * Registry ID 推断，也不作为可变运行状态保存。
 *
 * <p>Block or machine component that declares its machine profile. The returned
 * profile must be fixed; tiers are never inferred from registry IDs or stored
 * as mutable runtime state.
 */
public interface TieredMachine {

  /** {@return 该机器的固定规格} */
  MachineProfile machineProfile();

  /** {@return 该机器的固定等级} */
  default MachineTier tier() {
    return machineProfile().tier();
  }
}
