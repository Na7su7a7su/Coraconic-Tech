/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.client;

import aestastel.coraconic.energy.EnergyFaceMode;
import aestastel.coraconic.menu.EnergyFaceMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * 能量方块 GUI 共用的面输入/输出配置弹窗。
 *
 * <p>按钮位于 GUI 左侧外部上方，展开后的面板也完全位于 GUI 左侧，不会
 * 遮挡物品栏或界面主体。左键点击面模式向前循环，右键向后循环；模式变化
 * 通过容器菜单按钮包同步到服务端。
 *
 * <p>Shared face I/O configuration popup for energy block screens. The toggle
 * button and expanded panel sit outside the GUI on the upper-left, never
 * covering the inventory or main UI. Left-click cycles a face forward and
 * right-click cycles backward.
 */
public final class EnergyFaceConfigPanel {

  private static final int BUTTON_OFFSET_X = 22;
  private static final int BUTTON_OFFSET_Y = 4;
  private static final int BUTTON_WIDTH = 17;
  private static final int BUTTON_HEIGHT = 14;

  private static final Direction[] FACE_ORDER = {
    Direction.UP,
    Direction.DOWN,
    Direction.NORTH,
    Direction.SOUTH,
    Direction.WEST,
    Direction.EAST,
  };
  private static final int PANEL_Y = 20;
  private static final int PANEL_GAP = 4;
  private static final int MIN_PANEL_WIDTH = 48;
  private static final int MAX_PANEL_WIDTH = 116;
  private static final int COMPACT_WIDTH = 96;
  private static final int HEADER_HEIGHT = 20;
  private static final int ROW_HEIGHT = 15;
  private static final int BOTTOM_PADDING = 4;
  private static final int PANEL_HEIGHT =
    HEADER_HEIGHT + FACE_ORDER.length * ROW_HEIGHT + BOTTOM_PADDING;

  private static final int PANEL_BACKGROUND = 0xFF101010;
  private static final int PANEL_BORDER = 0xFFB0B0B0;
  private static final int ROW_HOVER = 0x30FFFFFF;
  private static final int TEXT_COLOR = 0xFFE6E6E6;
  private static final int MUTED_TEXT_COLOR = 0xFF9A9A9A;

  private final EnergyFaceMenu menu;
  private final int containerId;
  private boolean open;
  private boolean suppressNextRelease;

  public EnergyFaceConfigPanel(EnergyFaceMenu menu, int containerId) {
    this.menu = menu;
    this.containerId = containerId;
  }

