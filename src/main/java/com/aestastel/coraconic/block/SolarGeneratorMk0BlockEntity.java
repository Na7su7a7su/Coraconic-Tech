/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.block;

import com.aestastel.coraconic.energy.EnergyFaceConfigurable;
import com.aestastel.coraconic.energy.EnergyFaceMode;
import com.aestastel.coraconic.energy.EnergyFaceStorage;
import com.aestastel.coraconic.energy.EnergyFaceTransfer;
import com.aestastel.coraconic.menu.SolarGeneratorMk0Menu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 太阳能发电机 MK0 方块实体。
 *
 * <p>白天、维度不是下界、天空光大于 10，且正上方整个天空柱没有完整不透明
 * 方块遮挡时，每个服务器 tick 向 96,000 FE 缓存中增加 120 FE。缓存会优先
 * 给输出槽中的可充电物品充能，剩余能量通过六个可独立配置的面以每面最高
 * 600 FE/t 对外输出。
 *
 * <p>During daytime, outside the Nether, with sky light greater than 10 and no
 * full opaque block anywhere above the panel, it generates 120 FE per server
 * tick into a 96,000 FE buffer. The buffer first charges an energy-receiving
 * item in the output slot, then uses six independently configured faces at up
 * to 600 FE/t each.
 */
