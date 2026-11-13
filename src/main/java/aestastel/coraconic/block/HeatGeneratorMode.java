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
 * 热力发电机运行模式。
 *
 * <p>Heat generator runtime modes. Display names remain in English to match the
 * existing generator CLI style.
 */
public enum HeatGeneratorMode implements GeneratorMode {
  INITIACTIVE("Initiactive"),
  NULL("Null");

  private final String displayName;

  HeatGeneratorMode(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String displayName() {
    return this.displayName;
  }

  public static HeatGeneratorMode byOrdinal(int ordinal) {
    HeatGeneratorMode[] modes = values();
    return ordinal >= 0 && ordinal < modes.length
      ? modes[ordinal]
      : NULL;
  }
}
