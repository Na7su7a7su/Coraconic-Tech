/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.blockentity.SmeltingMetallurgyMachineBlockEntity;
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
 * 熔炼冶金炉 MK1 菜单：一个原料槽、一个成品槽、一个电源槽和玩家背包。
 *
 * <p>Smelting-Metallurgy Machine MK1 menu with one input slot, one output slot,
 * one power slot, and the player inventory.
 */
public class SmeltingMetallurgyMachineMenu extends AbstractContainerMenu {

  public static final int MACHINE_SLOTS = 3;
  public static final int INPUT_SLOT = 0;
  public static final int OUTPUT_SLOT = 1;
  public static final int POWER_SLOT = 2;

  private static final int PLAYER_INVENTORY_Y = 102;
  private static final int HOTBAR_Y = 160;
  private static final int ENERGY_LOW_INDEX = 0;
  private static final int ENERGY_HIGH_INDEX = 1;
  private static final int PROGRESS_INDEX = 2;
  private static final int MAX_PROGRESS_INDEX = 3;
  private static final int DATA_COUNT = 4;

  private final SmeltingMetallurgyMachineBlockEntity blockEntity;
  private final long capacity;
  private final ContainerData data;
  private final boolean clientSide;
  private int energyLow;
  private int energyHigh;
  private int progress;
  private int maxProgress =
    SmeltingMetallurgyMachineBlockEntity.DEFAULT_COOK_TIME;

  public SmeltingMetallurgyMachineMenu(
    int containerId,
    Inventory playerInventory,
    SmeltingMetallurgyMachineBlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity,
      SmeltingMetallurgyMachineBlockEntity.CAPACITY
    );
  }

  public SmeltingMetallurgyMachineMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    SmeltingMetallurgyMachineBlockEntity blockEntity,
    long capacity
  ) {
    super(ModMenuType.SMELTING_METALLURGY_MACHINE.get(), containerId);
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
            return clientSide
              ? energyLow
              : (int) blockEntity.getEnergyStoredLong();
          }
          if (index == ENERGY_HIGH_INDEX) {
            return clientSide
              ? energyHigh
              : (int) (blockEntity.getEnergyStoredLong() >>> 32);
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
            if (!clientSide) {
              blockEntity.setEnergyStored(join(energyLow, energyHigh));
            }
          } else if (index == ENERGY_HIGH_INDEX) {
            energyHigh = value;
            if (!clientSide) {
              blockEntity.setEnergyStored(join(energyLow, energyHigh));
            }
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

    addSlot(new Slot(container, INPUT_SLOT, 37, 38) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return true;
      }
    });
    addSlot(new Slot(container, OUTPUT_SLOT, 102, 38) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return false;
      }
    });
    addSlot(new Slot(container, POWER_SLOT, 148, 72) {
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

  public static SmeltingMetallurgyMachineMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    long capacity = buffer.readLong();
    SmeltingMetallurgyMachineBlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof SmeltingMetallurgyMachineBlockEntity machine
        ? machine
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new SmeltingMetallurgyMachineMenu(
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
      boolean movedToPower =
        canDischarge(stack) &&
        moveItemStackTo(stack, POWER_SLOT, POWER_SLOT + 1, false);
      if (
        !movedToPower &&
        !moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)
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
