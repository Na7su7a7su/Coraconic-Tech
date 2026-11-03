/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * 全局战利品修饰器：在原有掉落物基础上追加一个物品。
 *
 * <p>是否追加由 {@code conditions} 决定，因此同一个修饰器可以配合方块标签
 * 条件与随机概率条件，实现“某类方块以一定概率额外掉落某物品”。
 *
 * <p>Global loot modifier that appends one item to the existing drops. The
 * {@code conditions} decide whether it applies, so it can be combined with a
 * block-tag condition and a random-chance condition to implement "this kind of
 * block additionally drops an item with some chance".
 */
public class AddItemLootModifier extends LootModifier {

  public static final MapCodec<AddItemLootModifier> CODEC =
    RecordCodecBuilder.mapCodec(instance ->
      codecStart(instance)
        .and(ItemStack.CODEC.fieldOf("item").forGetter(modifier -> modifier.item))
        .and(
          NumberProviders.CODEC
            .optionalFieldOf("count", ConstantValue.exactly(1.0F))
            .forGetter(modifier -> modifier.count)
        )
        .apply(instance, AddItemLootModifier::new)
    );

  private final ItemStack item;
  private final NumberProvider count;

  public AddItemLootModifier(
    LootItemCondition[] conditions,
    ItemStack item
  ) {
    this(conditions, item, ConstantValue.exactly(1.0F));
  }

  public AddItemLootModifier(
    LootItemCondition[] conditions,
    ItemStack item,
    NumberProvider count
  ) {
    super(conditions);
    this.item = item;
    this.count = count;
  }

  @Override
  protected ObjectArrayList<ItemStack> doApply(
    ObjectArrayList<ItemStack> generatedLoot,
    LootContext context
  ) {
    int amount = Math.max(0, count.getInt(context));
    if (amount > 0) {
      generatedLoot.add(item.copyWithCount(amount));
    }
    return generatedLoot;
  }

  @Override
  public MapCodec<? extends IGlobalLootModifier> codec() {
    return ModLootModifiers.ADD_ITEM.get();
  }
}
