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
 * 合金配方序列化器。
 *
 * <p>Alloying recipe serializer.
 */
public class AlloyingRecipeSerializer
  implements RecipeSerializer<AlloyingRecipe>
{

  @Override
  public MapCodec<AlloyingRecipe> codec() {
    return AlloyingRecipe.CODEC;
  }

  @Override
  public StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> streamCodec() {
    return AlloyingRecipe.STREAM_CODEC;
  }
}
