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
import aestastel.coraconic.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 物品模型生成器：为普通物品、工具与方块物品生成对应模型。
 *
 * <p>Item model provider: generates models for regular items, tools, and block items.
 */
public class ModItemsProvider extends ItemModelProvider {

  public ModItemsProvider(
    PackOutput output,
    ExistingFileHelper existingFileHelper
  ) {
    super(output, CoraconicTech.MODID, existingFileHelper);
  }

  /**
   * 遍历模组物品注册表并生成物品模型。
   *
   * <p>Iterates the mod item registry and generates item models.
   */
  @Override
  protected void registerModels() {
    for (DeferredHolder<
      Item,
      ? extends Item
    > holder : ModItems.ITEMS.getEntries()) {
      Item item = holder.get();
      if (item instanceof BlockItem blockItem) {
        // 方块物品继承方块模型，避免重复定义。
        // Block items inherit their block model to avoid duplicate definitions.
        simpleBlockItem(blockItem.getBlock());
      } else if (item instanceof DiggerItem || item instanceof SwordItem) {
        // 工具与剑使用 item/handheld，保证持握时的朝向正确。
        // Tools and swords use item/handheld so they are held correctly.
        handheldItem(item);
      } else {
        // 普通物品使用 item/generated 与 item/<id> 纹理。
        // Regular items use item/generated and the item/<id> texture.
        basicItem(item);
      }
    }
  }
}
