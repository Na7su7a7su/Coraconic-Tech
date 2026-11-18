/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.menu;

import aestastel.coraconic.common.blockentity.InstrumentAssemblingMachineBlockEntity;
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
 * 器械组合仪 菜单：五个原料槽、一个成品槽、一个充电槽和玩家背包。
 *
 * <p>能量、加工进度与最大进度通过数据槽同步；当前没有配方，因此进度恒为
 * `0`，为后续配方实现预留同步字段。
 *
 * <p>Instrument Assembling Machine menu with five input slots, one output slot,
 * one charging slot, and the player inventory. Energy, progress, and maximum
 * progress are synchronized through data slots; progress stays at `0` until
 * recipes are implemented.
 */
public class InstrumentAssemblingMachineMenu
  extends AbstractContainerMenu
{

  public static final int MACHINE_SLOTS = 7;
  public static final int INPUT_SLOTS = 5;
  public static final int OUTPUT_SLOT = 5;
  public static final int CHARGE_SLOT = 6;

  private static final int PLAYER_INVENTORY_Y = 102;
  private static final int HOTBAR_Y = 160;
  private static final int ENERGY_INDEX = 0;
  private static final int PROGRESS_INDEX = 1;
  private static final int MAX_PROGRESS_INDEX = 2;
  private static final int DATA_COUNT = 3;

  private final InstrumentAssemblingMachineBlockEntity blockEntity;
  private final long capacity;
  private final ContainerData data;
  private final boolean clientSide;
  private int energy;
  private int progress;
  private int maxProgress;

  public InstrumentAssemblingMachineMenu(
    int containerId,
    Inventory playerInventory,
    InstrumentAssemblingMachineBlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity,
      InstrumentAssemblingMachineBlockEntity.CAPACITY
    );
  }

  public InstrumentAssemblingMachineMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    InstrumentAssemblingMachineBlockEntity blockEntity,
    long capacity
  ) {
    super(ModMenuType.INSTRUMENT_ASSEMBLING_MACHINE.get(), containerId);
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

    addSlot(new Slot(container, 0, 35, 14));
    addSlot(new Slot(container, 1, 59, 8));
    addSlot(new Slot(container, 2, 28, 38));
    addSlot(new Slot(container, 3, 35, 62));
    addSlot(new Slot(container, 4, 59, 68));
    addSlot(new Slot(container, OUTPUT_SLOT, 103, 38) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return false;
      }
    });
    addSlot(new Slot(container, CHARGE_SLOT, 148, 72) {
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

  public static InstrumentAssemblingMachineMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    InstrumentAssemblingMachineBlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof InstrumentAssemblingMachineBlockEntity machine
        ? machine
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new InstrumentAssemblingMachineMenu(
      containerId,
      playerInventory,
      container,
      blockEntity,
      InstrumentAssemblingMachineBlockEntity.CAPACITY
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
      boolean movedToCharge =
        canCharge(stack) &&
        moveItemStackTo(stack, CHARGE_SLOT, CHARGE_SLOT + 1, false);
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

  private static boolean canCharge(ItemStack stack) {
    IEnergyStorage storage = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    return storage != null && storage.canReceive();
  }
}
