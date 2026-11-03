/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.energy.EnergyFaceMode;
import net.minecraft.core.Direction;

/**
 * 提供逐面能量模式读写的容器菜单。
 *
 * <p>能量存储单元与太阳能发电机的 GUI 共用该接口，以便共享同一个面配置
 * 弹窗。客户端从菜单数据槽读取模式，点击通过原版菜单按钮包提交到服务端。
 *
 * <p>Container-menu contract for face-mode UIs. The energy storage and solar
 * generator screens share the same configuration popup; the client reads modes
 * from menu data slots and submits changes through the vanilla menu-button
 * packet.
 */
public interface EnergyFaceMenu {

  int FACE_MODE_COUNT = 6;

  /** {@return 当前客户端同步到的指定面模式} */
  EnergyFaceMode getFaceMode(Direction side);

  /**
   * 面模式按钮 ID：0..5 正向切换，6..11 反向切换。
   *
   * <p>Menu button id: 0..5 cycle forward, 6..11 cycle backward.
   */
  default int faceModeButtonId(Direction side, boolean reverse) {
    return side.get3DDataValue() + (reverse ? FACE_MODE_COUNT : 0);
  }
}
