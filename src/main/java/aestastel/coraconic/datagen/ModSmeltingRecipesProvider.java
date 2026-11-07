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
import aestastel.coraconic.tag.ModItemTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 熔炼配方生成器：只负责熔炉与高炉配方。
 *
 * <p>Smelting and blasting recipe provider.
 */
public class ModSmeltingRecipesProvider extends RecipeProvider {

  public ModSmeltingRecipesProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    // 粗锡/粗铅：熔炉与高炉均可冶炼为对应的金属锭。
    // Raw tin/lead: smelt or blast into the corresponding metal ingot.
    SimpleCookingRecipeBuilder.smelting(
      Ingredient.of(ModItems.RAW_TIN.get()),
      RecipeCategory.MISC,
      ModItems.TIN_INGOT.get(),
      0.7F,
      200
    )
      .unlockedBy("has_raw_tin", has(ModItems.RAW_TIN.get()))
      .save(
        recipeOutput,
        CoraconicTech.MODID + ":tin_ingot_from_smelting_raw_tin"
      );
    SimpleCookingRecipeBuilder.blasting(
      Ingredient.of(ModItems.RAW_TIN.get()),
      RecipeCategory.MISC,
      ModItems.TIN_INGOT.get(),
      0.7F,
      100
    )
      .unlockedBy("has_raw_tin", has(ModItems.RAW_TIN.get()))
      .save(
        recipeOutput,
        CoraconicTech.MODID + ":tin_ingot_from_blasting_raw_tin"
      );
    SimpleCookingRecipeBuilder.smelting(
      Ingredient.of(ModItems.RAW_LEAD.get()),
      RecipeCategory.MISC,
      ModItems.LEAD_INGOT.get(),
      0.7F,
      200
    )
      .unlockedBy("has_raw_lead", has(ModItems.RAW_LEAD.get()))
      .save(
        recipeOutput,
        CoraconicTech.MODID + ":lead_ingot_from_smelting_raw_lead"
      );
    SimpleCookingRecipeBuilder.blasting(
      Ingredient.of(ModItems.RAW_LEAD.get()),
      RecipeCategory.MISC,
      ModItems.LEAD_INGOT.get(),
      0.7F,
      100
    )
      .unlockedBy("has_raw_lead", has(ModItems.RAW_LEAD.get()))
      .save(
        recipeOutput,
        CoraconicTech.MODID + ":lead_ingot_from_blasting_raw_lead"
      );

    // 锡矿石/铅矿石：使用矿物标签，同时覆盖普通矿石与深板岩变种。
    // Tin/lead ores: use ore tags so both stone and deepslate variants are covered.
    SimpleCookingRecipeBuilder.smelting(
      Ingredient.of(ModItemTags.ORES_TIN),
      RecipeCategory.MISC,
      ModItems.TIN_INGOT.get(),
      0.7F,
      200
    )
      .unlockedBy("has_tin_ore", has(ModItemTags.ORES_TIN))
      .save(
        recipeOutput,
        CoraconicTech.MODID + ":tin_ingot_from_smelting_tin_ore"
      );
    SimpleCookingRecipeBuilder.blasting(
      Ingredient.of(ModItemTags.ORES_TIN),
      RecipeCategory.MISC,
      ModItems.TIN_INGOT.get(),
      0.7F,
      100
    )
      .unlockedBy("has_tin_ore", has(ModItemTags.ORES_TIN))
      .save(
        recipeOutput,
        CoraconicTech.MODID + ":tin_ingot_from_blasting_tin_ore"
      );
    SimpleCookingRecipeBuilder.smelting(
      Ingredient.of(ModItemTags.ORES_LEAD),
      RecipeCategory.MISC,
      ModItems.LEAD_INGOT.get(),
      0.7F,
      200
    )
      .unlockedBy("has_lead_ore", has(ModItemTags.ORES_LEAD))
      .save(
        recipeOutput,
        CoraconicTech.MODID + ":lead_ingot_from_smelting_lead_ore"
      );
    SimpleCookingRecipeBuilder.blasting(
      Ingredient.of(ModItemTags.ORES_LEAD),
      RecipeCategory.MISC,
      ModItems.LEAD_INGOT.get(),
      0.7F,
      100
    )
      .unlockedBy("has_lead_ore", has(ModItemTags.ORES_LEAD))
      .save(
        recipeOutput,
        CoraconicTech.MODID + ":lead_ingot_from_blasting_lead_ore"
      );

  }
}