  public void render(
    GuiGraphics graphics,
    int leftPos,
    int topPos,
    int mouseX,
    int mouseY,
    Font font
  ) {
    renderButton(graphics, leftPos, topPos, mouseX, mouseY, font);

    if (!this.open) {
      if (isHoveringButton(leftPos, topPos, mouseX, mouseY)) {
        graphics.renderTooltip(
          font,
          Component.translatable("gui.coraconic.face_config.button_hint"),
          mouseX,
          mouseY
        );
      }
      return;
    }

    int panelWidth = panelWidth(leftPos);
    int panelX = panelX(leftPos, panelWidth);
    int panelY = topPos + PANEL_Y;
    int panelRight = panelX + panelWidth;
    int panelBottom = panelY + PANEL_HEIGHT;
    boolean compact = panelWidth < COMPACT_WIDTH;

    graphics.fill(
      panelX,
      panelY,
      panelRight,
      panelBottom,
      PANEL_BACKGROUND
    );
    drawBorder(graphics, panelX, panelY, panelRight, panelBottom);

    graphics.drawString(
      font,
      Component.translatable(
        compact
          ? "gui.coraconic.face_config.title_short"
          : "gui.coraconic.face_config.title"
      ).getString(),
      panelX + 4,
      panelY + 6,
      0xFFFFFFFF,
      false
    );
    graphics.drawString(
      font,
      "x",
      panelRight - 11,
      panelY + 5,
      MUTED_TEXT_COLOR,
      false
    );

    int rowStart = panelY + HEADER_HEIGHT;
    Direction hoveredFace = null;
    for (int index = 0; index < FACE_ORDER.length; index++) {
      Direction face = FACE_ORDER[index];
      int rowY = rowStart + index * ROW_HEIGHT;
      boolean hovered = isHovering(
        panelX + 2,
        rowY,
        panelWidth - 4,
        ROW_HEIGHT,
        mouseX,
        mouseY
      );
      if (hovered) {
        hoveredFace = face;
        graphics.fill(
          panelX + 2,
          rowY,
          panelRight - 2,
          rowY + ROW_HEIGHT,
          ROW_HOVER
        );
      }

      EnergyFaceMode mode = this.menu.getFaceMode(face);
      String faceName = Component.translatable(
        compact
          ? shortFaceKey(face)
          : "direction.coraconic." + face.getName()
      ).getString();
      String modeName = Component.translatable(
        shortModeKey(mode)
      ).getString();
      int modeColor = colorFor(mode);
      int modeX = panelRight - 4 - font.width(modeName);

      graphics.drawString(
        font,
        faceName,
        panelX + 4,
        rowY + 3,
        TEXT_COLOR,
        false
      );
      graphics.fill(
        modeX - 7,
        rowY + 3,
        modeX - 3,
        rowY + 11,
        modeColor
      );
      graphics.drawString(
        font,
        modeName,
        modeX,
        rowY + 3,
        modeColor,
        false
      );
    }

    if (hoveredFace != null) {
      EnergyFaceMode mode = this.menu.getFaceMode(hoveredFace);
      graphics.renderTooltip(
        font,
        Component.translatable(
          "gui.coraconic.face_config.row_hint",
          Component.translatable(
            "direction.coraconic." + hoveredFace.getName()
          ),
          Component.translatable(mode.translationKey())
        ),
        mouseX,
        mouseY
      );
    }
  }

  /**
   * 处理面板和开关按钮的鼠标点击；返回 true 表示事件已被消费。
   *
   * <p>Handles popup and toggle-button clicks; returns true when consumed.
   */
  public boolean mouseClicked(
    double mouseX,
    double mouseY,
    int button,
    int leftPos,
    int topPos
  ) {
    int buttonX = buttonX(leftPos);
    int buttonY = topPos + BUTTON_OFFSET_Y;

    if (
      isHovering(
        buttonX,
        buttonY,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        mouseX,
        mouseY
      )
    ) {
      this.suppressNextRelease = true;
      if (button == 0) {
        this.open = !this.open;
      }
      return true;
    }

    if (!this.open) {
      return false;
    }

    this.suppressNextRelease = true;
    int panelWidth = panelWidth(leftPos);
    int panelX = panelX(leftPos, panelWidth);
    int panelY = topPos + PANEL_Y;
    int panelRight = panelX + panelWidth;
    int panelBottom = panelY + PANEL_HEIGHT;

    if (
      !isHovering(
        panelX,
        panelY,
        panelWidth,
        PANEL_HEIGHT,
        mouseX,
        mouseY
      )
    ) {
      this.open = false;
      return true;
    }

    if (
      isHovering(
        panelRight - 14,
        panelY + 3,
        12,
        12,
        mouseX,
        mouseY
      )
    ) {
      this.open = false;
      return true;
    }

    int rowStart = panelY + HEADER_HEIGHT;
    for (int index = 0; index < FACE_ORDER.length; index++) {
      int rowY = rowStart + index * ROW_HEIGHT;
      if (
        isHovering(
          panelX + 2,
          rowY,
          panelWidth - 4,
          ROW_HEIGHT,
          mouseX,
          mouseY
        )
      ) {
        if (button == 0 || button == 1) {
          Minecraft minecraft = Minecraft.getInstance();
          if (minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(
              this.containerId,
              this.menu.faceModeButtonId(
                FACE_ORDER[index],
                button == 1
              )
            );
          }
        }
        return true;
      }
    }

    return true;
  }

