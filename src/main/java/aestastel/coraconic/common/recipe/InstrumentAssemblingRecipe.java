/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 器械组合仪配方：按数量匹配最多五组原料，并在加工完成后产出结果。
 *
 * <p>配方允许原料放在五个原料槽的任意位置。能量为完整加工过程的总耗能，
 * {@code time} 为所需 tick 数。
 *
 * <p>Instrument assembling recipe: matches up to five ingredient groups by
 * count in any input slot, then produces the result after processing. Energy is
 * the total cost of the process and time is the number of ticks.
 */
public record InstrumentAssemblingRecipe(
  List<InstrumentAssemblingIngredient> ingredients,
  ItemStack result,
  int energy,
  int time
) implements Recipe<InstrumentAssemblingRecipeInput> {

  public static final MapCodec<InstrumentAssemblingRecipe> CODEC =
    RecordCodecBuilder.mapCodec(instance ->
      instance
        .group(
          InstrumentAssemblingIngredient.CODEC
            .listOf()
            .fieldOf("ingredients")
            .forGetter(InstrumentAssemblingRecipe::ingredients),
          ItemStack.STRICT_CODEC
            .fieldOf("result")
            .forGetter(InstrumentAssemblingRecipe::result),
          Codec.intRange(0, Integer.MAX_VALUE)
            .optionalFieldOf("energy", 20_000)
            .forGetter(InstrumentAssemblingRecipe::energy),
          Codec.intRange(1, 72_000)
            .optionalFieldOf("time", 100)
            .forGetter(InstrumentAssemblingRecipe::time)
        )
        .apply(instance, InstrumentAssemblingRecipe::new)
    );

  public static final StreamCodec<
    RegistryFriendlyByteBuf,
    InstrumentAssemblingRecipe
  > STREAM_CODEC = StreamCodec.of(
    InstrumentAssemblingRecipe::toNetwork,
    InstrumentAssemblingRecipe::fromNetwork
  );

  public InstrumentAssemblingRecipe {
    ingredients = List.copyOf(ingredients);
    if (ingredients.isEmpty() || ingredients.size() > 5) {
      throw new IllegalArgumentException(
        "Instrument assembling recipes require 1 to 5 ingredient groups"
      );
    }
    if (energy < 0) {
      throw new IllegalArgumentException(
        "Instrument assembling recipe energy must not be negative"
      );
    }
    if (time < 1) {
      throw new IllegalArgumentException(
        "Instrument assembling recipe time must be positive"
      );
    }
  }

  @Override
  public boolean matches(
    InstrumentAssemblingRecipeInput input,
    Level level
  ) {
    return findAssignment(input.items()) != null;
  }

  /**
   * 为当前输入计算一个完整原料分配。
   *
   * <p>使用二分图最大流，而不是按槽位贪心占用：输入槽作为容量来源，原料组
   * 作为需求节点，只要所有需求都能被满足就返回槽位到原料组的分配矩阵。
   *
   * <p>Computes a complete ingredient assignment using bipartite max flow.
   * Slots are supply nodes and recipe groups are demand nodes. Returns a
   * slot-to-group allocation matrix only when every requirement can be met.
   */
  private int[][] findAssignment(List<ItemStack> stacks) {
    int slotCount = stacks.size();
    int ingredientCount = this.ingredients.size();
    if (slotCount == 0 || ingredientCount == 0) {
      return null;
    }

    int source = 0;
    int slotBase = 1;
    int ingredientBase = slotBase + slotCount;
    int sink = ingredientBase + ingredientCount;
    int nodeCount = sink + 1;

    int[][] capacity = new int[nodeCount][nodeCount];
    int[][] edgeCapacity = new int[slotCount][ingredientCount];
    int demand = 0;

    for (int slot = 0; slot < slotCount; slot++) {
      ItemStack stack = stacks.get(slot);
      int count = stack.getCount();
      if (stack.isEmpty() || count <= 0) {
        continue;
      }

      capacity[source][slotBase + slot] = count;
      for (int index = 0; index < ingredientCount; index++) {
        InstrumentAssemblingIngredient required = this.ingredients.get(
          index
        );
        if (!required.test(stack)) {
          continue;
        }
        int edge = Math.min(count, required.count());
        capacity[slotBase + slot][ingredientBase + index] = edge;
        edgeCapacity[slot][index] = edge;
      }
    }

    for (int index = 0; index < ingredientCount; index++) {
      int count = this.ingredients.get(index).count();
      capacity[ingredientBase + index][sink] = count;
      demand += count;
    }

    int flow = 0;
    int[] parent = new int[nodeCount];
    while (true) {
      Arrays.fill(parent, -1);
      parent[source] = source;
      ArrayDeque<Integer> queue = new ArrayDeque<>();
      queue.addLast(source);

      while (!queue.isEmpty() && parent[sink] == -1) {
        int current = queue.removeFirst();
        for (int next = 0; next < nodeCount; next++) {
          if (
            parent[next] == -1 && capacity[current][next] > 0
          ) {
            parent[next] = current;
            queue.addLast(next);
          }
        }
      }

      if (parent[sink] == -1) {
        break;
      }

      int added = Integer.MAX_VALUE;
      for (int node = sink; node != source; node = parent[node]) {
        added = Math.min(added, capacity[parent[node]][node]);
      }
      for (int node = sink; node != source; node = parent[node]) {
        capacity[parent[node]][node] -= added;
        capacity[node][parent[node]] += added;
      }
      flow += added;
    }

    if (flow != demand) {
      return null;
    }

    int[][] allocation = new int[slotCount][ingredientCount];
    for (int slot = 0; slot < slotCount; slot++) {
      for (int index = 0; index < ingredientCount; index++) {
        allocation[slot][index] =
          edgeCapacity[slot][index] -
          capacity[slotBase + slot][ingredientBase + index];
      }
    }
    return allocation;
  }

  /**
   * 从五个原料槽扣除本配方所需的物品。
   *
   * <p>扣除时复用与匹配阶段相同的最大流分配，避免两个阶段选择不同物品。
   *
   * <p>Consumes this recipe's ingredients using the same max-flow allocation as
   * matching, preventing the two phases from choosing different items.
   */
  public void consume(Container container) {
    List<ItemStack> stacks = new ArrayList<>(5);
    for (int slot = 0; slot < 5; slot++) {
      stacks.add(
        slot < container.getContainerSize()
          ? container.getItem(slot)
          : ItemStack.EMPTY
      );
    }

    int[][] allocation = findAssignment(stacks);
    if (allocation == null) {
      return;
    }

    for (int slot = 0; slot < 5; slot++) {
      int amount = 0;
      for (int index = 0; index < this.ingredients.size(); index++) {
        amount += allocation[slot][index];
      }
      if (amount > 0) {
        container.removeItem(slot, amount);
      }
    }
  }

  @Override
  public ItemStack assemble(
    InstrumentAssemblingRecipeInput input,
    HolderLookup.Provider registries
  ) {
    return this.result.copy();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registries) {
    return this.result.copy();
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    NonNullList<Ingredient> list = NonNullList.create();
    for (InstrumentAssemblingIngredient ingredient : this.ingredients) {
      list.add(ingredient.ingredient());
    }
    return list;
  }

  /**
   * 计算指定进度下这一 tick 需要消耗的能量。
   *
   * <p>前面各 tick 消耗固定份额，最后一 tick 补齐剩余部分，确保总耗能
   * 精确等于 {@link #energy()}。
   *
   * <p>Returns the energy cost for the given progress. Earlier ticks consume
   * the regular share and the final tick consumes the remainder, so the total
   * cost is exactly {@link #energy()}.
   */
  public int energyForTick(int progress) {
    if (this.energy <= 0) {
      return 0;
    }

    int clampedProgress = Math.max(0, Math.min(progress, this.time - 1));
    int perTick = Math.max(1, this.energy / this.time);
    long consumedBefore = (long) perTick * clampedProgress;
    long remaining = (long) this.energy - consumedBefore;
    if (remaining <= 0L) {
      return 0;
    }
    if (progress >= this.time - 1) {
      return (int) Math.min(remaining, Integer.MAX_VALUE);
    }
    return (int) Math.min((long) perTick, remaining);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.INSTRUMENT_ASSEMBLING_SERIALIZER.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipes.INSTRUMENT_ASSEMBLING_TYPE.get();
  }

  private static void toNetwork(
    RegistryFriendlyByteBuf buffer,
    InstrumentAssemblingRecipe recipe
  ) {
    buffer.writeVarInt(recipe.ingredients.size());
    for (InstrumentAssemblingIngredient ingredient : recipe.ingredients) {
      InstrumentAssemblingIngredient.STREAM_CODEC.encode(buffer, ingredient);
    }
    ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    buffer.writeVarInt(recipe.energy);
    buffer.writeVarInt(recipe.time);
  }

  private static InstrumentAssemblingRecipe fromNetwork(
    RegistryFriendlyByteBuf buffer
  ) {
    int size = buffer.readVarInt();
    List<InstrumentAssemblingIngredient> ingredients = new ArrayList<>(size);
    for (int index = 0; index < size; index++) {
      ingredients.add(
        InstrumentAssemblingIngredient.STREAM_CODEC.decode(buffer)
      );
    }
    ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
    int energy = buffer.readVarInt();
    int time = buffer.readVarInt();
    return new InstrumentAssemblingRecipe(
      ingredients,
      result,
      energy,
      time
    );
  }
}
