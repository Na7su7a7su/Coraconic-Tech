/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.tag;

import com.aestastel.coraconic.CoraconicTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * 实体类型标签：集中声明本模组使用的实体标签。
 *
 * <p>Entity type tags used by this mod.
 */
public final class ModEntityTypeTags {

  /**
   * 额外掉落腾龙元素的 Boss：凋灵、末影龙、坚守者、远古守卫者。
   *
   * <p>Bosses that additionally drop aviraconic elements.
   */
  public static final TagKey<EntityType<?>> ELEMENT_SOURCE_BOSSES =
    TagKey.create(
      Registries.ENTITY_TYPE,
      ResourceLocation.fromNamespaceAndPath(
        CoraconicTech.MODID,
        "element_source_bosses"
      )
    );

  private ModEntityTypeTags() {}
}
