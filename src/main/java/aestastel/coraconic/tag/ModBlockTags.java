/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.tag;

import aestastel.coraconic.CoraconicTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * 方块标签键：集中声明本模组使用的方块标签。
 *
 * <p>Block tag keys: centralizes the block tags used by this mod.
 */
public class ModBlockTags {

  public static final TagKey<Block> ORES_TIN = commonTag("ores/tin");
  public static final TagKey<Block> ORES_LEAD = commonTag("ores/lead");
  public static final TagKey<Block> ORES_SILVER = commonTag("ores/silver");

  /**
   * 额外掉落核龙元素的方块标签：石头、深板岩、下界岩、末地石。
   *
   * <p>Block tag whose members can additionally drop a coraconic element.
   */
  public static final TagKey<Block> ELEMENT_SOURCE_STONES = create(
    "element_source_stones"
  );

  public static final TagKey<Block> STORAGE_BLOCKS_TIN = commonTag(
    "storage_blocks/tin"
  );
  public static final TagKey<Block> STORAGE_BLOCKS_LEAD = commonTag(
    "storage_blocks/lead"
  );
  public static final TagKey<Block> STORAGE_BLOCKS_SILVER = commonTag(
    "storage_blocks/silver"
  );
  public static final TagKey<Block> STORAGE_BLOCKS_CORACONIC = commonTag(
    "storage_blocks/coraconic"
  );
  public static final TagKey<Block> STORAGE_BLOCKS_AVIRACONIC = commonTag(
    "storage_blocks/aviraconic"
  );
  public static final TagKey<Block> STORAGE_BLOCKS_ROSE_GOLD = commonTag(
    "storage_blocks/rose_gold"
  );
  public static final TagKey<Block> STORAGE_BLOCKS_AMBER_GOLD = commonTag(
    "storage_blocks/amber_gold"
  );

  public static final TagKey<Block> INCORRECT_FOR_TIN_TOOL = commonTag(
    "incorrect_for_tin_tool"
  );
  public static final TagKey<Block> INCORRECT_FOR_LEAD_TOOL = commonTag(
    "incorrect_for_lead_tool"
  );
  public static final TagKey<Block> INCORRECT_FOR_CORACONICBASIC_TOOL =
    commonTag("incorrect_for_coraconicbasic_tool");
  public static final TagKey<Block> INCORRECT_FOR_AVIRACONICBASIC_TOOL =
    commonTag("incorrect_for_aviraconicbasic_tool");

  private ModBlockTags() {}

  private static TagKey<Block> commonTag(String path) {
    return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
  }

  public static TagKey<Block> create(String name) {
    return TagKey.create(
      Registries.BLOCK,
      ResourceLocation.fromNamespaceAndPath(CoraconicTech.MODID, name)
    );
  }

  public static TagKey<Block> create(ResourceLocation name) {
    return TagKey.create(Registries.BLOCK, name);
  }
}
