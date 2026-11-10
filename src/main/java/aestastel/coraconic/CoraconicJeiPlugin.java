/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic;

import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.client.AlloyingRecipeCategory;
import aestastel.coraconic.client.ResonanceCrystallizingRecipeCategory;
import aestastel.coraconic.client.SmeltingMetallurgyRecipeCategory;
import aestastel.coraconic.recipe.AlloyingRecipe;
import aestastel.coraconic.recipe.ModRecipes;
import aestastel.coraconic.recipe.ResonanceCrystallizingRecipe;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

@JeiPlugin
public class CoraconicJeiPlugin implements IModPlugin {

  public static final RecipeType<
    RecipeHolder<AlloyingRecipe>
  > ALLOYING_RECIPE_TYPE = RecipeType.createRecipeHolderType(
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "alloying"
    )
  );

  public static final RecipeType<
    RecipeHolder<ResonanceCrystallizingRecipe>
  > RESONANCE_CRYSTALLIZING_RECIPE_TYPE = RecipeType.createRecipeHolderType(
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "resonance_crystallizing"
    )
  );

  public static final RecipeType<
    RecipeHolder<SmeltingRecipe>
  > SMELTING_METALLURGY_RECIPE_TYPE = RecipeType.createRecipeHolderType(
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "smelting_metallurgy"
    )
  );

  @Override
  public ResourceLocation getPluginUid() {
    return ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "jei_plugin"
    );
  }

  @Override
  public void registerCategories(
    IRecipeCategoryRegistration registration
  ) {
    registration.addRecipeCategories(
      new AlloyingRecipeCategory(
        registration.getJeiHelpers().getGuiHelper()
      ),
      new ResonanceCrystallizingRecipeCategory(
        registration.getJeiHelpers().getGuiHelper()
      ),
      new SmeltingMetallurgyRecipeCategory(
        registration.getJeiHelpers().getGuiHelper()
      )
    );
  }

  @Override
  public void registerRecipes(IRecipeRegistration registration) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      return;
    }

    List<RecipeHolder<AlloyingRecipe>> recipes = level
      .getRecipeManager()
      .getAllRecipesFor(ModRecipes.ALLOYING_TYPE.get());
    registration.addRecipes(ALLOYING_RECIPE_TYPE, recipes);

    List<RecipeHolder<ResonanceCrystallizingRecipe>> resonanceRecipes =
      level
        .getRecipeManager()
        .getAllRecipesFor(ModRecipes.RESONANCE_CRYSTALLIZING_TYPE.get());
    registration.addRecipes(
      RESONANCE_CRYSTALLIZING_RECIPE_TYPE,
      resonanceRecipes
    );

    List<RecipeHolder<SmeltingRecipe>> smeltingRecipes = level
      .getRecipeManager()
      .getAllRecipesFor(
        net.minecraft.world.item.crafting.RecipeType.SMELTING
      );
    registration.addRecipes(
      SMELTING_METALLURGY_RECIPE_TYPE,
      smeltingRecipes
    );
  }

  @Override
  public void registerRecipeCatalysts(
    IRecipeCatalystRegistration registration
  ) {
    registration.addRecipeCatalyst(
      new ItemStack(ModBlocks.ALLOYING_MACHINE_MK1.get()),
      ALLOYING_RECIPE_TYPE
    );
    registration.addRecipeCatalyst(
      new ItemStack(ModBlocks.ALLOYING_MACHINE_MK2.get()),
      ALLOYING_RECIPE_TYPE
    );
    registration.addRecipeCatalyst(
      new ItemStack(ModBlocks.RESONANCE_CRYSTALLIZING_MACHINE.get()),
      RESONANCE_CRYSTALLIZING_RECIPE_TYPE
    );
    registration.addRecipeCatalyst(
      new ItemStack(ModBlocks.SMELTING_METALLURGY_MACHINE_MK1.get()),
      RecipeTypes.SMELTING
    );
    registration.addRecipeCatalyst(
      new ItemStack(ModBlocks.SMELTING_METALLURGY_MACHINE_MK1.get()),
      SMELTING_METALLURGY_RECIPE_TYPE
    );
  }
}
