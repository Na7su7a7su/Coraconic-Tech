/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.client;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.energy.EnergyFormat;
import aestastel.coraconic.menu.SolarGeneratorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.fml.ModList;

/**
 * 太阳能发电机 MK0–MK3 的共用客户端界面。
 *
 * <p>显示输出槽、缓存能量条、当前/最大能量和玩家背包。
 *
 * <p>Shared client screen for Solar Generator MK0–MK3. It shows the output slot,
 * buffered energy bar, current/max energy, and the player inventory.
 */
public class SolarGeneratorScreen
  extends AbstractContainerScreen<SolarGeneratorMenu>
{

  private static final ResourceLocation TEXTURE =
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "textures/gui/solar_generator_mk0.png"
    );

  private static final int BAR_X = 9;
  private static final int BAR_Y = 20;
  private static final int BAR_WIDTH = 14;
  private static final int BAR_HEIGHT = 54;
  private static final int ENERGY_COLOR = 0x9A4FD784;
  private static final int ENERGY_TEXT_Y = 78;

  private static final int CLI_TEXT_X = 36;
  private static final int CLI_TEXT_Y = 24;
  private static final int CLI_TEXT_COLOR = 0xFF55FF55;
  private static final float CLI_SCALE = 0.5F;

  private final EnergyFaceConfigPanel faceConfigPanel;

  public SolarGeneratorScreen(
    SolarGeneratorMenu menu,
    Inventory playerInventory,
    Component title
  ) {
    super(menu, playerInventory, title);
    this.imageWidth = 176;
    this.imageHeight = 184;
    this.inventoryLabelY = this.imageHeight - 94;
    this.faceConfigPanel = new EnergyFaceConfigPanel(menu, menu.containerId);
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

    this.faceConfigPanel.render(
      graphics,
      this.leftPos,
      this.topPos,
      mouseX,
      mouseY,
      this.font
    );
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (
      this.faceConfigPanel.mouseClicked(
        mouseX,
        mouseY,
        button,
        this.leftPos,
        this.topPos
      )
    ) {
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (this.faceConfigPanel.mouseReleased()) {
      return true;
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (this.faceConfigPanel.keyPressed(keyCode)) {
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
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

    int capacity = menu.getCapacity();
    double ratio =
      capacity <= 0
        ? 0.0D
        : Mth.clamp((double) menu.getEnergy() / (double) capacity, 0.0D, 1.0D);
    int filled = (int) Math.round((BAR_HEIGHT - 2) * ratio);
    if (filled > 0) {
      graphics.fill(
        x,
        y + BAR_HEIGHT - 2 - filled,
        x + BAR_WIDTH,
        y + BAR_HEIGHT,
        ENERGY_COLOR
      );
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
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

  private void renderCli(GuiGraphics graphics) {
    String[] lines = cliLines();

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

  protected String[] cliLines() {
    long dayTime =
      Minecraft.getInstance().level == null
        ? 0L
        : Minecraft.getInstance().level.getDayTime();
    String dimension =
      Minecraft.getInstance().level == null
        ? "unknown"
        : Minecraft.getInstance().level.dimension().location().toString();

    return new String[] {
      ">Solar Energy Generating System",
      ">Generation " +
        this.menu.getTier().name() +
        " - Version " +
        modVersion(),
      ">Power: " + this.menu.getCurrentRate() + " FE/t",
      ">Mode: " + this.menu.getMode().displayName(),
      ">Time " + dayTime + " | " + dimension,
    };
  }

  protected static String modVersion() {
    return ModList.get()
      .getModContainerById(CoraconicTech.MODID)
      .map(container -> container.getModInfo().getVersion().toString())
      .orElse("unknown");
  }

  private String energyText() {
    int energy = menu.getEnergy();
    int capacity = menu.getCapacity();

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
