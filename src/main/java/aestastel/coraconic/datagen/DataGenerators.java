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
import java.util.List;
import java.util.Set;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * 数据生成入口：集中注册所有非游戏内容生成器。
 *
 * <p>Data generation entry point: registers all non-game-content providers.
 */
@EventBusSubscriber(modid = CoraconicTech.MODID)
public class DataGenerators {

  /**
   * 在数据生成事件中注册客户端与服务端生成器。
   *
   * <p>Registers client- and server-side providers during the data generation event.
   */
  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

    // 客户端生成器：方块状态、方块模型与物品模型。
    // Client providers: block states, block models, and item models.
    generator.addProvider(
      event.includeClient(),
      new ModBlockStatesProvider(output, existingFileHelper)
    );
    generator.addProvider(
      event.includeClient(),
      new ModItemsProvider(output, existingFileHelper)
    );

    // 方块标签先生成，物品标签通过 contentsGetter 依赖其内容。
    // Block tags are generated first; item tags depend on them via contentsGetter.
    ModBlockTagsProvider blockTags = new ModBlockTagsProvider(
      output,
      event.getLookupProvider(),
      existingFileHelper
    );
    generator.addProvider(event.includeServer(), blockTags);
    generator.addProvider(
      event.includeServer(),
      new ModItemTagsProvider(
        output,
        event.getLookupProvider(),
        blockTags.contentsGetter(),
        existingFileHelper
      )
    );

    // 服务端生成器：配方及其解锁进度。
    // Server providers: recipes and their unlock advancements.
    generator.addProvider(
      event.includeServer(),
      new ModRecipesProvider(output, event.getLookupProvider())
    );

    // 服务端生成器：方块战利品表，使用 BLOCK 参数集。
    // Server provider: block loot tables using the BLOCK parameter set.
    generator.addProvider(
      event.includeServer(),
      new LootTableProvider(
        output,
        Set.of(),
        List.of(
          new LootTableProvider.SubProviderEntry(
            ModBlockLootTablesProvider::new,
            LootContextParamSets.BLOCK
          )
        ),
        event.getLookupProvider()
      )
    );

    // 服务端生成器：全局战利品修饰器（矿石/石头额外掉落核龙元素）。
    // Server provider: global loot modifiers (extra element drops from ores/stones).
    generator.addProvider(
      event.includeServer(),
      new ModGlobalLootModifiersProvider(output, event.getLookupProvider())
    );

    // 服务端生成器：实体类型标签（额外掉落腾龙元素的 Boss）。
    // Server provider: entity type tags (bosses that drop aviraconic elements).
    generator.addProvider(
      event.includeServer(),
      new ModEntityTypeTagsProvider(
        output,
        event.getLookupProvider(),
        existingFileHelper
      )
    );
  }
}