  /**
   * 防止面配置面板上的点击在释放时落到下层物品槽。
   *
   * <p>Consumes the matching release after a click handled by the panel, so
   * the release cannot reach a slot underneath.
   */
  public boolean mouseReleased() {
    if (this.suppressNextRelease) {
      this.suppressNextRelease = false;
      return true;
    }
    return false;
  }

  public boolean keyPressed(int keyCode) {
    if (this.open && keyCode == GLFW.GLFW_KEY_ESCAPE) {
      this.open = false;
      return true;
    }
    return false;
  }

  private static int buttonX(int leftPos) {
    return Math.max(2, leftPos - BUTTON_OFFSET_X);
  }

  private static int panelWidth(int leftPos) {
    return Math.min(
      MAX_PANEL_WIDTH,
      Math.max(MIN_PANEL_WIDTH, leftPos - 6)
    );
  }

  private static int panelX(int leftPos, int width) {
    return Math.max(2, leftPos - width - PANEL_GAP);
  }

  private static void drawBorder(
    GuiGraphics graphics,
    int left,
    int top,
    int right,
    int bottom
  ) {
    graphics.fill(left, top, right, top + 1, PANEL_BORDER);
    graphics.fill(left, bottom - 1, right, bottom, PANEL_BORDER);
    graphics.fill(left, top, left + 1, bottom, PANEL_BORDER);
    graphics.fill(right - 1, top, right, bottom, PANEL_BORDER);
  }

  private void renderButton(
    GuiGraphics graphics,
    int leftPos,
    int topPos,
    int mouseX,
    int mouseY,
    Font font
  ) {
    int x = buttonX(leftPos);
    int y = topPos + BUTTON_OFFSET_Y;
    boolean hovered = isHovering(
      x,
      y,
      BUTTON_WIDTH,
      BUTTON_HEIGHT,
      mouseX,
      mouseY
    );

    graphics.fill(
      x,
      y,
      x + BUTTON_WIDTH,
      y + BUTTON_HEIGHT,
      0xFF000000
    );
    graphics.fill(
      x + 1,
      y + 1,
      x + BUTTON_WIDTH - 1,
      y + BUTTON_HEIGHT - 1,
      this.open
        ? 0xFF4A90E2
        : hovered
          ? 0xFF3B6E9A
          : 0xFF555555
    );
    graphics.drawString(
      font,
      Component.translatable("gui.coraconic.face_config.button").getString(),
      x + 2,
      y + 3,
      0xFFFFFFFF,
      false
    );
  }

  private static boolean isHoveringButton(
    int leftPos,
    int topPos,
    int mouseX,
    int mouseY
  ) {
    return isHovering(
      buttonX(leftPos),
      topPos + BUTTON_OFFSET_Y,
      BUTTON_WIDTH,
      BUTTON_HEIGHT,
      mouseX,
      mouseY
    );
  }

  private static boolean isHovering(
    int x,
    int y,
    int width,
    int height,
    double mouseX,
    double mouseY
  ) {
    return (
      mouseX >= x &&
      mouseX < x + width &&
      mouseY >= y &&
      mouseY < y + height
    );
  }

  private static String shortFaceKey(Direction face) {
    return "gui.coraconic.face_config.face." + face.getName();
  }

  private static String shortModeKey(EnergyFaceMode mode) {
    return switch (mode) {
      case INPUT -> "gui.coraconic.face_config.mode.input";
      case OUTPUT -> "gui.coraconic.face_config.mode.output";
      case INPUT_OUTPUT -> "gui.coraconic.face_config.mode.input_output";
      case BLOCKED -> "gui.coraconic.face_config.mode.blocked";
    };
  }

  private static int colorFor(EnergyFaceMode mode) {
    return switch (mode) {
      case INPUT -> 0xFF4A90E2;
      case OUTPUT -> 0xFFE67E22;
      case INPUT_OUTPUT -> 0xFF2ECC71;
      case BLOCKED -> 0xFF9A9A9A;
    };
  }
}
