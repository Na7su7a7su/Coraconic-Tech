/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.blockentity.ResonanceCrystallizingMachineBlockEntity;
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
 * 共振晶化仪 菜单：两个原料槽、一个成品槽、一个能源槽和玩家背包。
 *
 * <p>能量、加工进度与最大进度通过数据槽同步。
 *
 * <p>Resonance Crystallizing Machine menu with two input slots, one output
 * slot, one energy slot, and the player inventory. Energy, progress, and maximum
 * progress are synchronized through data slots.
 */
public class ResonanceCrystallizingMachineMenu
  extends AbstractContainerMenu
{

  public static final int MACHINE_SLOTS = 4;
  public static final int INPUT_SLOTS = 2;
  public static final int OUTPUT_SLOT = 2;
  public static final int ENERGY_SLOT = 3;

  private static final int PLAYER_INVENTORY_Y = 102;
  private static final int HOTBAR_Y = 160;
  private static final int ENERGY_INDEX = 0;
  private static final int PROGRESS_INDEX = 1;
  private static final int MAX_PROGRESS_INDEX = 2;
  private static final int DATA_COUNT = 3;

  private final ResonanceCrystallizingMachineBlockEntity blockEntity;
  private final long capacity;
  private final ContainerData data;
  private final boolean clientSide;
  private int energy;
  private int progress;
  private int maxProgress;

  public ResonanceCrystallizingMachineMenu(
    int containerId,
    Inventory playerInventory,
    ResonanceCrystallizingMachineBlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity,
      ResonanceCrystallizingMachineBlockEntity.CAPACITY
    );
  }

  public ResonanceCrystallizingMachineMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    ResonanceCrystallizingMachineBlockEntity blockEntity,
    long capacity
  ) {
    super(ModMenuType.RESONANCE_CRYSTALLIZING_MACHINE.get(), containerId);
    checkContainerSize(container, MACHINE_SLOTS);
    this.blockEntity = blockEntity;
    this.capacity = capacity;
    this.clientSide = playerInventory.player.level().isClientSide;

    if (blockEntity != null) {
      this.energy = (int) blockEntity.getEnergyStoredLong();
      this.progress = blockEntity.getProgress();
      this.maxProgress = blockEntity.getMaxProgress();

      this.data = new ContainerData() {
        @Override
        public int get(int index) {
          if (index == ENERGY_INDEX) {
            return clientSide
              ? energy
              : (int) blockEntity.getEnergyStoredLong();
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
          if (index == ENERGY_INDEX) {
            energy = value;
            if (!clientSide) {
              blockEntity.setEnergyStored(value);
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

    addSlot(new Slot(container, 0, 37, 26) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return true;
      }
    });
    addSlot(new Slot(container, 1, 37, 50) {
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
    addSlot(new Slot(container, ENERGY_SLOT, 148, 72) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return canCharge(stack);
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

  public static ResonanceCrystallizingMachineMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    ResonanceCrystallizingMachineBlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof ResonanceCrystallizingMachineBlockEntity machine
        ? machine
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new ResonanceCrystallizingMachineMenu(
      containerId,
      playerInventory,
      container,
      blockEntity,
      ResonanceCrystallizingMachineBlockEntity.CAPACITY
    );
  }

  public long getEnergy() {
    return this.data.get(ENERGY_INDEX);
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
      boolean movedToEnergy =
        canCharge(stack) &&
        moveItemStackTo(stack, ENERGY_SLOT, ENERGY_SLOT + 1, false);
      if (
        !movedToEnergy &&
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

  private static boolean canCharge(ItemStack stack) {
    IEnergyStorage storage = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    return storage != null && storage.canReceive();
  }
}
