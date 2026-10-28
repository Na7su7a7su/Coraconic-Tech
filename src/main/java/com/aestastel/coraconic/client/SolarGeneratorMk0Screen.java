/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.client;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.energy.EnergyFormat;
import com.aestastel.coraconic.menu.SolarGeneratorMk0Menu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * 太阳能发电机 MK0 的客户端界面。
 *
 * <p>显示输出槽、缓存能量条、当前/最大能量和玩家背包。
 *
 * <p>Client screen for Solar Generator MK0. It shows the output slot, buffered
 * energy bar, current/max energy, and the player inventory.
 */
public class SolarGeneratorMk0Screen
  extends AbstractContainerScreen<SolarGeneratorMk0Menu>
{

  private static final ResourceLocation TEXTURE =
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "textures/gui/solar_generator_mk0.png"
    );

  private static final int BAR_X = 8;
  private static final int BAR_Y = 20;
  private static final int BAR_WIDTH = 16;
  private static final int BAR_HEIGHT = 54;
  private static final int ENERGY_COLOR = 0xFFFFD54F;
  private static final int BAR_BACKGROUND = 0xFF373737;
  private static final int ENERGY_TEXT_Y = 78;

  public SolarGeneratorMk0Screen(
    SolarGeneratorMk0Menu menu,
    Inventory playerInventory,
    Component title
  ) {
    super(menu, playerInventory, title);
    this.imageWidth = 176;
    this.imageHeight = 184;
    this.inventoryLabelY = this.imageHeight - 94;
  }

  @Override
  public void render(
    GuiGraphics graphics,
    int mouseX,
    int mouseY,
    float partialTick
  ) {
    super.render(graphics, mouseX, mouseY, partialTick);

    this.renderTooltip(graphics, mouseX, mouseY);

    if (
      isHovering(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY)
    ) {
      graphics.renderTooltip(
        this.font,
        Component.literal(energyText()),
        mouseX,
        mouseY
      );
    }
  }

  @Override
  protected void renderBg(
    GuiGraphics graphics,
    float partialTick,
    int mouseX,
    int mouseY
  ) {
    graphics.blit(
      TEXTURE,
      this.leftPos,
      this.topPos,
      0f,
      0f,
      this.imageWidth,
      this.imageHeight,
      this.imageWidth,
      this.imageHeight
    );

    int x = this.leftPos + BAR_X;
    int y = this.topPos + BAR_Y;
    graphics.fill(
      x,
      y,
      x + BAR_WIDTH,
      y + BAR_HEIGHT,
      BAR_BACKGROUND
    );

    int capacity = menu.getCapacity();
    double ratio = capacity <= 0
      ? 0.0D
      : Mth.clamp((double) menu.getEnergy() / (double) capacity, 0.0D, 1.0D);
    int filled = (int) Math.round((BAR_HEIGHT - 2) * ratio);
    if (filled > 0) {
      graphics.fill(
        x + 1,
        y + BAR_HEIGHT - 1 - filled,
        x + BAR_WIDTH - 1,
        y + BAR_HEIGHT - 1,
        ENERGY_COLOR
      );
    }
  }

  @Override
  protected void renderLabels(
    GuiGraphics graphics,
    int mouseX,
    int mouseY
  ) {
    super.renderLabels(graphics, mouseX, mouseY);

    String energy = energyText();
    graphics.drawString(
      this.font,
      energy,
      (this.imageWidth - this.font.width(energy)) / 2,
      ENERGY_TEXT_Y,
      4210752,
      false
    );
  }

  private String energyText() {
    int energy = menu.getEnergy();
    int capacity = menu.getCapacity();

    if (Screen.hasShiftDown()) {
      return EnergyFormat.detail(energy) +
        " / " +
        EnergyFormat.detail(capacity) +
        " FE";
    }
    return EnergyFormat.abbreviate(energy) +
      " / " +
      EnergyFormat.abbreviate(capacity) +
      " FE";
  }
}
