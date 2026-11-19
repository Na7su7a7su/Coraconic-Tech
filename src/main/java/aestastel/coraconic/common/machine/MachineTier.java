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
 * 机器技术等级。
 *
 * <p>等级是机器的静态规格属性，不是运行时状态，也不应通过 Registry ID
 * 推断。枚举顺序即等级顺序，从低到高依次为 Primitive、Refined、Enhanced、
 * Superior。
 *
 * <p>Machine technology tier. A tier is a static machine specification, not a
 * runtime state, and must not be inferred from a registry ID. Enum order is the
 * tier order from lowest to highest.
 */
public enum MachineTier {
  PRIMITIVE("primitive", 0),
  REFINED("refined", 1),
  ENHANCED("enhanced", 2),
  SUPERIOR("superior", 3);

  private final String id;
  private final int index;

  MachineTier(String id, int index) {
    this.id = id;
    this.index = index;
  }

  /** {@return 稳定的等级 ID，用于翻译键和配置} */
  public String id() {
    return this.id;
  }

  /** {@return 等级索引，数值越大等级越高} */
  public int index() {
    return this.index;
  }

  /** {@return 当前等级是否不低于 {@code other}} */
  public boolean isAtLeast(MachineTier other) {
    return this.index >= other.index;
  }

  /** {@return 等级显示名的翻译键} */
  public String translationKey() {
    return "machine_tier.coraconic." + this.id;
  }

  /**
   * 按索引查找等级。
   *
   * <p>非法索引会抛出异常，避免静默回退到较低等级。
   *
   * <p>Looks up a tier by index. Invalid indexes throw instead of silently
   * falling back to a lower tier.
   */
  public static MachineTier byIndex(int index) {
    MachineTier[] tiers = values();
    if (index < 0 || index >= tiers.length) {
      throw new IllegalArgumentException("Unknown machine tier index: " + index);
    }
    return tiers[index];
  }

  /**
   * 按稳定 ID 查找等级。
   *
   * <p>无法识别时抛出异常，避免把拼写错误当成有效等级。
   *
   * <p>Looks up a tier by stable ID. Unknown IDs throw instead of being treated
   * as valid tiers.
   */
  public static MachineTier byId(String id) {
    for (MachineTier tier : values()) {
      if (tier.id.equals(id)) {
        return tier;
      }
    }
    throw new IllegalArgumentException("Unknown machine tier id: " + id);
  }
}
