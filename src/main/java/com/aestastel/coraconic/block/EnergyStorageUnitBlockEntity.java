/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.block;

import com.aestastel.coraconic.energy.ModEnergyStorage;
import com.aestastel.coraconic.menu.EnergyStorageUnitMenu;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 能量存储单元方块实体：保存 FE 能量与两个物品槽位。
 *
 * <p>GUI 提供两个槽位：
 * <ul>
 *   <li>{@link #CHARGE_SLOT}：放入可充电物品，能量从单元输入到物品；</li>
 *   <li>{@link #DISCHARGE_SLOT}：放入可放电物品，能量从物品输出到单元。</li>
 * </ul>
 * 能量通过 NeoForge 的 {@code Capabilities.EnergyStorage.BLOCK} 能力暴露，
 * 物品能量通过 {@code Capabilities.EnergyStorage.ITEM} 能力读写。
 *
 * <p>当总容量超过 {@link Integer#MAX_VALUE}（例如 MK3 的 3.2G）时，内部会
 * 自动拆分为多个不超过 int 上限的能量段，并按查询面暴露不同段；线缆连接
 * 不同面即可访问完整容量。GUI 与充放电槽始终使用所有段的总和。
 *
 * <p>Energy storage unit block entity. It stores FE and two item slots:
 * a charge slot (energy flows from the unit into the item) and a discharge slot
 * (energy flows from the item into the unit). Capacities above
 * {@link Integer#MAX_VALUE} are split into int-sized segments mapped to
 * different sides; the GUI and item slots use the sum of all segments.
 */
public class EnergyStorageUnitBlockEntity
  extends BlockEntity
  implements Container, MenuProvider
{

  /** 充电槽：能量从单元进入物品。 */
  public static final int CHARGE_SLOT = 0;
  /** 放电槽：能量从物品进入单元。 */
  public static final int DISCHARGE_SLOT = 1;
  /** 机器槽位总数。 */
  public static final int SLOT_COUNT = 2;

  private final ModEnergyStorage[] banks;
  private final long totalCapacity;
  private final int transfer;

  private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
    @Override
    protected void onContentsChanged(int slot) {
      EnergyStorageUnitBlockEntity.this.setChanged();
    }
  };

  public EnergyStorageUnitBlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state,
    long capacity,
    int maxTransfer
  ) {
    super(type, pos, state);
    this.transfer = maxTransfer;
    this.totalCapacity = capacity;

    // 将总容量拆分为若干不超过 int 上限的段，供能力系统按面暴露。
    // Split the total capacity into int-sized segments exposed per side.
    int segments = (int) Math.max(
      1L,
      (capacity + Integer.MAX_VALUE - 1L) / Integer.MAX_VALUE
    );
    this.banks = new ModEnergyStorage[segments];
    long base = capacity / segments;
    long remainder = capacity % segments;
    for (int index = 0; index < segments; index++) {
      long segmentCapacity = base + (index < remainder ? 1L : 0L);
      this.banks[index] = new ModEnergyStorage(
        segmentCapacity,
        maxTransfer,
        this::setChanged
      );
    }
  }

  /**
   * 每个服务器 tick 处理充电槽与放电槽的物品能量交换。
   *
   * <p>Runs the charge/discharge item transfer on the server each tick.
   */
  public void tick() {
    if (level == null || level.isClientSide) {
      return;
    }
    chargeItem();
    dischargeItem();
  }

  /**
   * 充电槽：把单元能量输入到物品。
   *
   * <p>Charge slot: moves energy from the unit into the item.
   */
  private void chargeItem() {
    ItemStack stack = inventory.getStackInSlot(CHARGE_SLOT);
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
      this.transfer,
      getEnergyStoredLong()
    );
    if (available <= 0) {
      return;
    }
    int request = Math.min(transfer, available);
    int accepted = itemEnergy.receiveEnergy(request, true);
    if (accepted <= 0) {
      return;
    }

    int extracted = extractFromBanks(accepted, false);
    if (extracted <= 0) {
      return;
    }

    int inserted = itemEnergy.receiveEnergy(extracted, false);
    if (inserted < extracted) {
      // 理论上不会发生；若发生则把未使用的能量退回单元。
      // Should not happen; refund unused energy if it does.
      receiveIntoBanks(extracted - inserted, false);
    }
    if (inserted > 0) {
      setChanged();
    }
  }

  /**
   * 放电槽：把物品能量输出到单元。
   *
   * <p>Discharge slot: moves energy from the item into the unit.
   */
  private void dischargeItem() {
    ItemStack stack = inventory.getStackInSlot(DISCHARGE_SLOT);
    if (stack.isEmpty()) {
      return;
    }
    IEnergyStorage itemEnergy = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    if (itemEnergy == null || !itemEnergy.canExtract()) {
      return;
    }

    long space = totalCapacity - getEnergyStoredLong();
    if (space <= 0) {
      return;
    }
    int request = (int) Math.min(this.transfer, space);
    if (request <= 0) {
      return;
    }
    int available = itemEnergy.extractEnergy(request, true);
    if (available <= 0) {
      return;
    }

    int accepted = receiveIntoBanks(available, false);
    if (accepted <= 0) {
      return;
    }

    int extracted = itemEnergy.extractEnergy(accepted, false);
    if (extracted < accepted) {
      // 理论上不会发生；若发生则把多接收的能量退回。
      // Should not happen; push received energy back if it does.
      extractFromBanks(accepted - extracted, false);
    }
    if (extracted > 0) {
      setChanged();
    }
  }

  /**
   * 按面返回对应的能量段。
   *
   * <p>容量不超过 int 上限时只有一个段；MK3 被拆成两个 1.6G 段，不同面会
   * 暴露不同段，线缆连接不同面即可访问完整的 3.2G。
   *
   * <p>Returns the energy segment for the queried side. Large capacities are
   * split into int-sized segments and mapped to different faces.
   *
   * @param side 查询面；{@code null} 表示不区分面 / queried side, null for side-agnostic
   */
  public ModEnergyStorage getEnergyStorageForSide(Direction side) {
    if (banks.length == 1) {
      return banks[0];
    }
    int index = side == null
      ? 0
      : Math.floorMod(side.get3DDataValue(), banks.length);
    return banks[index];
  }

  private int extractFromBanks(int amount, boolean simulate) {
    int remaining = amount;
    int extracted = 0;
    for (ModEnergyStorage bank : banks) {
      if (remaining <= 0) {
        break;
      }
      int got = bank.extractEnergy(remaining, simulate);
      extracted += got;
      remaining -= got;
    }
    return extracted;
  }

  private int receiveIntoBanks(int amount, boolean simulate) {
    int remaining = amount;
    int accepted = 0;
    for (ModEnergyStorage bank : banks) {
      if (remaining <= 0) {
        break;
      }
      int got = bank.receiveEnergy(remaining, simulate);
      accepted += got;
      remaining -= got;
    }
    return accepted;
  }

  /** {@return 所有段的总储存能量（FE）} */
  public long getEnergyStoredLong() {
    long stored = 0L;
    for (ModEnergyStorage bank : banks) {
      stored += bank.getEnergyStoredLong();
    }
    return stored;
  }

  /** {@return 总容量（FE）} */
  public long getCapacityLong() {
    return totalCapacity;
  }

  /**
   * 把总能量平均分配到各段，供菜单客户端同步与调试使用。
   *
   * <p>Distributes a total energy value across the segments (client sync).
   */
  public void setEnergyStored(long value) {
    long remaining = Math.max(0L, Math.min(value, totalCapacity));
    for (ModEnergyStorage bank : banks) {
      long amount = Math.min(remaining, bank.getCapacityLong());
      bank.setEnergyStored(amount);
      remaining -= amount;
    }
  }

  /** {@return 两个物品槽位} */
  public ItemStackHandler getInventory() {
    return inventory;
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
      if (!inventory.getStackInSlot(slot).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    return inventory.getStackInSlot(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    return inventory.extractItem(slot, amount, false);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return inventory.extractItem(
      slot,
      inventory.getStackInSlot(slot).getCount(),
      false
    );
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    inventory.setStackInSlot(slot, stack);
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public void clearContent() {
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      inventory.setStackInSlot(slot, ItemStack.EMPTY);
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
  public EnergyStorageUnitMenu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new EnergyStorageUnitMenu(containerId, playerInventory, this);
  }

  // ---------------------------------------------------------------------------
  // 物品组件 / Item components（掉落与中键选取保留内容）
  // ---------------------------------------------------------------------------

  /**
   * 把能量与两个槽位写入 {@code minecraft:block_entity_data} 组件。
   *
   * <p>该组件会在生存模式破坏方块时由战利品表的 {@code copy_components}
   * 函数复制到掉落物，并在放置时由 {@code BlockItem} 自动载入方块实体。
   * 空单元不写入组件，确保仍可与新合成单元堆叠。
   *
   * <p>Writes the energy and both machine slots into the
   * {@code minecraft:block_entity_data} component. The loot table's
   * {@code copy_components} function copies it to the drop in survival, and
   * {@code BlockItem} loads it back when placed. Empty units get no component so
   * they still stack with freshly crafted ones.
   */
  @Override
  protected void collectImplicitComponents(
    DataComponentMap.Builder components
  ) {
    super.collectImplicitComponents(components);
    if (getEnergyStoredLong() <= 0L && isEmpty()) {
      return;
    }
    if (this.level == null) {
      return;
    }

    CompoundTag tag = new CompoundTag();
    this.saveAdditional(tag, this.level.registryAccess());
    if (!tag.isEmpty()) {
      // BLOCK_ENTITY_DATA 组件在物品栈序列化时必须带有方块实体类型 id。
      // The BLOCK_ENTITY_DATA component requires the block entity type id when
      // the item stack is serialized.
      BlockEntity.addEntityType(tag, this.getType());
      components.set(
        DataComponents.BLOCK_ENTITY_DATA,
        CustomData.of(tag)
      );
    }
  }

  /**
   * 从 {@code saveToItem} 的 NBT 中移除已进入组件的内容，避免重复保存。
   *
   * <p>Removes content already handled by the implicit component from the
   * {@code saveToItem} NBT, preventing duplicate storage.
   */
  @Deprecated
  @Override
  public void removeComponentsFromTag(CompoundTag tag) {
    tag.remove("energy");
    tag.remove("inventory");
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
    long[] energies = new long[banks.length];
    for (int index = 0; index < banks.length; index++) {
      energies[index] = banks[index].getEnergyStoredLong();
    }
    tag.putLongArray("energy", energies);
    tag.put("inventory", inventory.serializeNBT(registries));
  }

  @Override
  public void loadAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.loadAdditional(tag, registries);

    long[] stored = tag.getLongArray("energy");
    if (stored.length == 0 && tag.contains("energy")) {
      // 兼容旧版单个 int/long energy 字段，并自动分配到各段。
      // Backwards compatibility with the old single value, split across banks.
      setEnergyStored(tag.getLong("energy"));
    } else {
      for (int index = 0; index < banks.length; index++) {
        banks[index].setEnergyStored(
          index < stored.length ? stored[index] : 0L
        );
      }
    }

    if (tag.contains("inventory")) {
      inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }
  }
}
