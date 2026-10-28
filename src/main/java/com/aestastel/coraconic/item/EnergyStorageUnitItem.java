/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.item;

import com.aestastel.coraconic.block.EnergyStorageUnitBlock;
import com.aestastel.coraconic.energy.EnergyFormat;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * 能量存储单元的方块物品：显示电量，并作为可携带的 FE 容器。
 *
 * <p>物品电量与方块实体共用 {@code minecraft:block_entity_data} 组件，因此
 * 物品可以被放入其他能量单元的充电/放电槽，充放电结果会直接写回组件；放置
 * 方块时也会恢复完整的能量与内部槽位。
 *
 * <p>Block item for the energy storage units. It displays the stored charge and
 * acts as a portable FE container. Energy lives in the same
 * {@code minecraft:block_entity_data} component as the block entity, so the item
 * can be charged/discharged inside another unit's slots and restores its energy
 * when placed.
 */
public class EnergyStorageUnitItem extends BlockItem {

  /** 与能量 GUI 一致的绿色电量条颜色（RGB）。 */
  private static final int BAR_COLOR = 0x4FD784;

  private final EnergyStorageUnitBlock energyBlock;
  private final long capacity;
  private final int transfer;

  public EnergyStorageUnitItem(
    Block block,
    Item.Properties properties
  ) {
    // 带电量的物品必须独立，避免一个堆叠共享同一份能量数据。
    // Charged items must not share one stack's energy data.
    super(block, properties.stacksTo(1));
    if (!(block instanceof EnergyStorageUnitBlock energyBlock)) {
      throw new IllegalArgumentException(
        "EnergyStorageUnitItem requires an EnergyStorageUnitBlock"
      );
    }
    this.energyBlock = energyBlock;
    this.capacity = energyBlock.getCapacity();
    this.transfer = energyBlock.getTransfer();
  }

  /** {@return 物品形式的 FE 能力} */
  public IEnergyStorage getEnergyStorage(ItemStack stack) {
    return new ItemEnergyStorage(stack);
  }

  /**
   * 电量大于 0 时在物品栏显示类似耐久条的电量条。
   *
   * <p>Shows a durability-style charge bar in inventories while the unit has
   * stored energy.
   */
  @Override
  public boolean isBarVisible(ItemStack stack) {
    return getStoredEnergy(stack) > 0L;
  }

  /**
   * 将当前电量映射到原版 13 像素耐久条宽度。
   *
   * <p>Maps the stored energy to the vanilla 13-pixel bar width. Non-empty
   * units keep at least one pixel so small charges remain visible.
   */
  @Override
  public int getBarWidth(ItemStack stack) {
    long stored = getStoredEnergy(stack);
    if (stored <= 0L) {
      return 0;
    }
    int width = (int) Math.round(
      13.0D * (double) stored / (double) capacity
    );
    return Math.max(1, Math.min(13, width));
  }

  /**
   * 电量条颜色与能量 GUI 保持一致。
   *
   * <p>Uses the same green as the energy GUI.
   */
  @Override
  public int getBarColor(ItemStack stack) {
    return BAR_COLOR;
  }

  @Override
  public void appendHoverText(
    ItemStack stack,
    Item.TooltipContext context,
    List<Component> tooltipComponents,
    TooltipFlag tooltipFlag
  ) {
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

    tooltipComponents.add(
      Component.translatable(
        "tooltip.coraconic.energy_storage_unit.energy",
        EnergyFormat.abbreviate(getStoredEnergy(stack)),
        EnergyFormat.abbreviate(capacity)
      ).withStyle(ChatFormatting.GRAY)
    );
  }

  private long getStoredEnergy(ItemStack stack) {
    return Math.min(capacity, Math.max(0L, readEnergy(stack)));
  }

  /**
   * 读取组件中的总能量，兼容方块实体保存的 long[] 与物品能力写入的 long。
   *
   * <p>Reads the total energy from the component, accepting both the block
   * entity's long[] form and the long form written by the item capability.
   */
  private static long readEnergy(ItemStack stack) {
    CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
    if (data == null) {
      return 0L;
    }

    CompoundTag tag = data.copyTag();
    long[] banks = tag.getLongArray("energy");
    if (banks.length > 0) {
      long total = 0L;
      for (long bank : banks) {
        total += bank;
      }
      return total;
    }
    return tag.getLong("energy");
  }

  /**
   * 写回总能量，同时保留内部槽位数据。
   *
   * <p>Writes the total energy back while preserving the internal slot data.
   * Empty units with no stored items lose the component again so they remain
   * clean items.
   */
  private void setStoredEnergy(ItemStack stack, long energy) {
    long clamped = Math.min(capacity, Math.max(0L, energy));
    CompoundTag tag = stack
      .getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)
      .copyTag();

    if (clamped <= 0L && !hasStoredItems(tag)) {
      CompoundTag remaining = tag.copy();
      remaining.remove("energy");
      remaining.remove("inventory");
      remaining.remove("id");
      if (remaining.isEmpty()) {
        stack.remove(DataComponents.BLOCK_ENTITY_DATA);
        return;
      }
    }

    // 物品栈序列化要求 BLOCK_ENTITY_DATA 带有方块实体类型 id。
    // Item stack serialization requires the block entity type id.
    BlockEntity.addEntityType(tag, energyBlock.getBlockEntityType());
    tag.putLong("energy", clamped);
    stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
  }

  private static boolean hasStoredItems(CompoundTag tag) {
    CompoundTag inventory = tag.getCompound("inventory");
    return !inventory.getList("Items", Tag.TAG_COMPOUND).isEmpty();
  }

  /**
   * 以当前 ItemStack 为后端的 FE 能力。
   *
   * <p>FE capability backed by the current ItemStack. Each access reads the
   * stack's component, so cached capability instances still see the latest
   * energy value.
   */
  private final class ItemEnergyStorage implements IEnergyStorage {

    private final ItemStack stack;

    private ItemEnergyStorage(ItemStack stack) {
      this.stack = stack;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
      if (!canReceive() || toReceive <= 0) {
        return 0;
      }
      long stored = getStoredEnergy(stack);
      long accepted = Math.min(
        capacity - stored,
        Math.min(transfer, toReceive)
      );
      if (accepted <= 0L) {
        return 0;
      }
      if (!simulate) {
        setStoredEnergy(stack, stored + accepted);
      }
      return (int) accepted;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
      if (!canExtract() || toExtract <= 0) {
        return 0;
      }
      long stored = getStoredEnergy(stack);
      long extracted = Math.min(
        stored,
        Math.min(transfer, toExtract)
      );
      if (extracted <= 0L) {
        return 0;
      }
      if (!simulate) {
        setStoredEnergy(stack, stored - extracted);
      }
      return (int) extracted;
    }

    @Override
    public int getEnergyStored() {
      return (int) Math.min(getStoredEnergy(stack), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
      return (int) Math.min(capacity, Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
      return transfer > 0;
    }

    @Override
    public boolean canReceive() {
      return transfer > 0;
    }
  }
}
