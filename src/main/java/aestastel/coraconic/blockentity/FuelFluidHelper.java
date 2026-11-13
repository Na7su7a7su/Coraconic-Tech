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
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

/**
 * 流体燃料辅助工具。
 *
 * <p>以该流体对应的桶装物品作为燃烧时间来源；每次转换固定使用 1000 mB。
 *
 * <p>Utility for fluid fuels. The burn time comes from the fluid's bucket item,
 * and each conversion uses exactly 1000 mB.
 */
public final class FuelFluidHelper {

  public static final int BUCKET_VOLUME = 1000;

  private FuelFluidHelper() {}

  public static int getBurnTime(FluidStack fluid) {
    if (fluid.isEmpty()) {
      return 0;
    }
    ItemStack bucket = fluid
      .getFluid()
      .getFluidType()
      .getBucket(fluid);
    return bucket.getBurnTime(RecipeType.SMELTING);
  }

  /**
   * {@return 第一个含有至少 1000 mB 可燃烧流体的槽位，找不到时返回 -1}
   *
   * <p>Returns the first tank containing at least 1000 mB of burnable fluid,
   * or -1 when none exists.
   */
  public static int findFuelTank(IFluidHandlerItem handler) {
    for (int tank = 0; tank < handler.getTanks(); tank++) {
      FluidStack fluid = handler.getFluidInTank(tank);
      if (
        fluid.getAmount() >= BUCKET_VOLUME &&
        getBurnTime(fluid) > 0
      ) {
        return tank;
      }
    }
    return -1;
  }

  public static boolean hasFuelFluid(ItemStack stack) {
    IFluidHandlerItem handler = stack.getCapability(
      Capabilities.FluidHandler.ITEM
    );
    return handler != null && findFuelTank(handler) >= 0;
  }
}
