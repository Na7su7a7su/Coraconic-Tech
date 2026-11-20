/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.tag;

import aestastel.coraconic.CoraconicTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * 物品标签键：集中声明本模组使用的物品标签。
 *
 * <p>Item tag keys: centralizes the item tags used by this mod.
 */
public class ModItemTags {

  public static final TagKey<Item> INGOTS_TIN = commonTag("ingots/tin");
  public static final TagKey<Item> INGOTS_LEAD = commonTag("ingots/lead");
  public static final TagKey<Item> INGOTS_SILVER = commonTag("ingots/silver");
  public static final TagKey<Item> INGOTS_CORACONIC = commonTag(
    "ingots/coraconic"
  );
  public static final TagKey<Item> INGOTS_AVIRACONIC = commonTag(
    "ingots/aviraconic"
  );
  public static final TagKey<Item> INGOTS_EMBERACONIC = commonTag(
    "ingots/emberaconic"
  );
  public static final TagKey<Item> INGOTS_ROSE_GOLD = commonTag(
    "ingots/rose_gold"
  );
  public static final TagKey<Item> INGOTS_AMBER_GOLD = commonTag(
    "ingots/amber_gold"
  );

  public static final TagKey<Item> NUGGETS_TIN = commonTag("nuggets/tin");
  public static final TagKey<Item> NUGGETS_LEAD = commonTag("nuggets/lead");
  public static final TagKey<Item> NUGGETS_SILVER = commonTag("nuggets/silver");
  public static final TagKey<Item> NUGGETS_CORACONIC = commonTag(
    "nuggets/coraconic"
  );
  public static final TagKey<Item> NUGGETS_AVIRACONIC = commonTag(
    "nuggets/aviraconic"
  );
  public static final TagKey<Item> NUGGETS_EMBERACONIC = commonTag(
    "nuggets/emberaconic"
  );
  public static final TagKey<Item> NUGGETS_ROSE_GOLD = commonTag(
    "nuggets/rose_gold"
  );
  public static final TagKey<Item> NUGGETS_AMBER_GOLD = commonTag(
    "nuggets/amber_gold"
  );

  public static final TagKey<Item> RAW_MATERIALS_TIN = commonTag(
    "raw_materials/tin"
  );
  public static final TagKey<Item> RAW_MATERIALS_LEAD = commonTag(
    "raw_materials/lead"
  );
  public static final TagKey<Item> RAW_MATERIALS_SILVER = commonTag(
    "raw_materials/silver"
  );

  public static final TagKey<Item> ORES_TIN = commonTag("ores/tin");
  public static final TagKey<Item> ORES_LEAD = commonTag("ores/lead");
  public static final TagKey<Item> ORES_SILVER = commonTag("ores/silver");

  public static final TagKey<Item> STORAGE_BLOCKS_TIN = commonTag(
    "storage_blocks/tin"
  );
  public static final TagKey<Item> STORAGE_BLOCKS_LEAD = commonTag(
    "storage_blocks/lead"
  );
  public static final TagKey<Item> STORAGE_BLOCKS_SILVER = commonTag(
    "storage_blocks/silver"
  );
  public static final TagKey<Item> STORAGE_BLOCKS_CORACONIC = commonTag(
    "storage_blocks/coraconic"
  );
  public static final TagKey<Item> STORAGE_BLOCKS_AVIRACONIC = commonTag(
    "storage_blocks/aviraconic"
  );
  public static final TagKey<Item> STORAGE_BLOCKS_EMBERACONIC = commonTag(
    "storage_blocks/emberaconic"
  );
  public static final TagKey<Item> STORAGE_BLOCKS_ROSE_GOLD = commonTag(
    "storage_blocks/rose_gold"
  );
  public static final TagKey<Item> STORAGE_BLOCKS_AMBER_GOLD = commonTag(
    "storage_blocks/amber_gold"
  );
  public static final TagKey<Item> SILICON = commonTag("silicon");

  public static final TagKey<Item> ELEMENTS_CORACONIC = commonTag(
    "elements/coraconic"
  );
  public static final TagKey<Item> ELEMENTS_AVIRACONIC = commonTag(
    "elements/aviraconic"
  );
  public static final TagKey<Item> ELEMENTS_EMBERACONIC = commonTag(
    "elements/emberaconic"
  );

  public static final TagKey<Item> GEMS_CORACONIC_DS = commonTag(
    "gems/coraconic-diamond-structured"
  );
  public static final TagKey<Item> GEMS_AVIRACONIC_DS = commonTag(
    "gems/aviraconic-diamond-structured"
  );
  public static final TagKey<Item> GEMS_EMBERACONIC_DS = commonTag(
    "gems/emberaconic-diamond-structured"
  );

  public static final TagKey<Item> CIRCUITS = commonTag("circuits");
  public static final TagKey<Item> CIRCUITS_BASIC = commonTag(
    "circuits/basic"
  );
  public static final TagKey<Item> CIRCUITS_PRIMITIVE = commonTag(
    "circuits/primitive"
  );
  public static final TagKey<Item> CIRCUITS_ADVANCED = commonTag(
    "circuits/advanced"
  );
  public static final TagKey<Item> CIRCUITS_REFINED = commonTag(
    "circuits/refined"
  );
  public static final TagKey<Item> CIRCUITS_ELITE = commonTag(
    "circuits/elite"
  );
  public static final TagKey<Item> CIRCUITS_ENHANCED = commonTag(
    "circuits/enhanced"
  );
  public static final TagKey<Item> CIRCUITS_ULTIMATE = commonTag(
    "circuits/ultimate"
  );
  public static final TagKey<Item> CIRCUITS_SUPERIOR = commonTag(
    "circuits/superior"
  );

  private ModItemTags() {}

  private static TagKey<Item> commonTag(String path) {
    return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
  }

  public static TagKey<Item> create(String name) {
    return TagKey.create(
      Registries.ITEM,
      ResourceLocation.fromNamespaceAndPath(CoraconicTech.MODID, name)
    );
  }

  public static TagKey<Item> create(ResourceLocation name) {
    return TagKey.create(Registries.ITEM, name);
  }
}
