/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.tag.ModEntityTypeTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 实体类型标签生成器：登记额外掉落腾龙元素的 Boss。
 *
 * <p>Entity type tag provider for bosses that drop aviraconic elements.
 */
public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

  public ModEntityTypeTagsProvider(
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

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(ModEntityTypeTags.ELEMENT_SOURCE_BOSSES).add(
      EntityType.WITHER,
      EntityType.ENDER_DRAGON,
      EntityType.WARDEN,
      EntityType.ELDER_GUARDIAN
    );
  }
}
