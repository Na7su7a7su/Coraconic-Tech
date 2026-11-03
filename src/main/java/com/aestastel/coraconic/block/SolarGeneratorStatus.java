/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.block;

/**
 * 太阳能发电机一次状态计算结果。
 *
 * <p>{@code generation} 是规则允许的当前速率，即使缓存已满也保留该值，便于
 * GUI 显示环境状态；{@code mode} 是该条件下的运行模式。
 *
 * <p>One solar generator status result. {@code generation} is the rule-based
 * current rate even when the buffer is full; {@code mode} is the condition
 * label shown by the GUI.
 */
public record SolarGeneratorStatus(
  int generation,
  SolarGeneratorMode mode
) {}
