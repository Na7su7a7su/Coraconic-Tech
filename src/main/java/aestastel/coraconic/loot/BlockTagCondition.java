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
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * 战利品条件：当前破坏的方块是否属于指定方块标签。
 *
 * <p>用于全局战利品修饰器按标签匹配方块（例如 {@code c:ores}），从而同时
 * 覆盖原版与其他模组的矿石，而不必为每个方块单独编写战利品表。
 *
 * <p>Loot condition that matches when the broken block belongs to the given
 * block tag. This lets global loot modifiers target tags such as
 * {@code c:ores} and automatically cover ores from other mods.
 *
 * @param tag 要匹配的方块标签 / the block tag to match
 */
public record BlockTagCondition(TagKey<Block> tag)
  implements LootItemCondition
{

  public static final MapCodec<BlockTagCondition> CODEC =
    RecordCodecBuilder.mapCodec(instance ->
      instance
        .group(
          TagKey.codec(Registries.BLOCK)
            .fieldOf("tag")
            .forGetter(BlockTagCondition::tag)
        )
        .apply(instance, BlockTagCondition::new)
    );

  @Override
  public boolean test(LootContext context) {
    BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
    return state != null && state.is(tag);
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return Set.of(LootContextParams.BLOCK_STATE);
  }

  @Override
  public LootItemConditionType getType() {
    return ModLootConditions.BLOCK_TAG.get();
  }
}
