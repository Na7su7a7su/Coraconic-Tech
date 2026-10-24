package com.aestastel.coraconic.block;

import com.aestastel.coraconic.CoraconicTech;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块实体类型注册器：声明物品存储单元与能量存储单元的 {@link BlockEntityType}。
 *
 * <p>物品存储三个等级共用 {@link ModBlockEntity}，能量存储三个等级共用
 * {@link EnergyStorageUnitBlockEntity}；工厂方法从方块状态读取所属方块，
 * 再取得该等级的类型、容量与速率，避免在字段初始化器里引用自身。
 *
 * <p>Block entity type registry for the item storage and energy storage units.
 * The factories read the owning block from the block state to obtain the tier's
 * type, capacity and transfer rate.
 */
public class ModBlockEntityType {

  /**
   * 方块实体类型延迟注册器。
   *
   * <p>Deferred block entity type register.
   */
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
    DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CoraconicTech.MODID);

  /**
   * 物品存储单元 MK1：81 格（9 行 × 9 列）。
   *
   * <p>Item Storage Unit MK1: 81 slots (9 rows × 9 columns).
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<ModBlockEntity>
  > ITEM_STORAGE_UNIT_MK1_ENTITY = BLOCK_ENTITY_TYPES.register(
    "item_storage_unit_mk1_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createBlockEntity,
        ModBlocks.ITEM_STORAGE_UNIT_MK1.get()
      ).build(null)
  );

  /**
   * 物品存储单元 MK2：162 格（9 行 × 18 列）。
   *
   * <p>Item Storage Unit MK2: 162 slots (9 rows × 18 columns).
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<ModBlockEntity>
  > ITEM_STORAGE_UNIT_MK2_ENTITY = BLOCK_ENTITY_TYPES.register(
    "item_storage_unit_mk2_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createBlockEntity,
        ModBlocks.ITEM_STORAGE_UNIT_MK2.get()
      ).build(null)
  );

  /**
   * 物品存储单元 MK3：243 格（9 行 × 27 列）。
   *
   * <p>Item Storage Unit MK3: 243 slots (9 rows × 27 columns).
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<ModBlockEntity>
  > ITEM_STORAGE_UNIT_MK3_ENTITY = BLOCK_ENTITY_TYPES.register(
    "item_storage_unit_mk3_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createBlockEntity,
        ModBlocks.ITEM_STORAGE_UNIT_MK3.get()
      ).build(null)
  );

  /**
   * 能量存储单元 MK1：32,000,000 FE，1,000 FE/t。
   *
   * <p>Energy Storage Unit MK1: 32,000,000 FE, 1,000 FE/t.
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<EnergyStorageUnitBlockEntity>
  > ENERGY_STORAGE_UNIT_MK1_ENTITY = BLOCK_ENTITY_TYPES.register(
    "energy_storage_unit_mk1_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createEnergyBlockEntity,
        ModBlocks.ENERGY_STORAGE_UNIT_MK1.get()
      ).build(null)
  );

  /**
   * 能量存储单元 MK2：320,000,000 FE，4,000 FE/t。
   *
   * <p>Energy Storage Unit MK2: 320,000,000 FE, 4,000 FE/t.
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<EnergyStorageUnitBlockEntity>
  > ENERGY_STORAGE_UNIT_MK2_ENTITY = BLOCK_ENTITY_TYPES.register(
    "energy_storage_unit_mk2_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createEnergyBlockEntity,
        ModBlocks.ENERGY_STORAGE_UNIT_MK2.get()
      ).build(null)
  );

  /**
   * 能量存储单元 MK3：3,200,000,000 FE（3.2G），16,000 FE/t。
   *
   * <p>Energy Storage Unit MK3: 3,200,000,000 FE (3.2G), 16,000 FE/t.
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<EnergyStorageUnitBlockEntity>
  > ENERGY_STORAGE_UNIT_MK3_ENTITY = BLOCK_ENTITY_TYPES.register(
    "energy_storage_unit_mk3_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createEnergyBlockEntity,
        ModBlocks.ENERGY_STORAGE_UNIT_MK3.get()
      ).build(null)
  );

  /**
   * 方块实体工厂：根据方块状态取得对应等级的类型与容量。
   *
   * <p>方块实体类型只会注册给它自己的方块，因此这里的类型转换是安全的。
   *
   * <p>Block entity factory: resolves the tier's type and capacity from the
   * block state. The cast is safe because each type only accepts its own block.
   */
  private static ModBlockEntity createBlockEntity(
    BlockPos pos,
    BlockState state
  ) {
    if (state.getBlock() instanceof ItemStorageUnitBlock storageBlock) {
      return new ModBlockEntity(
        storageBlock.getBlockEntityType(),
        pos,
        state,
        storageBlock.getSlots()
      );
    }
    throw new IllegalStateException(
      "Unexpected block for item storage unit block entity: " + state
    );
  }

  /**
   * 能量方块实体工厂：根据方块状态取得对应等级的容量与速率。
   *
   * <p>Energy block entity factory: resolves the tier's capacity and transfer
   * rate from the block state.
   */
  private static EnergyStorageUnitBlockEntity createEnergyBlockEntity(
    BlockPos pos,
    BlockState state
  ) {
    if (state.getBlock() instanceof EnergyStorageUnitBlock energyBlock) {
      return new EnergyStorageUnitBlockEntity(
        energyBlock.getBlockEntityType(),
        pos,
        state,
        energyBlock.getCapacity(),
        energyBlock.getTransfer()
      );
    }
    throw new IllegalStateException(
      "Unexpected block for energy storage unit block entity: " + state
    );
  }

  /**
   * 将方块实体类型延迟注册器挂到模组事件总线。
   *
   * <p>Attaches the deferred register to the mod event bus.
   *
   * @param eventBus 模组专用事件总线 / mod event bus
   */
  public static void register(IEventBus eventBus) {
    BLOCK_ENTITY_TYPES.register(eventBus);
  }
}
