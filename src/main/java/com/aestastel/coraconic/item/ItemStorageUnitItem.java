package com.aestastel.coraconic.item;

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
 * 具体物品名称、附魔等信息不会展示。
 *
 * <p>Block item for the item storage units. It summarises the stored contents in
 * the tooltip without listing the individual items. The contents live in the
 * {@code minecraft:container} component; only non-empty units show the two
 * summary lines.
 */
public class ItemStorageUnitItem extends BlockItem {

  public ItemStorageUnitItem(Block block, Properties properties) {
    super(block, properties);
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

    ItemContainerContents contents = stack.getOrDefault(
      DataComponents.CONTAINER,
      ItemContainerContents.EMPTY
    );

    int occupiedSlots = 0;
    for (ItemStack ignored : contents.nonEmptyItems()) {
      occupiedSlots++;
    }

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
