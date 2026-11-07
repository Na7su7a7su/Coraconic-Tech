/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.recipe;

import aestastel.coraconic.CoraconicTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组配方类型与序列化器注册器。
 *
 * <p>Mod recipe type and serializer registry.
 */
public final class ModRecipes {

  public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
    DeferredRegister.create(Registries.RECIPE_TYPE, CoraconicTech.MODID);

  public static final DeferredRegister<RecipeSerializer<?>>
    RECIPE_SERIALIZERS = DeferredRegister.create(
      Registries.RECIPE_SERIALIZER,
      CoraconicTech.MODID
    );

  public static final DeferredHolder<
    RecipeType<?>,
    RecipeType<AlloyingRecipe>
  > ALLOYING_TYPE = RECIPE_TYPES.register(
    "alloying",
    () ->
      RecipeType.simple(
        ResourceLocation.fromNamespaceAndPath(
          CoraconicTech.MODID,
          "alloying"
        )
      )
  );

  public static final DeferredHolder<
    RecipeSerializer<?>,
    RecipeSerializer<AlloyingRecipe>
  > ALLOYING_SERIALIZER = RECIPE_SERIALIZERS.register(
    "alloying",
    AlloyingRecipeSerializer::new
  );

  public static final DeferredHolder<
    RecipeType<?>,
    RecipeType<ResonanceCrystallizingRecipe>
  > RESONANCE_CRYSTALLIZING_TYPE = RECIPE_TYPES.register(
    "resonance_crystallizing",
    () ->
      RecipeType.simple(
        ResourceLocation.fromNamespaceAndPath(
          CoraconicTech.MODID,
          "resonance_crystallizing"
        )
      )
  );

  public static final DeferredHolder<
    RecipeSerializer<?>,
    RecipeSerializer<ResonanceCrystallizingRecipe>
  > RESONANCE_CRYSTALLIZING_SERIALIZER =
    RECIPE_SERIALIZERS.register(
      "resonance_crystallizing",
      ResonanceCrystallizingRecipeSerializer::new
    );

  private ModRecipes() {}

  public static void register(IEventBus eventBus) {
    RECIPE_TYPES.register(eventBus);
    RECIPE_SERIALIZERS.register(eventBus);
  }
}
