/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.menu;

import aestastel.coraconic.block.GeomagneticGeneratorMode;
import aestastel.coraconic.block.GeneratorMode;
import aestastel.coraconic.block.SolarGeneratorBlockEntity;
import aestastel.coraconic.block.SolarGeneratorMode;
import aestastel.coraconic.block.SolarGeneratorTier;
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
 * 太阳能发电机 MK0–MK3 的共用菜单。
 *
 * <p>输出槽只接受具有物品 FE 能力且能够接收能量的物品。缓存优先向该槽位
 * 中的物品充能，剩余能量由外部面抽取。容量由打开界面时服务端发送，避免
 * 每个等级硬编码在菜单类中。
 *
 * <p>Shared solar generator menu for MK0–MK3. The output slot accepts only
 * energy-receiving items; the buffer charges that item before external sides
 * consume the remainder. Capacity is sent by the server when the menu opens.
 */
public class SolarGeneratorMenu
  extends AbstractContainerMenu
  implements EnergyFaceMenu
{

  public static final int MACHINE_SLOTS = 1;
  private static final int PLAYER_INVENTORY_Y = 102;
  private static final int HOTBAR_Y = 160;
  private static final int FACE_MODE_DATA_START = 1;
  private static final int RATE_DATA_INDEX = 7;
  private static final int MODE_DATA_INDEX = 8;
  private static final int DATA_COUNT = 9;

  private final SolarGeneratorBlockEntity blockEntity;
  private final int capacity;
  private final SolarGeneratorTier tier;
  private final ContainerData data;
  private final boolean clientSide;
  private final boolean geomagnetic;
  private final EnergyFaceMode[] syncedFaceModes =
    new EnergyFaceMode[FACE_MODE_COUNT];
  private int currentRate;
  private GeneratorMode currentMode = SolarGeneratorMode.SOLAR;

  public SolarGeneratorMenu(
    int containerId,
    Inventory playerInventory,
    SolarGeneratorBlockEntity blockEntity
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity.isGeomagnetic()
    );
  }

  public SolarGeneratorMenu(
    int containerId,
    Inventory playerInventory,
    SolarGeneratorBlockEntity blockEntity,
    boolean geomagnetic
  ) {
    this(
      containerId,
      playerInventory,
      blockEntity,
      blockEntity,
      blockEntity.getCapacity(),
      blockEntity.getTier(),
      geomagnetic
    );
  }

  public SolarGeneratorMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    SolarGeneratorBlockEntity blockEntity,
    int capacity,
    SolarGeneratorTier tier
  ) {
    this(
      containerId,
      playerInventory,
      container,
      blockEntity,
      capacity,
      tier,
      blockEntity != null && blockEntity.isGeomagnetic()
    );
  }

  public SolarGeneratorMenu(
    int containerId,
    Inventory playerInventory,
    Container container,
    SolarGeneratorBlockEntity blockEntity,
    int capacity,
    SolarGeneratorTier tier,
    boolean geomagnetic
  ) {
    super(ModMenuType.SOLAR_GENERATOR.get(), containerId);
    checkContainerSize(container, MACHINE_SLOTS);
    this.blockEntity = blockEntity;
    this.capacity = capacity;
    this.tier = tier;
    this.clientSide = playerInventory.player.level().isClientSide;
    this.geomagnetic = geomagnetic;
    if (blockEntity != null) {
      this.currentRate = blockEntity.getCurrentGeneration();
      this.currentMode = blockEntity.getCurrentMode();
    } else if (geomagnetic) {
      this.currentMode = GeomagneticGeneratorMode.NULL;
    }
    for (Direction direction : Direction.values()) {
      this.syncedFaceModes[direction.get3DDataValue()] =
        blockEntity != null
          ? blockEntity.getFaceMode(direction)
          : EnergyFaceMode.OUTPUT;
    }

    if (blockEntity != null) {
      this.data = new ContainerData() {
        @Override
        public int get(int index) {
          if (index == RATE_DATA_INDEX) {
            return clientSide
              ? currentRate
              : blockEntity.getCurrentGeneration();
          }
          if (index == MODE_DATA_INDEX) {
            return (
              clientSide ? currentMode : blockEntity.getCurrentMode()
            ).ordinal();
          }
          if (index >= FACE_MODE_DATA_START) {
            Direction side = Direction.from3DDataValue(
              index - FACE_MODE_DATA_START
            );
            if (!clientSide) {
              return blockEntity.getFaceMode(side).ordinal();
            }
            return syncedFaceModes[side.get3DDataValue()].ordinal();
          }

          return blockEntity.getEnergyStored();
        }

        @Override
        public void set(int index, int value) {
          if (index == RATE_DATA_INDEX) {
            currentRate = value;
            return;
          }
          if (index == MODE_DATA_INDEX) {
            currentMode = geomagnetic
              ? GeomagneticGeneratorMode.byOrdinal(value)
              : SolarGeneratorMode.byOrdinal(value);
            return;
          }
          if (index >= FACE_MODE_DATA_START) {
            Direction side = Direction.from3DDataValue(
              index - FACE_MODE_DATA_START
            );
            syncedFaceModes[side.get3DDataValue()] =
              EnergyFaceMode.byOrdinal(value);
            return;
          }

          blockEntity.setEnergyStored(value);
        }

        @Override
        public int getCount() {
          return DATA_COUNT;
        }
      };
    } else {
      this.data = new SimpleContainerData(DATA_COUNT);
    }

    addSlot(
      new Slot(
        container,
        SolarGeneratorBlockEntity.OUTPUT_SLOT,
        146,
        39
      ) {
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

  public static SolarGeneratorMenu fromNetwork(
    int containerId,
    Inventory playerInventory,
    RegistryFriendlyByteBuf buffer
  ) {
    BlockPos pos = buffer.readBlockPos();
    int capacity = buffer.readVarInt();
    SolarGeneratorTier tier = SolarGeneratorTier.byOrdinal(
      buffer.readVarInt()
    );
    boolean geomagnetic = buffer.readBoolean();
    SolarGeneratorBlockEntity blockEntity =
      playerInventory.player.level().getBlockEntity(pos)
        instanceof SolarGeneratorBlockEntity generator
        ? generator
        : null;
    Container container = blockEntity != null
      ? blockEntity
      : new SimpleContainer(MACHINE_SLOTS);
    return new SolarGeneratorMenu(
      containerId,
      playerInventory,
      container,
      blockEntity,
      capacity,
      tier,
      geomagnetic
    );
  }

  /** {@return 当前缓存（FE）} */
  public int getEnergy() {
    return this.data.get(0);
  }

  /** {@return 当前等级缓存容量（FE）} */
  public int getCapacity() {
    return this.capacity;
  }

  /** {@return 当前太阳能等级} */
  public SolarGeneratorTier getTier() {
    return this.tier;
  }

  /** {@return 当前规则发电速率} */
  public int getCurrentRate() {
    return this.currentRate;
  }

  /** {@return 当前运行模式} */
  public GeneratorMode getMode() {
    return this.currentMode;
  }

  /** {@return 是否使用地磁发电机的 CLI 来源标识} */
  public boolean isGeomagnetic() {
    return this.geomagnetic;
  }

  @Override
  public EnergyFaceMode getFaceMode(Direction side) {
    return EnergyFaceMode.byOrdinal(
      data.get(FACE_MODE_DATA_START + side.get3DDataValue())
    );
  }

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
