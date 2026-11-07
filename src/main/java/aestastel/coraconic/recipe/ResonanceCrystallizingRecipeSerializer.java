/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 共振晶化配方序列化器。
 *
 * <p>Resonance crystallizing recipe serializer.
 */
public class ResonanceCrystallizingRecipeSerializer
  implements RecipeSerializer<ResonanceCrystallizingRecipe>
{

  @Override
  public MapCodec<ResonanceCrystallizingRecipe> codec() {
    return ResonanceCrystallizingRecipe.CODEC;
  }

  @Override
  public StreamCodec<
    RegistryFriendlyByteBuf,
    ResonanceCrystallizingRecipe
  > streamCodec() {
    return ResonanceCrystallizingRecipe.STREAM_CODEC;
  }
}
