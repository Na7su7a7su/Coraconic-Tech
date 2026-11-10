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
import aestastel.coraconic.blockentity.SmeltingMetallurgyMachineBlockEntity;
import java.util.Arrays;
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
import net.minecraft.world.item.crafting.SmeltingRecipe;

/**
 * JEI 熔炼冶金炉独立配方分类。
 *
 * <p>复用原版熔炼配方，显示单个原料、加工箭头、成品、总耗能与时间。
 *
 * <p>Dedicated JEI category for the Smelting-Metallurgy Machine. It reuses
 * vanilla smelting recipes and displays the input, arrow, output, total energy,
 * and cooking time.
 */
public final class SmeltingMetallurgyRecipeCategory
  extends AbstractRecipeCategory<RecipeHolder<SmeltingRecipe>>
{

  private static final int WIDTH = 128;
  private static final int HEIGHT = 64;
  private static final int INPUT_X = 8;
  private static final int INPUT_Y = 20;
  private static final int OUTPUT_X = 102;

  public SmeltingMetallurgyRecipeCategory(IGuiHelper guiHelper) {
    super(
      CoraconicJeiPlugin.SMELTING_METALLURGY_RECIPE_TYPE,
      Component.translatable("jei.coraconic.smelting_metallurgy"),
      guiHelper.createDrawableItemStack(
        new ItemStack(ModBlocks.SMELTING_METALLURGY_MACHINE_MK1.get())
      ),
      WIDTH,
      HEIGHT
    );
  }

  @Override
  public void setRecipe(
    IRecipeLayoutBuilder builder,
    RecipeHolder<SmeltingRecipe> holder,
    IFocusGroup focuses
  ) {
    SmeltingRecipe recipe = holder.value();
    builder
      .addInputSlot(INPUT_X, INPUT_Y)
      .addItemStacks(
        Arrays.asList(recipe.getIngredients().get(0).getItems())
      )
      .setStandardSlotBackground();

    ItemStack result = recipe
      .getResultItem(Minecraft.getInstance().level.registryAccess())
      .copy();
    builder
      .addOutputSlot(OUTPUT_X, INPUT_Y)
      .addItemStack(result)
      .setOutputSlotBackground();
  }

  @Override
  public void draw(
    RecipeHolder<SmeltingRecipe> holder,
    IRecipeSlotsView recipeSlots,
    GuiGraphics graphics,
    double mouseX,
    double mouseY
  ) {
    int arrowY = INPUT_Y + 6;
    graphics.fill(72, arrowY + 3, 91, arrowY + 5, 0xFF555555);
    graphics.fill(89, arrowY, 91, arrowY + 8, 0xFF555555);

    SmeltingRecipe recipe = holder.value();
    var font = Minecraft.getInstance().font;
    graphics.drawString(
      font,
      Component.translatable(
        "jei.coraconic.smelting_metallurgy.energy",
        recipe.getCookingTime() *
          SmeltingMetallurgyMachineBlockEntity.ENERGY_PER_TICK
      ).getString(),
      8,
      46,
      ChatFormatting.DARK_GRAY.getColor(),
      false
    );
    graphics.drawString(
      font,
      Component.translatable(
        "jei.coraconic.smelting_metallurgy.time",
        recipe.getCookingTime()
      ).getString(),
      8,
      56,
      ChatFormatting.DARK_GRAY.getColor(),
      false
    );
  }
}
