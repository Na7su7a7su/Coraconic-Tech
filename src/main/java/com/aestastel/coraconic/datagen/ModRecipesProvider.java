package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.block.ModBlocks;
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
import net.minecraft.world.level.ItemLike;

/**
 * 配方生成器：生成模组物品的合成配方与解锁进度。
 *
 * <p>Recipe provider: generates crafting recipes and unlock advancements for mod items.
 */
public class ModRecipesProvider extends RecipeProvider {

  public ModRecipesProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    super(output, registries);
  }

  /**
   * 注册所有模组配方。
   *
   * <p>Registers all mod recipes.
   */
  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    // HCV2：九个附魔金苹果合成的有序配方。
    // HCV2: shaped recipe using nine enchanted golden apples.
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

    // 核龙金属核心：核龙锭、金锭、黑曜石与钻石合成两个。
    // Coraconic core: two cores from coraconic ingots, gold, obsidian, and diamonds.
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

    // 腾龙金属核心：腾龙锭、钻石、荧石粉与绿宝石合成两个。
    // Aviraconic core: two cores from aviraconic ingots, diamonds, glowstone dust, and emeralds.
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

    // 常金属核心：铅锭、铁锭、红石、金锭与锡锭合成两个。
    // Common metal core: two cores from lead, iron, redstone, gold, and tin.
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

    // 核龙金属锭：两个金锭、锡锭、铅锭与四个核龙元素无序合成。
    // Coraconic ingot: shapeless recipe from gold, tin, lead, and four coraconic elements.
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

    // 腾龙金属锭：两个钻石、绿宝石、核龙锭与四个腾龙元素无序合成。
    // Aviraconic ingot: shapeless recipe from diamonds, emerald, coraconic ingot, and four aviraconic elements.
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

    // 金属块与金属锭的压缩/解压配方。
    // Packing/unpacking recipes between metal blocks and ingots.
    packingRecipe(
      recipeOutput,
      ModBlocks.TIN_BLOCK.get(),
      ModItems.TIN_INGOT.get(),
      "has_tin_ingot"
    );
    unpackingRecipe(
      recipeOutput,
      ModBlocks.TIN_BLOCK.get(),
      ModItems.TIN_INGOT.get(),
      "tin_ingot_from_tin_block",
      "has_tin_block"
    );

    packingRecipe(
      recipeOutput,
      ModBlocks.LEAD_BLOCK.get(),
      ModItems.LEAD_INGOT.get(),
      "has_lead_ingot"
    );
    unpackingRecipe(
      recipeOutput,
      ModBlocks.LEAD_BLOCK.get(),
      ModItems.LEAD_INGOT.get(),
      "lead_ingot_from_lead_block",
      "has_lead_block"
    );

    packingRecipe(
      recipeOutput,
      ModBlocks.CORACONIC_BLOCK.get(),
      ModItems.CORACONIC_INGOT.get(),
      "has_coraconic_ingot"
    );
    unpackingRecipe(
      recipeOutput,
      ModBlocks.CORACONIC_BLOCK.get(),
      ModItems.CORACONIC_INGOT.get(),
      "coraconic_ingot_from_coraconic_block",
      "has_coraconic_block"
    );

    packingRecipe(
      recipeOutput,
      ModBlocks.AVIRACONIC_BLOCK.get(),
      ModItems.AVIRACONIC_INGOT.get(),
      "has_aviraconic_ingot"
    );
    unpackingRecipe(
      recipeOutput,
      ModBlocks.AVIRACONIC_BLOCK.get(),
      ModItems.AVIRACONIC_INGOT.get(),
      "aviraconic_ingot_from_aviraconic_block",
      "has_aviraconic_block"
    );
  }

  /**
   * 生成“九个锭合成一个金属块”的无序配方。
   *
   * <p>Generates a shapeless recipe that packs nine ingots into one metal block.
   */
  private static void packingRecipe(
    RecipeOutput recipeOutput,
    ItemLike block,
    ItemLike ingot,
    String criterionName
  ) {
    ShapelessRecipeBuilder.shapeless(
      RecipeCategory.BUILDING_BLOCKS,
      block
    )
      .requires(ingot, 9)
      .unlockedBy(criterionName, has(ingot))
      .save(recipeOutput);
  }

  /**
   * 生成“一个金属块分解为九个锭”的配方。
   *
   * <p>Generates a recipe that unpacks one metal block into nine ingots.
   */
  private static void unpackingRecipe(
    RecipeOutput recipeOutput,
    ItemLike block,
    ItemLike ingot,
    String recipeName,
    String criterionName
  ) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
      .requires(block)
      .unlockedBy(criterionName, has(block))
      .save(recipeOutput, CoraconicTech.MODID + ":" + recipeName);
  }
}
