/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.blockentity.AlloyingMachineMk2BlockEntity;
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
 * 合金仪 MK2 菜单：三条独立生产线、一个共享放电槽和玩家背包。
 *
 * <p>能量与三条生产线的进度通过数据槽同步。
 *
 * <p>Alloying Machine MK2 menu with three independent production lines, one
 * shared discharge slot, and the player inventory. Energy and all three
 * production-line progress values are synchronized through data slots.
 */
public class AlloyingMachineMk2Menu extends AbstractContainerMenu {

  public static final int LINE_COUNT = 3;
  public static final int INPUT_SLOTS = 9;
  public static final int OUTPUT_SLOT_START = 9;
  public static final int DISCHARGE_SLOT = 12;
  public static final int MACHINE_SLOTS = 13;

  private static final int PLAYER_INVENTORY_Y = 110;
  private static final int HOTBAR_Y = 168;
  private static final int ENERGY_LOW_INDEX = 0;
  private static final int ENERGY_HIGH_INDEX = 1;
  private static final int PROGRESS_START_INDEX = 2;
  private static final int MAX_PROGRESS_START_INDEX = 5;
  private static final int DATA_COUNT = 8;

  private final AlloyingMachineMk2BlockEntity blockEntity;
  private final long capacity;
  private final ContainerData data;
  private final boolean clientSide;
  private final int[] progress = new int[LINE_COUNT];
  private final int[] maxProgress = new int[LINE_COUNT];
  private int energyLow;
  private int energyHigh;

  public AlloyingMachineMk2Menu(
    int containerId,
    Inventory playerInventory,
    AlloyingMachineMk2BlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity,
      AlloyingMachineMk2BlockEntity.CAPACITY
    );
  }

  public AlloyingMachineMk2Menu(
    int containerId,
    Inventory playerInventory,
    Container container,
    AlloyingMachineMk2BlockEntity blockEntity,
    long capacity
  ) {
    super(ModMenuType.ALLOYING_MACHINE_MK2.get(), containerId);
    checkContainerSize(container, MACHINE_SLOTS);
    this.blockEntity = blockEntity;
    this.capacity = capacity;
    this.clientSide = playerInventory.player.level().isClientSide;

    if (blockEntity != null) {
      long stored = blockEntity.getEnergyStoredLong();
      this.energyLow = (int) stored;
      this.energyHigh = (int) (stored >>> 32);
      for (int lineIndex = 0; lineIndex < LINE_COUNT; lineIndex++) {
        this.progress[lineIndex] = blockEntity.getProgress(lineIndex);
        this.maxProgress[lineIndex] = blockEntity.getMaxProgress(lineIndex);
      }

      this.data = new ContainerData() {
        @Override
        public int get(int index) {
          if (index == ENERGY_LOW_INDEX) {
            return clientSide
              ? energyLow
              : (int) blockEntity.getEnergyStoredLong();
          }
          if (index == ENERGY_HIGH_INDEX) {
            return clientSide
              ? energyHigh
              : (int) (blockEntity.getEnergyStoredLong() >>> 32);
          }
          if (
            index >= PROGRESS_START_INDEX &&
            index < PROGRESS_START_INDEX + LINE_COUNT
          ) {
            int lineIndex = index - PROGRESS_START_INDEX;
            return clientSide
              ? progress[lineIndex]
              : blockEntity.getProgress(lineIndex);
          }
          int lineIndex = index - MAX_PROGRESS_START_INDEX;
          return clientSide
            ? maxProgress[lineIndex]
            : blockEntity.getMaxProgress(lineIndex);
        }

        @Override
        public void set(int index, int value) {
          if (index == ENERGY_LOW_INDEX) {
            energyLow = value;
            if (!clientSide) {
              blockEntity.setEnergyStored(join(energyLow, energyHigh));
            }
          } else if (index == ENERGY_HIGH_INDEX) {
            energyHigh = value;
            if (!clientSide) {
              blockEntity.setEnergyStored(join(energyLow, energyHigh));
            }
          } else if (
            index >= PROGRESS_START_INDEX &&
            index < PROGRESS_START_INDEX + LINE_COUNT
          ) {
            progress[index - PROGRESS_START_INDEX] = value;
          } else if (
            index >= MAX_PROGRESS_START_INDEX &&
            index < MAX_PROGRESS_START_INDEX + LINE_COUNT
          ) {
            maxProgress[index - MAX_PROGRESS_START_INDEX] = value;
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

    for (int lineIndex = 0; lineIndex < LINE_COUNT; lineIndex++) {
      int inputBase = lineIndex * 3;
      int rowY = 19 + lineIndex * 28;
      addSlot(new Slot(container, inputBase, 22, rowY) {
        @Override
        public boolean mayPlace(ItemStack stack) {
          return true;
        }
      });
      addSlot(new Slot(container, inputBase + 1, 42, rowY) {
        @Override
        public boolean mayPlace(ItemStack stack) {
          return true;
        }
      });
      addSlot(new Slot(container, inputBase + 2, 62, rowY) {
        @Override
        public boolean mayPlace(ItemStack stack) {
          return true;
        }
      });
      addSlot(
        new Slot(container, OUTPUT_SLOT_START + lineIndex, 118, rowY) {
          @Override
          public boolean mayPlace(ItemStack stack) {
            return false;
          }
        }
      );
    }

    addSlot(new Slot(container, DISCHARGE_SLOT, 148, 74) {
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

  public static AlloyingMachineMk2Menu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    long capacity = buffer.readLong();
    AlloyingMachineMk2BlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof AlloyingMachineMk2BlockEntity machine
        ? machine
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new AlloyingMachineMk2Menu(
      containerId,
      playerInventory,
      container,
      blockEntity,
      capacity
    );
  }

  public long getEnergy() {
    return join(
      this.data.get(ENERGY_LOW_INDEX),
      this.data.get(ENERGY_HIGH_INDEX)
    );
  }

  public long getCapacity() {
    return this.capacity;
  }

  public int getProgress(int lineIndex) {
    return this.data.get(PROGRESS_START_INDEX + lineIndex);
  }

  public int getMaxProgress(int lineIndex) {
    return this.data.get(MAX_PROGRESS_START_INDEX + lineIndex);
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
      boolean movedToDischarge =
        canDischarge(stack) &&
        moveItemStackTo(stack, DISCHARGE_SLOT, DISCHARGE_SLOT + 1, false);
      if (
        !movedToDischarge &&
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
