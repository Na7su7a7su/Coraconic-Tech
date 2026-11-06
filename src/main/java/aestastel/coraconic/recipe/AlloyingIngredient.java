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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 带数量的合金原料。
 *
 * <p>An alloying ingredient with a required stack count.
 */
public record AlloyingIngredient(Ingredient ingredient, int count) {

  public static final Codec<AlloyingIngredient> CODEC =
    RecordCodecBuilder.create(instance ->
      instance
        .group(
          Ingredient.CODEC
            .fieldOf("ingredient")
            .forGetter(AlloyingIngredient::ingredient),
          Codec.intRange(1, 64)
            .fieldOf("count")
            .forGetter(AlloyingIngredient::count)
        )
        .apply(instance, AlloyingIngredient::new)
    );

  public static final StreamCodec<
    RegistryFriendlyByteBuf,
    AlloyingIngredient
  > STREAM_CODEC = StreamCodec.of(
    (buffer, value) -> {
      Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, value.ingredient());
      buffer.writeVarInt(value.count());
    },
    buffer ->
      new AlloyingIngredient(
        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
        buffer.readVarInt()
      )
  );

  public boolean test(ItemStack stack) {
    return this.ingredient.test(stack);
  }
}
