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
import aestastel.coraconic.recipe.AlloyingRecipe;
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
 * 合金配方生成器：只负责合金仪配方。
 *
 * <p>Alloying recipe provider.
 */
public class ModAlloyingRecipesProvider extends RecipeProvider {

  public ModAlloyingRecipesProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    // 合金仪测试配方：四个金锭和四个下界合金碎片合成一个下界合金锭。
    // Alloying machine test recipe: four gold ingots and four netherite
    // scraps produce one netherite ingot.
    recipeOutput.accept(
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "alloying/netherite_ingot"
      ),
      new AlloyingRecipe(
        List.of(
          new AlloyingIngredient(Ingredient.of(Items.GOLD_INGOT), 4),
          new AlloyingIngredient(Ingredient.of(Items.NETHERITE_SCRAP), 4)
        ),
        new ItemStack(Items.NETHERITE_INGOT),
        20_000,
        100
      ),
      null
    );
  }
}