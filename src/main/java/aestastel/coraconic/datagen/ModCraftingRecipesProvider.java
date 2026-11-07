/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.datagen;

import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.item.ModItems;
import aestastel.coraconic.tag.ModItemTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

/**
 * 独立合成配方生成器：只负责直接编写的合成配方。
 *
 * <p>Standalone crafting recipe provider for directly authored recipes.
 */
public class ModCraftingRecipesProvider extends RecipeProvider {

  public ModCraftingRecipesProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    // HCV2：九个附魔金苹果合成的有序配方。
    // HCV2: shaped recipe using nine enchanted golden apples.
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HCV2.get())
      .define('#', Items.ENCHANTED_GOLDEN_APPLE)
      .define('S', ModBlocks.AVIRACONIC_METAL_BLOCK)
      .pattern("###")
      .pattern("#S#")
      .pattern("###")
      .unlockedBy(
        "has_enchanted_golden_apple",
        has(Items.ENCHANTED_GOLDEN_APPLE)
      )
      .save(recipeOutput);

    // 核龙金属核心：核龙锭、金锭、黑曜石与钻石合成两个。
    // Coraconic core: two cores from coraconic ingots, gold, obsidian, and diamonds.
    ShapedRecipeBuilder.shaped(
      RecipeCategory.MISC,
      ModItems.CORACONIC_CORE.get(),
      2
    )
      .define('C', ModItems.CORACONIC_METAL_INGOT.get())
      .define('G', Items.GOLD_INGOT)
      .define('O', Items.OBSIDIAN)
      .define('#', Items.DIAMOND)
      .pattern("CGO")
      .pattern("G#G")
      .pattern("OGC")
      .unlockedBy(
        "has_coraconic_metal_ingot",
        has(ModItems.CORACONIC_METAL_INGOT.get())
      )
      .save(recipeOutput);

    // 腾龙金属核心：腾龙锭、钻石、荧石粉与绿宝石合成两个。
    // Aviraconic core: two cores from aviraconic ingots, diamonds, glowstone dust, and emeralds.
    ShapedRecipeBuilder.shaped(
      RecipeCategory.MISC,
      ModItems.AVIRACONIC_CORE.get(),
      2
    )
      .define('C', ModItems.AVIRACONIC_METAL_INGOT.get())
      .define('G', Items.DIAMOND)
      .define('O', Items.GLOWSTONE_DUST)
      .define('#', Items.EMERALD)
      .pattern("CGO")
      .pattern("G#G")
      .pattern("OGC")
      .unlockedBy(
        "has_aviraconic_metal_ingot",
        has(ModItems.AVIRACONIC_METAL_INGOT.get())
      )
      .save(recipeOutput);

    // 常金属核心：铅锭、铁锭、红石、金锭与锡锭合成两个。
    // Common metal core: two cores from lead, iron, redstone, gold, and tin.
    ShapedRecipeBuilder.shaped(
      RecipeCategory.MISC,
      ModItems.COMMONMETAL_CORE.get(),
      2
    )
      .define('D', ModItemTags.INGOTS_LEAD)
      .define('G', Items.IRON_INGOT)
      .define('O', Items.REDSTONE)
      .define('#', Items.GOLD_INGOT)
      .define('C', ModItemTags.INGOTS_TIN)
      .pattern("DGO")
      .pattern("G#G")
      .pattern("OGC")
      .unlockedBy("has_tin_ingot", has(ModItems.TIN_INGOT.get()))
      .save(recipeOutput);

    // 核龙金属锭：两个金锭、锡锭、铅锭与四个核龙元素无序合成。
    // Coraconic ingot: shapeless recipe from gold, tin, lead, and four coraconic elements.
    ShapelessRecipeBuilder.shapeless(
      RecipeCategory.MISC,
      ModItems.CORACONIC_METAL_INGOT.get()
    )
      .requires(Items.GOLD_INGOT, 2)
      .requires(ModItemTags.INGOTS_TIN)
      .requires(ModItemTags.INGOTS_LEAD)
      .requires(ModItems.CORACONIC_ELEMENT.get(), 4)
      .unlockedBy(
        "has_coraconic_element",
        has(ModItems.CORACONIC_ELEMENT.get())
      )
      .save(recipeOutput);

    // 腾龙金属锭：两个钻石、绿宝石、核龙锭与四个腾龙元素无序合成。
    // Aviraconic ingot: shapeless recipe from diamonds, emerald, coraconic ingot, and four aviraconic elements.
    ShapelessRecipeBuilder.shapeless(
      RecipeCategory.MISC,
      ModItems.AVIRACONIC_METAL_INGOT.get()
    )
      .requires(Items.DIAMOND, 2)
      .requires(Items.EMERALD)
      .requires(ModItems.CORACONIC_METAL_INGOT.get())
      .requires(ModItems.AVIRACONIC_ELEMENT.get(), 4)
      .unlockedBy(
        "has_aviraconic_element",
        has(ModItems.AVIRACONIC_ELEMENT.get())
      )
      .save(recipeOutput);
  }
}
