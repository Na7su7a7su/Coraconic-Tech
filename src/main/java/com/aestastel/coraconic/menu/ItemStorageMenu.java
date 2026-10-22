package com.aestastel.coraconic.menu;

import com.aestastel.coraconic.block.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 物品存储容器的菜单：固定 9 行，列数由容量决定。
 *
 * <p>三个等级共用本菜单，布局规则完全一致：
 * <ul>
 *   <li>MK0：9 行 × 9 列 = 81 格；</li>
 *   <li>MK1：9 行 × 18 列 = 162 格；</li>
 *   <li>MK2：9 行 × 27 列 = 243 格。</li>
 * </ul>
 * 槽位索引按“行优先”排列（索引 ÷ 列数 = 行，索引 % 列数 = 列），因此
 * MK0 的既有存档布局与之前完全一致。界面宽度随列数增长，玩家背包始终居中。
 *
 * <p>Menu shared by all storage tiers: always 9 rows, with the column count
 * derived from the capacity. Slot indices are row-major, preserving the existing
 * MK0 save layout. The GUI widens with the column count while the player
 * inventory stays centered.
 */
public class ItemStorageMenu extends AbstractContainerMenu {

  /** 固定行数。 */
  public static final int ROWS = 9;
  /** 每行槽位数（玩家背包与容器槽位均按 18 像素间隔排列）。 */
  public static final int COLUMN_WIDTH = 18;
  /** 玩家背包每行 9 格。 */
  private static final int PLAYER_COLUMNS = 9;

  private static final int CONTAINER_X = 8;
  private static final int CONTAINER_Y = 18;
  private static final int PLAYER_INVENTORY_Y = 193;
  private static final int HOTBAR_OFFSET = 58;

  private final Container container;
  private final int totalSlots;
  private final int columns;

  /**
   * 服务端构造器：容量直接取自方块实体。
   *
   * <p>Server-side constructor; the capacity comes from the block entity.
   */
  public ItemStorageMenu(
    int containerId,
    Inventory playerInventory,
    Container container
  ) {
    this(containerId, playerInventory, container, container.getContainerSize());
  }

  /**
   * 通用构造器：显式指定总槽位数，客户端也用它恢复正确尺寸。
   *
   * <p>General constructor with an explicit slot count, also used by the client
   * to restore the correct size.
   */
  public ItemStorageMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    int totalSlots
  ) {
    super(ModMenuType.ITEM_STORAGE.get(), containerId);
    checkContainerSize(container, totalSlots);
    this.container = container;
    this.totalSlots = totalSlots;
    this.columns = totalSlots / ROWS;
    container.startOpen(playerInventory.player);

    // 容器槽位：9 行 × columns 列，索引按行优先。
    // Container slots: 9 rows by `columns`, row-major indices.
    for (int index = 0; index < totalSlots; index++) {
      int row = index / columns;
      int column = index % columns;
      addSlot(
        new Slot(
          container,
          index,
          CONTAINER_X + column * COLUMN_WIDTH,
          CONTAINER_Y + row * COLUMN_WIDTH
        )
      );
    }

    int playerX = getPlayerInventoryX();

    // 玩家主背包：3 行 × 9 列。
    // Player main inventory: 3 rows by 9 columns.
    for (int row = 0; row < 3; row++) {
      for (int column = 0; column < PLAYER_COLUMNS; column++) {
        addSlot(
          new Slot(
            playerInventory,
            column + row * PLAYER_COLUMNS + 9,
            playerX + column * COLUMN_WIDTH,
            PLAYER_INVENTORY_Y + row * COLUMN_WIDTH
          )
        );
      }
    }

    // 快捷栏：1 行 × 9 列。
    // Hotbar: 1 row by 9 columns.
    for (int column = 0; column < PLAYER_COLUMNS; column++) {
      addSlot(
        new Slot(
          playerInventory,
          column,
          playerX + column * COLUMN_WIDTH,
          PLAYER_INVENTORY_Y + HOTBAR_OFFSET
        )
      );
    }
  }

  /**
   * 客户端工厂：读取方块坐标与总槽位数并找回方块实体。
   *
   * <p>若客户端暂时没有对应方块实体，退化为正确尺寸的空容器，保证菜单能够
   * 正常创建而不会崩溃。
   *
   * <p>Client factory: reads the block position and slot count, then resolves the
   * block entity. Falls back to a correctly sized empty container if needed.
   *
   * @param containerId 窗口 ID / window id
   * @param playerInventory 玩家背包 / player inventory
   * @param buffer 服务端写入的附加数据 / extra data written by the server
   * @return 客户端菜单实例 / the client-side menu
   */
  public static ItemStorageMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    int totalSlots = buffer.readVarInt();
    Container container =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof ModBlockEntity blockEntity &&
        blockEntity.getContainerSize() == totalSlots
        ? blockEntity
        : new SimpleContainer(totalSlots);
    return new ItemStorageMenu(
      containerId,
      playerInventory,
      container,
      totalSlots
    );
  }

  /** {@return 容器列数} */
  public int getColumns() {
    return columns;
  }

  /** {@return 容器总槽位数} */
  public int getTotalSlots() {
    return totalSlots;
  }

  /**
   * 界面宽度：左右各 8/6 像素边距 + 列数 × 18 像素。
   *
   * <p>GUI width: side margins plus the slot grid.
   */
  public int getImageWidth() {
    return CONTAINER_X + columns * COLUMN_WIDTH + 6;
  }

  /**
   * 玩家背包的起始 X：在容器网格下方居中，窄界面时保持原版左边距。
   *
   * <p>Player inventory start X: centered under the container grid, keeping the
   * vanilla left margin for narrow GUIs.
   */
  public int getPlayerInventoryX() {
    return Math.max(
      CONTAINER_X,
      (getImageWidth() - PLAYER_COLUMNS * COLUMN_WIDTH) / 2
    );
  }

  @Override
  public boolean stillValid(Player player) {
    return container.stillValid(player);
  }

  /**
   * Shift + 点击时的批量转移规则：
   * <ul>
   *   <li>容器槽位 → 玩家背包；</li>
   *   <li>玩家背包 → 容器空位。</li>
   * </ul>
   *
   * <p>Shift-click transfer: container to player inventory, and player inventory
   * to the container.
   */
  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack moved = ItemStack.EMPTY;
    Slot slot = slots.get(index);
    if (slot == null || !slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    ItemStack stack = slot.getItem();
    moved = stack.copy();

    if (index < totalSlots) {
      if (!moveItemStackTo(stack, totalSlots, slots.size(), true)) {
        return ItemStack.EMPTY;
      }
    } else if (!moveItemStackTo(stack, 0, totalSlots, false)) {
      return ItemStack.EMPTY;
    }

    if (stack.isEmpty()) {
      slot.setByPlayer(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }

    return moved;
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    container.stopOpen(player);
  }

  /**
   * 供界面读取底层容器。
   *
   * <p>Exposes the backing container.
   */
  public Container getContainer() {
    return container;
  }
}
