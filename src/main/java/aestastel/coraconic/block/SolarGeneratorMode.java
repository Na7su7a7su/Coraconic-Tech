/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

/**
 * 太阳能发电机界面的运行模式。
 *
 * <p>模式名称固定为英文，便于保持 CLI 风格。
 *
 * <p>Solar generator runtime modes. Display names remain in English to keep the
 * requested CLI style.
 */
public enum SolarGeneratorMode {
  SOLAR("Solar"),
  LUNAR("Lunar"),
  REFLECTION("Reflection"),
  NETHER("Nether"),
  THUNDER("Thunder");

  private final String displayName;

  SolarGeneratorMode(String displayName) {
    this.displayName = displayName;
  }

  public String displayName() {
    return displayName;
  }

  public static SolarGeneratorMode byOrdinal(int ordinal) {
    SolarGeneratorMode[] modes = values();
    return ordinal >= 0 && ordinal < modes.length
      ? modes[ordinal]
      : SOLAR;
  }
}
