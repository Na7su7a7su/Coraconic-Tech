/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.energy;

import java.util.Locale;

/**
 * 能量方块单个面的输入/输出模式。
 *
 * <p>枚举顺序就是扳手配置模式下的切换顺序：
 * 输入 → 输出 → 输入/输出 → 阻断 → 输入。
 *
 * <p>Per-face energy transfer mode. The enum order is also the order used by
 * the wrench configuration mode: input, output, input/output, blocked.
 */
public enum EnergyFaceMode {
  INPUT(true, false),
  OUTPUT(false, true),
  INPUT_OUTPUT(true, true),
  BLOCKED(false, false);

  private final boolean allowsInput;
  private final boolean allowsOutput;

  EnergyFaceMode(boolean allowsInput, boolean allowsOutput) {
    this.allowsInput = allowsInput;
    this.allowsOutput = allowsOutput;
  }

  /** {@return 该面是否允许能量输入} */
  public boolean allowsInput() {
    return allowsInput;
  }

  /** {@return 该面是否允许能量输出} */
  public boolean allowsOutput() {
    return allowsOutput;
  }

  /** {@return 配置模式下的下一个模式} */
  public EnergyFaceMode next() {
    EnergyFaceMode[] modes = values();
    return modes[(ordinal() + 1) % modes.length];
  }

  /** {@return 该模式的翻译键} */
  public String translationKey() {
    return "energy_face_mode.coraconic." + name().toLowerCase(Locale.ROOT);
  }

  /**
   * 从网络或 NBT 序号还原模式；非法序号按阻断处理。
   *
   * <p>Restores a mode from a network/NBT ordinal, falling back to blocked.
   */
  public static EnergyFaceMode byOrdinal(int ordinal) {
    EnergyFaceMode[] modes = values();
    return ordinal >= 0 && ordinal < modes.length
      ? modes[ordinal]
      : BLOCKED;
  }
}
