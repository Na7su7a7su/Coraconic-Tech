/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.datagen;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.recipe.AlloyingIngredient;
import aestastel.coraconic.recipe.ResonanceCrystallizingRecipe;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 共振晶化配方生成器。
 *
 * <p>Resonance crystallizing recipe provider.
 */
public class ModResonanceCrystallizingRecipesProvider extends RecipeProvider {

  public ModResonanceCrystallizingRecipesProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    // 测试配方：一个钻石和一个木炭产出两个钻石。
    // Test recipe: one diamond and one charcoal produce two diamonds.
    recipeOutput.accept(
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "resonance_crystallizing/diamond_from_charcoal"
      ),
      new ResonanceCrystallizingRecipe(
        List.of(
          new AlloyingIngredient(Ingredient.of(Items.DIAMOND), 1),
          new AlloyingIngredient(Ingredient.of(Items.CHARCOAL), 1)
        ),
        new ItemStack(Items.DIAMOND, 2),
        100_000,
        300
      ),
      null
    );
  }
}
