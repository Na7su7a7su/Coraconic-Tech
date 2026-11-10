/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.blockentity;

import aestastel.coraconic.energy.ModEnergyStorage;
import aestastel.coraconic.menu.SmeltingMetallurgyMachineMenu;
import aestastel.coraconic.recipe.RecipeProcessState;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 熔炼冶金炉 MK1 方块实体。
 *
 * <p>提供一个原料槽、一个成品槽、一个电源槽和 320,000 FE 缓存。
 * 使用原版熔炼配方，每运行一个 tick 消耗 2 FE。
 *
 * <p>Smelting-Metallurgy Machine MK1 block entity. It provides one input slot,
 * one output slot, one power slot, and a 320,000 FE buffer. It uses vanilla
 * smelting recipes and consumes 2 FE per processing tick.
 */
public class SmeltingMetallurgyMachineBlockEntity
  extends BlockEntity
  implements Container, MenuProvider
{

  public static final int INPUT_SLOT = 0;
  public static final int OUTPUT_SLOT = 1;
  public static final int POWER_SLOT = 2;
  public static final int SLOT_COUNT = 3;
  public static final long CAPACITY = 320_000L;
  public static final long MAX_RECEIVE = 10_000L;
  public static final int ENERGY_PER_TICK = 2;
  public static final int DEFAULT_COOK_TIME = 200;

  private final ModEnergyStorage energyStorage = new ModEnergyStorage(
    CAPACITY,
    MAX_RECEIVE,
    0L,
    this::setChanged
  );

  private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
    @Override
    protected void onContentsChanged(int slot) {
      SmeltingMetallurgyMachineBlockEntity.this.setChanged();
    }
  };

  private final RecipeProcessState<
    SingleRecipeInput,
    SmeltingRecipe
  > processState = new RecipeProcessState<>(DEFAULT_COOK_TIME);

  public SmeltingMetallurgyMachineBlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state
  ) {
    super(type, pos, state);
  }

  /**
   * 服务端 tick：先处理熔炼配方，再处理电源槽。
   *
   * <p>Server tick: processes the smelting recipe first, then the power slot.
   */
  public void tick() {
    if (this.level == null || this.level.isClientSide) {
      return;
    }
    processRecipe();
    dischargePowerSlot();
  }

  private void processRecipe() {
    SingleRecipeInput input = createInput();
    if (
      !this.processState.hasRecipe() &&
      this.processState.getRecipeId() != null
    ) {
      this.processState.restore(
        this.level,
        RecipeType.SMELTING,
        input,
        SmeltingRecipe::getCookingTime
      );
    }

    if (
      this.processState.hasRecipe() &&
      !this.processState.getRecipe().matches(input, this.level)
    ) {
      resetProcessing();
    }

    if (!this.processState.hasRecipe()) {
      RecipeHolder<SmeltingRecipe> holder = this.level
        .getRecipeManager()
        .getRecipeFor(RecipeType.SMELTING, input, this.level)
        .orElse(null);
      if (holder == null) {
        resetProgressOnly();
        return;
      }
      this.processState.select(
        holder,
        holder.value().getCookingTime()
      );
      setChanged();
    }

    SmeltingRecipe recipe = this.processState.getRecipe();
    if (recipe == null || !canAcceptResult(recipe)) {
      resetProcessing();
      return;
    }

    if (this.energyStorage.getEnergyStoredLong() < ENERGY_PER_TICK) {
      return;
    }

    this.energyStorage.consumeEnergy(ENERGY_PER_TICK);
    this.processState.addProgress(1);
    setChanged();

    if (
      this.processState.getProgress() >=
      this.processState.getMaxProgress()
    ) {
      finishRecipe(input);
    }
  }

  private SingleRecipeInput createInput() {
    return new SingleRecipeInput(this.inventory.getStackInSlot(INPUT_SLOT));
  }

  private boolean canAcceptResult(SmeltingRecipe recipe) {
    if (this.level == null) {
      return false;
    }
    ItemStack result = recipe.getResultItem(this.level.registryAccess());
    ItemStack output = this.inventory.getStackInSlot(OUTPUT_SLOT);
    if (output.isEmpty()) {
      return true;
    }
    return (
      ItemStack.isSameItemSameComponents(output, result) &&
      output.getCount() + result.getCount() <= output.getMaxStackSize()
    );
  }

  private void finishRecipe(SingleRecipeInput input) {
    SmeltingRecipe recipe = this.processState.getRecipe();
    if (recipe == null || this.level == null) {
      return;
    }
    ItemStack result = recipe.assemble(
      input,
      this.level.registryAccess()
    );
    ItemStack output = this.inventory.getStackInSlot(OUTPUT_SLOT);
    if (output.isEmpty()) {
      this.inventory.setStackInSlot(OUTPUT_SLOT, result);
    } else {
      output.grow(result.getCount());
      this.inventory.setStackInSlot(OUTPUT_SLOT, output);
    }

    this.inventory.extractItem(INPUT_SLOT, 1, false);
    resetProcessing();
  }

  /**
   * 从电源槽向缓存提取能量，单 tick 最高 {@link #MAX_RECEIVE} FE。
   *
   * <p>Extracts energy from the power-slot item into the buffer, up to
   * {@link #MAX_RECEIVE} FE per tick. It never charges the item.
   */
  private void dischargePowerSlot() {
    ItemStack stack = this.inventory.getStackInSlot(POWER_SLOT);
    if (stack.isEmpty()) {
      return;
    }
    IEnergyStorage itemEnergy = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    if (itemEnergy == null || !itemEnergy.canExtract()) {
      return;
    }

    long space = CAPACITY - this.energyStorage.getEnergyStoredLong();
    if (space <= 0L) {
      return;
    }
    int request = (int) Math.min(
      Math.min(space, MAX_RECEIVE),
      Integer.MAX_VALUE
    );
    int available = itemEnergy.extractEnergy(request, true);
    if (available <= 0) {
      return;
    }
    int accepted = this.energyStorage.receiveEnergy(available, true);
    if (accepted <= 0) {
      return;
    }

    int extracted = itemEnergy.extractEnergy(accepted, false);
    if (extracted <= 0) {
      return;
    }
    int inserted = this.energyStorage.receiveEnergy(extracted, false);
    if (inserted < extracted) {
      itemEnergy.receiveEnergy(extracted - inserted, false);
    }
    if (inserted > 0) {
      setChanged();
    }
  }

  private void resetProcessing() {
    if (
      this.processState.hasRecipe() ||
      this.processState.hasProgress() ||
      this.processState.getMaxProgress() != DEFAULT_COOK_TIME
    ) {
      this.processState.reset();
      setChanged();
    }
  }

  private void resetProgressOnly() {
    if (
      this.processState.hasProgress() ||
      this.processState.getMaxProgress() != DEFAULT_COOK_TIME
    ) {
      this.processState.resetProgressOnly();
      setChanged();
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

  public int getProgress() {
    return this.processState.getProgress();
  }

  public int getMaxProgress() {
    return this.processState.getMaxProgress();
  }

  public boolean hasProgress() {
    return this.processState.hasProgress();
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
  public SmeltingMetallurgyMachineMenu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new SmeltingMetallurgyMachineMenu(
      containerId,
      playerInventory,
      this
    );
  }

  @Override
  protected void collectImplicitComponents(
    DataComponentMap.Builder components
  ) {
    super.collectImplicitComponents(components);
    if (
      this.energyStorage.getEnergyStoredLong() <= 0L &&
      isEmpty() &&
      !this.processState.hasProgress()
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
    tag.remove("inventory");
    tag.remove("recipe");
    tag.remove("progress");
  }

  @Override
  public void saveAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.saveAdditional(tag, registries);
    tag.putLong("energy", this.energyStorage.getEnergyStoredLong());
    tag.put("inventory", this.inventory.serializeNBT(registries));
    this.processState.saveTo(tag, "recipe", "progress");
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
    if (tag.contains("inventory")) {
      this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }
    MachineStateSupport.sanitizeInventory(this.inventory, SLOT_COUNT);
    this.processState.loadFrom(tag, "recipe", "progress");
  }
}
