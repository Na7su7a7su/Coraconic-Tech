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

public class ModBlockLootTablesProvider extends BlockLootSubProvider {

  public ModBlockLootTablesProvider(HolderLookup.Provider registries) {
    super(Set.of(), FeatureFlags.VANILLA_SET, registries);
  }

  @Override
  protected void generate() {
    dropSelf(ModBlocks.TIN_BLOCK.get());
    dropSelf(ModBlocks.LEAD_BLOCK.get());
    dropSelf(ModBlocks.CORACONIC_BLOCK.get());
    dropSelf(ModBlocks.AVIRACONIC_BLOCK.get());

    add(ModBlocks.TIN_ORE.get(), this::createTinOreDrops);
    add(ModBlocks.DEEPSLAT_TIN_ORE.get(), this::createTinOreDrops);
    add(
      ModBlocks.LEAD_ORE.get(),
      block -> createOreDrop(block, ModItems.RAW_LEAD.get())
    );
    add(
      ModBlocks.DEEPSLAT_LEAD_ORE.get(),
      block -> createOreDrop(block, ModItems.RAW_LEAD.get())
    );
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return ModBlocks.BLOCK.getEntries()
      .stream()
      .<Block>map(holder -> holder.get())
      .toList();
  }

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
