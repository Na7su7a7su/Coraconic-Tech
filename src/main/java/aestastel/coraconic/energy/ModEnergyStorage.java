/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.energy;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * 长整型能量存储实现，用于支持超过 {@link Integer#MAX_VALUE} 的容量。
 *
 * <p>NeoForge 的 {@code IEnergyStorage} 接口只使用 {@code int}，因此：
 * <ul>
 *   <li>内部能量与容量使用 {@code long} 保存，可达到 3.2G FE；</li>
 *   <li>对外能力接口仍然返回 {@code int}，超过 2,147,483,647 FE 的部分会被
 *       截断显示；外部线缆/机器单次传输也受 {@code int} 限制；</li>
 *   <li>本模组自己的 GUI 与物品充放电槽使用 {@code long} 值，可正常显示与
 *       填充超过 {@code int} 上限的能量。</li>
 * </ul>
 *
 * <p>Long-backed energy storage for capacities above {@link Integer#MAX_VALUE}.
 * The public {@link IEnergyStorage} view is still int-based, so external
 * machines see energy clamped to {@code Integer.MAX_VALUE}; this mod's GUI and
 * item slots use the full long value.
 */
public class ModEnergyStorage implements IEnergyStorage {

  private final long capacity;
  private final int maxReceive;
  private final int maxExtract;
  private final Runnable onChanged;

  private long energy;

  public ModEnergyStorage(
    long capacity,
    int maxTransfer,
    Runnable onChanged
  ) {
    this.capacity = capacity;
    this.maxReceive = maxTransfer;
    this.maxExtract = maxTransfer;
    this.onChanged = onChanged;
  }

  @Override
  public int receiveEnergy(int toReceive, boolean simulate) {
    if (!canReceive() || toReceive <= 0) {
      return 0;
    }
    long accepted = Math.min(
      this.capacity - this.energy,
      Math.min(this.maxReceive, toReceive)
    );
    if (accepted <= 0) {
      return 0;
    }
    if (!simulate) {
      this.energy += accepted;
      this.onChanged.run();
    }
    return (int) accepted;
  }

  @Override
  public int extractEnergy(int toExtract, boolean simulate) {
    if (!canExtract() || toExtract <= 0) {
      return 0;
    }
    long extracted = Math.min(
      this.energy,
      Math.min(this.maxExtract, toExtract)
    );
    if (extracted <= 0) {
      return 0;
    }
    if (!simulate) {
      this.energy -= extracted;
      this.onChanged.run();
    }
    return (int) extracted;
  }

  @Override
  public int getEnergyStored() {
    return (int) Math.min(this.energy, Integer.MAX_VALUE);
  }

  @Override
  public int getMaxEnergyStored() {
    return (int) Math.min(this.capacity, Integer.MAX_VALUE);
  }

  @Override
  public boolean canExtract() {
    return this.maxExtract > 0;
  }

  @Override
  public boolean canReceive() {
    return this.maxReceive > 0;
  }

  /** {@return 当前储存的能量（long，完整值）} */
  public long getEnergyStoredLong() {
    return this.energy;
  }

  /** {@return 最大容量（long，完整值）} */
  public long getCapacityLong() {
    return this.capacity;
  }

  /**
   * 直接设置当前能量（用于 NBT 读取与客户端同步）。
   *
   * <p>Sets the stored energy directly for NBT loading and client sync.
   */
  public void setEnergyStored(long value) {
    this.energy = Mth.clamp(value, 0L, this.capacity);
  }
}
