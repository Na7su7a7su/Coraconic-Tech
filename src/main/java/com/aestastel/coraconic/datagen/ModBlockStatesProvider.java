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
import com.aestastel.coraconic.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 方块状态与方块模型生成器：为所有已注册方块生成 cube_all 资源。
 *
 * <p>Block state and block model provider: generates cube_all resources for every registered block.
 */
public class ModBlockStatesProvider extends BlockStateProvider {

  public ModBlockStatesProvider(
    PackOutput output,
    ExistingFileHelper exFileHelper
  ) {
    super(output, CoraconicTech.MODID, exFileHelper);
  }

  /**
   * 遍历模组方块注册表并生成方块状态与模型。
   *
   * <p>Iterates the mod block registry and generates block states and models.
   */
  @Override
  protected void registerStatesAndModels() {
    // 当前所有方块均为六面同纹理，因此统一使用 simpleBlock。
    // All current blocks use the same texture on every face, so simpleBlock is sufficient.
    for (DeferredHolder<Block, ? extends Block> block : ModBlocks.BLOCK.getEntries()) {
      // 三个物品存储单元使用手写模型，跳过自动生成以免覆盖。
      // All three item storage units use hand-authored models; skip datagen.
      Block blockObject = block.get();
      if (
        blockObject == ModBlocks.ITEM_STORAGE_UNIT_MK1.get() ||
        blockObject == ModBlocks.ITEM_STORAGE_UNIT_MK2.get() ||
        blockObject == ModBlocks.ITEM_STORAGE_UNIT_MK3.get()
      ) {
        continue;
      }
      simpleBlock(blockObject);
    }
  }
}
