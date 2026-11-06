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
 * 合金仪配方：按数量匹配最多三组原料，并在加工完成后产出结果。
 *
 * <p>配方允许原料放在三个原料槽的任意位置。能量为完整加工过程的总耗能，
 * {@code time} 为所需 tick 数。
 *
 * <p>Alloying recipe: matches up to three ingredient groups by count in any
 * input slot, then produces the result after processing. Energy is the total
 * cost of the process and time is the number of ticks.
 */
public record AlloyingRecipe(
  List<AlloyingIngredient> ingredients,
  ItemStack result,
  int energy,
  int time
) implements Recipe<AlloyingRecipeInput> {

  public static final MapCodec<AlloyingRecipe> CODEC =
    RecordCodecBuilder.mapCodec(instance ->
      instance
        .group(
          AlloyingIngredient.CODEC
            .listOf()
            .fieldOf("ingredients")
            .forGetter(AlloyingRecipe::ingredients),
          ItemStack.STRICT_CODEC
            .fieldOf("result")
            .forGetter(AlloyingRecipe::result),
          Codec.INT
            .optionalFieldOf("energy", 20_000)
            .forGetter(AlloyingRecipe::energy),
          Codec.intRange(1, 72_000)
            .optionalFieldOf("time", 100)
            .forGetter(AlloyingRecipe::time)
        )
        .apply(instance, AlloyingRecipe::new)
    );

  public static final StreamCodec<
    RegistryFriendlyByteBuf,
    AlloyingRecipe
  > STREAM_CODEC = StreamCodec.of(
    AlloyingRecipe::toNetwork,
    AlloyingRecipe::fromNetwork
  );

  @Override
  public boolean matches(AlloyingRecipeInput input, Level level) {
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
   * 从原料槽扣除本配方所需的物品。
   *
   * <p>Consumes this recipe's ingredients from the container input slots.
   */
  public void consume(Container container) {
    int inputSlots = Math.min(3, container.getContainerSize());
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
    AlloyingRecipeInput input,
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

  /** {@return 单个 tick 的能量消耗} */
  public int energyPerTick() {
    return Math.max(1, (this.energy + this.time - 1) / this.time);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipes.ALLOYING_SERIALIZER.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipes.ALLOYING_TYPE.get();
  }

  private static void toNetwork(
    RegistryFriendlyByteBuf buffer,
    AlloyingRecipe recipe
  ) {
    buffer.writeVarInt(recipe.ingredients.size());
    for (AlloyingIngredient ingredient : recipe.ingredients) {
      AlloyingIngredient.STREAM_CODEC.encode(buffer, ingredient);
    }
    ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    buffer.writeVarInt(recipe.energy);
    buffer.writeVarInt(recipe.time);
  }

  private static AlloyingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
    int size = buffer.readVarInt();
    List<AlloyingIngredient> ingredients = new ArrayList<>(size);
    for (int index = 0; index < size; index++) {
      ingredients.add(AlloyingIngredient.STREAM_CODEC.decode(buffer));
    }
    ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
    int energy = buffer.readVarInt();
    int time = buffer.readVarInt();
    return new AlloyingRecipe(ingredients, result, energy, time);
  }
}
