package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.item.ModItems;
import com.aestastel.coraconic.loot.AddItemLootModifier;
import com.aestastel.coraconic.loot.BlockTagCondition;
import com.aestastel.coraconic.tag.ModBlockTags;
import com.aestastel.coraconic.tag.ModEntityTypeTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

/**
 * 全局战利品修饰器生成器：矿石与石头额外掉落核龙元素。
 *
 * <p>生成两条规则：
 * <ul>
 *   <li>{@code c:ores} 标签中的矿石：10% 概率额外掉落 1 个核龙元素；</li>
 *   <li>石头、深板岩、下界岩、末地石：0.2% 概率额外掉落 1 个核龙元素。</li>
 * </ul>
 *
 * <p>Global loot modifier provider: ores and stones additionally drop a
 * coraconic element with a small chance.
 */
public class ModGlobalLootModifiersProvider
  extends GlobalLootModifierProvider
{

  public ModGlobalLootModifiersProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    super(output, registries, CoraconicTech.MODID);
  }

  @Override
  protected void start() {
    add(
      "element_from_ores",
      new AddItemLootModifier(
        new LootItemCondition[] {
          new BlockTagCondition(Tags.Blocks.ORES),
          LootItemRandomChanceCondition.randomChance(0.1F).build(),
        },
        new ItemStack(ModItems.CORACONIC_ELEMENT.get())
      )
    );

    add(
      "element_from_stones",
      new AddItemLootModifier(
        new LootItemCondition[] {
          new BlockTagCondition(ModBlockTags.ELEMENT_SOURCE_STONES),
          LootItemRandomChanceCondition.randomChance(0.002F).build(),
        },
        new ItemStack(ModItems.CORACONIC_ELEMENT.get())
      )
    );

    add(
      "aviraconic_element_from_bosses",
      new AddItemLootModifier(
        new LootItemCondition[] {
          LootItemEntityPropertyCondition.hasProperties(
            LootContext.EntityTarget.THIS,
            EntityPredicate.Builder.entity()
              .of(ModEntityTypeTags.ELEMENT_SOURCE_BOSSES)
          ).build(),
        },
        new ItemStack(ModItems.AVIRACONIC_ELEMENT.get()),
        UniformGenerator.between(3.0F, 5.0F)
      )
    );
  }
}
