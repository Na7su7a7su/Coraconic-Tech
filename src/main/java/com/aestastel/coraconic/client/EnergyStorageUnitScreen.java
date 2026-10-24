package com.aestastel.coraconic.client;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.energy.EnergyFormat;
import com.aestastel.coraconic.menu.EnergyStorageUnitMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * 能量存储单元的客户端界面。
 *
 * <p>显示两个物品槽位（充电/放电）、竖直能量条、当前/最大能量以及玩家背包。
 * 悬停物品槽位时显示物品 tooltip，悬停能量条时显示能量 tooltip。
 *
 * <p>Client screen for the energy storage units. It shows the charge/discharge
 * slots, a vertical energy bar, the current/max energy and the player inventory.
 */
public class EnergyStorageUnitScreen
  extends AbstractContainerScreen<EnergyStorageUnitMenu>
{

  private static final ResourceLocation TEXTURE =
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "textures/gui/energy_storage_unit.png"
    );

  private static final int BAR_X = 80;
  private static final int BAR_Y = 20;
  private static final int BAR_WIDTH = 68;
  private static final int BAR_HEIGHT = 54;
  /** #4FD784，alpha 154（0x9A）。 */
  private static final int ENERGY_COLOR = 0x9A4FD784;
  private static final int BAR_BACKGROUND = 0xFF373737;

  private static final int ENERGY_TEXT_Y = 78;

  public EnergyStorageUnitScreen(
    EnergyStorageUnitMenu menu,
    Inventory playerInventory,
    Component title
  ) {
    super(menu, playerInventory, title);
    this.imageWidth = 176;
    this.imageHeight = 184;
    // 父类按默认高度计算了背包标题位置，这里按实际高度重新定位。
    // The super constructor used the default height, so re-position the label.
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

    // 物品槽位 tooltip（AbstractContainerScreen 不会自动绘制）。
    // Item slot tooltips; the superclass does not draw them automatically.
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

    long capacity = menu.getCapacity();
    double ratio = capacity <= 0L
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
    // 标题与玩家背包标题。
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
    long energy = menu.getEnergy();
    long capacity = menu.getCapacity();

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
