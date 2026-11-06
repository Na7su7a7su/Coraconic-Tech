/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.client;

import aestastel.coraconic.CoraconicJeiPlugin;
import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.recipe.AlloyingIngredient;
import aestastel.coraconic.recipe.AlloyingRecipe;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * JEI 合金仪配方分类。
 *
 * <p>显示最多三个带数量的原料槽、加工箭头、成品槽以及总耗能和加工时间。
 *
 * <p>JEI recipe category for the alloying machine. It displays up to three
 * counted input slots, a progress arrow, the output, total energy, and
 * processing time.
 */
public final class AlloyingRecipeCategory
  extends AbstractRecipeCategory<RecipeHolder<AlloyingRecipe>>
{

  private static final int WIDTH = 128;
  private static final int HEIGHT = 64;
  private static final int INPUT_Y = 20;
  private static final int[] INPUT_X = { 8, 30, 52 };
  private static final int OUTPUT_X = 102;

  public AlloyingRecipeCategory(IGuiHelper guiHelper) {
    super(
      CoraconicJeiPlugin.ALLOYING_RECIPE_TYPE,
      Component.translatable("jei.coraconic.alloying"),
      guiHelper.createDrawableItemStack(
        new ItemStack(ModBlocks.ALLOYING_MACHINE_MK1.get())
      ),
      WIDTH,
      HEIGHT
    );
  }

  @Override
  public void setRecipe(
    IRecipeLayoutBuilder builder,
    RecipeHolder<AlloyingRecipe> holder,
    IFocusGroup focuses
  ) {
    AlloyingRecipe recipe = holder.value();
    builder.setShapeless();

    for (
      int index = 0;
      index < recipe.ingredients().size() && index < INPUT_X.length;
      index++
    ) {
      AlloyingIngredient ingredient = recipe.ingredients().get(index);
      builder
        .addInputSlot(INPUT_X[index], INPUT_Y)
        .addItemStacks(displayStacks(ingredient))
        .setStandardSlotBackground();
    }

    builder
      .addOutputSlot(OUTPUT_X, INPUT_Y)
      .addItemStack(recipe.result().copy())
      .setOutputSlotBackground();
  }

  @Override
  public void draw(
    RecipeHolder<AlloyingRecipe> holder,
    IRecipeSlotsView recipeSlots,
    GuiGraphics graphics,
    double mouseX,
    double mouseY
  ) {
    int arrowY = INPUT_Y + 6;
    graphics.fill(72, arrowY + 3, 91, arrowY + 5, 0xFF555555);
    graphics.fill(89, arrowY, 91, arrowY + 8, 0xFF555555);

    AlloyingRecipe recipe = holder.value();
    var font = Minecraft.getInstance().font;
    graphics.drawString(
      font,
      Component.translatable(
        "jei.coraconic.alloying.energy",
        recipe.energy()
      ).getString(),
      8,
      46,
      ChatFormatting.DARK_GRAY.getColor(),
      false
    );
    graphics.drawString(
      font,
      Component.translatable(
        "jei.coraconic.alloying.time",
        recipe.time()
      ).getString(),
      8,
      56,
      ChatFormatting.DARK_GRAY.getColor(),
      false
    );
  }

  private static List<ItemStack> displayStacks(
    AlloyingIngredient ingredient
  ) {
    return Arrays
      .stream(ingredient.ingredient().getItems())
      .map(stack -> {
        ItemStack copy = stack.copy();
        copy.setCount(ingredient.count());
        return copy;
      })
      .toList();
  }
}
