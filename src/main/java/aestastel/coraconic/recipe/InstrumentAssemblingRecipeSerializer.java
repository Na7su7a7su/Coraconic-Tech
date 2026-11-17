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
 * 器械组合配方序列化器。
 *
 * <p>Instrument assembling recipe serializer.
 */
public class InstrumentAssemblingRecipeSerializer
  implements RecipeSerializer<InstrumentAssemblingRecipe>
{

  @Override
  public MapCodec<InstrumentAssemblingRecipe> codec() {
    return InstrumentAssemblingRecipe.CODEC;
  }

  @Override
  public StreamCodec<
    RegistryFriendlyByteBuf,
    InstrumentAssemblingRecipe
  > streamCodec() {
    return InstrumentAssemblingRecipe.STREAM_CODEC;
  }
}
