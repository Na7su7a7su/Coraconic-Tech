/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
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
 * 共振晶化仪配方：按数量匹配最多两组原料，并在加工完成后产出结果。
 *
 * <p>原料可以放在两个输入槽的任意位置。能量为完整加工过程的总耗能，
 * {@code time} 为所需 tick 数。
 *
 * <p>Resonance crystallizing recipe: matches up to two ingredient groups by
 * count in either input slot, then produces the result after processing.
 * Energy is the total cost of the process and time is the number of ticks.
 */
public record ResonanceCrystallizingRecipe(
  List<AlloyingIngredient> ingredients,
  ItemStack result,
  int energy,
  int time
) implements Recipe<ResonanceCrystallizingRecipeInput> {

  public static final MapCodec<ResonanceCrystallizingRecipe> CODEC =
    RecordCodecBuilder.mapCodec(instance ->
      instance
        .group(
          AlloyingIngredient.CODEC
            .listOf()
            .fieldOf("ingredients")
            .forGetter(ResonanceCrystallizingRecipe::ingredients),
          ItemStack.STRICT_CODEC
            .fieldOf("result")
            .forGetter(ResonanceCrystallizingRecipe::result),
          Codec.INT
            .fieldOf("energy")
            .forGetter(ResonanceCrystallizingRecipe::energy),
          Codec.intRange(1, 72_000)
            .fieldOf("time")
            .forGetter(ResonanceCrystallizingRecipe::time)
        )
        .apply(instance, ResonanceCrystallizingRecipe::new)
    );

  public static final StreamCodec<
    RegistryFriendlyByteBuf,
    ResonanceCrystallizingRecipe
  > STREAM_CODEC = StreamCodec.of(
    ResonanceCrystallizingRecipe::toNetwork,
    ResonanceCrystallizingRecipe::fromNetwork
  );

  @Override
  public boolean matches(
    ResonanceCrystallizingRecipeInput input,
    Level level
  ) {
    int[] available = new int[input.size()];
    for (int index = 0; index < input.size(); index++) {
      available[index] = input.getItem(index).getCount();
    }

    for (AlloyingIngredient required : this.ingredients) {
      int remaining = required.count();
      for (int index = 0; index < input.size(); index++) {
        ItemStack stack = input.getItem(index);
        if (
          available[index] <= 0 ||
          stack.isEmpty() ||
          !required.test(stack)
        ) {
          continue;
        }
        int used = Math.min(remaining, available[index]);
        available[index] -= used;
        remaining -= used;
        if (remaining <= 0) {
          break;
        }
      }
      if (remaining > 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * 从两个原料槽扣除本配方所需的物品。
   *
   * <p>Consumes this recipe's ingredients from the two input slots.
   */
  public void consume(Container container) {
    int inputSlots = Math.min(2, container.getContainerSize());
    for (AlloyingIngredient required : this.ingredients) {
      int remaining = required.count();
      for (int slot = 0; slot < inputSlots && remaining > 0; slot++) {
        ItemStack stack = container.getItem(slot);
        if (stack.isEmpty() || !required.test(stack)) {
          continue;
        }
        int used = Math.min(remaining, stack.getCount());
        container.removeItem(slot, used);
        remaining -= used;
      }
    }
  }

  @Override
  public ItemStack assemble(
    ResonanceCrystallizingRecipeInput input,
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
    for (AlloyingIngredient ingredient : this.ingredients) {
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
    return ModRecipes.RESONANCE_CRYSTALLIZING_SERIALIZER.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipes.RESONANCE_CRYSTALLIZING_TYPE.get();
  }

  private static void toNetwork(
    RegistryFriendlyByteBuf buffer,
    ResonanceCrystallizingRecipe recipe
  ) {
    buffer.writeVarInt(recipe.ingredients.size());
    for (AlloyingIngredient ingredient : recipe.ingredients) {
      AlloyingIngredient.STREAM_CODEC.encode(buffer, ingredient);
    }
    ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    buffer.writeVarInt(recipe.energy);
    buffer.writeVarInt(recipe.time);
  }

  private static ResonanceCrystallizingRecipe fromNetwork(
    RegistryFriendlyByteBuf buffer
  ) {
    int size = buffer.readVarInt();
    List<AlloyingIngredient> ingredients = new ArrayList<>(size);
    for (int index = 0; index < size; index++) {
      ingredients.add(AlloyingIngredient.STREAM_CODEC.decode(buffer));
    }
    ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
    int energy = buffer.readVarInt();
    int time = buffer.readVarInt();
    return new ResonanceCrystallizingRecipe(
      ingredients,
      result,
      energy,
      time
    );
  }
}
