/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.datagen;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.tag.ModBlockTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 方块标签生成器：定义金属块与矿石的挖掘工具和等级要求。
 *
 * <p>Block tag provider: defines mining tool and tier requirements for metal blocks and ores.
 */
public class ModBlockTagsProvider extends BlockTagsProvider {

  public ModBlockTagsProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> lookupProvider,
    ExistingFileHelper existingFileHelper
  ) {
    super(
      output,
      lookupProvider,
      CoraconicTech.MODID,
      existingFileHelper
    );
  }

  /**
   * 注册方块标签及其成员。
   *
   * <p>Registers block tags and their members.
   */
  @Override
  protected void addTags(HolderLookup.Provider provider) {
    // 通用矿石标签：按矿物类型与生成岩层归类。
    // Common ore tags: classify by mineral type and host stone.
    tag(ModBlockTags.ORES_TIN).add(
      ModBlocks.TIN_ORE.get(),
      ModBlocks.DEEPSLAT_TIN_ORE.get()
    );
    tag(ModBlockTags.ORES_LEAD).add(
      ModBlocks.LEAD_ORE.get(),
      ModBlocks.DEEPSLAT_LEAD_ORE.get()
    );
    tag(Tags.Blocks.ORES)
      .addTag(ModBlockTags.ORES_TIN)
      .addTag(ModBlockTags.ORES_LEAD);
    tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(
      ModBlocks.TIN_ORE.get(),
      ModBlocks.LEAD_ORE.get()
    );
    tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(
      ModBlocks.DEEPSLAT_TIN_ORE.get(),
      ModBlocks.DEEPSLAT_LEAD_ORE.get()
    );

    // 核龙元素的额外掉落来源：石头、深板岩、下界岩、末地石。
    // Extra sources of coraconic elements: stone, deepslate, netherrack, end stone.
    tag(ModBlockTags.ELEMENT_SOURCE_STONES).add(
      Blocks.STONE,
      Blocks.DEEPSLATE,
      Blocks.NETHERRACK,
      Blocks.END_STONE
    );

    // 通用存储块标签：注册金属块并挂到 c:storage_blocks 父标签。
    // Common storage block tags: register metal blocks and attach them to c:storage_blocks.
    tag(ModBlockTags.STORAGE_BLOCKS_TIN).add(ModBlocks.TIN_BLOCK.get());
    tag(ModBlockTags.STORAGE_BLOCKS_LEAD).add(ModBlocks.LEAD_BLOCK.get());
    tag(ModBlockTags.STORAGE_BLOCKS_CORACONIC).add(
      ModBlocks.CORACONIC_METAL_BLOCK.get()
    );
    tag(ModBlockTags.STORAGE_BLOCKS_AVIRACONIC).add(
      ModBlocks.AVIRACONIC_METAL_BLOCK.get()
    );
    tag(Tags.Blocks.STORAGE_BLOCKS)
      .addTag(ModBlockTags.STORAGE_BLOCKS_TIN)
      .addTag(ModBlockTags.STORAGE_BLOCKS_LEAD)
      .addTag(ModBlockTags.STORAGE_BLOCKS_CORACONIC)
      .addTag(ModBlockTags.STORAGE_BLOCKS_AVIRACONIC);

    // 四种金属块可以作为信标基座。
    // All four metal blocks can be used as beacon base blocks.
    tag(BlockTags.BEACON_BASE_BLOCKS).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get(),
      ModBlocks.CORACONIC_METAL_BLOCK.get(),
      ModBlocks.AVIRACONIC_METAL_BLOCK.get()
    );

    // 四种金属块与四种矿石可由镐加速挖掘。
    // All four metal blocks and all four ores can be mined faster with a pickaxe.
    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get(),
      ModBlocks.CORACONIC_METAL_BLOCK.get(),
      ModBlocks.AVIRACONIC_METAL_BLOCK.get(),
      ModBlocks.TIN_ORE.get(),
      ModBlocks.DEEPSLAT_TIN_ORE.get(),
      ModBlocks.LEAD_ORE.get(),
      ModBlocks.DEEPSLAT_LEAD_ORE.get()
    );

    // 锡块、铅块和四种矿石至少需要石镐。
    // Tin/lead blocks and all four ores require at least a stone pickaxe.
    tag(BlockTags.NEEDS_STONE_TOOL).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get(),
      ModBlocks.TIN_ORE.get(),
      ModBlocks.DEEPSLAT_TIN_ORE.get(),
      ModBlocks.LEAD_ORE.get(),
      ModBlocks.DEEPSLAT_LEAD_ORE.get()
    );

    // 锡块和铅块需要铁镐或更高级工具。
    // Tin and lead blocks require an iron pickaxe or better.
    tag(BlockTags.NEEDS_IRON_TOOL).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get()
    );

    // 核龙金属块和腾龙金属块需要钻石镐或更高级工具。
    // Coraconic and aviraconic blocks require a diamond pickaxe or better.
    tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
      ModBlocks.CORACONIC_METAL_BLOCK.get(),
      ModBlocks.AVIRACONIC_METAL_BLOCK.get()
    );
  }
}
