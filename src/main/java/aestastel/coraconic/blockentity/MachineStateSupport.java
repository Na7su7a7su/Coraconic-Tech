/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.blockentity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 机器状态读档时的边界保护工具。
 *
 * <p>Boundary checks applied when loading machine state.
 */
public final class MachineStateSupport {

  private MachineStateSupport() {}

  public static long clampEnergy(long value, long capacity) {
    return Math.max(0L, Math.min(value, capacity));
  }

  /**
   * 修正反序列化后的库存数量与空槽。
   *
   * <p>Fixes deserialized stack counts and empty slots.
   */
  public static void sanitizeInventory(
    ItemStackHandler handler,
    int slotCount
  ) {
    int limit = Math.min(slotCount, handler.getSlots());
    for (int slot = 0; slot < limit; slot++) {
      ItemStack stack = handler.getStackInSlot(slot);
      if (stack.isEmpty() || stack.getCount() <= 0) {
        if (!stack.isEmpty()) {
          handler.setStackInSlot(slot, ItemStack.EMPTY);
        }
        continue;
      }

      int maxCount = stack.getMaxStackSize();
      if (stack.getCount() > maxCount) {
        ItemStack clamped = stack.copy();
        clamped.setCount(maxCount);
        handler.setStackInSlot(slot, clamped);
      }
    }
  }
}
