package com.aestastel.coraconic.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 物品存储方块：把方块与世界中的 {@link ModBlockEntity} 关联起来。
 *
 * <p>三个等级共用本类，通过构造参数传入方块实体类型与容量。方块布局始终
 * 为 9 行，等级越高列数越多：
 * <ul>
 *   <li>MK0：9 行 × 9 列 = 81 格；</li>
 *   <li>MK1：9 行 × 18 列 = 162 格；</li>
 *   <li>MK2：9 行 × 27 列 = 243 格。</li>
 * </ul>
 *
 * <p>Item storage block shared by all tiers. The block entity type and capacity
 * are supplied by the constructor. All tiers keep 9 rows and only widen the
 * number of columns.
 */
public class ItemStorageBlock extends Block implements EntityBlock {

  public static final int SLOTS_MK0 = 81;
  public static final int SLOTS_MK1 = 162;
  public static final int SLOTS_MK2 = 243;

  /** 方块实体类型来源；延迟求值，避免方块与方块实体注册相互初始化。 */
  private final Supplier<BlockEntityType<?>> typeSupplier;
  /** 本等级的总槽位数。 */
  private final int slots;

  public ItemStorageBlock(
    Properties properties,
    Supplier<BlockEntityType<?>> typeSupplier,
    int slots
  ) {
    super(properties);
    this.typeSupplier = typeSupplier;
    this.slots = slots;
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
   * 方块被移除时掉落容器内的全部物品。
   *
   * <p>{@link Containers#dropContentsOnDestroy} 会处理玩家挖掘、爆炸、活塞等
   * 所有移除途径，并在掉落完成后清空容器，避免物品被复制。
   *
   * <p>Drops every stored item when the block is removed, covering mining,
   * explosions, and pistons, then clears the container to avoid duplication.
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
