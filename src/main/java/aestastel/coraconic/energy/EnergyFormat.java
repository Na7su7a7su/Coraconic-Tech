/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.energy;

import java.util.Locale;

/**
 * 能量数值显示工具：GUI 与物品 tooltip 共用同一套缩略规则。
 *
 * <p>1,000 以上使用 k，1,000,000 以上使用 M，1,000,000,000 以上使用 G，
 * 并保留两位小数；四舍五入达到 1000.00 时自动进位到下一单位。
 *
 * <p>Shared energy text formatting for the GUI and item tooltips. Values above
 * 1,000 use k, above 1,000,000 use M, and above 1,000,000,000 use G, with two
 * decimal places and automatic promotion when rounding would produce 1000.00.
 */
public final class EnergyFormat {

  private EnergyFormat() {}

  /**
   * 缩略格式（不含末尾的 FE）。
   *
   * <p>Abbreviated value without the trailing FE unit.
   */
  public static String abbreviate(long value) {
    if (value < 1_000L) {
      return Long.toString(value);
    }

    double scaled;
    String unit;
    if (value >= 1_000_000_000L) {
      scaled = value / 1_000_000_000.0D;
      unit = "G";
    } else if (value >= 1_000_000L) {
      scaled = value / 1_000_000.0D;
      unit = "M";
    } else {
      scaled = value / 1_000.0D;
      unit = "k";
    }

    if (scaled >= 999.995D && !"G".equals(unit)) {
      scaled /= 1_000.0D;
      unit = "k".equals(unit) ? "M" : "G";
    }

    return String.format(Locale.ROOT, "%.2f %s", scaled, unit);
  }

  /**
   * 详情格式：完整 FE 数值，不缩略、不加千分位。
   *
   * <p>Full FE value without abbreviation or grouping.
   */
  public static String detail(long value) {
    return Long.toString(value);
  }
}
