/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.item;

import aestastel.coraconic.block.ItemStorageUnitBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;

/**
 * 物品存储单元的方块物品：在 tooltip 中汇总内容数量，但不列出具体物品。
 *
 * <p>内容保存在 {@code minecraft:container} 组件中。tooltip 只在容器非空时
 * 显示两行提示：一行说明内容已随物品保存，另一行给出非空槽位（堆）数量。
 * 物品栏中还会按已占用槽位比例显示黄色空间条，具体物品名称、附魔等信息不会
 * 展示。
 *
 * <p>Block item for the item storage units. It summarises the stored contents in
 * the tooltip without listing the individual items, and shows a yellow
 * occupied-slot bar in inventories. The contents live in the
 * {@code minecraft:container} component; only non-empty units show the two
 * summary lines.
 */
public class ItemStorageUnitItem extends BlockItem {

  /** 与物品存储界面的分级配色区分开的黄色空间条。 */
  private static final int BAR_COLOR = 0xFFD54F;

  private final int totalSlots;

  public ItemStorageUnitItem(Block block, Properties properties) {
    super(block, properties);
    this.totalSlots =
      block instanceof ItemStorageUnitBlock storage ? storage.getSlots() : 0;
  }

  /**
   * 物品栏中的空间条：按照已占用槽位数量显示。
   *
   * <p>Inventory space bar: shown according to the number of occupied slots.
   */
  @Override
  public boolean isBarVisible(ItemStack stack) {
    return getOccupiedSlots(stack) > 0;
  }

  /**
   * 将已占用槽位数量映射到原版 13 像素耐久条宽度。
   *
   * <p>Maps the number of occupied slots to the vanilla 13-pixel bar width.
   */
  @Override
  public int getBarWidth(ItemStack stack) {
    int occupiedSlots = getOccupiedSlots(stack);
    if (occupiedSlots <= 0 || totalSlots <= 0) {
      return 0;
    }

    int width = (int) Math.round(
      (13.0D * (double) occupiedSlots) / (double) totalSlots
    );
    return Math.max(1, Math.min(13, width));
  }

  @Override
  public int getBarColor(ItemStack stack) {
    return BAR_COLOR;
  }

  /**
   * 统计容器组件中的非空槽位（堆）数量。
   *
   * <p>Counts non-empty slots (stacks) in the container component.
   */
  private static int getOccupiedSlots(ItemStack stack) {
    ItemContainerContents contents = stack.getOrDefault(
      DataComponents.CONTAINER,
      ItemContainerContents.EMPTY
    );

    int occupiedSlots = 0;
    for (ItemStack ignored : contents.nonEmptyItems()) {
      occupiedSlots++;
    }
    return occupiedSlots;
  }

  /**
   * 追加内容汇总 tooltip。
   *
   * <p>“项目数”统计的是非空槽位（堆）的数量，而不是物品总个数，因此最多
   * 显示为该等级的槽位上限（81 / 162 / 243）。
   *
   * <p>Appends the summary tooltip. The item count is the number of non-empty
   * slots (stacks), not the total quantity of items.
   */
  @Override
  public void appendHoverText(
    ItemStack stack,
    Item.TooltipContext context,
    List<Component> tooltipComponents,
    TooltipFlag tooltipFlag
  ) {
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

    int occupiedSlots = getOccupiedSlots(stack);

    if (occupiedSlots > 0) {
      tooltipComponents.add(
        Component.translatable(
          "tooltip.coraconic.item_storage_unit.saved"
        ).withStyle(ChatFormatting.GRAY)
      );
      tooltipComponents.add(
        Component.translatable(
          "tooltip.coraconic.item_storage_unit.items",
          occupiedSlots
        ).withStyle(ChatFormatting.GRAY)
      );
    }
  }
}
