/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.menu;

import com.aestastel.coraconic.block.SolarGeneratorMk0BlockEntity;
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
 * 太阳能发电机 MK0 菜单：一个输出槽加玩家背包。
 *
 * <p>输出槽只接受具有物品 FE 能力且能够接收能量的物品。缓存优先向该槽位
 * 中的物品充能，剩余能量由外部面抽取。
 *
 * <p>Menu for Solar Generator MK0: one output slot plus the player inventory.
 * The slot accepts energy-receiving items; the buffer charges that item before
 * external sides consume the remainder.
 */
public class SolarGeneratorMk0Menu extends AbstractContainerMenu {

  public static final int MACHINE_SLOTS = 1;
  private static final int PLAYER_INVENTORY_Y = 102;
  private static final int HOTBAR_Y = 160;

  private final SolarGeneratorMk0BlockEntity blockEntity;
  private final ContainerData data;

  public SolarGeneratorMk0Menu(
    int containerId,
    Inventory playerInventory,
    SolarGeneratorMk0BlockEntity blockEntity
  ) {
    this(containerId, playerInventory, blockEntity, blockEntity);
  }

  public SolarGeneratorMk0Menu(
    int containerId,
    Inventory playerInventory,
    Container container,
    SolarGeneratorMk0BlockEntity blockEntity
  ) {
    super(ModMenuType.SOLAR_GENERATOR_MK0.get(), containerId);
    checkContainerSize(container, MACHINE_SLOTS);
    this.blockEntity = blockEntity;

    if (blockEntity != null) {
      this.data = new ContainerData() {
        @Override
        public int get(int index) {
          return blockEntity.getEnergyStored();
        }

        @Override
        public void set(int index, int value) {
          blockEntity.setEnergyStored(value);
        }

        @Override
        public int getCount() {
          return 1;
        }
      };
    } else {
      this.data = new SimpleContainerData(1);
    }

    addSlot(
      new Slot(container, SolarGeneratorMk0BlockEntity.OUTPUT_SLOT, 80, 35) {
        @Override
        public boolean mayPlace(ItemStack stack) {
          return canCharge(stack);
        }
      }
    );

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

  public static SolarGeneratorMk0Menu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    SolarGeneratorMk0BlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof SolarGeneratorMk0BlockEntity generator
        ? generator
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new SolarGeneratorMk0Menu(
      containerId,
      playerInventory,
      container,
      blockEntity
    );
  }

  /** {@return 当前缓存（FE）} */
  public int getEnergy() {
    return this.data.get(0);
  }

  /** {@return 缓存容量（FE）} */
  public int getCapacity() {
    return SolarGeneratorMk0BlockEntity.CAPACITY;
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
    } else if (!canCharge(stack) || !moveItemStackTo(stack, 0, 1, false)) {
      return ItemStack.EMPTY;
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
