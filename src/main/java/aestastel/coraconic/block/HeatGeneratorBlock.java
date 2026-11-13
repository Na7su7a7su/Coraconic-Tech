/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

import aestastel.coraconic.blockentity.HeatGeneratorBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 热力发电机 MK0 方块。
 *
 * <p>Heat Generator MK0 block. It creates the block entity, opens the GUI, and
 * preserves buffered energy, heat, and slot contents when broken.
 */
public class HeatGeneratorBlock extends Block implements EntityBlock {

  private final Supplier<BlockEntityType<?>> typeSupplier;

  public HeatGeneratorBlock(
    Properties properties,
    Supplier<BlockEntityType<?>> typeSupplier
  ) {
    super(properties);
    this.typeSupplier = typeSupplier;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new HeatGeneratorBlockEntity(
      this.typeSupplier.get(),
      pos,
      state
    );
  }

  public BlockEntityType<?> getBlockEntityType() {
    return this.typeSupplier.get();
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
      if (blockEntity instanceof HeatGeneratorBlockEntity generator) {
        generator.tick();
      }
    };
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
      level.getBlockEntity(pos) instanceof HeatGeneratorBlockEntity blockEntity
    ) {
      player.openMenu(blockEntity, buffer -> buffer.writeBlockPos(pos));
    }
    return InteractionResult.CONSUME;
  }

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
      level.getBlockEntity(pos) instanceof HeatGeneratorBlockEntity blockEntity &&
      (blockEntity.getEnergyStoredLong() > 0L ||
        blockEntity.getHeat() > 0 ||
        !blockEntity.isEmpty())
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
      level.getBlockEntity(pos) instanceof HeatGeneratorBlockEntity blockEntity
    ) {
      blockEntity.saveToItem(stack, level.registryAccess());
    }
    return stack;
  }

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
