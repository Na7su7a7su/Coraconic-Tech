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
 * 地磁发电机界面的运行模式。
 *
 * <p>模式名称固定为英文，便于保持 CLI 风格。
 *
 * <p>Geomagnetic generator runtime modes. Display names remain in English to
 * keep the requested CLI style.
 */
public enum GeomagneticGeneratorMode implements GeneratorMode {
  OVERWORLD("Overworld"),
  NETHER("Nether"),
  END("End"),
  NULL("Null");

  private final String displayName;

  GeomagneticGeneratorMode(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String displayName() {
    return displayName;
  }

  public static GeomagneticGeneratorMode byOrdinal(int ordinal) {
    GeomagneticGeneratorMode[] modes = values();
    return ordinal >= 0 && ordinal < modes.length
      ? modes[ordinal]
      : NULL;
  }
}
