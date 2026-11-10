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
 * 合金仪 MK2 方块实体。
 *
 * <p>提供 1,040,000 FE 缓存、三条独立生产线、一个共享放电槽和独立
 * 进度同步。每条生产线拥有三个原料槽和一个成品槽，三条生产线可以同时工作。
 * 默认处理速度为 MK1 的 1.2 倍，单次配方总耗能保持不变。
 *
 * <p>Alloying Machine MK2 block entity. It provides a 1,040,000 FE buffer,
 * three independent production lines, one shared discharge slot, and separate
 * progress synchronization. Each line has three input slots and one output slot,
 * and all three lines can work at the same time. Processing runs at 1.2x the
 * MK1 speed while preserving each recipe's total energy cost.
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
  public static final int SPEED_MULTIPLIER_NUMERATOR = 6;
  public static final int SPEED_MULTIPLIER_DENOMINATOR = 5;

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
    RecipeProcessState<
      AlloyingRecipeInput,
      AlloyingRecipe
    > state = line.state;
    AlloyingRecipeInput input = createInput(lineIndex);
    if (!state.hasRecipe() && state.getRecipeId() != null) {
      state.restore(
        this.level,
        ModRecipes.ALLOYING_TYPE.get(),
        input,
        recipe -> recipe.time() * SPEED_MULTIPLIER_DENOMINATOR
      );
    }

    if (
      state.hasRecipe() &&
      !state.getRecipe().matches(input, this.level)
    ) {
      resetLine(line);
    }

    if (!state.hasRecipe()) {
      RecipeHolder<AlloyingRecipe> holder = this.level
        .getRecipeManager()
        .getRecipeFor(ModRecipes.ALLOYING_TYPE.get(), input, this.level)
        .orElse(null);
      if (holder == null) {
        resetLineProgressOnly(line);
        return;
      }
      state.select(
        holder,
        holder.value().time() * SPEED_MULTIPLIER_DENOMINATOR
      );
      setChanged();
    }

    AlloyingRecipe recipe = state.getRecipe();
    if (recipe == null || !canAcceptResult(lineIndex, recipe)) {
      resetLine(line);
      return;
    }

    int energyPerTick = energyForTick(
      recipe,
      state.getProgress(),
      state.getMaxProgress()
    );
    if (this.energyStorage.getEnergyStoredLong() < energyPerTick) {
      return;
    }

    this.energyStorage.consumeEnergy(energyPerTick);
    state.addProgress(SPEED_MULTIPLIER_NUMERATOR);
    setChanged();

    if (state.getProgress() >= state.getMaxProgress()) {
      finishLine(lineIndex, line, input);
    }
  }

  /**
   * 计算当前 tick 的能量消耗，并保持整条配方的总耗能不变。
   *
   * <p>Calculates the current tick's energy cost while preserving the recipe's
   * total energy cost.
   */
  private int energyForTick(
    AlloyingRecipe recipe,
    int progress,
    int maxProgress
  ) {
    int totalTicks = Math.max(
      1,
      (maxProgress + SPEED_MULTIPLIER_NUMERATOR - 1) /
        SPEED_MULTIPLIER_NUMERATOR
    );
    int tickIndex = Math.min(
      progress / SPEED_MULTIPLIER_NUMERATOR,
      totalTicks - 1
    );
    long totalEnergy = Math.max(0L, recipe.energy());
    long before =
      totalEnergy * tickIndex / totalTicks;
    long after =
      totalEnergy * (tickIndex + 1L) / totalTicks;
    return (int) Math.min(
      Integer.MAX_VALUE,
      Math.max(0L, after - before)
    );
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
    AlloyingRecipe recipe = line.state.getRecipe();
    if (recipe == null || this.level == null) {
      return;
    }
    ItemStack result = recipe.assemble(
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

    recipe.consume(this, lineIndex * INPUTS_PER_LINE);
    resetLine(line);
  }

  private void resetLine(Line line) {
    if (
      line.state.hasRecipe() ||
      line.state.hasProgress() ||
      line.state.getMaxProgress() != MAX_PROGRESS
    ) {
      line.state.reset();
      setChanged();
    }
  }

  private void resetLineProgressOnly(Line line) {
    if (
      line.state.hasProgress() ||
      line.state.getMaxProgress() != MAX_PROGRESS
    ) {
      line.state.resetProgressOnly();
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
    return this.lines[lineIndex].state.getProgress();
  }

  public int getMaxProgress(int lineIndex) {
    if (lineIndex < 0 || lineIndex >= LINE_COUNT) {
      return MAX_PROGRESS;
    }
    return this.lines[lineIndex].state.getMaxProgress();
  }

  public boolean hasProgress() {
    for (Line line : this.lines) {
      if (line.state.hasProgress()) {
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
      tag.remove("recipe" + lineIndex);
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
      this.lines[lineIndex].state.saveTo(
        tag,
        "recipe" + lineIndex,
        "progress" + lineIndex
      );
    }
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
    for (int lineIndex = 0; lineIndex < LINE_COUNT; lineIndex++) {
      this.lines[lineIndex].state.loadFrom(
        tag,
        "recipe" + lineIndex,
        "progress" + lineIndex
      );
    }
  }

  private static final class Line {

    private final RecipeProcessState<
      AlloyingRecipeInput,
      AlloyingRecipe
    > state = new RecipeProcessState<>(MAX_PROGRESS);
  }
}
