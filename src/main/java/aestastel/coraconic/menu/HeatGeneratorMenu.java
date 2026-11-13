/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.block.HeatGeneratorMode;
import aestastel.coraconic.blockentity.FuelFluidHelper;
import aestastel.coraconic.blockentity.HeatGeneratorBlockEntity;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * 热力发电机 MK0 菜单。
 *
 * <p>输出槽只接受可接收能量的物品；燃料槽只接受熔炉燃料。
 *
 * <p>Heat Generator MK0 menu. The output slot only accepts energy-receiving
 * items, and the fuel slot only accepts furnace fuel.
 */
public class HeatGeneratorMenu extends AbstractContainerMenu {

  public static final int MACHINE_SLOTS = 2;
  public static final int OUTPUT_SLOT = 0;
  public static final int FUEL_SLOT = 1;

  private static final int PLAYER_INVENTORY_Y = 102;
  private static final int HOTBAR_Y = 160;
  private static final int ENERGY_INDEX = 0;
  private static final int HEAT_INDEX = 1;
  private static final int GENERATION_INDEX = 2;
  private static final int MODE_INDEX = 3;
  private static final int LAVA_COUNT_INDEX = 4;
  private static final int DATA_COUNT = 5;

  private final HeatGeneratorBlockEntity blockEntity;
  private final ContainerData data;
  private final boolean clientSide;
  private int energy;
  private int heat;
  private int currentGeneration;
  private HeatGeneratorMode currentMode = HeatGeneratorMode.NULL;
  private int adjacentLavaCount;

  public HeatGeneratorMenu(
    int containerId,
    Inventory playerInventory,
    HeatGeneratorBlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity
    );
  }

  public HeatGeneratorMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    HeatGeneratorBlockEntity blockEntity
  ) {
    super(ModMenuType.HEAT_GENERATOR.get(), containerId);
    checkContainerSize(container, MACHINE_SLOTS);
    this.blockEntity = blockEntity;
    this.clientSide = playerInventory.player.level().isClientSide;

    if (blockEntity != null) {
      this.energy = (int) blockEntity.getEnergyStoredLong();
      this.heat = blockEntity.getHeat();
      this.currentGeneration = blockEntity.getCurrentGeneration();
      this.currentMode = blockEntity.getCurrentMode();
      this.adjacentLavaCount = blockEntity.getAdjacentLavaCount();

      this.data = new ContainerData() {
        @Override
        public int get(int index) {
          if (index == ENERGY_INDEX) {
            return clientSide
              ? energy
              : (int) blockEntity.getEnergyStoredLong();
          }
          if (index == HEAT_INDEX) {
            return clientSide ? heat : blockEntity.getHeat();
          }
          if (index == GENERATION_INDEX) {
            return clientSide
              ? currentGeneration
              : blockEntity.getCurrentGeneration();
          }
          if (index == MODE_INDEX) {
            return (
              clientSide ? currentMode : blockEntity.getCurrentMode()
            ).ordinal();
          }
          return clientSide
            ? adjacentLavaCount
            : blockEntity.getAdjacentLavaCount();
        }

        @Override
        public void set(int index, int value) {
          if (index == ENERGY_INDEX) {
            energy = value;
            if (!clientSide) {
              blockEntity.setEnergyStored(value);
            }
          } else if (index == HEAT_INDEX) {
            heat = value;
          } else if (index == GENERATION_INDEX) {
            currentGeneration = value;
          } else if (index == MODE_INDEX) {
            currentMode = HeatGeneratorMode.byOrdinal(value);
          } else {
            adjacentLavaCount = value;
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

    addSlot(new Slot(container, OUTPUT_SLOT, 146, 39) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return canCharge(stack);
      }
    });
    addSlot(new Slot(container, FUEL_SLOT, 146, 76) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return isFuel(stack);
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

  public static HeatGeneratorMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    HeatGeneratorBlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof HeatGeneratorBlockEntity generator
        ? generator
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new HeatGeneratorMenu(
      containerId,
      playerInventory,
      container,
      blockEntity
    );
  }

  public long getEnergy() {
    return this.data.get(ENERGY_INDEX);
  }

  public long getCapacity() {
    return HeatGeneratorBlockEntity.CAPACITY;
  }

  public int getHeat() {
    return this.data.get(HEAT_INDEX);
  }

  public int getHeatCapacity() {
    return HeatGeneratorBlockEntity.HEAT_CAPACITY;
  }

  public int getCurrentGeneration() {
    return this.data.get(GENERATION_INDEX);
  }

  public HeatGeneratorMode getMode() {
    return HeatGeneratorMode.byOrdinal(this.data.get(MODE_INDEX));
  }

  public int getAdjacentLavaCount() {
    return this.data.get(LAVA_COUNT_INDEX);
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
      boolean movedToOutput =
        canCharge(stack) &&
        moveItemStackTo(stack, OUTPUT_SLOT, OUTPUT_SLOT + 1, false);
      if (
        !movedToOutput &&
        !(
          isFuel(stack) &&
          moveItemStackTo(stack, FUEL_SLOT, FUEL_SLOT + 1, false)
        )
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

  private static boolean isFuel(ItemStack stack) {
    return (
      !stack.isEmpty() &&
      (
        stack.getBurnTime(RecipeType.SMELTING) > 0 ||
        FuelFluidHelper.hasFuelFluid(stack)
      )
    );
  }
}
