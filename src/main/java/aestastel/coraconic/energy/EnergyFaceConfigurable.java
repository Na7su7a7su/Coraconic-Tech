/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.energy;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * 支持逐面配置能量输入/输出模式的方块实体。
 *
 * <p>当前由能量存储单元与太阳能发电机实现。扳手配置模式通过本接口读取和
 * 切换模式；能力接口通过 {@link EnergyFaceStorage} 按面门控。
 *
 * <p>Implemented by energy-capable block entities. The wrench configuration
 * mode reads and cycles modes through this interface, while capabilities gate
 * each physical side through {@link EnergyFaceStorage}.
 */
public interface EnergyFaceConfigurable {

  /** {@return 指定面的模式；{@code null} 表示不区分面} */
  EnergyFaceMode getFaceMode(@Nullable Direction side);

  /** 设置指定面的模式。 */
  void setFaceMode(Direction side, EnergyFaceMode mode);

  /** {@return 新方块或缺失 NBT 时使用的默认模式} */
  EnergyFaceMode getDefaultFaceMode();

  /** 按配置顺序切换指定面的模式，并返回新模式。 */
  default EnergyFaceMode cycleFaceMode(Direction side) {
    return cycleFaceMode(side, false);
  }

  /**
   * 按配置顺序切换指定面的模式；{@code reverse} 为 true 时反向切换。
   *
   * <p>Cycles the face mode in either direction and returns the new mode.
   */
  default EnergyFaceMode cycleFaceMode(Direction side, boolean reverse) {
    EnergyFaceMode next = reverse
      ? getFaceMode(side).previous()
      : getFaceMode(side).next();
    setFaceMode(side, next);
    return next;
  }

  /** {@return 是否至少有一个面的模式不是默认值} */
  default boolean hasNonDefaultFaceModes() {
    EnergyFaceMode fallback = getDefaultFaceMode();
    for (Direction side : Direction.values()) {
      if (getFaceMode(side) != fallback) {
        return true;
      }
    }
    return false;
  }
}
