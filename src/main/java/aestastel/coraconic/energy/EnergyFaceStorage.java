/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.energy;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * 带面模式门控的能量能力视图。
 *
 * <p>把实际能量存储包装成某个面的 {@link IEnergyStorage}：输入模式只允许
 * 接收，输出模式只允许抽取，输入/输出模式两者都允许，阻断模式全部拒绝。
 *
 * <p>An {@link IEnergyStorage} view for one configured face. It delegates to
 * the real storage only when the face's mode permits the requested operation.
 */
public final class EnergyFaceStorage implements IEnergyStorage {

  private final IEnergyStorage delegate;
  private final EnergyFaceConfigurable config;
  @Nullable
  private final Direction side;

  public EnergyFaceStorage(
    IEnergyStorage delegate,
    EnergyFaceConfigurable config,
    @Nullable Direction side
  ) {
    this.delegate = delegate;
    this.config = config;
    this.side = side;
  }

  private EnergyFaceMode mode() {
    return this.config.getFaceMode(this.side);
  }

  @Override
  public int receiveEnergy(int toReceive, boolean simulate) {
    return mode().allowsInput()
      ? this.delegate.receiveEnergy(toReceive, simulate)
      : 0;
  }

  @Override
  public int extractEnergy(int toExtract, boolean simulate) {
    return mode().allowsOutput()
      ? this.delegate.extractEnergy(toExtract, simulate)
      : 0;
  }

  @Override
  public int getEnergyStored() {
    return this.delegate.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return this.delegate.getMaxEnergyStored();
  }

  @Override
  public boolean canExtract() {
    return mode().allowsOutput() && this.delegate.canExtract();
  }

  @Override
  public boolean canReceive() {
    return mode().allowsInput() && this.delegate.canReceive();
  }
}
