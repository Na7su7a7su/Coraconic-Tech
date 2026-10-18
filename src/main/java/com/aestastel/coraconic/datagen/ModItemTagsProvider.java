package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.block.ModBlocks;
import com.aestastel.coraconic.item.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 物品标签生成器：为锭、粒、粗矿、矿石、存储块与食物生成通用标签。
 *
 * <p>Item tag provider: generates common tags for ingots, nuggets, raw materials, ores, storage blocks, and foods.
 */
public class ModItemTagsProvider extends ItemTagsProvider {

  // 本模组使用的 c: 通用物品标签键。
  // Common c: item tag keys used by this mod.
  private static final TagKey<Item> INGOTS_TIN = commonTag("ingots/tin");
  private static final TagKey<Item> INGOTS_LEAD = commonTag("ingots/lead");
  private static final TagKey<Item> INGOTS_CORACONIC =
    commonTag("ingots/coraconic");
  private static final TagKey<Item> INGOTS_AVIRACONIC =
    commonTag("ingots/aviraconic");
  private static final TagKey<Item> NUGGETS_TIN = commonTag("nuggets/tin");
  private static final TagKey<Item> NUGGETS_LEAD = commonTag("nuggets/lead");
  private static final TagKey<Item> NUGGETS_CORACONIC =
    commonTag("nuggets/coraconic");
  private static final TagKey<Item> NUGGETS_AVIRACONIC =
    commonTag("nuggets/aviraconic");
  private static final TagKey<Item> RAW_MATERIALS_TIN =
    commonTag("raw_materials/tin");
  private static final TagKey<Item> RAW_MATERIALS_LEAD =
    commonTag("raw_materials/lead");
  private static final TagKey<Item> ORES_TIN = commonTag("ores/tin");
  private static final TagKey<Item> ORES_LEAD = commonTag("ores/lead");
  private static final TagKey<Item> STORAGE_BLOCKS_TIN =
    commonTag("storage_blocks/tin");
  private static final TagKey<Item> STORAGE_BLOCKS_LEAD =
    commonTag("storage_blocks/lead");
  private static final TagKey<Item> STORAGE_BLOCKS_CORACONIC =
    commonTag("storage_blocks/coraconic");
  private static final TagKey<Item> STORAGE_BLOCKS_AVIRACONIC =
    commonTag("storage_blocks/aviraconic");

