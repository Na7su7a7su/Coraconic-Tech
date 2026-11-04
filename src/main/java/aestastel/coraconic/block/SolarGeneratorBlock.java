/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
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
 * 太阳能发电机 MK0–MK3 的共用方块。
 *
 * <p>方块本身只负责创建方块实体、驱动服务端 ticker 和打开界面；发电判定、
 * 缓存与面向外部的能量能力由 {@link SolarGeneratorBlockEntity} 处理。
 *
 * <p>Shared solar generator block for MK0–MK3. It creates the block entity,
 * runs the server-side ticker, and opens the GUI; generation and energy
 * handling live in {@link SolarGeneratorBlockEntity}.
 */
public class SolarGeneratorBlock extends Block implements EntityBlock {

  public static final DirectionProperty FACING =
    HorizontalDirectionalBlock.FACING;

  private final Supplier<BlockEntityType<?>> typeSupplier;
  private final SolarGeneratorTier tier;

  public SolarGeneratorBlock(
    Properties properties,
    Supplier<BlockEntityType<?>> typeSupplier,
    SolarGeneratorTier tier
  ) {
    super(properties);
    this.typeSupplier = typeSupplier;
    this.tier = tier;
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
    return new SolarGeneratorBlockEntity(
      typeSupplier.get(),
      pos,
      state,
      this.tier
    );
  }

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
      if (blockEntity instanceof SolarGeneratorBlockEntity generator) {
        generator.tick();
      }
    };
  }

  /** {@return 本方块对应的方块实体类型} */
  public BlockEntityType<?> getBlockEntityType() {
    return typeSupplier.get();
  }

  /** {@return 本方块对应的太阳能发电机等级} */
  public SolarGeneratorTier getTier() {
    return this.tier;
  }

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
    if (
      level.getBlockEntity(pos) instanceof
        SolarGeneratorBlockEntity blockEntity
    ) {
      player.openMenu(blockEntity, buffer -> {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(blockEntity.getCapacity());
        buffer.writeVarInt(blockEntity.getTier().ordinal());
        buffer.writeBoolean(blockEntity.isGeomagnetic());
      });
    }
    return InteractionResult.CONSUME;
  }

  /**
   * 创造模式破坏时手动掉落带缓存能量的方块物品。
   *
   * <p>In creative mode the loot table is not used, so an item carrying only
   * the buffered energy is spawned manually. Output-slot items still drop from
   * {@link #onRemove}.
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
        SolarGeneratorBlockEntity blockEntity &&
      (blockEntity.getEnergyStored() > 0 ||
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
   * 中键选取方块时保留缓存能量，但不复制输出槽物品。
   *
   * <p>Preserves buffered energy when pick-blocking, without copying the
   * output-slot item into the block item.
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
        SolarGeneratorBlockEntity blockEntity
    ) {
      blockEntity.saveToItem(stack, level.registryAccess());
    }
    return stack;
  }

  /**
   * 方块被移除时掉落输出槽中的物品。
   *
   * <p>Drops the output slot contents when the block is removed.
   */
  @Override
  protected void onRemove(
    BlockState state,
    Level level,
    BlockPos pos,
    BlockState newState,
    boolean movedByPiston
  ) {
    Containers.dropContentsOnDestroy(state, newState, level, pos);
    super.onRemove(state, level, pos, newState, movedByPiston);
  }
}
