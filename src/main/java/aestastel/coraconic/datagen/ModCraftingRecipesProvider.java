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
import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.item.ModItems;
import aestastel.coraconic.tag.ModItemTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

/**
 * 合成配方生成器：只负责有序合成、无序合成、工具与压缩/解压配方。
 *
 * <p>Crafting recipe provider for shaped recipes, shapeless recipes, tools,
 * packing, and unpacking.
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
      .define('S', ModBlocks.AVIRACONIC_BLOCK)
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
      ModItems.CORACONIC_METAL_INGOT.get()
    )
      .requires(Items.GOLD_INGOT, 2)
      .requires(ModItemTags.INGOTS_TIN)
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


    // 金属块与金属锭、金属锭与金属粒的压缩/解压配方。
    // Packing/unpacking recipes between metal blocks/ingots and metal ingots/nuggets.
    packingRecipe(
      recipeOutput,
      RecipeCategory.BUILDING_BLOCKS,
      ModBlocks.TIN_BLOCK.get(),
      ModItems.TIN_INGOT.get(),
      "tin_block",
      "has_tin_ingot"
    );
    unpackingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.TIN_INGOT.get(),
      ModBlocks.TIN_BLOCK.get(),
      "tin_ingot_from_tin_block",
      "has_tin_block"
    );
    packingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.TIN_INGOT.get(),
      ModItems.TIN_NUGGET.get(),
      "tin_ingot_from_tin_nuggets",
      "has_tin_nugget"
    );
    unpackingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.TIN_NUGGET.get(),
      ModItems.TIN_INGOT.get(),
      "tin_nugget_from_tin_ingot",
      "has_tin_ingot"
    );

    packingRecipe(
      recipeOutput,
      RecipeCategory.BUILDING_BLOCKS,
      ModBlocks.LEAD_BLOCK.get(),
      ModItems.LEAD_INGOT.get(),
      "lead_block",
      "has_lead_ingot"
    );
    unpackingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.LEAD_INGOT.get(),
      ModBlocks.LEAD_BLOCK.get(),
      "lead_ingot_from_lead_block",
      "has_lead_block"
    );
    packingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.LEAD_INGOT.get(),
      ModItems.LEAD_NUGGET.get(),
      "lead_ingot_from_lead_nuggets",
      "has_lead_nugget"
    );
    unpackingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.LEAD_NUGGET.get(),
      ModItems.LEAD_INGOT.get(),
      "lead_nugget_from_lead_ingot",
      "has_lead_ingot"
    );

    packingRecipe(
      recipeOutput,
      RecipeCategory.BUILDING_BLOCKS,
      ModBlocks.CORACONIC_BLOCK.get(),
      ModItems.CORACONIC_METAL_INGOT.get(),
      "coraconic_block",
      "has_coraconic_metal_ingot"
    );
    unpackingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.CORACONIC_METAL_INGOT.get(),
      ModBlocks.CORACONIC_BLOCK.get(),
      "coraconic_metal_ingot_from_coraconic_block",
      "has_coraconic_block"
    );
    packingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.CORACONIC_METAL_INGOT.get(),
      ModItems.CORACONIC_NUGGET.get(),
      "coraconic_metal_ingot_from_coraconic_nuggets",
      "has_coraconic_nugget"
    );
    unpackingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.CORACONIC_NUGGET.get(),
      ModItems.CORACONIC_METAL_INGOT.get(),
      "coraconic_nugget_from_coraconic_metal_ingot",
      "has_coraconic_metal_ingot"
    );

    packingRecipe(
      recipeOutput,
      RecipeCategory.BUILDING_BLOCKS,
      ModBlocks.AVIRACONIC_BLOCK.get(),
      ModItems.AVIRACONIC_METAL_INGOT.get(),
      "aviraconic_block",
      "has_aviraconic_metal_ingot"
    );
    unpackingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.AVIRACONIC_METAL_INGOT.get(),
      ModBlocks.AVIRACONIC_BLOCK.get(),
      "aviraconic_metal_ingot_from_aviraconic_block",
      "has_aviraconic_block"
    );
    packingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.AVIRACONIC_METAL_INGOT.get(),
      ModItems.AVIRACONIC_NUGGET.get(),
      "aviraconic_metal_ingot_from_aviraconic_nuggets",
      "has_aviraconic_nugget"
    );
    unpackingRecipe(
      recipeOutput,
      RecipeCategory.MISC,
      ModItems.AVIRACONIC_NUGGET.get(),
      ModItems.AVIRACONIC_METAL_INGOT.get(),
      "aviraconic_nugget_from_aviraconic_metal_ingot",
      "has_aviraconic_metal_ingot"
    );
    swordRecipe(
      recipeOutput,
      ModItems.TIN_SWORD.get(),
      ModItemTags.INGOTS_TIN,
      "tin_sword",
      "has_tin_sword"
    );
    swordRecipe(
      recipeOutput,
      ModItems.LEAD_SWORD.get(),
      ModItemTags.INGOTS_LEAD,
      "lead_sword",
      "has_lead_sword"
    );
    swordRecipe(
      recipeOutput,
      ModItems.CORACONIC_SWORD.get(),
      ModItemTags.ELEMENTS_CORACONIC,
      "coraconic_sword",
      "has_coraconic_sword"
    );
    swordRecipe(
      recipeOutput,
      ModItems.AVIRACONIC_SWORD.get(),
      ModItemTags.ELEMENTS_AVIRACONIC,
      "aviraconic_sword",
      "has_aviraconic_sword"
    );

    pickaxeRecipe(
      recipeOutput,
      ModItems.TIN_PICKAXE.get(),
      ModItemTags.INGOTS_TIN,
      "tin_pickaxe",
      "has_tin_pickaxe"
    );
    pickaxeRecipe(
      recipeOutput,
      ModItems.LEAD_PICKAXE.get(),
      ModItemTags.INGOTS_LEAD,
      "lead_pickaxe",
      "has_lead_pickaxe"
    );
    pickaxeRecipe(
      recipeOutput,
      ModItems.CORACONIC_PICKAXE.get(),
      ModItemTags.ELEMENTS_CORACONIC,
      "coraconic_pickaxe",
      "has_coraconic_pickaxe"
    );
    pickaxeRecipe(
      recipeOutput,
      ModItems.AVIRACONIC_PICKAXE.get(),
      ModItemTags.ELEMENTS_AVIRACONIC,
      "aviraconic_pickaxe",
      "has_aviraconic_pickaxe"
    );

    axeRecipeA(
      recipeOutput,
      ModItems.TIN_AXE.get(),
      ModItemTags.INGOTS_TIN,
      "tin_axe",
      "has_tin_axe"
    );
    axeRecipeA(
      recipeOutput,
      ModItems.LEAD_AXE.get(),
      ModItemTags.INGOTS_LEAD,
      "lead_axe",
      "has_lead_axe"
    );
    axeRecipeA(
      recipeOutput,
      ModItems.CORACONIC_AXE.get(),
      ModItemTags.ELEMENTS_CORACONIC,
      "coraconic_axe",
      "has_coraconic_axe"
    );
    axeRecipeA(
      recipeOutput,
      ModItems.AVIRACONIC_AXE.get(),
      ModItemTags.ELEMENTS_AVIRACONIC,
      "aviraconic_axe",
      "has_aviraconic_axe"
    );

    shovelRecipe(
      recipeOutput,
      ModItems.TIN_SHOVEL.get(),
      ModItemTags.INGOTS_TIN,
      "tin_shovel",
      "has_tin_shovel"
    );
    shovelRecipe(
      recipeOutput,
      ModItems.LEAD_SHOVEL.get(),
      ModItemTags.INGOTS_LEAD,
      "lead_shovel",
      "has_lead_shovel"
    );
    shovelRecipe(
      recipeOutput,
      ModItems.CORACONIC_SHOVEL.get(),
      ModItemTags.ELEMENTS_CORACONIC,
      "coraconic_shovel",
      "has_coraconic_shovel"
    );
    shovelRecipe(
      recipeOutput,
      ModItems.AVIRACONIC_SHOVEL.get(),
      ModItemTags.ELEMENTS_AVIRACONIC,
      "aviraconic_shovel",
      "has_aviraconic_shovel"
    );

    hoeRecipe(
      recipeOutput,
      ModItems.TIN_HOE.get(),
      ModItemTags.INGOTS_TIN,
      "tin_hoe",
      "has_tin_hoe"
    );
    hoeRecipe(
      recipeOutput,
      ModItems.LEAD_HOE.get(),
      ModItemTags.INGOTS_LEAD,
      "lead_hoe",
      "has_lead_hoe"
    );
    hoeRecipe(
      recipeOutput,
      ModItems.CORACONIC_HOE.get(),
      ModItemTags.ELEMENTS_CORACONIC,
      "coraconic_hoe",
      "has_coraconic_hoe"
    );
    hoeRecipe(
      recipeOutput,
      ModItems.AVIRACONIC_HOE.get(),
      ModItemTags.ELEMENTS_AVIRACONIC,
      "aviraconic_hoe",
      "has_aviraconic_hoe"
    );

    // ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.ENCHANTED_GOLDEN_APPLE);
  }


  /**
   * 生成“九个基础单位压缩成一个成品”的无序合成配方。
   *
   * <p>该方法同时服务于两种物品转换：
   * <ul>
   *   <li>九个金属锭 → 一个金属块，配方类别为
   *       {@link RecipeCategory#BUILDING_BLOCKS}，因为金属块属于建筑方块；</li>
   *   <li>九个金属粒 → 一个金属锭，配方类别为
   *       {@link RecipeCategory#MISC}，因为金属锭属于材料而非建筑方块。</li>
   * </ul>
   *
   * <p>之所以使用无序配方（{@link ShapelessRecipeBuilder}）而不是有序配方，
   * 是因为压缩属于纯粹的物品数量转换：玩家只需要在 3×3 合成格中放入九个
   * 相同的基础单位，不应该被额外的图案布局限制。这一行为也与原版
   * “金粒 ↔ 金锭”的转换方式保持一致，便于玩家凭借既有习惯理解。
   *
   * <p>参数含义如下：
   * <ul>
   *   <li>{@code recipeOutput}：数据生成器提供的配方输出目标，所有配方 JSON
   *       和对应的解锁进度都会写入该目标；</li>
   *   <li>{@code category}：配方书中的分类标签页，决定玩家在配方书里从哪个
   *       分类找到该配方，不影响玩家手动摆放合成；</li>
   *   <li>{@code result}：一次合成得到的成品，即金属块或金属锭；</li>
   *   <li>{@code ingredient}：每个配方固定消耗九个的基础单位，即金属锭或
   *       金属粒；</li>
   *   <li>{@code recipeName}：期望使用的配方 ID，最终会写成
   *       {@code <modid>:<recipeName>}。金属块压缩配方通常直接使用成品 ID
   *       （例如 {@code tin_block}），此时 Minecraft 要求调用不带显式 ID 的
   *       {@code save(recipeOutput)}；而金属粒压缩配方必须使用独立名称
   *       （例如 {@code tin_ingot_from_tin_nuggets}），因为同一个金属锭可能
   *       已经有其他来源配方。若核龙金属锭的元素合成与粒压缩都使用默认 ID
   *       {@code coraconic_metal_ingot}，后生成的配方会覆盖先生成的配方，导致其中
   *       一种合成方式丢失。本方法会在内部判断该名称是否等于成品默认 ID，
   *       自动选择正确的保存方式，调用方无需关心这一细节；</li>
   *   <li>{@code criterionName}：解锁条件的名称，通常写成
   *       {@code has_<ingredient>}。数据生成器会据此生成一个
   *       {@code inventory_changed} 触发器，玩家首次获得基础单位后，该配方
   *       才会出现在配方书中，避免新玩家在尚未接触材料时就看到大量配方。</li>
   * </ul>
   *
   * <p>输出位置：配方本体写入
   * {@code data/<modid>/recipe/<recipeName>.json}，解锁进度由
   * {@link RecipeProvider} 自动写入
   * {@code data/<modid>/advancement/recipes/...}。两者必须同时生成，
   * 否则配方在配方书中不会解锁。
   */
  private static void packingRecipe(
    RecipeOutput recipeOutput,
    RecipeCategory category,
    ItemLike result,
    ItemLike ingredient,
    String recipeName,
    String criterionName
  ) {
    // 3×3 合成格中任意位置放满九个基础单位即可，无需固定图案。
    // Nine base units anywhere in the 3x3 grid are enough; no fixed pattern is needed.
    ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(
      category,
      result
    )
      .requires(ingredient, 9)
      .unlockedBy(criterionName, has(ingredient));

    // Minecraft 的 RecipeBuilder 会校验显式 ID：当传入的 ID 与成品默认 ID
    // 完全相同时会抛出异常，要求改用不带 ID 的 save 重载。因此这里先取出
    // 成品在物品注册表中的路径作为默认 ID，再根据调用方传入的名称决定保存
    // 方式。这样金属块压缩配方可以继续生成 tin_block 等默认文件名，而金属粒
    // 压缩配方仍能使用 tin_ingot_from_tin_nuggets 这类不会冲突的独立名称。
    // RecipeBuilder rejects an explicit id equal to the result's default id, so
    // choose the overload based on whether the requested name is the default.
    String defaultRecipeName = BuiltInRegistries.ITEM.getKey(
      result.asItem()
    ).getPath();
    if (recipeName.equals(defaultRecipeName)) {
      builder.save(recipeOutput);
    } else {
      builder.save(recipeOutput, CoraconicTech.MODID + ":" + recipeName);
    }
  }

  /**
   * 生成“一个成品分解为九个基础单位”的无序合成配方。
   *
   * <p>该方法与 {@link #packingRecipe} 互为逆操作，同时服务于两种转换：
   * <ul>
   *   <li>一个金属块 → 九个金属锭，配方类别为
   *       {@link RecipeCategory#MISC}；</li>
   *   <li>一个金属锭 → 九个金属粒，配方类别同样为
   *       {@link RecipeCategory#MISC}。</li>
   * </ul>
   *
   * <p>解压配方使用无序形式的原因与压缩配方相同：玩家只需把单个成品放进
   * 合成格，物品在格子中的具体位置没有任何意义。固定产物数量为九个，
   * 是为了与压缩配方的“九个换一个”保持严格对称，确保玩家反复压缩、解压
   * 时不会凭空产生或丢失材料。
   *
   * <p>参数含义如下：
   * <ul>
   *   <li>{@code recipeOutput}：数据生成器提供的配方输出目标；</li>
   *   <li>{@code category}：配方书分类。解压得到的通常是材料，因此调用方
   *       统一使用 {@link RecipeCategory#MISC}；</li>
   *   <li>{@code result}：一次合成得到的九个基础单位，即金属锭或金属粒；</li>
   *   <li>{@code ingredient}：被分解的单个成品，即金属块或金属锭；</li>
   *   <li>{@code recipeName}：显式指定的配方 ID，最终写成
   *       {@code <modid>:<recipeName>}。解压配方必须显式命名，因为同一物品
   *       可能既是某个配方的产物，又是另一个配方的原料；显式命名可以避免
   *       不同来源的配方互相覆盖；</li>
   *   <li>{@code criterionName}：解锁条件名称。这里通常传入
   *       {@code has_<ingredient>}，即玩家获得被分解的成品后，解压配方才会
   *       在配方书中解锁。</li>
   * </ul>
   *
   * <p>输出位置：配方本体写入
   * {@code data/<modid>/recipe/<recipeName>.json}，解锁进度由
   * {@link RecipeProvider} 自动生成。
   */
  private static void unpackingRecipe(
    RecipeOutput recipeOutput,
    RecipeCategory category,
    ItemLike result,
    ItemLike ingredient,
    String recipeName,
    String criterionName
  ) {
    // 单个成品放入合成格即可得到九个基础单位，产物数量由第三个参数固定。
    // One finished item in the grid yields nine base units; the count is fixed by the third argument.
    ShapelessRecipeBuilder.shapeless(category, result, 9)
      .requires(ingredient)
      .unlockedBy(criterionName, has(ingredient))
      .save(recipeOutput, CoraconicTech.MODID + ":" + recipeName);
  }

  /**
   * 生成一把剑的有序合成配方：两格材料竖向排列，下方一格木棍。
   *
   * <p>Generates a shaped sword recipe: two material slots vertically above
   * one wooden rod.
   *
   * @param recipeOutput 配方输出目标 / recipe output target
   * @param result 配方产物 / recipe result
   * @param material 剑身材料标签 / sword material tag
   * @param recipeName 配方 ID 路径 / recipe id path
   * @param criterionName 解锁条件名称 / unlock criterion name
   */
  private static void swordRecipe(
    RecipeOutput recipeOutput,
    ItemLike result,
    TagKey<Item> material,
    String recipeName,
    String criterionName
  ) {
    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(
      RecipeCategory.COMBAT,
      result
    )
      .define('S', material)
      .define('I', Tags.Items.RODS_WOODEN)
      .pattern("S")
      .pattern("S")
      .pattern("I")
      .unlockedBy(criterionName, has(material));

    String defaultRecipeName = BuiltInRegistries.ITEM.getKey(
      result.asItem()
    ).getPath();
    if (recipeName.equals(defaultRecipeName)) {
      builder.save(recipeOutput);
    } else {
      builder.save(recipeOutput, CoraconicTech.MODID + ":" + recipeName);
    }
  }

  private static void pickaxeRecipe(
    RecipeOutput recipeOutput,
    ItemLike result,
    TagKey<Item> material,
    String recipeName,
    String criterionName
  ) {
    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(
      RecipeCategory.TOOLS,
      result
    )
      .define('S', material)
      .define('I', Tags.Items.RODS_WOODEN)
      .pattern("SSS")
      .pattern(" I ")
      .pattern(" I ")
      .unlockedBy(criterionName, has(material));

    String defaultRecipeName = BuiltInRegistries.ITEM.getKey(
      result.asItem()
    ).getPath();
    if (recipeName.equals(defaultRecipeName)) {
      builder.save(recipeOutput);
    } else {
      builder.save(recipeOutput, CoraconicTech.MODID + ":" + recipeName);
    }
  }

  private static void axeRecipeA(
    RecipeOutput recipeOutput,
    ItemLike result,
    TagKey<Item> material,
    String recipeName,
    String criterionName
  ) {
    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(
      RecipeCategory.TOOLS,
      result
    )
      .define('S', material)
      .define('I', Tags.Items.RODS_WOODEN)
      .pattern("SS")
      .pattern("SI")
      .pattern(" I")
      .unlockedBy(criterionName, has(material));

    String defaultRecipeName = BuiltInRegistries.ITEM.getKey(
      result.asItem()
    ).getPath();
    if (recipeName.equals(defaultRecipeName)) {
      builder.save(recipeOutput);
    } else {
      builder.save(recipeOutput, CoraconicTech.MODID + ":" + recipeName);
    }
  }

  private static void shovelRecipe(
    RecipeOutput recipeOutput,
    ItemLike result,
    TagKey<Item> material,
    String recipeName,
    String criterionName
  ) {
    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(
      RecipeCategory.TOOLS,
      result
    )
      .define('S', material)
      .define('I', Tags.Items.RODS_WOODEN)
      .pattern("S")
      .pattern("I")
      .pattern("I")
      .unlockedBy(criterionName, has(material));

    String defaultRecipeName = BuiltInRegistries.ITEM.getKey(
      result.asItem()
    ).getPath();
    if (recipeName.equals(defaultRecipeName)) {
      builder.save(recipeOutput);
    } else {
      builder.save(recipeOutput, CoraconicTech.MODID + ":" + recipeName);
    }
  }

  private static void hoeRecipe(
    RecipeOutput recipeOutput,
    ItemLike result,
    TagKey<Item> material,
    String recipeName,
    String criterionName
  ) {
    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(
      RecipeCategory.TOOLS,
      result
    )
      .define('S', material)
      .define('I', Tags.Items.RODS_WOODEN)
      .pattern("SS")
      .pattern(" I")
      .pattern(" I")
      .unlockedBy(criterionName, has(material));

    String defaultRecipeName = BuiltInRegistries.ITEM.getKey(
      result.asItem()
    ).getPath();
    if (recipeName.equals(defaultRecipeName)) {
      builder.save(recipeOutput);
    } else {
      builder.save(recipeOutput, CoraconicTech.MODID + ":" + recipeName);
    }
  }
}