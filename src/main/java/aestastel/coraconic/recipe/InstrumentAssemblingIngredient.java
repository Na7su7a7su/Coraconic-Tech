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
 * 带数量的器械组合原料。
 *
 * <p>与合金原料分开声明，使器械组合配方拥有独立的序列化结构，后续可以
 * 单独扩展原料槽位或其他约束而不影响合金仪配方。
 *
 * <p>An instrument assembling ingredient with a required stack count. It is
 * declared separately from the alloying ingredient so the instrument assembling
 * recipe can evolve its ingredient structure independently.
 */
public record InstrumentAssemblingIngredient(
  Ingredient ingredient,
  int count
) {

  public static final Codec<InstrumentAssemblingIngredient> CODEC =
    RecordCodecBuilder.create(instance ->
      instance
        .group(
          Ingredient.CODEC
            .fieldOf("ingredient")
            .forGetter(InstrumentAssemblingIngredient::ingredient),
          Codec.intRange(1, 64)
            .fieldOf("count")
            .forGetter(InstrumentAssemblingIngredient::count)
        )
        .apply(instance, InstrumentAssemblingIngredient::new)
    );

  public static final StreamCodec<
    RegistryFriendlyByteBuf,
    InstrumentAssemblingIngredient
  > STREAM_CODEC = StreamCodec.of(
    (buffer, value) -> {
      Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, value.ingredient());
      buffer.writeVarInt(value.count());
    },
    buffer ->
      new InstrumentAssemblingIngredient(
        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
        buffer.readVarInt()
      )
  );

  public boolean test(ItemStack stack) {
    return this.ingredient.test(stack);
  }
}
