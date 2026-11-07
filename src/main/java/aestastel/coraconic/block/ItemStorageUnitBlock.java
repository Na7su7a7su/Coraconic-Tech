/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

import aestastel.coraconic.blockentity.ModBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 物品存储单元方块：把方块与世界中的 {@link ModBlockEntity} 关联起来。
 *
 * <p>三个等级共用本类，通过构造参数传入方块实体类型与容量。方块布局始终
 * 为 9 行，等级越高列数越多：
 * <ul>
 *   <li>MK1：9 行 × 9 列 = 81 格；</li>
 *   <li>MK2：9 行 × 18 列 = 162 格；</li>
 *   <li>MK3：9 行 × 27 列 = 243 格。</li>
 * </ul>
 *
 * <p>Item Storage Unit block shared by all tiers. The block entity type and capacity
 * are supplied by the constructor. All tiers keep 9 rows and only widen the
 * number of columns.
 */
public class ItemStorageUnitBlock extends Block implements EntityBlock {

  public static final int SLOTS_MK1 = 81;
  public static final int SLOTS_MK2 = 162;
  public static final int SLOTS_MK3 = 243;

  public static final DirectionProperty FACING =
    HorizontalDirectionalBlock.FACING;

  /** 方块实体类型来源；延迟求值，避免方块与方块实体注册相互初始化。 */
  private final Supplier<BlockEntityType<?>> typeSupplier;
  /** 本等级的总槽位数。 */
  private final int slots;

  public ItemStorageUnitBlock(
    Properties properties,
    Supplier<BlockEntityType<?>> typeSupplier,
    int slots
  ) {
    super(properties);
    this.typeSupplier = typeSupplier;
    this.slots = slots;
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

  /**
   * 在方块被放置时创建方块实体实例。
   *
   * <p>类型与容量在创建时从构造参数取得，三个等级复用同一个方块实体类。
   *
   * <p>Creates the block entity instance, passing the tier's type and capacity.
   */
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ModBlockEntity(typeSupplier.get(), pos, state, slots);
  }

  /** {@return 本等级对应的方块实体类型} */
  public BlockEntityType<?> getBlockEntityType() {
    return typeSupplier.get();
  }

  /** {@return 本等级的总槽位数} */
  public int getSlots() {
    return slots;
  }

  /**
   * 未使用物品右键时打开容器界面。
   *
   * <p>除方块坐标外还会发送总槽位数，使客户端在没有加载方块实体时也能构建
   * 尺寸正确的空容器，避免菜单校验失败。
   *
   * <p>Opens the container GUI. The total slot count is sent alongside the
   * position so the client can build a correctly sized fallback container.
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
    if (level.getBlockEntity(pos) instanceof ModBlockEntity blockEntity) {
      player.openMenu(blockEntity, buffer -> {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(slots);
      });
    }
    return InteractionResult.CONSUME;
  }

  /**
   * 创造模式挖掘时手动掉落带内容的物品。
   *
   * <p>创造模式不触发战利品表，因此需要像潜影盒一样手动生成一个带有
   * {@code minecraft:container} 组件的物品实体；生存模式仍由战利品表处理。
   *
   * <p>In creative mode loot tables are not used, so an item carrying the
   * {@code minecraft:container} component is spawned manually, mirroring shulker
   * boxes. Survival mode is handled by the loot table.
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
      level.getBlockEntity(pos) instanceof ModBlockEntity blockEntity &&
      !blockEntity.isEmpty()
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
   * 中键选取方块时保留内容。
   *
   * <p>使用 NeoForge 提供的敏感版本（带 {@link HitResult} 与玩家参数），
   * 通过 {@link ModBlockEntity#saveToItem} 把库存写入物品组件。
   *
   * <p>Preserves contents when the block is picked, using NeoForge's
   * hit-result-aware overload and writing the inventory into the item component.
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
    if (level.getBlockEntity(pos) instanceof ModBlockEntity blockEntity) {
      blockEntity.saveToItem(stack, level.registryAccess());
    }
    return stack;
  }
}
