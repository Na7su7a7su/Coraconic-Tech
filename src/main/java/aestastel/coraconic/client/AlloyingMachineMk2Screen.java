/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.client;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.energy.EnergyFormat;
import aestastel.coraconic.menu.AlloyingMachineMk2Menu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * 合金仪 MK2 的客户端界面。
 *
 * <p>显示三条独立生产线、共享放电槽、能量缓存和三个独立进度条。
 *
 * <p>Client screen for Alloying Machine MK2. It shows three independent
 * production lines, the shared discharge slot, the energy buffer, and three
 * separate progress bars.
 */
public class AlloyingMachineMk2Screen
  extends AbstractContainerScreen<AlloyingMachineMk2Menu>
{

  private static final ResourceLocation TEXTURE =
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "textures/gui/alloying_machine_mk2.png"
    );

  private static final int BAR_X = 149;
  private static final int BAR_Y = 17;
  private static final int BAR_WIDTH = 14;
  private static final int BAR_HEIGHT = 54;
  private static final int ENERGY_COLOR = 0x9A4FD784;

  private static final int PROGRESS_X = 83;
  private static final int PROGRESS_START_Y = 23;
  private static final int PROGRESS_LINE_SPACING = 28;
  private static final int PROGRESS_WIDTH = 30;
  private static final int PROGRESS_HEIGHT = 8;
  private static final int PROGRESS_COLOR = 0xFF4FD784;

  public AlloyingMachineMk2Screen(
    AlloyingMachineMk2Menu menu,
    Inventory playerInventory,
    Component title
  ) {
    super(menu, playerInventory, title);
    this.imageWidth = 176;
    this.imageHeight = 192;
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

    if (isHovering(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY)) {
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

    int energyX = this.leftPos + BAR_X;
    int energyY = this.topPos + BAR_Y;
    long capacity = menu.getCapacity();
    double energyRatio = capacity <= 0L
      ? 0.0D
      : Mth.clamp(
          (double) menu.getEnergy() / (double) capacity,
          0.0D,
          1.0D
        );
    int energyFilled = (int) Math.round(
      (BAR_HEIGHT - 2) * energyRatio
    );
    if (energyFilled > 0) {
      graphics.fill(
        energyX,
        energyY + BAR_HEIGHT - 2 - energyFilled,
        energyX + BAR_WIDTH,
        energyY + BAR_HEIGHT,
        ENERGY_COLOR
      );
    }

    for (
      int lineIndex = 0;
      lineIndex < AlloyingMachineMk2Menu.LINE_COUNT;
      lineIndex++
    ) {
      int progressX = this.leftPos + PROGRESS_X;
      int progressY =
        this.topPos +
        PROGRESS_START_Y +
        lineIndex * PROGRESS_LINE_SPACING;
      int maxProgress = menu.getMaxProgress(lineIndex);
      double progressRatio = maxProgress <= 0
        ? 0.0D
        : Mth.clamp(
            (double) menu.getProgress(lineIndex) / (double) maxProgress,
            0.0D,
            1.0D
          );
      int progressFilled = (int) Math.round(
        PROGRESS_WIDTH * progressRatio
      );
      if (progressFilled > 0) {
        graphics.fill(
          progressX,
          progressY,
          progressX + progressFilled,
          progressY + PROGRESS_HEIGHT,
          PROGRESS_COLOR
        );
      }
    }
  }

  private String energyText() {
    long energy = menu.getEnergy();
    long capacity = menu.getCapacity();

    if (Screen.hasShiftDown()) {
      return (
        EnergyFormat.detail(energy) +
        " / " +
        EnergyFormat.detail(capacity) +
        " FE"
      );
    }
    return (
      EnergyFormat.abbreviate(energy) +
      " / " +
      EnergyFormat.abbreviate(capacity) +
      " FE"
    );
  }
}
