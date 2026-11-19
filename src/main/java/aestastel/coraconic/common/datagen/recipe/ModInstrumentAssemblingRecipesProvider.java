/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.datagen.recipe;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.common.block.ModBlocks;
import aestastel.coraconic.common.item.ModItems;
import aestastel.coraconic.common.recipe.InstrumentAssemblingIngredient;
import aestastel.coraconic.common.recipe.InstrumentAssemblingRecipe;
import aestastel.coraconic.common.tag.ModItemTags;
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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

/**
 * 器械组合配方生成器：生成器械组合仪的配方。
 *
 * <p>Instrument assembling recipe provider.
 */
public class ModInstrumentAssemblingRecipesProvider extends RecipeProvider {

  public ModInstrumentAssemblingRecipesProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    recipeOutput.accept(
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "instrument_assembling/solar_generator_primitive"
      ),
      new InstrumentAssemblingRecipe(
        List.of(
          new InstrumentAssemblingIngredient(
            Ingredient.of(ModItems.COMMONMETAL_CORE.get()),
            1
          ),
          new InstrumentAssemblingIngredient(
            Ingredient.of(Tags.Items.GLASS_PANES),
            3
          ),
          new InstrumentAssemblingIngredient(
            Ingredient.of(Blocks.IRON_BLOCK),
            1
          ),
          new InstrumentAssemblingIngredient(
            Ingredient.of(ModItemTags.INGOTS_AMBER_GOLD),
            1
          ),
          new InstrumentAssemblingIngredient(
            Ingredient.of(Items.COPPER_INGOT),
            2
          )
        ),
        new ItemStack(ModBlocks.SOLAR_GENERATOR_PRIMITIVE, 1),
        640,
        40
      ),
      null
    );
  }
}
