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
import aestastel.coraconic.menu.ResonanceCrystallizingMachineMenu;
import aestastel.coraconic.recipe.ModRecipes;
import aestastel.coraconic.recipe.RecipeProcessState;
import aestastel.coraconic.recipe.ResonanceCrystallizingRecipe;
import aestastel.coraconic.recipe.ResonanceCrystallizingRecipeInput;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 共振晶化仪 方块实体。
 *
 * <p>提供两个原料槽、一个成品槽、一个能源槽和 640,000 FE 缓存。
 * 支持共振晶化配方，能源槽只负责从缓存为物品充能。
 *
 * <p>Resonance Crystallizing Machine block entity. It provides two input
 * slots, one output slot, one energy slot, and a 640,000 FE buffer. It supports
 * resonance crystallizing recipes; the energy slot only charges items from the
 * buffer.
 */
public class ResonanceCrystallizingMachineBlockEntity
  extends BlockEntity
  implements Container, MenuProvider
{

  public static final int INPUT_SLOT_COUNT = 2;
  public static final int OUTPUT_SLOT = 2;
  public static final int ENERGY_SLOT = 3;
  public static final int SLOT_COUNT = 4;
  public static final long CAPACITY = 640_000L;
  public static final long MAX_RECEIVE = 1_000L;

  private final ModEnergyStorage energyStorage = new ModEnergyStorage(
    CAPACITY,
    MAX_RECEIVE,
    0L,
    this::setChanged
  );

  private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
    @Override
    protected void onContentsChanged(int slot) {
      ResonanceCrystallizingMachineBlockEntity.this.setChanged();
    }
  };

  private final RecipeProcessState<
    ResonanceCrystallizingRecipeInput,
    ResonanceCrystallizingRecipe
  > processState = new RecipeProcessState<>(0);

  public ResonanceCrystallizingMachineBlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state
  ) {
    super(type, pos, state);
  }

  /**
   * 服务端 tick：先处理共振晶化配方，再处理能源槽物品充能。
   *
   * <p>Server tick: processes the resonance crystallizing recipe first, then
   * charges the item in the energy slot.
   */
  public void tick() {
    if (this.level == null || this.level.isClientSide) {
      return;
    }
    processRecipe();
    chargeEnergySlot();
  }

  private void processRecipe() {
    ResonanceCrystallizingRecipeInput input = createInput();
    if (
      !this.processState.hasRecipe() &&
      this.processState.getRecipeId() != null
    ) {
      this.processState.restore(
        this.level,
        ModRecipes.RESONANCE_CRYSTALLIZING_TYPE.get(),
        input,
        ResonanceCrystallizingRecipe::time
      );
    }

    if (
      this.processState.hasRecipe() &&
      !this.processState.getRecipe().matches(input, this.level)
    ) {
      resetProcessing();
    }

    if (!this.processState.hasRecipe()) {
      RecipeHolder<ResonanceCrystallizingRecipe> holder = this.level
        .getRecipeManager()
        .getRecipeFor(
          ModRecipes.RESONANCE_CRYSTALLIZING_TYPE.get(),
          input,
          this.level
        )
        .orElse(null);
      if (holder == null) {
        resetProgressOnly();
        return;
      }
      this.processState.select(holder, holder.value().time());
      setChanged();
    }

    ResonanceCrystallizingRecipe recipe = this.processState.getRecipe();
    if (recipe == null) {
      resetProcessing();
      return;
    }
    if (!canAcceptResult(recipe)) {
      return;
    }

    int energyForTick = recipe.energyForTick(
      this.processState.getProgress()
    );
    if (
      energyForTick > 0 &&
      this.energyStorage.getEnergyStoredLong() < energyForTick
    ) {
      return;
    }
    if (energyForTick > 0) {
      this.energyStorage.consumeEnergy(energyForTick);
    }

    this.processState.addProgress(1);
    setChanged();

    if (
      this.processState.getProgress() >=
      this.processState.getMaxProgress()
    ) {
      finishRecipe(input);
    }
  }

  private ResonanceCrystallizingRecipeInput createInput() {
    return new ResonanceCrystallizingRecipeInput(
      List.of(
        this.inventory.getStackInSlot(0),
        this.inventory.getStackInSlot(1)
      )
    );
  }

  private boolean canAcceptResult(ResonanceCrystallizingRecipe recipe) {
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

  private void finishRecipe(ResonanceCrystallizingRecipeInput input) {
    ResonanceCrystallizingRecipe recipe = this.processState.getRecipe();
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
      this.processState.getMaxProgress() != 0
    ) {
      this.processState.reset();
      setChanged();
    }
  }

  private void resetProgressOnly() {
    if (
      this.processState.hasProgress() ||
      this.processState.getMaxProgress() != 0
    ) {
      this.processState.resetProgressOnly();
      setChanged();
    }
  }

  private void chargeEnergySlot() {
    ItemStack stack = this.inventory.getStackInSlot(ENERGY_SLOT);
    if (stack.isEmpty()) {
      return;
    }

    IEnergyStorage itemEnergy = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    if (itemEnergy == null || !itemEnergy.canReceive()) {
      return;
    }

    long stored = this.energyStorage.getEnergyStoredLong();
    if (stored <= 0L) {
      return;
    }

    int request = (int) Math.min(
      Math.min(stored, MAX_RECEIVE),
      Integer.MAX_VALUE
    );
    int accepted = itemEnergy.receiveEnergy(request, true);
    if (accepted <= 0) {
      return;
    }

    long consumed = this.energyStorage.consumeEnergy(accepted);
    if (consumed <= 0L) {
      return;
    }

    int inserted = itemEnergy.receiveEnergy((int) consumed, false);
    if (inserted < consumed) {
      this.energyStorage.receiveEnergy(
        (int) (consumed - inserted),
        false
      );
    }
    this.setChanged();
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
  public ResonanceCrystallizingMachineMenu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new ResonanceCrystallizingMachineMenu(
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
    tag.remove("progress");
    tag.remove("recipe");
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
