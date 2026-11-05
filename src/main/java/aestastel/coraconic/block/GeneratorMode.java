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
 * 发电机运行模式的公共接口。
 *
 * <p>太阳能与地磁发电机使用各自的模式枚举，但 GUI 和状态对象只需要读取
 * 显示名称与同步序号。显示名称保持英文以维持 CLI 风格。
 *
 * <p>Common interface for generator runtime modes. Solar and geomagnetic
 * generators use separate enums, while the GUI and status objects only need a
 * display name and synchronization ordinal. Display names remain English to
 * keep the CLI style.
 */
public interface GeneratorMode {
  String displayName();

  int ordinal();
}
