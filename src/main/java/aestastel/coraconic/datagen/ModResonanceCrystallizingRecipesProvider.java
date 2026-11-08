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
import aestastel.coraconic.item.ModItems;
import aestastel.coraconic.recipe.AlloyingIngredient;
import aestastel.coraconic.recipe.ResonanceCrystallizingRecipe;
import aestastel.coraconic.tag.ModItemTags;
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
        150
      ),
      null
    );

    recipeOutput.accept(
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "resonance_crystallizing/diamond_from_coal"
      ),
      new ResonanceCrystallizingRecipe(
        List.of(
          new AlloyingIngredient(Ingredient.of(Items.DIAMOND), 2),
          new AlloyingIngredient(Ingredient.of(Items.COAL), 1)
        ),
        new ItemStack(Items.DIAMOND, 3),
        100_000,
        150
      ),
      null
    );

    recipeOutput.accept(
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "resonance_crystallizing/coradiamond"
      ),
      new ResonanceCrystallizingRecipe(
        List.of(
          new AlloyingIngredient(Ingredient.of(Items.DIAMOND), 1),
          new AlloyingIngredient(
            Ingredient.of(ModItemTags.ELEMENTS_CORACONIC),
            1
          )
        ),
        new ItemStack(ModItems.CORACONIC_DIAMOND_STRUCTURED_CRYSTAL.get(), 1),
        1_000_000,
        600
      ),
      null
    );
    recipeOutput.accept(
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "resonance_crystallizing/aviradiamond"
      ),
      new ResonanceCrystallizingRecipe(
        List.of(
          new AlloyingIngredient(Ingredient.of(Items.DIAMOND), 1),
          new AlloyingIngredient(
            Ingredient.of(ModItemTags.ELEMENTS_AVIRACONIC),
            1
          )
        ),
        new ItemStack(ModItems.AVIRACONIC_DIAMOND_STRUCTURED_CRYSTAL.get(), 1),
        1_000_000,
        600
      ),
      null
    );

    recipeOutput.accept(
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "resonance_crystallizing/quartz_from_netherrack"
      ),
      new ResonanceCrystallizingRecipe(
        List.of(
          new AlloyingIngredient(Ingredient.of(Items.QUARTZ), 1),
          new AlloyingIngredient(Ingredient.of(Items.NETHERRACK), 1)
        ),
        new ItemStack(Items.QUARTZ, 2),
        10_000,
        80
      ),
      null
    );

    recipeOutput.accept(
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "resonance_crystallizing/emerald_from_copper"
      ),
      new ResonanceCrystallizingRecipe(
        List.of(
          new AlloyingIngredient(Ingredient.of(Items.EMERALD), 1),
          new AlloyingIngredient(Ingredient.of(Items.RAW_COPPER), 1)
        ),
        new ItemStack(Items.EMERALD, 2),
        100_000,
        50
      ),
      null
    );
  }
}
