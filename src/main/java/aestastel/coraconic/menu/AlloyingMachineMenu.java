/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.block.AlloyingMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
 * 合金仪 MK1 菜单：三个原料槽、一个成品槽、一个放电槽和玩家背包。
 *
 * <p>能量与进度通过数据槽同步；放电槽只接受可提取能量的物品。
 *
 * <p>Alloying Machine MK1 menu with three input slots, one output slot, one
 * discharge slot, and the player inventory. Energy and progress are synchronized
 * through data slots; the discharge slot only accepts energy-extractable items.
 */
public class AlloyingMachineMenu extends AbstractContainerMenu {

  public static final int MACHINE_SLOTS = 5;
  public static final int INPUT_SLOTS = 3;
  public static final int OUTPUT_SLOT = 3;
  public static final int DISCHARGE_SLOT = 4;

  private static final int PLAYER_INVENTORY_Y = 102;
  private static final int HOTBAR_Y = 160;
  private static final int ENERGY_LOW_INDEX = 0;
  private static final int ENERGY_HIGH_INDEX = 1;
  private static final int PROGRESS_INDEX = 2;
  private static final int MAX_PROGRESS_INDEX = 3;
  private static final int DATA_COUNT = 4;

  private final AlloyingMachineBlockEntity blockEntity;
  private final long capacity;
  private final ContainerData data;
  private final boolean clientSide;
  private int energyLow;
  private int energyHigh;
  private int progress;
  private int maxProgress = AlloyingMachineBlockEntity.MAX_PROGRESS;

  public AlloyingMachineMenu(
    int containerId,
    Inventory playerInventory,
    AlloyingMachineBlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity,
      AlloyingMachineBlockEntity.CAPACITY
    );
  }

  public AlloyingMachineMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    AlloyingMachineBlockEntity blockEntity,
    long capacity
  ) {
    super(ModMenuType.ALLOYING_MACHINE.get(), containerId);
    checkContainerSize(container, MACHINE_SLOTS);
    this.blockEntity = blockEntity;
    this.capacity = capacity;
    this.clientSide = playerInventory.player.level().isClientSide;

    if (blockEntity != null) {
      long stored = blockEntity.getEnergyStoredLong();
      this.energyLow = (int) stored;
      this.energyHigh = (int) (stored >>> 32);
      this.progress = blockEntity.getProgress();
      this.maxProgress = blockEntity.getMaxProgress();

      this.data = new ContainerData() {
        @Override
        public int get(int index) {
          if (index == ENERGY_LOW_INDEX) {
            return (int) blockEntity.getEnergyStoredLong();
          }
          if (index == ENERGY_HIGH_INDEX) {
            return (int) (blockEntity.getEnergyStoredLong() >>> 32);
          }
          if (index == PROGRESS_INDEX) {
            return clientSide ? progress : blockEntity.getProgress();
          }
          return clientSide
            ? maxProgress
            : blockEntity.getMaxProgress();
        }

        @Override
        public void set(int index, int value) {
          if (index == ENERGY_LOW_INDEX) {
            energyLow = value;
            blockEntity.setEnergyStored(join(energyLow, energyHigh));
          } else if (index == ENERGY_HIGH_INDEX) {
            energyHigh = value;
            blockEntity.setEnergyStored(join(energyLow, energyHigh));
          } else if (index == PROGRESS_INDEX) {
            progress = value;
          } else {
            maxProgress = value;
          }
        }

        @Override
        public int getCount() {
          return DATA_COUNT;
        }
      };
    } else {
      this.data = new SimpleContainerData(DATA_COUNT);
    }

    addSlot(new Slot(container, 0, 55, 17) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return true;
      }
    });
    addSlot(new Slot(container, 1, 34, 38) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return true;
      }
    });
    addSlot(new Slot(container, 2, 55, 59) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return true;
      }
    });
    addSlot(new Slot(container, OUTPUT_SLOT, 99, 38) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return false;
      }
    });
    addSlot(new Slot(container, DISCHARGE_SLOT, 146, 38) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return canDischarge(stack);
      }
    });

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

    addDataSlots(this.data);
  }

  public static AlloyingMachineMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    long capacity = buffer.readLong();
    AlloyingMachineBlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof AlloyingMachineBlockEntity machine
        ? machine
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new AlloyingMachineMenu(
      containerId,
      playerInventory,
      container,
      blockEntity,
      capacity
    );
  }

  public long getEnergy() {
    return join(this.data.get(ENERGY_LOW_INDEX), this.data.get(ENERGY_HIGH_INDEX));
  }

  public long getCapacity() {
    return this.capacity;
  }

  public int getProgress() {
    return this.data.get(PROGRESS_INDEX);
  }

  public int getMaxProgress() {
    return this.data.get(MAX_PROGRESS_INDEX);
  }

  @Override
  public boolean stillValid(Player player) {
    return (
      this.blockEntity != null &&
      Container.stillValidBlockEntity(this.blockEntity, player)
    );
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    Slot slot = this.slots.get(index);
    if (slot == null || !slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    ItemStack stack = slot.getItem();
    ItemStack moved = stack.copy();

    if (index < MACHINE_SLOTS) {
      if (!moveItemStackTo(stack, MACHINE_SLOTS, this.slots.size(), true)) {
        return ItemStack.EMPTY;
      }
    } else {
      boolean movedToCharge =
        canDischarge(stack) &&
        moveItemStackTo(stack, DISCHARGE_SLOT, DISCHARGE_SLOT + 1, false);
      if (
        !movedToCharge &&
        !moveItemStackTo(stack, 0, INPUT_SLOTS, false)
      ) {
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

  private static boolean canDischarge(ItemStack stack) {
    IEnergyStorage storage = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    return storage != null && storage.canExtract();
  }

  private static long join(int low, int high) {
    return ((long) high << 32) | (low & 0xFFFFFFFFL);
  }
}
