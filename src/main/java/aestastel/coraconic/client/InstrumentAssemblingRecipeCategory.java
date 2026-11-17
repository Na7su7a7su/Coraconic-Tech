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
import aestastel.coraconic.recipe.InstrumentAssemblingIngredient;
import aestastel.coraconic.recipe.InstrumentAssemblingRecipe;
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
 * JEI 器械组合仪配方分类。
 *
 * <p>显示最多五个带数量的原料槽、加工箭头、成品槽以及总耗能和加工时间。
 *
 * <p>JEI recipe category for the instrument assembling machine. It displays up
 * to five counted input slots, a progress arrow, the output, total energy, and
 * processing time.
 */
public final class InstrumentAssemblingRecipeCategory
  extends AbstractRecipeCategory<RecipeHolder<InstrumentAssemblingRecipe>>
{

  private static final int WIDTH = 160;
  private static final int HEIGHT = 64;
  private static final int INPUT_Y = 20;
  private static final int[] INPUT_X = { 8, 30, 52, 74, 96 };
  private static final int OUTPUT_X = 138;

  public InstrumentAssemblingRecipeCategory(IGuiHelper guiHelper) {
    super(
      CoraconicJeiPlugin.INSTRUMENT_ASSEMBLING_RECIPE_TYPE,
      Component.translatable("jei.coraconic.instrument_assembling"),
      guiHelper.createDrawableItemStack(
        new ItemStack(ModBlocks.INSTRUMENT_ASSEMBLING_MACHINE.get())
      ),
      WIDTH,
      HEIGHT
    );
  }

  @Override
  public void setRecipe(
    IRecipeLayoutBuilder builder,
    RecipeHolder<InstrumentAssemblingRecipe> holder,
    IFocusGroup focuses
  ) {
    InstrumentAssemblingRecipe recipe = holder.value();
    builder.setShapeless();

    for (
      int index = 0;
      index < recipe.ingredients().size() && index < INPUT_X.length;
      index++
    ) {
      InstrumentAssemblingIngredient ingredient = recipe
        .ingredients()
        .get(index);
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
    RecipeHolder<InstrumentAssemblingRecipe> holder,
    IRecipeSlotsView recipeSlots,
    GuiGraphics graphics,
    double mouseX,
    double mouseY
  ) {
    int arrowY = INPUT_Y + 6;
    graphics.fill(116, arrowY + 3, 135, arrowY + 5, 0xFF555555);
    graphics.fill(133, arrowY, 135, arrowY + 8, 0xFF555555);

    InstrumentAssemblingRecipe recipe = holder.value();
    var font = Minecraft.getInstance().font;
    graphics.drawString(
      font,
      Component.translatable(
        "jei.coraconic.instrument_assembling.energy",
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
        "jei.coraconic.instrument_assembling.time",
        recipe.time()
      ).getString(),
      8,
      56,
      ChatFormatting.DARK_GRAY.getColor(),
      false
    );
  }

  private static List<ItemStack> displayStacks(
    InstrumentAssemblingIngredient ingredient
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