  public ModItemTagsProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> lookupProvider,
    CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
    ExistingFileHelper existingFileHelper
  ) {
    // blockTags 用于在需要时复制方块标签内容。
    // blockTags is used to copy block tag contents when needed.
    super(
      output,
      lookupProvider,
      blockTags,
      CoraconicTech.MODID,
      existingFileHelper
    );
  }

  /**
   * 注册物品标签及其成员。
   *
   * <p>Registers item tags and their members.
   */
  @Override
  protected void addTags(HolderLookup.Provider provider) {
    // 锭：注册具体金属标签，并挂到 c:ingots 父标签。
    // Ingots: register metal-specific tags and attach them to the c:ingots parent tag.
    tag(INGOTS_TIN).add(ModItems.TIN_INGOT.get());
    tag(INGOTS_LEAD).add(ModItems.LEAD_INGOT.get());
    tag(INGOTS_CORACONIC).add(ModItems.CORACONIC_INGOT.get());
    tag(INGOTS_AVIRACONIC).add(ModItems.AVIRACONIC_INGOT.get());
    tag(Tags.Items.INGOTS)
      .addTag(INGOTS_TIN)
      .addTag(INGOTS_LEAD)
      .addTag(INGOTS_CORACONIC)
      .addTag(INGOTS_AVIRACONIC);

    // 粒：注册具体金属标签，并挂到 c:nuggets 父标签。
    // Nuggets: register metal-specific tags and attach them to the c:nuggets parent tag.
    tag(NUGGETS_TIN).add(ModItems.TIN_NUGGET.get());
    tag(NUGGETS_LEAD).add(ModItems.LEAD_NUGGET.get());
    tag(NUGGETS_CORACONIC).add(ModItems.CORACONIC_NUGGET.get());
    tag(NUGGETS_AVIRACONIC).add(ModItems.AVIRACONIC_NUGGET.get());
    tag(Tags.Items.NUGGETS)
      .addTag(NUGGETS_TIN)
      .addTag(NUGGETS_LEAD)
      .addTag(NUGGETS_CORACONIC)
      .addTag(NUGGETS_AVIRACONIC);

    // 粗矿：注册粗锡/粗铅并挂到 c:raw_materials 父标签。
    // Raw materials: register raw tin/lead and attach them to the c:raw_materials parent tag.
    tag(RAW_MATERIALS_TIN).add(ModItems.RAW_TIN.get());
    tag(RAW_MATERIALS_LEAD).add(ModItems.RAW_LEAD.get());
    tag(Tags.Items.RAW_MATERIALS)
      .addTag(RAW_MATERIALS_TIN)
      .addTag(RAW_MATERIALS_LEAD);

    // 矿石：按矿物类型与生成岩层归类。
    // Ores: classify by mineral type and host stone.
    tag(ORES_TIN).add(ModBlocks.TIN_ORE.get().asItem());
    tag(ORES_LEAD).add(ModBlocks.LEAD_ORE.get().asItem());
    tag(Tags.Items.ORES).addTag(ORES_TIN).addTag(ORES_LEAD);
    tag(Tags.Items.ORES_IN_GROUND_STONE).add(
      ModBlocks.TIN_ORE.get().asItem(),
      ModBlocks.LEAD_ORE.get().asItem()
    );
    tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(
      ModBlocks.DEEPSLAT_TIN_ORE.get().asItem(),
      ModBlocks.DEEPSLAT_LEAD_ORE.get().asItem()
    );

    // 存储块：注册金属块物品并挂到 c:storage_blocks 父标签。
    // Storage blocks: register metal block items and attach them to the c:storage_blocks parent tag.
    tag(STORAGE_BLOCKS_TIN).add(ModBlocks.TIN_BLOCK.get().asItem());
    tag(STORAGE_BLOCKS_LEAD).add(ModBlocks.LEAD_BLOCK.get().asItem());
    tag(STORAGE_BLOCKS_CORACONIC).add(
      ModBlocks.CORACONIC_BLOCK.get().asItem()
    );
    tag(STORAGE_BLOCKS_AVIRACONIC).add(
      ModBlocks.AVIRACONIC_BLOCK.get().asItem()
    );
    tag(Tags.Items.STORAGE_BLOCKS)
      .addTag(STORAGE_BLOCKS_TIN)
      .addTag(STORAGE_BLOCKS_LEAD)
      .addTag(STORAGE_BLOCKS_CORACONIC)
      .addTag(STORAGE_BLOCKS_AVIRACONIC);

    // 食物：将 HCV2 标记为可食用物品。
    // Foods: mark HCV2 as a food item.
    tag(Tags.Items.FOODS).add(ModItems.HCV2.get());
  }

  /**
   * 创建 {@code c:} 命名空间下的物品标签键。
   *
   * <p>NeoForge 的通用标签约定使用 {@code c} 作为命名空间，以便不同模组之间
   * 通过统一标签互相识别同类物品。例如其他模组的锡锭只要加入
   * {@code c:ingots/tin}，本模组的配方、标签查询和兼容逻辑就能把它与
   * 本模组的锡锭同等对待，而不需要针对具体模组 ID 编写硬编码判断。
   *
   * <p>本方法只负责构造标签键，不会立即写入任何标签内容；真正的成员关系由
   * {@link #addTags(HolderLookup.Provider)} 中的 {@code tag(...).add(...)}
   * 和 {@code tag(...).addTag(...)} 调用建立。
   *
   * <p>参数 {@code path} 是标签在 {@code c} 命名空间下的完整路径，例如
   * {@code "ingots/tin"}、{@code "nuggets/tin"} 或
   * {@code "storage_blocks/coraconic"}。路径中的斜杠表示标签层级：
   * 子标签需要由父标签通过 {@code addTag(...)} 显式挂载，Minecraft 不会
   * 因为路径前缀相同而自动建立父子关系。
   *
   * <p>Creates an item tag key in the {@code c:} namespace. The path is the
   * full tag path, such as {@code "ingots/tin"} or {@code "nuggets/tin"}.
   * This helper only builds the key; tag membership is declared separately in
   * {@link #addTags(HolderLookup.Provider)}.
   */
  private static TagKey<Item> commonTag(String path) {
    return ItemTags.create(
      ResourceLocation.fromNamespaceAndPath("c", path)
    );
  }
}
