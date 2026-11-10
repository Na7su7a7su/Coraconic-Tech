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
import aestastel.coraconic.menu.AlloyingMachineMenu;
import aestastel.coraconic.recipe.AlloyingRecipe;
import aestastel.coraconic.recipe.AlloyingRecipeInput;
import aestastel.coraconic.recipe.ModRecipes;
import aestastel.coraconic.recipe.RecipeProcessState;
import java.util.List;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 合金仪 MK1 方块实体。
 *
 * <p>当前提供 320,000 FE 缓存、三个原料槽、一个成品槽、一个放电槽和
 * 进度条同步。机器从所有面接收能量。
 *
 * <p>Alloying Machine MK1 block entity. It provides a 320,000 FE buffer,
 * three input slots, one output slot, one discharge slot, and progress
 * synchronization. Energy can be received from every side.
 */
public class AlloyingMachineBlockEntity
  extends BlockEntity
  implements Container, MenuProvider
{

  public static final int INPUT_SLOT_COUNT = 3;
  public static final int OUTPUT_SLOT = 3;
  public static final int DISCHARGE_SLOT = 4;
  public static final int SLOT_COUNT = 5;
  public static final long CAPACITY = 320_000L;
  public static final long MAX_RECEIVE = 10_000L;
  public static final int MAX_PROGRESS = 100;

  private final ModEnergyStorage energyStorage = new ModEnergyStorage(
    CAPACITY,
    MAX_RECEIVE,
    0L,
    this::setChanged
  );

  private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
    @Override
    protected void onContentsChanged(int slot) {
      AlloyingMachineBlockEntity.this.setChanged();
    }
  };

  private final RecipeProcessState<
    AlloyingRecipeInput,
    AlloyingRecipe
  > processState = new RecipeProcessState<>(MAX_PROGRESS);

  public AlloyingMachineBlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state
  ) {
    super(type, pos, state);
  }

  /**
   * 服务端 tick：先处理合金配方，再从放电槽物品提取能量。
   *
   * <p>Server tick: processes the alloying recipe first, then extracts energy
   * from the item in the discharge slot.
   */
  public void tick() {
    if (this.level == null || this.level.isClientSide) {
      return;
    }
    processRecipe();
    dischargeSlot();
  }

  private void processRecipe() {
    AlloyingRecipeInput input = createInput();
    if (
      !this.processState.hasRecipe() &&
      this.processState.getRecipeId() != null
    ) {
      this.processState.restore(
        this.level,
        ModRecipes.ALLOYING_TYPE.get(),
        input,
        AlloyingRecipe::time
      );
    }

    if (
      this.processState.hasRecipe() &&
      !this.processState.getRecipe().matches(input, this.level)
    ) {
      resetProcessing();
    }

    if (!this.processState.hasRecipe()) {
      RecipeHolder<AlloyingRecipe> holder = this.level
        .getRecipeManager()
        .getRecipeFor(ModRecipes.ALLOYING_TYPE.get(), input, this.level)
        .orElse(null);
      if (holder == null) {
        resetProgressOnly();
        return;
      }
      this.processState.select(holder, holder.value().time());
      setChanged();
    }

    AlloyingRecipe recipe = this.processState.getRecipe();
    if (recipe == null || !canAcceptResult(recipe)) {
      resetProcessing();
      return;
    }

    int energyPerTick = recipe.energyPerTick();
    if (this.energyStorage.getEnergyStoredLong() < energyPerTick) {
      return;
    }

    this.energyStorage.consumeEnergy(energyPerTick);
    this.processState.addProgress(1);
    setChanged();

    if (
      this.processState.getProgress() >=
      this.processState.getMaxProgress()
    ) {
      finishRecipe(input);
    }
  }

  /**
   * 从放电槽物品向缓存提取能量，单 tick 最高 {@link #MAX_RECEIVE} FE。
   *
   * <p>Extracts energy from the discharge-slot item into the buffer, up to
   * {@link #MAX_RECEIVE} FE per tick. It never charges the item.
   */
  private void dischargeSlot() {
    ItemStack stack = this.inventory.getStackInSlot(DISCHARGE_SLOT);
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

  public ItemStackHandler getInventory() {
    return this.inventory;
  }

  private AlloyingRecipeInput createInput() {
    return new AlloyingRecipeInput(
      List.of(
        this.inventory.getStackInSlot(0),
        this.inventory.getStackInSlot(1),
        this.inventory.getStackInSlot(2)
      )
    );
  }

  private boolean canAcceptResult(AlloyingRecipe recipe) {
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

  private void finishRecipe(AlloyingRecipeInput input) {
    AlloyingRecipe recipe = this.processState.getRecipe();
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

    recipe.consume(this);
    resetProcessing();
  }

  private void resetProcessing() {
    if (
      this.processState.hasRecipe() ||
      this.processState.hasProgress() ||
      this.processState.getMaxProgress() != MAX_PROGRESS
    ) {
      this.processState.reset();
      setChanged();
    }
  }

  private void resetProgressOnly() {
    if (
      this.processState.hasProgress() ||
      this.processState.getMaxProgress() != MAX_PROGRESS
    ) {
      this.processState.resetProgressOnly();
      setChanged();
    }
  }

  // ---------------------------------------------------------------------------
  // Container
  // ---------------------------------------------------------------------------

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

  // ---------------------------------------------------------------------------
  // MenuProvider
  // ---------------------------------------------------------------------------

  @Override
  public Component getDisplayName() {
    return getBlockState().getBlock().getName();
  }

  @Override
  public AlloyingMachineMenu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new AlloyingMachineMenu(containerId, playerInventory, this);
  }

  // ---------------------------------------------------------------------------
  // 物品组件 / Item components
  // ---------------------------------------------------------------------------

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

  // ---------------------------------------------------------------------------
  // 存档 / Persistence
  // ---------------------------------------------------------------------------

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
