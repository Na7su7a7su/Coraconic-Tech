/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 共振晶化仪的配方输入视图。
 *
 * <p>输入列表对应两个原料槽；空槽以 {@link ItemStack#EMPTY} 表示。
 *
 * <p>Recipe input view for the resonance crystallizing machine. The list maps
 * to the two input slots; empty slots use {@link ItemStack#EMPTY}.
 */
public record ResonanceCrystallizingRecipeInput(List<ItemStack> items)
  implements RecipeInput
{

  public ResonanceCrystallizingRecipeInput {
    items = List.copyOf(items);
  }

  @Override
  public ItemStack getItem(int index) {
    return this.items.get(index);
  }

  @Override
  public int size() {
    return this.items.size();
  }
}
