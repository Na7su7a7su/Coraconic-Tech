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
import aestastel.coraconic.menu.AlloyingMachineMk2Menu;
import aestastel.coraconic.recipe.AlloyingRecipe;
import aestastel.coraconic.recipe.AlloyingRecipeInput;
import aestastel.coraconic.recipe.ModRecipes;
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
 * 合金仪 MK2 方块实体。
 *
 * <p>提供 1,040,000 FE 缓存、三条独立生产线、一个共享放电槽和独立
 * 进度同步。每条生产线拥有三个原料槽和一个成品槽，三条生产线可以同时工作。
 *
 * <p>Alloying Machine MK2 block entity. It provides a 1,040,000 FE buffer,
 * three independent production lines, one shared discharge slot, and separate
 * progress synchronization. Each line has three input slots and one output slot,
 * and all three lines can work at the same time.
 */
public class AlloyingMachineMk2BlockEntity
  extends BlockEntity
  implements Container, MenuProvider
{

  public static final int LINE_COUNT = 3;
  public static final int INPUTS_PER_LINE = 3;
  public static final int INPUT_SLOT_COUNT = LINE_COUNT * INPUTS_PER_LINE;
  public static final int OUTPUT_SLOT_START = INPUT_SLOT_COUNT;
  public static final int OUTPUT_SLOT_COUNT = LINE_COUNT;
  public static final int DISCHARGE_SLOT = OUTPUT_SLOT_START + OUTPUT_SLOT_COUNT;
  public static final int SLOT_COUNT = DISCHARGE_SLOT + 1;
  public static final long CAPACITY = 1_040_000L;
  public static final long MAX_RECEIVE = 40_000L;
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
      AlloyingMachineMk2BlockEntity.this.setChanged();
    }
  };

  private final Line[] lines = {
    new Line(),
    new Line(),
    new Line(),
  };

  public AlloyingMachineMk2BlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state
  ) {
    super(type, pos, state);
  }

  /**
   * 服务端 tick：依次推进三条独立生产线，再处理共享放电槽。
   *
   * <p>Server tick: advances all three independent production lines, then
   * processes the shared discharge slot.
   */
  public void tick() {
    if (this.level == null || this.level.isClientSide) {
      return;
    }
    for (int lineIndex = 0; lineIndex < LINE_COUNT; lineIndex++) {
      processLine(lineIndex);
    }
    dischargeSlot();
  }

  private void processLine(int lineIndex) {
    Line line = this.lines[lineIndex];
    AlloyingRecipeInput input = createInput(lineIndex);
    if (
      line.currentRecipe != null &&
      !line.currentRecipe.matches(input, this.level)
    ) {
      resetLine(line);
    }

    if (line.currentRecipe == null) {
      line.currentRecipe = this.level
        .getRecipeManager()
        .getRecipeFor(ModRecipes.ALLOYING_TYPE.get(), input, this.level)
        .map(RecipeHolder::value)
        .orElse(null);
      if (line.currentRecipe == null) {
        resetLineProgressOnly(line);
        return;
      }
      line.maxProgress = line.currentRecipe.time();
      line.progress = 0;
      setChanged();
    }

    if (!canAcceptResult(lineIndex, line.currentRecipe)) {
      resetLine(line);
      return;
    }

    int energyPerTick = line.currentRecipe.energyPerTick();
    if (this.energyStorage.getEnergyStoredLong() < energyPerTick) {
      return;
    }

    this.energyStorage.consumeEnergy(energyPerTick);
    line.progress++;
    setChanged();

    if (line.progress >= line.maxProgress) {
      finishLine(lineIndex, line, input);
    }
  }

  private AlloyingRecipeInput createInput(int lineIndex) {
    int firstSlot = lineIndex * INPUTS_PER_LINE;
    return new AlloyingRecipeInput(
      List.of(
        this.inventory.getStackInSlot(firstSlot),
        this.inventory.getStackInSlot(firstSlot + 1),
        this.inventory.getStackInSlot(firstSlot + 2)
      )
    );
  }

  private boolean canAcceptResult(
    int lineIndex,
    AlloyingRecipe recipe
  ) {
    if (this.level == null) {
      return false;
    }
    ItemStack result = recipe.getResultItem(this.level.registryAccess());
    ItemStack output = this.inventory.getStackInSlot(
      OUTPUT_SLOT_START + lineIndex
    );
    if (output.isEmpty()) {
      return true;
    }
    return (
      ItemStack.isSameItemSameComponents(output, result) &&
      output.getCount() + result.getCount() <= output.getMaxStackSize()
    );
  }

  private void finishLine(
    int lineIndex,
    Line line,
    AlloyingRecipeInput input
  ) {
    if (line.currentRecipe == null || this.level == null) {
      return;
    }
    ItemStack result = line.currentRecipe.assemble(
      input,
      this.level.registryAccess()
    );
    int outputSlot = OUTPUT_SLOT_START + lineIndex;
    ItemStack output = this.inventory.getStackInSlot(outputSlot);
    if (output.isEmpty()) {
      this.inventory.setStackInSlot(outputSlot, result);
    } else {
      output.grow(result.getCount());
      this.inventory.setStackInSlot(outputSlot, output);
    }

    line.currentRecipe.consume(
      this,
      lineIndex * INPUTS_PER_LINE
    );
    resetLine(line);
  }

  private void resetLine(Line line) {
    if (
      line.currentRecipe != null ||
      line.progress != 0 ||
      line.maxProgress != MAX_PROGRESS
    ) {
      line.currentRecipe = null;
      line.progress = 0;
      line.maxProgress = MAX_PROGRESS;
      setChanged();
    }
  }

  private void resetLineProgressOnly(Line line) {
    if (line.progress != 0 || line.maxProgress != MAX_PROGRESS) {
      line.progress = 0;
      line.maxProgress = MAX_PROGRESS;
      setChanged();
    }
  }

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

  public int getProgress(int lineIndex) {
    if (lineIndex < 0 || lineIndex >= LINE_COUNT) {
      return 0;
    }
    return this.lines[lineIndex].progress;
  }

  public int getMaxProgress(int lineIndex) {
    if (lineIndex < 0 || lineIndex >= LINE_COUNT) {
      return MAX_PROGRESS;
    }
    return this.lines[lineIndex].maxProgress;
  }

  public boolean hasProgress() {
    for (Line line : this.lines) {
      if (line.progress > 0) {
        return true;
      }
    }
    return false;
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
  public AlloyingMachineMk2Menu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new AlloyingMachineMk2Menu(
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
      !hasProgress()
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
    for (int lineIndex = 0; lineIndex < LINE_COUNT; lineIndex++) {
      tag.remove("progress" + lineIndex);
    }
  }

  @Override
  public void saveAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.saveAdditional(tag, registries);
    tag.putLong("energy", this.energyStorage.getEnergyStoredLong());
    tag.put("inventory", this.inventory.serializeNBT(registries));
    for (int lineIndex = 0; lineIndex < LINE_COUNT; lineIndex++) {
      tag.putInt("progress" + lineIndex, this.lines[lineIndex].progress);
    }
  }

  @Override
  public void loadAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.loadAdditional(tag, registries);
    this.energyStorage.setEnergyStored(tag.getLong("energy"));
    if (tag.contains("inventory")) {
      this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }
    for (int lineIndex = 0; lineIndex < LINE_COUNT; lineIndex++) {
      Line line = this.lines[lineIndex];
      line.currentRecipe = null;
      line.progress = Math.max(0, tag.getInt("progress" + lineIndex));
      line.maxProgress = MAX_PROGRESS;
    }
  }

  private static final class Line {

    private AlloyingRecipe currentRecipe;
    private int progress;
    private int maxProgress = MAX_PROGRESS;
  }
}
