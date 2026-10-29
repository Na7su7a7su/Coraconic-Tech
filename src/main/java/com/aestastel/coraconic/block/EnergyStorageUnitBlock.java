/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 能量存储单元方块：创建方块实体、提供 ticker 并打开能量界面。
 *
 * <p>三个等级共用本类，容量与传输速率由构造参数传入。ticker 只在服务端
 * 运行，用于处理充电/放电槽的物品能量交换。
 *
 * <p>Energy storage unit block shared by all tiers. It creates the block entity,
 * provides a server-side ticker for item charge/discharge, and opens the GUI.
 */
public class EnergyStorageUnitBlock extends Block implements EntityBlock {

  public static final DirectionProperty FACING =
    HorizontalDirectionalBlock.FACING;

  private final Supplier<BlockEntityType<?>> typeSupplier;
  private final long capacity;
  private final int transfer;

  public EnergyStorageUnitBlock(
    Properties properties,
    Supplier<BlockEntityType<?>> typeSupplier,
    long capacity,
    int transfer
  ) {
    super(properties);
    this.typeSupplier = typeSupplier;
    this.capacity = capacity;
    this.transfer = transfer;
    registerDefaultState(
      this.stateDefinition.any().setValue(FACING, Direction.NORTH)
    );
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    builder.add(FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(
      FACING,
      context.getHorizontalDirection().getOpposite()
    );
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new EnergyStorageUnitBlockEntity(
      typeSupplier.get(),
      pos,
      state,
      capacity,
      transfer
    );
  }

  /**
   * 服务端 ticker：驱动方块实体的充电/放电逻辑。
   *
   * <p>Server ticker driving the block entity's charge/discharge logic.
   */
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
    Level level,
    BlockState state,
    BlockEntityType<T> type
  ) {
    if (level.isClientSide) {
      return null;
    }
    return (tickLevel, pos, tickState, blockEntity) -> {
      if (blockEntity instanceof EnergyStorageUnitBlockEntity energyUnit) {
        energyUnit.tick();
      }
    };
  }

  /** {@return 本等级对应的方块实体类型} */
  public BlockEntityType<?> getBlockEntityType() {
    return typeSupplier.get();
  }

  /** {@return 本等级的容量（FE）} */
  public long getCapacity() {
    return capacity;
  }

  /** {@return 本等级的输入/输出上限（FE/t）} */
  public int getTransfer() {
    return transfer;
  }

  /**
   * 未使用物品右键时打开能量界面。
   *
   * <p>Opens the energy GUI with the block position and capacity.
   */
  @Override
  protected InteractionResult useWithoutItem(
    BlockState state,
    Level level,
    BlockPos pos,
    Player player,
    BlockHitResult hitResult
  ) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    if (level.getBlockEntity(pos) instanceof EnergyStorageUnitBlockEntity blockEntity) {
      player.openMenu(blockEntity, buffer -> {
        buffer.writeBlockPos(pos);
        buffer.writeLong(capacity);
      });
    }
    return InteractionResult.CONSUME;
  }

  /**
   * 创造模式挖掘时手动掉落带内容的物品。
   *
   * <p>创造模式不触发战利品表，因此需要像潜影盒一样手动生成一个带有
   * {@code minecraft:block_entity_data} 组件的物品实体；该组件包含能量与
   * 两个机器槽位。生存模式仍由战利品表处理。
   *
   * <p>In creative mode loot tables are not used, so an item carrying the
   * {@code minecraft:block_entity_data} component (energy and both machine
   * slots) is spawned manually, mirroring shulker boxes. Survival mode is
   * handled by the loot table.
   */
  @Override
  public BlockState playerWillDestroy(
    Level level,
    BlockPos pos,
    BlockState state,
    Player player
  ) {
    if (
      !level.isClientSide &&
      player.isCreative() &&
      level.getBlockEntity(pos) instanceof
        EnergyStorageUnitBlockEntity blockEntity &&
      (blockEntity.getEnergyStoredLong() > 0L ||
        !blockEntity.isEmpty() ||
        blockEntity.hasNonDefaultFaceModes())
    ) {
      ItemStack stack = new ItemStack(this);
      blockEntity.saveToItem(stack, level.registryAccess());
      ItemEntity entity = new ItemEntity(
        level,
        pos.getX() + 0.5,
        pos.getY() + 0.5,
        pos.getZ() + 0.5,
        stack
      );
      entity.setDefaultPickUpDelay();
      level.addFreshEntity(entity);
    }
    return super.playerWillDestroy(level, pos, state, player);
  }

  /**
   * 中键选取方块时保留能量与机器槽位内容。
   *
   * <p>Preserves energy and machine slot contents when the block is picked.
   */
  @Override
  public ItemStack getCloneItemStack(
    BlockState state,
    HitResult target,
    LevelReader level,
    BlockPos pos,
    Player player
  ) {
    ItemStack stack = new ItemStack(this);
    if (
      level.getBlockEntity(pos) instanceof
        EnergyStorageUnitBlockEntity blockEntity
    ) {
      blockEntity.saveToItem(stack, level.registryAccess());
    }
    return stack;
  }
}
