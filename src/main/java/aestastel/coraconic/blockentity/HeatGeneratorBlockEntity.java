/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.blockentity;

import aestastel.coraconic.block.HeatGeneratorMode;
import aestastel.coraconic.energy.ModEnergyStorage;
import aestastel.coraconic.menu.HeatGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 热力发电机 MK0 方块实体。
 *
 * <p>燃料槽中的熔炉燃料会被转换为热量，每 1 tick 燃烧时间等于 1 单位热量。
 * 主动模式每 tick 消耗 40 单位热量并产生 80 FE。
 *
 * <p>Heat Generator MK0 block entity. Furnace fuel in the fuel slot is
 * converted into heat, with 1 tick of burn time equal to 1 heat unit. The
 * Initiactive mode consumes 40 heat and generates 80 FE per tick.
 */
public class HeatGeneratorBlockEntity
  extends BlockEntity
  implements Container, MenuProvider
{

  public static final int OUTPUT_SLOT = 0;
  public static final int FUEL_SLOT = 1;
  public static final int SLOT_COUNT = 2;
  public static final long CAPACITY = 96_000L;
  public static final long MAX_TRANSFER = 600L;
  public static final int ITEM_CHARGE_RATE = 600;
  public static final int HEAT_CAPACITY = 640_000;
  public static final int HEAT_PER_TICK = 40;
  public static final int FE_PER_HEAT = 2;
  public static final int GENERATION_PER_TICK = HEAT_PER_TICK * FE_PER_HEAT;

  private final ModEnergyStorage energyStorage = new ModEnergyStorage(
    CAPACITY,
    MAX_TRANSFER,
    MAX_TRANSFER,
    this::setChanged
  );

  private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
    @Override
    protected void onContentsChanged(int slot) {
      HeatGeneratorBlockEntity.this.setChanged();
    }
  };

  private int heat;
  private int currentGeneration;
  private HeatGeneratorMode currentMode = HeatGeneratorMode.NULL;

  public HeatGeneratorBlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state
  ) {
    super(type, pos, state);
  }

  /**
   * 服务端 tick：燃料转热、主动发电、输出槽充能。
   *
   * <p>Server tick: converts fuel, generates energy, and charges the output
   * slot.
   */
  public void tick() {
    if (this.level == null || this.level.isClientSide) {
      return;
    }
    convertFuel();
    generateEnergy();
    chargeOutputItem();
    setChanged();
  }

  private void convertFuel() {
    ItemStack stack = this.inventory.getStackInSlot(FUEL_SLOT);
    if (stack.isEmpty()) {
      return;
    }
    int burnTime = stack.getBurnTime(RecipeType.SMELTING);
    if (burnTime <= 0) {
      return;
    }
    if (this.heat + burnTime > HEAT_CAPACITY) {
      return;
    }

    this.inventory.extractItem(FUEL_SLOT, 1, false);
    this.heat += burnTime;
  }

  private void generateEnergy() {
    this.currentGeneration = 0;
    if (this.heat < HEAT_PER_TICK) {
      this.currentMode = HeatGeneratorMode.NULL;
      return;
    }
    if (this.energyStorage.getEnergyStoredLong() > CAPACITY - GENERATION_PER_TICK) {
      this.currentMode = HeatGeneratorMode.NULL;
      return;
    }

    this.heat -= HEAT_PER_TICK;
    this.energyStorage.receiveEnergy(GENERATION_PER_TICK, false);
    this.currentGeneration = GENERATION_PER_TICK;
    this.currentMode = HeatGeneratorMode.INITIACTIVE;
  }

  private void chargeOutputItem() {
    ItemStack stack = this.inventory.getStackInSlot(OUTPUT_SLOT);
    if (stack.isEmpty()) {
      return;
    }
    IEnergyStorage itemEnergy = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    if (itemEnergy == null || !itemEnergy.canReceive()) {
      return;
    }

    int available = (int) Math.min(
      Math.min(
        this.energyStorage.getEnergyStoredLong(),
        ITEM_CHARGE_RATE
      ),
      Integer.MAX_VALUE
    );
    int accepted = itemEnergy.receiveEnergy(available, true);
    if (accepted <= 0) {
      return;
    }
    int consumed = this.energyStorage.extractEnergy(accepted, false);
    if (consumed <= 0) {
      return;
    }
    int inserted = itemEnergy.receiveEnergy(consumed, false);
    if (inserted < consumed) {
      this.energyStorage.receiveEnergy(consumed - inserted, false);
    }
  }

  /** {@return 暴露给能量能力的缓存} */
  public IEnergyStorage getEnergyStorageForSide(@Nullable Direction side) {
    return this.energyStorage;
  }

  public long getEnergyStoredLong() {
    return this.energyStorage.getEnergyStoredLong();
  }

  public long getCapacity() {
    return CAPACITY;
  }

  public void setEnergyStored(long value) {
    this.energyStorage.setEnergyStored(value);
  }

  public int getHeat() {
    return this.heat;
  }

  public int getHeatCapacity() {
    return HEAT_CAPACITY;
  }

  public void setHeat(int value) {
    this.heat = Math.max(0, Math.min(value, HEAT_CAPACITY));
  }

  public int getCurrentGeneration() {
    return this.currentGeneration;
  }

  public HeatGeneratorMode getCurrentMode() {
    return this.currentMode;
  }

  public ItemStackHandler getInventory() {
    return this.inventory;
  }

  @Override
  public int getContainerSize() {
    return SLOT_COUNT;
  }

  @Override
  public boolean isEmpty() {
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      if (!this.inventory.getStackInSlot(slot).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    return this.inventory.getStackInSlot(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    return this.inventory.extractItem(slot, amount, false);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return this.inventory.extractItem(
      slot,
      this.inventory.getStackInSlot(slot).getCount(),
      false
    );
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    this.inventory.setStackInSlot(slot, stack);
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public void clearContent() {
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
    }
  }

  @Override
  public Component getDisplayName() {
    return getBlockState().getBlock().getName();
  }

  @Override
  public HeatGeneratorMenu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new HeatGeneratorMenu(containerId, playerInventory, this);
  }

  @Override
  protected void collectImplicitComponents(
    DataComponentMap.Builder components
  ) {
    super.collectImplicitComponents(components);
    if (
      this.energyStorage.getEnergyStoredLong() <= 0L &&
      this.heat <= 0 &&
      isEmpty()
    ) {
      return;
    }
    if (this.level == null) {
      return;
    }

    CompoundTag tag = new CompoundTag();
    saveAdditional(tag, this.level.registryAccess());
    if (!tag.isEmpty()) {
      BlockEntity.addEntityType(tag, this.getType());
      components.set(
        DataComponents.BLOCK_ENTITY_DATA,
        CustomData.of(tag)
      );
    }
  }

  @Deprecated
  @Override
  public void removeComponentsFromTag(CompoundTag tag) {
    tag.remove("energy");
    tag.remove("heat");
    tag.remove("inventory");
  }

  @Override
  public void saveAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.saveAdditional(tag, registries);
    tag.putLong("energy", this.energyStorage.getEnergyStoredLong());
    tag.putInt("heat", this.heat);
    tag.put("inventory", this.inventory.serializeNBT(registries));
  }

  @Override
  public void loadAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.loadAdditional(tag, registries);
    this.energyStorage.setEnergyStored(
      MachineStateSupport.clampEnergy(tag.getLong("energy"), CAPACITY)
    );
    this.heat = Math.max(0, Math.min(tag.getInt("heat"), HEAT_CAPACITY));
    if (tag.contains("inventory")) {
      this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }
    MachineStateSupport.sanitizeInventory(this.inventory, SLOT_COUNT);
  }
}
