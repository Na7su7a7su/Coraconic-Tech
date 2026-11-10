/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.datagen;

import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.item.ModItems;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

/**
 * 方块战利品表数据生成器，负责定义本模组金属块、锡矿与铅矿的掉落规则。
 *
 * <p>Block loot table data provider responsible for defining the drop rules of this mod's metal
 * blocks, tin ores, and lead ores.
 */
public class ModBlockLootTablesProvider extends BlockLootSubProvider {

  /**
   * 构造生成器：本模组没有爆炸抗性方块，因此传入空集合与原版特性标记集。
   *
   * <p>Constructs the provider: the mod has no explosion-resistant blocks, so an empty set and the
   * vanilla feature flag set are used.
   *
   * @param registries 注册表提供者，用于查询附魔等数据 / registry provider for enchantment lookups
   */
  public ModBlockLootTablesProvider(HolderLookup.Provider registries) {
    super(Set.of(), FeatureFlags.VANILLA_SET, registries);
  }

  /**
   * 生成本模组全部方块的战利品表。
   *
   * <p>Generates loot tables for every block in this mod.
   */
  @Override
  protected void generate() {
    // 金属块使用 dropSelf：正常破坏时掉落自身，并附带爆炸衰减保护。
    // Metal blocks use dropSelf: they drop themselves when mined normally, with explosion decay.
    dropSelf(ModBlocks.TIN_BLOCK.get());
    dropSelf(ModBlocks.LEAD_BLOCK.get());
    dropSelf(ModBlocks.CORACONIC_METAL_BLOCK.get());
    dropSelf(ModBlocks.AVIRACONIC_METAL_BLOCK.get());

    // 物品存储单元：掉落自身并把容器内容复制到物品组件（类似潜影盒）。
    // Item storage units drop themselves and copy their contents into the item
    // components (like shulker boxes).
    add(
      ModBlocks.ITEM_STORAGE_UNIT_MK1.get(),
      createShulkerBoxDrop(ModBlocks.ITEM_STORAGE_UNIT_MK1.get())
    );
    add(
      ModBlocks.ITEM_STORAGE_UNIT_MK2.get(),
      createShulkerBoxDrop(ModBlocks.ITEM_STORAGE_UNIT_MK2.get())
    );
    add(
      ModBlocks.ITEM_STORAGE_UNIT_MK3.get(),
      createShulkerBoxDrop(ModBlocks.ITEM_STORAGE_UNIT_MK3.get())
    );

    // 能量存储单元：掉落自身并复制方块实体数据（能量与两个机器槽位）。
    // Energy storage units drop themselves and copy the block entity data
    // (stored energy and both machine slots).
    add(
      ModBlocks.ENERGY_STORAGE_UNIT_MK1.get(),
      createBlockEntityDataDrop(ModBlocks.ENERGY_STORAGE_UNIT_MK1.get())
    );
    add(
      ModBlocks.ENERGY_STORAGE_UNIT_MK2.get(),
      createBlockEntityDataDrop(ModBlocks.ENERGY_STORAGE_UNIT_MK2.get())
    );
    add(
      ModBlocks.ENERGY_STORAGE_UNIT_MK3.get(),
      createBlockEntityDataDrop(ModBlocks.ENERGY_STORAGE_UNIT_MK3.get())
    );

    // 合金仪：掉落自身并复制缓存能量与四个槽位。
    // Alloying machine: drops itself with buffered energy and all four slots.
    add(
      ModBlocks.ALLOYING_MACHINE_MK1.get(),
      createBlockEntityDataDrop(ModBlocks.ALLOYING_MACHINE_MK1.get())
    );
    add(
      ModBlocks.ALLOYING_MACHINE_MK2.get(),
      createBlockEntityDataDrop(ModBlocks.ALLOYING_MACHINE_MK2.get())
    );
    add(
      ModBlocks.RESONANCE_CRYSTALLIZING_MACHINE.get(),
      createBlockEntityDataDrop(
        ModBlocks.RESONANCE_CRYSTALLIZING_MACHINE.get()
      )
    );
    add(
      ModBlocks.SMELTING_METALLURGY_MACHINE_MK1.get(),
      createBlockEntityDataDrop(
        ModBlocks.SMELTING_METALLURGY_MACHINE_MK1.get()
      )
    );

    // 太阳能发电机：掉落自身并复制缓存能量；输出槽内容在方块移除时单独掉落。
    // Solar generator: drops itself with buffered energy; output-slot contents
    // drop separately when the block is removed.
    add(
      ModBlocks.SOLAR_GENERATOR_MK0.get(),
      createBlockEntityDataDrop(ModBlocks.SOLAR_GENERATOR_MK0.get())
    );
    add(
      ModBlocks.GEOMAGNETIC_GENERATOR_MK0.get(),
      createBlockEntityDataDrop(ModBlocks.GEOMAGNETIC_GENERATOR_MK0.get())
    );
    add(
      ModBlocks.GEOMAGNETIC_GENERATOR_MK1.get(),
      createBlockEntityDataDrop(ModBlocks.GEOMAGNETIC_GENERATOR_MK1.get())
    );
    add(
      ModBlocks.GEOMAGNETIC_GENERATOR_MK2.get(),
      createBlockEntityDataDrop(ModBlocks.GEOMAGNETIC_GENERATOR_MK2.get())
    );
    add(
      ModBlocks.GEOMAGNETIC_GENERATOR_MK3.get(),
      createBlockEntityDataDrop(ModBlocks.GEOMAGNETIC_GENERATOR_MK3.get())
    );
    add(
      ModBlocks.SOLAR_GENERATOR_MK1.get(),
      createBlockEntityDataDrop(ModBlocks.SOLAR_GENERATOR_MK1.get())
    );
    add(
      ModBlocks.SOLAR_GENERATOR_MK2.get(),
      createBlockEntityDataDrop(ModBlocks.SOLAR_GENERATOR_MK2.get())
    );
    add(
      ModBlocks.SOLAR_GENERATOR_MK3.get(),
      createBlockEntityDataDrop(ModBlocks.SOLAR_GENERATOR_MK3.get())
    );

    // 锡矿石：精准采集掉落矿石本身，否则掉落 2-5 个粗锡；深层变种规则相同。
    // Tin ores: silk touch drops the ore itself; otherwise 2-5 raw tin. The deepslate variant
    // shares the same rule.
    add(ModBlocks.TIN_ORE.get(), this::createTinOreDrops);
    add(ModBlocks.DEEPSLAT_TIN_ORE.get(), this::createTinOreDrops);

    // 铅矿石：精准采集掉落矿石本身，否则掉落 1-2 个粗铅；深层变种规则相同。
    // Lead ores: silk touch drops the ore itself; otherwise 1-2 raw lead. The deepslate variant
    // shares the same rule.
    add(ModBlocks.LEAD_ORE.get(), this::createLeadOreDrops);
    add(ModBlocks.DEEPSLAT_LEAD_ORE.get(), this::createLeadOreDrops);
  }

