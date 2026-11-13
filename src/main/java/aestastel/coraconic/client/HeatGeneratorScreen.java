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
import aestastel.coraconic.menu.HeatGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.fml.ModList;

/**
 * 热力发电机 MK0 的客户端界面。
 *
 * <p>显示输出槽、燃料槽、能量条、横向热量条和 CLI 状态。
 *
 * <p>Client screen for Heat Generator MK0. It shows the output slot, fuel slot,
 * energy bar, horizontal heat bar, and CLI status.
 */
public class HeatGeneratorScreen
  extends AbstractContainerScreen<HeatGeneratorMenu>
{

  private static final ResourceLocation TEXTURE =
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "textures/gui/heat_generator_mk0.png"
    );

  private static final int BAR_X = 9;
  private static final int BAR_Y = 20;
  private static final int BAR_WIDTH = 14;
  private static final int BAR_HEIGHT = 54;
  private static final int ENERGY_COLOR = 0x9A4FD784;

  private static final int HEAT_X = 34;
  private static final int HEAT_Y = 81;
  private static final int HEAT_WIDTH = 103;
  private static final int HEAT_HEIGHT = 5;
  private static final int HEAT_COLOR = 0xFFFF8C42;

  private static final int CLI_TEXT_X = 36;
  private static final int CLI_TEXT_Y = 24;
  private static final int CLI_TEXT_COLOR = 0xFF55FF55;
  private static final float CLI_SCALE = 0.5F;

  public HeatGeneratorScreen(
    HeatGeneratorMenu menu,
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

    renderCli(graphics);
    this.renderTooltip(graphics, mouseX, mouseY);

    if (isHovering(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY)) {
      graphics.renderTooltip(
        this.font,
        Component.literal(energyText()),
        mouseX,
        mouseY
      );
    }
    if (
      isHovering(HEAT_X, HEAT_Y, HEAT_WIDTH, HEAT_HEIGHT, mouseX, mouseY)
    ) {
      graphics.renderTooltip(
        this.font,
        Component.literal(heatText()),
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
    double energyRatio = Mth.clamp(
      (double) menu.getEnergy() / (double) menu.getCapacity(),
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

    double heatRatio = Mth.clamp(
      (double) menu.getHeat() / (double) menu.getHeatCapacity(),
      0.0D,
      1.0D
    );
    int heatFilled = (int) Math.round(HEAT_WIDTH * heatRatio);
    if (heatFilled > 0) {
      graphics.fill(
        this.leftPos + HEAT_X,
        this.topPos + HEAT_Y,
        this.leftPos + HEAT_X + heatFilled,
        this.topPos + HEAT_Y + HEAT_HEIGHT,
        HEAT_COLOR
      );
    }
  }

  private void renderCli(GuiGraphics graphics) {
    String[] lines = {
      ">Heat Generating System",
      ">Generation MK0 - Version " + modVersion(),
      ">Power: " + this.menu.getCurrentGeneration() + " FE/t",
      ">Mode: " + this.menu.getMode().displayName(),
      ">Heat: " +
        this.menu.getHeat() +
        " / " +
        this.menu.getHeatCapacity(),
    };

    graphics.pose().pushPose();
    graphics
      .pose()
      .translate(
        (float) (this.leftPos + CLI_TEXT_X),
        (float) (this.topPos + CLI_TEXT_Y),
        0.0F
      );
    graphics.pose().scale(CLI_SCALE, CLI_SCALE, 1.0F);
    for (int index = 0; index < lines.length; index++) {
      graphics.drawString(
        this.font,
        lines[index],
        0,
        index * 20,
        CLI_TEXT_COLOR,
        false
      );
    }
    graphics.pose().popPose();
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

  private String heatText() {
    return (
      menu.getHeat() +
      " / " +
      menu.getHeatCapacity() +
      " heat"
    );
  }

  private static String modVersion() {
    return ModList.get()
      .getModContainerById(CoraconicTech.MODID)
      .map(container -> container.getModInfo().getVersion().toString())
      .orElse("unknown");
  }
}
