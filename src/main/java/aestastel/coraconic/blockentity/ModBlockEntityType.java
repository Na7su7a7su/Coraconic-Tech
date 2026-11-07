/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.blockentity;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.block.AlloyingMachineBlock;
import aestastel.coraconic.block.EnergyStorageUnitBlock;
import aestastel.coraconic.block.GeomagneticGeneratorBlock;
import aestastel.coraconic.block.ItemStorageUnitBlock;
import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.block.ResonanceCrystallizingMachineBlock;
import aestastel.coraconic.block.SolarGeneratorBlock;
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
   * 能量存储单元 MK1：32,000,000 FE，4,000,000 FE/t。
   *
   * <p>Energy Storage Unit MK1: 32,000,000 FE, 4,000,000 FE/t.
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
   * 能量存储单元 MK2：320,000,000 FE，16,000,000 FE/t。
   *
   * <p>Energy Storage Unit MK2: 320,000,000 FE, 16,000,000 FE/t.
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
   * 能量存储单元 MK3：3,200,000,000 FE（3.2G），64,000,000 FE/t。
   *
   * <p>Energy Storage Unit MK3: 3,200,000,000 FE (3.2G), 64,000,000 FE/t.
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
   * 合金仪 MK1。
   *
   * <p>Alloying Machine MK1.
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<AlloyingMachineBlockEntity>
  > ALLOYING_MACHINE_MK1_ENTITY = BLOCK_ENTITY_TYPES.register(
    "alloying_machine_mk1_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createAlloyingMachineBlockEntity,
        ModBlocks.ALLOYING_MACHINE_MK1.get()
      ).build(null)
  );

  /**
   * 共振晶化仪 MK1。
   *
   * <p>Resonance Crystallizing Machine MK1.
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<ResonanceCrystallizingMachineBlockEntity>
  > RESONANCE_CRYSTALLIZING_MACHINE_MK1_ENTITY =
    BLOCK_ENTITY_TYPES.register(
      "resonance_crystallizing_machine_mk1_entity",
      () ->
        BlockEntityType.Builder.of(
          ModBlockEntityType::createResonanceCrystallizingMachineBlockEntity,
          ModBlocks.RESONANCE_CRYSTALLIZING_MACHINE_MK1.get()
        ).build(null)
    );

  /**
   * 太阳能发电机 MK0–MK3：共用方块实体逻辑，等级由所属方块提供。
   *
   * <p>Solar Generator MK0–MK3: shared block entity logic with the tier
   * supplied by the owning block.
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<SolarGeneratorBlockEntity>
  > SOLAR_GENERATOR_MK0_ENTITY = BLOCK_ENTITY_TYPES.register(
    "solar_generator_mk0_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createSolarGeneratorBlockEntity,
        ModBlocks.SOLAR_GENERATOR_MK0.get()
      ).build(null)
  );

  /**
   * 地磁发电机 MK0：复用太阳能方块实体逻辑，独立注册便于能力绑定。
   *
   * <p>Geomagnetic Generator MK0: reuses the solar block entity logic and is
   * registered separately for capability binding.
   */
  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<GeomagneticGeneratorBlockEntity>
  > GEOMAGNETIC_GENERATOR_MK0_ENTITY = BLOCK_ENTITY_TYPES.register(
    "geomagnetic_generator_mk0_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createGeomagneticGeneratorBlockEntity,
        ModBlocks.GEOMAGNETIC_GENERATOR_MK0.get()
      ).build(null)
  );

  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<GeomagneticGeneratorBlockEntity>
  > GEOMAGNETIC_GENERATOR_MK1_ENTITY = BLOCK_ENTITY_TYPES.register(
    "geomagnetic_generator_mk1_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createGeomagneticGeneratorBlockEntity,
        ModBlocks.GEOMAGNETIC_GENERATOR_MK1.get()
      ).build(null)
  );

  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<GeomagneticGeneratorBlockEntity>
  > GEOMAGNETIC_GENERATOR_MK2_ENTITY = BLOCK_ENTITY_TYPES.register(
    "geomagnetic_generator_mk2_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createGeomagneticGeneratorBlockEntity,
        ModBlocks.GEOMAGNETIC_GENERATOR_MK2.get()
      ).build(null)
  );

  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<GeomagneticGeneratorBlockEntity>
  > GEOMAGNETIC_GENERATOR_MK3_ENTITY = BLOCK_ENTITY_TYPES.register(
    "geomagnetic_generator_mk3_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createGeomagneticGeneratorBlockEntity,
        ModBlocks.GEOMAGNETIC_GENERATOR_MK3.get()
      ).build(null)
  );

  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<SolarGeneratorBlockEntity>
  > SOLAR_GENERATOR_MK1_ENTITY = BLOCK_ENTITY_TYPES.register(
    "solar_generator_mk1_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createSolarGeneratorBlockEntity,
        ModBlocks.SOLAR_GENERATOR_MK1.get()
      ).build(null)
  );

  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<SolarGeneratorBlockEntity>
  > SOLAR_GENERATOR_MK2_ENTITY = BLOCK_ENTITY_TYPES.register(
    "solar_generator_mk2_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createSolarGeneratorBlockEntity,
        ModBlocks.SOLAR_GENERATOR_MK2.get()
      ).build(null)
  );

  public static final DeferredHolder<
    BlockEntityType<?>,
    BlockEntityType<SolarGeneratorBlockEntity>
  > SOLAR_GENERATOR_MK3_ENTITY = BLOCK_ENTITY_TYPES.register(
    "solar_generator_mk3_entity",
    () ->
      BlockEntityType.Builder.of(
        ModBlockEntityType::createSolarGeneratorBlockEntity,
        ModBlocks.SOLAR_GENERATOR_MK3.get()
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
   * 合金仪方块实体工厂。
   *
   * <p>Alloying machine block entity factory.
   */
  private static AlloyingMachineBlockEntity createAlloyingMachineBlockEntity(
    BlockPos pos,
    BlockState state
  ) {
    if (state.getBlock() instanceof AlloyingMachineBlock machineBlock) {
      return new AlloyingMachineBlockEntity(
        machineBlock.getBlockEntityType(),
        pos,
        state
      );
    }
    throw new IllegalStateException(
      "Unexpected block for alloying machine block entity: " + state
    );
  }

  /**
   * 共振晶化仪方块实体工厂。
   *
   * <p>Resonance crystallizing machine block entity factory.
   */
  private static ResonanceCrystallizingMachineBlockEntity
  createResonanceCrystallizingMachineBlockEntity(
    BlockPos pos,
    BlockState state
  ) {
    if (
      state.getBlock() instanceof
        ResonanceCrystallizingMachineBlock machineBlock
    ) {
      return new ResonanceCrystallizingMachineBlockEntity(
        machineBlock.getBlockEntityType(),
        pos,
        state
      );
    }
    throw new IllegalStateException(
      "Unexpected block for resonance crystallizing machine block entity: " +
      state
    );
  }

  /**
   * 太阳能发电机方块实体工厂。
   *
   * <p>Solar generator block entity factory.
   */
  private static SolarGeneratorBlockEntity createSolarGeneratorBlockEntity(
    BlockPos pos,
    BlockState state
  ) {
    if (state.getBlock() instanceof SolarGeneratorBlock generatorBlock) {
      return new SolarGeneratorBlockEntity(
        generatorBlock.getBlockEntityType(),
        pos,
        state,
        generatorBlock.getTier()
      );
    }
    throw new IllegalStateException(
      "Unexpected block for solar generator block entity: " + state
    );
  }

  /**
   * 地磁发电机方块实体工厂。
   *
   * <p>Geomagnetic generator block entity factory.
   */
  private static GeomagneticGeneratorBlockEntity createGeomagneticGeneratorBlockEntity(
    BlockPos pos,
    BlockState state
  ) {
    if (state.getBlock() instanceof GeomagneticGeneratorBlock generatorBlock) {
      return new GeomagneticGeneratorBlockEntity(
        generatorBlock.getBlockEntityType(),
        pos,
        state,
        generatorBlock.getGeomagneticTier()
      );
    }
    throw new IllegalStateException(
      "Unexpected block for geomagnetic generator block entity: " + state
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
