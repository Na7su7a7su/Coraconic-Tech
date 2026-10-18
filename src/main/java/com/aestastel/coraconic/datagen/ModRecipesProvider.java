package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.item.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

public class ModRecipesProvider extends RecipeProvider {

  public ModRecipesProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HCV2.get())
      .define('#', Items.ENCHANTED_GOLDEN_APPLE)
      .pattern("###")
      .pattern("###")
      .pattern("###")
      .unlockedBy(
        "has_enchanted_golden_apple",
        has(Items.ENCHANTED_GOLDEN_APPLE)
      )
      .save(recipeOutput);

    ShapedRecipeBuilder.shaped(
      RecipeCategory.MISC,
      ModItems.CORACONIC_CORE.get(),
      2
    )
      .define('C', ModItems.CORACONIC_INGOT.get())
      .define('G', Items.GOLD_INGOT)
      .define('O', Items.OBSIDIAN)
      .define('#', Items.DIAMOND)
      .pattern("CGO")
      .pattern("G#G")
      .pattern("OGC")
      .unlockedBy("has_coraconic_ingot", has(ModItems.CORACONIC_INGOT.get()))
      .save(recipeOutput);

    ShapedRecipeBuilder.shaped(
      RecipeCategory.MISC,
      ModItems.AVIRACONIC_CORE.get(),
      2
    )
      .define('C', ModItems.AVIRACONIC_INGOT.get())
      .define('G', Items.DIAMOND)
      .define('O', Items.GLOWSTONE_DUST)
      .define('#', Items.EMERALD)
      .pattern("CGO")
      .pattern("G#G")
      .pattern("OGC")
      .unlockedBy("has_aviraconic_ingot", has(ModItems.AVIRACONIC_INGOT.get()))
      .save(recipeOutput);

    ShapedRecipeBuilder.shaped(
      RecipeCategory.MISC,
      ModItems.COMMONMETAL_CORE.get(),
      2
    )
      .define('D', ModItems.LEAD_INGOT.get())
      .define('G', Items.IRON_INGOT)
      .define('O', Items.REDSTONE)
      .define('#', Items.GOLD_INGOT)
      .define('C', ModItems.TIN_INGOT.get())
      .pattern("DGO")
      .pattern("G#G")
      .pattern("OGC")
      .unlockedBy("has_tin_ingot", has(ModItems.TIN_INGOT.get()))
      .save(recipeOutput);

    ShapelessRecipeBuilder.shapeless(
      RecipeCategory.MISC,
      ModItems.CORACONIC_INGOT.get()
    )
      .requires(Items.GOLD_INGOT, 2)
      .requires(ModItems.TIN_INGOT.get())
      .requires(ModItems.LEAD_INGOT.get())
      .requires(ModItems.CORACONIC_ELEMENT.get(), 4)
      .unlockedBy(
        "has_coraconic_element",
        has(ModItems.CORACONIC_ELEMENT.get())
      )
      .save(recipeOutput);

    ShapelessRecipeBuilder.shapeless(
      RecipeCategory.MISC,
      ModItems.AVIRACONIC_INGOT.get()
    )
      .requires(Items.DIAMOND, 2)
      .requires(Items.EMERALD)
      .requires(ModItems.CORACONIC_INGOT.get())
      .requires(ModItems.AVIRACONIC_ELEMENT.get(), 4)
      .unlockedBy(
        "has_aviraconic_element",
        has(ModItems.AVIRACONIC_ELEMENT.get())
      )
      .save(recipeOutput);
  }
}
