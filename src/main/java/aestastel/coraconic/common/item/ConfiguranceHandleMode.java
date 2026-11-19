/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.item;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * 配置手柄工作模式。
 *
 * <p>三态顺序：旋转 → 扳手 → 配置 → 旋转。
 *
 * <p>Configurance Handle working modes, cycled in rotate → wrench → configure
 * order.
 */
public enum ConfiguranceHandleMode {
  ROTATE,
  WRENCH,
  CONFIGURE;

  /** {@return Shift + 滚轮切换后的下一个模式} */
  public ConfiguranceHandleMode next() {
    ConfiguranceHandleMode[] modes = values();
    return modes[(ordinal() + 1) % modes.length];
  }

  /** {@return 该模式的翻译键} */
  public String translationKey() {
    return (
      "tooltip.coraconic.configurance_handle.mode." +
      name().toLowerCase(Locale.ROOT)
    );
  }

  /** 从网络序号还原模式；非法序号按旋转处理。 */
  public static ConfiguranceHandleMode byOrdinal(int ordinal) {
    ConfiguranceHandleMode[] modes = values();
    return ordinal >= 0 && ordinal < modes.length
      ? modes[ordinal]
      : ROTATE;
  }

  /** 从存档名称还原模式；无法识别时返回 {@code null}。 */
  @Nullable
  public static ConfiguranceHandleMode byName(String name) {
    for (ConfiguranceHandleMode mode : values()) {
      if (mode.name().equals(name)) {
        return mode;
      }
    }
    return null;
  }
}