  /**
   * 仅返回本模组注册的方块，防止数据生成器要求原版方块也提供战利品表。
   *
   * <p>Returns only blocks registered by this mod, preventing the provider from requiring loot
   * tables for vanilla blocks.
   */
  @Override
  protected Iterable<Block> getKnownBlocks() {
    return ModBlocks.BLOCK.getEntries()
      .stream()
      .<Block>map(holder -> holder.get())
      .toList();
  }

  /**
   * 构建方块实体数据掉落表：掉落自身并复制 block_entity_data 组件。
   *
   * <p>Builds a loot table that drops the block and copies its block entity data
   * component. Energy storage units include energy and slots; the solar
   * generator includes only buffered energy.
   */
  private LootTable.Builder createBlockEntityDataDrop(Block block) {
    return LootTable.lootTable()
      .withPool(
        this.applyExplosionCondition(
          block,
          LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1.0F))
            .add(
              LootItem.lootTableItem(block)
                .apply(
                  CopyComponentsFunction.copyComponents(
                    CopyComponentsFunction.Source.BLOCK_ENTITY
                  )
                    .include(DataComponents.CUSTOM_NAME)
                    .include(DataComponents.BLOCK_ENTITY_DATA)
                )
            )
        )
      );
  }

  /**
   * 构建锡矿掉落表：精准采集时掉落矿石本身，否则掉落 2-5 个粗锡，并应用时运加成与爆炸衰减。
   *
   * <p>Builds the tin ore loot table: silk touch drops the ore itself; otherwise 2-5 raw tin with
   * fortune bonus and explosion decay applied.
   */
  private LootTable.Builder createTinOreDrops(Block block) {
    HolderLookup.RegistryLookup<Enchantment> enchantments =
      this.registries.lookupOrThrow(Registries.ENCHANTMENT);
    return this.createSilkTouchDispatchTable(
      block,
      (LootPoolEntryContainer.Builder<?>) this.applyExplosionDecay(
        block,
        LootItem.lootTableItem(ModItems.RAW_TIN.get())
          .apply(
            SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))
          )
          .apply(
            ApplyBonusCount.addOreBonusCount(
              enchantments.getOrThrow(Enchantments.FORTUNE)
            )
          )
      )
    );
  }

  /**
   * 构建铅矿掉落表：精准采集时掉落矿石本身，否则掉落 1-2 个粗铅，并应用时运加成与爆炸衰减。
   *
   * <p>Builds the lead ore loot table: silk touch drops the ore itself; otherwise 1-2 raw lead with
   * fortune bonus and explosion decay applied.
   */
  private LootTable.Builder createLeadOreDrops(Block block) {
    HolderLookup.RegistryLookup<Enchantment> enchantments =
      this.registries.lookupOrThrow(Registries.ENCHANTMENT);
    return this.createSilkTouchDispatchTable(
      block,
      (LootPoolEntryContainer.Builder<?>) this.applyExplosionDecay(
        block,
        LootItem.lootTableItem(ModItems.RAW_LEAD.get())
          .apply(
            SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))
          )
          .apply(
            ApplyBonusCount.addOreBonusCount(
              enchantments.getOrThrow(Enchantments.FORTUNE)
            )
          )
      )
    );
  }
}
