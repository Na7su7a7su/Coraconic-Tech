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
import aestastel.coraconic.menu.ItemStorageUnitMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 物品存储单元容器的客户端界面：高度固定，宽度随列数增长。
 *
 * <p>三个等级共用本界面，只按列数选择不同的背景纹理：
 * <ul>
 *   <li>9 列（MK1）使用 176 像素宽背景；</li>
 *   <li>18 列（MK2）使用 338 像素宽背景；</li>
 *   <li>27 列（MK3）使用 500 像素宽背景。</li>
 * </ul>
 * 玩家背包标题与槽位由菜单统一计算位置，界面只需按实际宽度绘制背景。
 *
 * <p>Client screen shared by all tiers. The height is fixed and the width grows
 * with the column count; only the background texture differs per tier.
 */
public class ItemStorageUnitScreen
  extends AbstractContainerScreen<ItemStorageUnitMenu>
{

  private static final int IMAGE_HEIGHT = 276;

  private final ResourceLocation texture;

  public ItemStorageUnitScreen(
    ItemStorageUnitMenu menu,
    Inventory playerInventory,
    Component title
  ) {
    super(menu, playerInventory, title);
    this.imageWidth = menu.getImageWidth();
    this.imageHeight = IMAGE_HEIGHT;
    this.inventoryLabelX = menu.getPlayerInventoryX();
    // 父类构造时按默认高度计算了标题位置，这里按实际高度重新定位。
    // The super constructor used the default height, so re-position the label.
    this.inventoryLabelY = this.imageHeight - 94;
    this.texture = textureFor(menu.getColumns());
  }

  /**
   * 按容器列数选择背景纹理。
   *
   * <p>Selects the background texture for the given column count.
   *
   * @param columns 容器列数 / container column count
   * @return 纹理路径 / texture location
   */
  private static ResourceLocation textureFor(int columns) {
    String tier = switch (columns) {
      case 18 -> "mk2";
      case 27 -> "mk3";
      default -> "mk1";
    };
    return ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "textures/gui/item_storage_unit_" + tier + ".png"
    );
  }

  @Override
  public void render(
    GuiGraphics graphics,
    int mouseX,
    int mouseY,
    float partialTick
  ) {
    super.render(graphics, mouseX, mouseY, partialTick);
    // AbstractContainerScreen 的 render 不会自动绘制物品提示框，必须显式调用。
    // AbstractContainerScreen#render does not render item tooltips on its own;
    // the screen has to invoke renderTooltip explicitly.
    this.renderTooltip(graphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(
    GuiGraphics graphics,
    float partialTick,
    int mouseX,
    int mouseY
  ) {
    // 9 参数重载：显式声明贴图尺寸，按 1:1 像素采样。
    // 7 参数重载固定按 256×256 计算 UV，而本模组 GUI 贴图是原生尺寸
    // （176×276 / 338×276 / 500×276），用默认重载会导致拉伸与越界采样。
    // The 9-argument overload takes the real texture size and samples 1:1;
    // the 7-argument overload assumes a 256×256 sheet.
    graphics.blit(
      texture,
      this.leftPos,
      this.topPos,
      0f,
      0f,
      this.imageWidth,
      this.imageHeight,
      this.imageWidth,
      this.imageHeight
    );
  }
}
