package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.block.ModBlocks;
import com.aestastel.coraconic.item.ModItems;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

/**
 * 方块战利品表生成器：定义金属块与锡/铅矿石的掉落规则。
 *
 * <p>Block loot table provider: defines drop rules for metal blocks and tin/lead ores.
 */
public class ModBlockLootTablesProvider extends BlockLootSubProvider {

  public ModBlockLootTablesProvider(HolderLookup.Provider registries) {
    // 无爆炸抗性方块，使用原版特性标记集。
    // No explosion-resistant blocks; use the vanilla feature flag set.
    super(Set.of(), FeatureFlags.VANILLA_SET, registries);
  }

  /**
   * 注册所有模组方块的战利品表。
   *
   * <p>Registers loot tables for all modded blocks.
   */
  @Override
  protected void generate() {
    // 金属块：掉落自身，并受爆炸存活条件保护。
    // Metal blocks: drop themselves, guarded by the survives-explosion condition.
    dropSelf(ModBlocks.TIN_BLOCK.get());
    dropSelf(ModBlocks.LEAD_BLOCK.get());
    dropSelf(ModBlocks.CORACONIC_BLOCK.get());
    dropSelf(ModBlocks.AVIRACONIC_BLOCK.get());

    // 锡矿：精准采集掉矿石，否则掉 2-5 个粗锡。
    // Tin ores: silk touch drops the ore; otherwise drops 2-5 raw tin.
    add(ModBlocks.TIN_ORE.get(), this::createTinOreDrops);
    add(ModBlocks.DEEPSLAT_TIN_ORE.get(), this::createTinOreDrops);

    // 铅矿：精准采集掉矿石，否则掉 1 个粗铅。
    // Lead ores: silk touch drops the ore; otherwise drops 1 raw lead.
    add(
      ModBlocks.LEAD_ORE.get(),
      block -> createOreDrop(block, ModItems.RAW_LEAD.get())
    );
    add(
      ModBlocks.DEEPSLAT_LEAD_ORE.get(),
      block -> createOreDrop(block, ModItems.RAW_LEAD.get())
    );
  }

  /**
   * 仅遍历本模组方块，避免为原版方块要求战利品表。
   *
   * <p>Iterates only this mod's blocks to avoid requiring loot tables for vanilla blocks.
   */
  @Override
  protected Iterable<Block> getKnownBlocks() {
    return ModBlocks.BLOCK.getEntries()
      .stream()
      .<Block>map(holder -> holder.get())
      .toList();
  }

  /**
   * 生成锡矿掉落：2-5 个粗锡，支持时运与爆炸衰减。
   *
   * <p>Builds tin ore drops: 2-5 raw tin with fortune and explosion decay support.
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
}
