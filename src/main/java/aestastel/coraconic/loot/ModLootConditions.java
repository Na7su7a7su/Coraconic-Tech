/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.loot;

import aestastel.coraconic.CoraconicTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 战利品条件类型注册器：把本模组的条件编解码器写入游戏注册表。
 *
 * <p>Loot condition type registry for this mod's custom conditions.
 */
public final class ModLootConditions {

  public static final DeferredRegister<LootItemConditionType>
    LOOT_CONDITION_TYPES = DeferredRegister.create(
      Registries.LOOT_CONDITION_TYPE,
      CoraconicTech.MODID
    );

  /**
   * 方块标签条件：JSON 中写作 {@code "condition": "coraconic:block_tag"}。
   *
   * <p>Block tag condition, serialized as {@code coraconic:block_tag}.
   */
  public static final DeferredHolder<
    LootItemConditionType,
    LootItemConditionType
  > BLOCK_TAG = LOOT_CONDITION_TYPES.register(
    "block_tag",
    () -> new LootItemConditionType(BlockTagCondition.CODEC)
  );

  private ModLootConditions() {}

  public static void register(IEventBus eventBus) {
    LOOT_CONDITION_TYPES.register(eventBus);
  }
}