public class SolarGeneratorMk0BlockEntity
  extends BlockEntity
  implements Container, MenuProvider, EnergyFaceConfigurable
{

  public static final int CAPACITY = 96_000;
  public static final int GENERATION = 120;
  public static final int OUTPUT_PER_SIDE = 600;
  public static final int ITEM_CHARGE_RATE = 600;
  public static final int OUTPUT_SLOT = 0;
  public static final int SLOT_COUNT = 1;

  private int energy;
  private int itemChargeThisTick = ITEM_CHARGE_RATE;

  private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
    @Override
    protected void onContentsChanged(int slot) {
      SolarGeneratorMk0BlockEntity.this.setChanged();
    }
  };

  private final EnergyFaceMode[] faceModes = new EnergyFaceMode[6];
  private final IEnergyStorage[] sideStorages = new IEnergyStorage[6];
  private final IEnergyStorage nullSideStorage;
  private final IEnergyStorage bufferStorage = new SolarBufferEnergyStorage();

  public SolarGeneratorMk0BlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state
  ) {
    super(type, pos, state);
    for (Direction direction : Direction.values()) {
      this.faceModes[direction.get3DDataValue()] = EnergyFaceMode.OUTPUT;
      this.sideStorages[direction.get3DDataValue()] =
        new EnergyFaceStorage(
          this.bufferStorage,
          this,
          direction
        );
    }
    this.nullSideStorage = new EnergyFaceStorage(
      this.bufferStorage,
      this,
      null
    );
  }

  /**
   * 每个服务器 tick：先发电，再优先给输出槽物品充能。
   *
   * <p>Each server tick: generate energy, then charge the output-slot item
   * before external extraction can consume the buffer.
   */
  public void tick() {
    if (this.level == null || this.level.isClientSide) {
      return;
    }

    this.itemChargeThisTick = 0;
    boolean changed = false;

    if (canGenerate() && this.energy < CAPACITY) {
      this.energy = Math.min(CAPACITY, this.energy + GENERATION);
      changed = true;
    }

    if (chargeOutputItem() > 0) {
      changed = true;
    }

    transferFaces();

    if (changed) {
      setChanged();
    }
  }

  private boolean canGenerate() {
    if (this.level == null) {
      return false;
    }
    if (
      !this.level.isDay() ||
      this.level.dimension().equals(Level.NETHER) ||
      hasSolidBlockAbove()
    ) {
      return false;
    }
    return (
      this.level.getBrightness(
        LightLayer.SKY,
        this.worldPosition.above()
      ) > 10
    );
  }

  /**
   * 检查整个上方天空柱是否存在完整不透明方块。
   *
   * <p>Uses the motion-blocking heightmap to bound the scan, then checks for
   * any full opaque block above the panel.
   */
  private boolean hasSolidBlockAbove() {
    if (this.level == null) {
      return true;
    }

    int x = this.worldPosition.getX();
    int z = this.worldPosition.getZ();
    int top = this.level.getHeight(
      Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
      x,
      z
    );

    for (
      int y = this.worldPosition.getY() + 1;
      y < top;
      y++
    ) {
      BlockPos checkPos = new BlockPos(x, y, z);
      BlockState state = this.level.getBlockState(checkPos);
      if (state.isSolidRender(this.level, checkPos)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 从缓存向输出槽物品充能，单 tick 最多 600 FE。
   *
   * <p>Charges the output-slot item from the buffer, up to 600 FE per tick.
   *
   * @return 实际传输量 / transferred amount
   */
  private int chargeOutputItem() {
    int budget = ITEM_CHARGE_RATE - this.itemChargeThisTick;
    if (budget <= 0 || this.energy <= 0) {
      return 0;
    }

    ItemStack stack = this.inventory.getStackInSlot(OUTPUT_SLOT);
    if (stack.isEmpty()) {
      return 0;
    }

    IEnergyStorage itemEnergy = stack.getCapability(
      Capabilities.EnergyStorage.ITEM
    );
    if (itemEnergy == null || !itemEnergy.canReceive()) {
      return 0;
    }

    int request = Math.min(this.energy, budget);
    int accepted = itemEnergy.receiveEnergy(request, true);
    if (accepted <= 0) {
      return 0;
    }

    int inserted = itemEnergy.receiveEnergy(accepted, false);
    if (inserted <= 0) {
      return 0;
    }

    this.energy -= inserted;
    this.itemChargeThisTick += inserted;
    return inserted;
  }

  /** {@return 面向指定面的能力视图} */
  public IEnergyStorage getEnergyStorageForSide(@Nullable Direction side) {
    return side == null
      ? nullSideStorage
      : sideStorages[side.get3DDataValue()];
  }

  @Override
  public EnergyFaceMode getFaceMode(@Nullable Direction side) {
    if (side == null) {
      return getDefaultFaceMode();
    }
    EnergyFaceMode mode = faceModes[side.get3DDataValue()];
    return mode == null ? getDefaultFaceMode() : mode;
  }

  @Override
  public void setFaceMode(Direction side, EnergyFaceMode mode) {
    faceModes[side.get3DDataValue()] = mode;
    setChanged();
  }

  @Override
  public EnergyFaceMode getDefaultFaceMode() {
    return EnergyFaceMode.OUTPUT;
  }

  private void transferFaces() {
    if (this.level == null) {
      return;
    }

    for (Direction side : Direction.values()) {
      if (
        !EnergyFaceTransfer.ownsTransfer(
          this.level,
          this.worldPosition,
          side
        )
      ) {
        continue;
      }

      EnergyFaceMode mode = getFaceMode(side);
      if (mode == EnergyFaceMode.BLOCKED) {
        continue;
      }

      if (mode == EnergyFaceMode.INPUT) {
        pullFrom(side, OUTPUT_PER_SIDE);
        continue;
      }
      if (mode == EnergyFaceMode.OUTPUT) {
        pushTo(side, OUTPUT_PER_SIDE);
        continue;
      }

      IEnergyStorage neighbour = getNeighbourStorage(side);
      if (neighbour == null) {
        continue;
      }
      int ownEnergy = getEnergyStored();
      int neighbourEnergy = neighbour.getEnergyStored();
      if (ownEnergy < neighbourEnergy) {
        int limit = Math.min(
          OUTPUT_PER_SIDE,
          (neighbourEnergy - ownEnergy) / 2
        );
        if (limit > 0) {
          pullFrom(side, limit);
        }
      } else if (ownEnergy > neighbourEnergy) {
        int limit = Math.min(
          OUTPUT_PER_SIDE,
          (ownEnergy - neighbourEnergy) / 2
        );
        if (limit > 0) {
          pushTo(side, limit);
        }
      }
    }
  }

  private int pushTo(Direction side, int limit) {
    IEnergyStorage self = getEnergyStorageForSide(side);
    if (!self.canExtract()) {
      return 0;
    }
    IEnergyStorage neighbour = getNeighbourStorage(side);
    if (neighbour == null || !neighbour.canReceive()) {
      return 0;
    }

    int available = self.extractEnergy(limit, true);
    if (available <= 0) {
      return 0;
    }
    int accepted = neighbour.receiveEnergy(available, true);
    if (accepted <= 0) {
      return 0;
    }

    int extracted = self.extractEnergy(accepted, false);
    if (extracted <= 0) {
      return 0;
    }
    int inserted = neighbour.receiveEnergy(extracted, false);
    if (inserted < extracted) {
      this.energy = Math.min(
        CAPACITY,
        this.energy + (extracted - inserted)
      );
      setChanged();
    }
    return inserted;
  }

  private int pullFrom(Direction side, int limit) {
    IEnergyStorage self = getEnergyStorageForSide(side);
    if (!self.canReceive()) {
      return 0;
    }
    IEnergyStorage neighbour = getNeighbourStorage(side);
    if (neighbour == null || !neighbour.canExtract()) {
      return 0;
    }

    int offered = neighbour.extractEnergy(limit, true);
    if (offered <= 0) {
      return 0;
    }
    int accepted = self.receiveEnergy(offered, true);
    if (accepted <= 0) {
      return 0;
    }

    int extracted = neighbour.extractEnergy(accepted, false);
    if (extracted <= 0) {
      return 0;
    }
    int inserted = self.receiveEnergy(extracted, false);
    if (inserted < extracted) {
      neighbour.receiveEnergy(extracted - inserted, false);
    }
    return inserted;
  }

  @Nullable
  private IEnergyStorage getNeighbourStorage(Direction side) {
    if (this.level == null) {
      return null;
    }
    return this.level.getCapability(
      Capabilities.EnergyStorage.BLOCK,
      this.worldPosition.relative(side),
      side.getOpposite()
    );
  }

  /** {@return 当前缓存（FE）} */
  public int getEnergyStored() {
    return this.energy;
  }

  /** {@return 缓存容量（FE）} */
  public int getCapacity() {
    return CAPACITY;
  }

  /** 供菜单客户端同步使用。 */
  public void setEnergyStored(int value) {
    this.energy = Mth.clamp(value, 0, CAPACITY);
  }

  /** {@return 输出槽物品栏} */
  public ItemStackHandler getInventory() {
    return this.inventory;
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
    return this.inventory.getStackInSlot(OUTPUT_SLOT).isEmpty();
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
    this.inventory.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
  }

  // ---------------------------------------------------------------------------
  // MenuProvider
  // ---------------------------------------------------------------------------

  @Override
  public Component getDisplayName() {
    return getBlockState().getBlock().getName();
  }

  @Override
  public SolarGeneratorMk0Menu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new SolarGeneratorMk0Menu(containerId, playerInventory, this);
  }

  // ---------------------------------------------------------------------------
  // 物品组件 / Item components（仅保留缓存能量）
  // ---------------------------------------------------------------------------

  /**
   * 把缓存能量写入方块物品，输出槽物品不进入组件。
   *
   * <p>Writes the buffered energy into the block item; output-slot contents are
   * intentionally excluded.
   */
  @Override
  protected void collectImplicitComponents(
    DataComponentMap.Builder components
  ) {
    super.collectImplicitComponents(components);
    if (this.energy <= 0 && !hasNonDefaultFaceModes()) {
      return;
    }

    CompoundTag tag = new CompoundTag();
    tag.putInt("energy", this.energy);
    tag.putIntArray("face_modes", serializeFaceModes());
    BlockEntity.addEntityType(tag, this.getType());
    components.set(
      DataComponents.BLOCK_ENTITY_DATA,
      CustomData.of(tag)
    );
  }

  /**
   * 从 saveToItem 的 NBT 中移除能量与输出槽，避免内容重复保存。
   *
   * <p>Removes the energy and output-slot inventory from the saveToItem NBT so
   * contents are not stored twice.
   */
  @Deprecated
  @Override
  public void removeComponentsFromTag(CompoundTag tag) {
    tag.remove("energy");
    tag.remove("inventory");
    tag.remove("face_modes");
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
    tag.putInt("energy", this.energy);
    tag.put("inventory", this.inventory.serializeNBT(registries));
    tag.putIntArray("face_modes", serializeFaceModes());
  }

  private int[] serializeFaceModes() {
    int[] modes = new int[6];
    for (Direction direction : Direction.values()) {
      modes[direction.get3DDataValue()] =
        getFaceMode(direction).ordinal();
    }
    return modes;
  }

  private void loadFaceModes(int[] modes) {
    if (modes.length != 6) {
      return;
    }
    EnergyFaceMode[] values = EnergyFaceMode.values();
    for (Direction direction : Direction.values()) {
      int ordinal = modes[direction.get3DDataValue()];
      if (ordinal >= 0 && ordinal < values.length) {
        faceModes[direction.get3DDataValue()] = values[ordinal];
      }
    }
  }

  @Override
  public void loadAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.loadAdditional(tag, registries);
    this.energy = Mth.clamp(tag.getInt("energy"), 0, CAPACITY);
    if (tag.contains("inventory")) {
      this.inventory.deserializeNBT(
        registries,
        tag.getCompound("inventory")
      );
    }

    loadFaceModes(tag.getIntArray("face_modes"));
  }

  /**
   * 太阳能缓存的实际能量操作；面模式由 {@link EnergyFaceStorage} 门控。
   *
   * <p>Backend buffer storage. Face modes are applied by
   * {@link EnergyFaceStorage}.
   */
  private final class SolarBufferEnergyStorage implements IEnergyStorage {

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
      if (toReceive <= 0 || SolarGeneratorMk0BlockEntity.this.energy >= CAPACITY) {
        return 0;
      }

      int accepted = Math.min(
        CAPACITY - SolarGeneratorMk0BlockEntity.this.energy,
        Math.min(OUTPUT_PER_SIDE, toReceive)
      );
      if (accepted <= 0) {
        return 0;
      }
      if (!simulate) {
        SolarGeneratorMk0BlockEntity.this.energy += accepted;
        setChanged();
      }
      return accepted;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
      if (toExtract <= 0 || SolarGeneratorMk0BlockEntity.this.energy <= 0) {
        return 0;
      }

      int charged = 0;
      if (!simulate) {
        charged = chargeOutputItem();
      }

      int extracted = Math.min(
        SolarGeneratorMk0BlockEntity.this.energy,
        Math.min(OUTPUT_PER_SIDE, toExtract)
      );
      if (extracted <= 0) {
        if (charged > 0) {
          setChanged();
        }
        return 0;
      }

      if (!simulate) {
        SolarGeneratorMk0BlockEntity.this.energy -= extracted;
        setChanged();
      }
      return extracted;
    }

    @Override
    public int getEnergyStored() {
      return SolarGeneratorMk0BlockEntity.this.energy;
    }

    @Override
    public int getMaxEnergyStored() {
      return CAPACITY;
    }

    @Override
    public boolean canExtract() {
      return true;
    }

    @Override
    public boolean canReceive() {
      return true;
    }
  }
}
