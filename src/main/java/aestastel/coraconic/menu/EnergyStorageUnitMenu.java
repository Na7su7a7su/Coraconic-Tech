/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.blockentity.EnergyStorageUnitBlockEntity;
import aestastel.coraconic.energy.EnergyFaceMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * 能量存储单元菜单：两个物品槽位（充电/放电）加玩家背包。
 *
 * <p>服务器端读取方块实体的能量，客户端通过数据槽接收同步值；客户端若
 * 暂时找不到方块实体，则退化为一个空的 {@link SimpleContainerData}。
 *
 * <p>Menu for the energy storage units: two machine slots (charge/discharge)
 * plus the player inventory. Only the energy value is synchronised through
 * data slots.
 */
public class EnergyStorageUnitMenu
  extends AbstractContainerMenu
  implements EnergyFaceMenu
{

  public static final int MACHINE_SLOTS = 2;
  private static final int PLAYER_INVENTORY_Y = 102;
  private static final int HOTBAR_Y = 160;
  private static final int FACE_MODE_DATA_START = 2;
  private static final int DATA_COUNT =
    FACE_MODE_DATA_START + FACE_MODE_COUNT;

  private final EnergyStorageUnitBlockEntity blockEntity;
  private final Container container;
  private final long capacity;
  private final ContainerData data;
  private final boolean clientSide;
  private final EnergyFaceMode[] syncedFaceModes =
    new EnergyFaceMode[FACE_MODE_COUNT];
  private int energyLow;
  private int energyHigh;

  public EnergyStorageUnitMenu(
    int containerId,
    Inventory playerInventory,
    EnergyStorageUnitBlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity,
      blockEntity.getCapacityLong()
    );
  }

  /**
   * 通用构造器：服务端使用方块实体作为容器，客户端可使用退化的空容器。
   *
   * <p>General constructor used by both sides.
   */
  public EnergyStorageUnitMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    EnergyStorageUnitBlockEntity blockEntity,
    long capacity
  ) {
    super(ModMenuType.ENERGY_STORAGE_UNIT.get(), containerId);
    checkContainerSize(container, MACHINE_SLOTS);
    this.container = container;
    this.blockEntity = blockEntity;
    this.capacity = capacity;
    this.clientSide = playerInventory.player.level().isClientSide;
    for (Direction direction : Direction.values()) {
      this.syncedFaceModes[direction.get3DDataValue()] =
        blockEntity != null
          ? blockEntity.getFaceMode(direction)
          : EnergyFaceMode.INPUT_OUTPUT;
    }

    if (blockEntity != null) {
      long stored = blockEntity.getEnergyStoredLong();
      this.energyLow = (int) stored;
      this.energyHigh = (int) (stored >>> 32);
      this.data = new ContainerData() {
        @Override
        public int get(int index) {
          if (index >= FACE_MODE_DATA_START) {
            Direction side = Direction.from3DDataValue(
              index - FACE_MODE_DATA_START
            );
            if (!clientSide) {
              return blockEntity.getFaceMode(side).ordinal();
            }
            return syncedFaceModes[side.get3DDataValue()].ordinal();
          }

          long current = blockEntity.getEnergyStoredLong();
          return index == 0
            ? (int) current
            : (int) (current >>> 32);
        }

        @Override
        public void set(int index, int value) {
          if (index >= FACE_MODE_DATA_START) {
            Direction side = Direction.from3DDataValue(
              index - FACE_MODE_DATA_START
            );
            syncedFaceModes[side.get3DDataValue()] =
              EnergyFaceMode.byOrdinal(value);
            return;
          }

          if (index == 0) {
            energyLow = value;
          } else {
            energyHigh = value;
          }
          blockEntity.setEnergyStored(join(energyLow, energyHigh));
        }

        @Override
        public int getCount() {
          return DATA_COUNT;
        }
      };
    } else {
      this.data = new SimpleContainerData(DATA_COUNT);
    }

    // 充电槽：只接受可以接收能量的物品。
    // Charge slot: only accepts items that can receive energy.
    addSlot(
      new Slot(container, EnergyStorageUnitBlockEntity.CHARGE_SLOT, 25, 17) {
        @Override
        public boolean mayPlace(ItemStack stack) {
          return canCharge(stack);
        }
      }
    );

    // 放电槽：只接受可以输出能量的物品。
    // Discharge slot: only accepts items that can provide energy.
    addSlot(
      new Slot(
        container,
        EnergyStorageUnitBlockEntity.DISCHARGE_SLOT,
        25,
        60
      ) {
        @Override
        public boolean mayPlace(ItemStack stack) {
          return canDischarge(stack);
        }
      }
    );

    // 玩家主背包：3 行 × 9 列。
    for (int row = 0; row < 3; row++) {
      for (int column = 0; column < 9; column++) {
        addSlot(
          new Slot(
            playerInventory,
            column + row * 9 + 9,
            8 + column * 18,
            PLAYER_INVENTORY_Y + row * 18
          )
        );
      }
    }

    // 快捷栏：1 行 × 9 列。
    for (int column = 0; column < 9; column++) {
      addSlot(
        new Slot(
          playerInventory,
          column,
          8 + column * 18,
          HOTBAR_Y
        )
      );
    }

    addDataSlots(data);
  }

  /**
   * 客户端工厂：从网络数据读取方块坐标与容量。
   *
   * <p>Client factory reading the block position and capacity.
   */
  public static EnergyStorageUnitMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    long capacity = buffer.readLong();
    EnergyStorageUnitBlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof EnergyStorageUnitBlockEntity energyUnit
        ? energyUnit
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new EnergyStorageUnitMenu(
      containerId,
      playerInventory,
      container,
      blockEntity,
      capacity
    );
  }

  /** {@return 当前储存的能量（FE）} */
  public long getEnergy() {
    long low = data.get(0) & 0xFFFFFFFFL;
    long high = data.get(1) & 0xFFFFFFFFL;
    return low | (high << 32);
  }

  /** {@return 最大容量（FE）} */
  public long getCapacity() {
    return capacity;
  }

  @Override
  public EnergyFaceMode getFaceMode(Direction side) {
    return EnergyFaceMode.byOrdinal(
      data.get(FACE_MODE_DATA_START + side.get3DDataValue())
    );
  }

  /**
   * 处理 GUI 面配置按钮：0..5 正向循环，6..11 反向循环。
   *
   * <p>Handles face-mode buttons: 0..5 forward and 6..11 backward.
   */
  @Override
  public boolean clickMenuButton(Player player, int id) {
    if (
      id < 0 ||
      id >= FACE_MODE_COUNT * 2 ||
      blockEntity == null ||
      !stillValid(player)
    ) {
      return false;
    }

    Direction side = Direction.from3DDataValue(id % FACE_MODE_COUNT);
    boolean reverse = id >= FACE_MODE_COUNT;
    EnergyFaceMode mode = blockEntity.cycleFaceMode(side, reverse);
    syncedFaceModes[side.get3DDataValue()] = mode;

    player.level().playSound(
      null,
      blockEntity.getBlockPos(),
      SoundEvents.LEVER_CLICK,
      SoundSource.BLOCKS,
      0.4F,
      1.0F
    );
    broadcastChanges();
    return true;
  }

  private static long join(int low, int high) {
    return (low & 0xFFFFFFFFL) | ((long) high << 32);
  }

  @Override
  public boolean stillValid(Player player) {
    return (
      blockEntity != null &&
      Container.stillValidBlockEntity(blockEntity, player)
    );
  }

  /**
   * Shift 点击：机器槽与玩家背包之间转移；玩家物品按能量能力自动选择槽位。
   *
   * <p>Shift-click transfer between the machine slots and the player inventory;
   * player items are routed by their energy capability.
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

    if (index < MACHINE_SLOTS) {
      if (!moveItemStackTo(stack, MACHINE_SLOTS, slots.size(), true)) {
        return ItemStack.EMPTY;
      }
    } else {
      boolean didMove = false;
      if (canCharge(stack)) {
        didMove = moveItemStackTo(stack, 0, 1, false);
      }
      if (!didMove && canDischarge(stack)) {
        didMove = moveItemStackTo(stack, 1, 2, false);
      }
      if (!didMove) {
        return ItemStack.EMPTY;
      }
    }

    if (stack.isEmpty()) {
      slot.setByPlayer(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }

    return moved;
  }

  private static boolean canCharge(ItemStack stack) {
    IEnergyStorage storage = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    return storage != null && storage.canReceive();
  }

  private static boolean canDischarge(ItemStack stack) {
    IEnergyStorage storage = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    return storage != null && storage.canExtract();
  }
}
