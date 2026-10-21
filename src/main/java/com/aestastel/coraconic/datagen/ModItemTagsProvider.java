package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.item.ModItems;
import com.aestastel.coraconic.tag.ModBlockTags;
import com.aestastel.coraconic.tag.ModItemTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 物品标签生成器：为锭、粒、粗矿、矿石、存储块、晶体、工具与食物生成标签。
 *
 * <p>Item tag provider: generates tags for ingots, nuggets, raw materials, ores, storage blocks, gems, tools, and foods.
 */
public class ModItemTagsProvider extends ItemTagsProvider {

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
    tag(ModItemTags.INGOTS_TIN).add(ModItems.TIN_INGOT.get());
    tag(ModItemTags.INGOTS_LEAD).add(ModItems.LEAD_INGOT.get());
    tag(ModItemTags.INGOTS_CORACONIC).add(ModItems.CORACONIC_METAL_INGOT.get());
    tag(ModItemTags.INGOTS_AVIRACONIC).add(
      ModItems.AVIRACONIC_METAL_INGOT.get()
    );
    tag(Tags.Items.INGOTS)
      .addTag(ModItemTags.INGOTS_TIN)
      .addTag(ModItemTags.INGOTS_LEAD)
      .addTag(ModItemTags.INGOTS_CORACONIC)
      .addTag(ModItemTags.INGOTS_AVIRACONIC);

    // 粒：注册具体金属标签，并挂到 c:nuggets 父标签。
    // Nuggets: register metal-specific tags and attach them to the c:nuggets parent tag.
    tag(ModItemTags.NUGGETS_TIN).add(ModItems.TIN_NUGGET.get());
    tag(ModItemTags.NUGGETS_LEAD).add(ModItems.LEAD_NUGGET.get());
    tag(ModItemTags.NUGGETS_CORACONIC).add(ModItems.CORACONIC_NUGGET.get());
    tag(ModItemTags.NUGGETS_AVIRACONIC).add(ModItems.AVIRACONIC_NUGGET.get());
    tag(Tags.Items.NUGGETS)
      .addTag(ModItemTags.NUGGETS_TIN)
      .addTag(ModItemTags.NUGGETS_LEAD)
      .addTag(ModItemTags.NUGGETS_CORACONIC)
      .addTag(ModItemTags.NUGGETS_AVIRACONIC);

    // 粗矿：注册粗锡/粗铅并挂到 c:raw_materials 父标签。
    // Raw materials: register raw tin/lead and attach them to the c:raw_materials parent tag.
    tag(ModItemTags.RAW_MATERIALS_TIN).add(ModItems.RAW_TIN.get());
    tag(ModItemTags.RAW_MATERIALS_LEAD).add(ModItems.RAW_LEAD.get());
    tag(Tags.Items.RAW_MATERIALS)
      .addTag(ModItemTags.RAW_MATERIALS_TIN)
      .addTag(ModItemTags.RAW_MATERIALS_LEAD);

    // 矿石：从方块标签复制，按矿物类型与生成岩层归类。
    // Ores: copy from block tags and classify by mineral type and host stone.
    copy(ModBlockTags.ORES_TIN, ModItemTags.ORES_TIN);
    copy(ModBlockTags.ORES_LEAD, ModItemTags.ORES_LEAD);
    tag(Tags.Items.ORES)
      .addTag(ModItemTags.ORES_TIN)
      .addTag(ModItemTags.ORES_LEAD);
    copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
    copy(
      Tags.Blocks.ORES_IN_GROUND_DEEPSLATE,
      Tags.Items.ORES_IN_GROUND_DEEPSLATE
    );

    // 存储块：从方块标签复制并挂到 c:storage_blocks 父标签。
    // Storage blocks: copy from block tags and attach them to the c:storage_blocks parent tag.
    copy(ModBlockTags.STORAGE_BLOCKS_TIN, ModItemTags.STORAGE_BLOCKS_TIN);
    copy(ModBlockTags.STORAGE_BLOCKS_LEAD, ModItemTags.STORAGE_BLOCKS_LEAD);
    copy(
      ModBlockTags.STORAGE_BLOCKS_CORACONIC,
      ModItemTags.STORAGE_BLOCKS_CORACONIC
    );
    copy(
      ModBlockTags.STORAGE_BLOCKS_AVIRACONIC,
      ModItemTags.STORAGE_BLOCKS_AVIRACONIC
    );
    tag(Tags.Items.STORAGE_BLOCKS)
      .addTag(ModItemTags.STORAGE_BLOCKS_TIN)
      .addTag(ModItemTags.STORAGE_BLOCKS_LEAD)
      .addTag(ModItemTags.STORAGE_BLOCKS_CORACONIC)
      .addTag(ModItemTags.STORAGE_BLOCKS_AVIRACONIC);

    // 元素：注册核龙元素与腾龙元素的通用标签。
    // Elements: register common tags for coraconic and aviraconic elements.
    tag(ModItemTags.ELEMENTS_CORACONIC).add(ModItems.CORACONIC_ELEMENT.get());
    tag(ModItemTags.ELEMENTS_AVIRACONIC).add(ModItems.AVIRACONIC_ELEMENT.get());

    // 晶体：注册核龙晶体与腾龙晶体的通用宝石标签，并挂到 c:gems 父标签。
    // Gems: register common gem tags for the coraconic and aviraconic crystals, and attach them to c:gems.
    tag(ModItemTags.GEMS_CORACONIC_DS).add(
      ModItems.CORACONIC_DIAMOND_STRUCTURED_CRYSTAL.get()
    );
    tag(ModItemTags.GEMS_AVIRACONIC_DS).add(
      ModItems.AVIRACONIC_DIAMOND_STRUCTURED_CRYSTAL.get()
    );
    tag(Tags.Items.GEMS)
      .addTag(ModItemTags.GEMS_CORACONIC_DS)
      .addTag(ModItemTags.GEMS_AVIRACONIC_DS);

    // 工具：将锡、铅、核龙金属与腾龙金属四套工具挂到原版工具标签。
    // Tools: attach the tin, lead, coraconic, and aviraconic tool sets to vanilla tool tags.
    tag(ItemTags.SWORDS)
      .add(ModItems.TIN_SWORD.get())
      .add(ModItems.LEAD_SWORD.get())
      .add(ModItems.CORACONIC_SWORD.get())
      .add(ModItems.AVIRACONIC_SWORD.get());
    tag(ItemTags.PICKAXES)
      .add(ModItems.TIN_PICKAXE.get())
      .add(ModItems.LEAD_PICKAXE.get())
      .add(ModItems.CORACONIC_PICKAXE.get())
      .add(ModItems.AVIRACONIC_PICKAXE.get());
    tag(ItemTags.AXES)
      .add(ModItems.TIN_AXE.get())
      .add(ModItems.LEAD_AXE.get())
      .add(ModItems.CORACONIC_AXE.get())
      .add(ModItems.AVIRACONIC_AXE.get());
    tag(ItemTags.SHOVELS)
      .add(ModItems.TIN_SHOVEL.get())
      .add(ModItems.LEAD_SHOVEL.get())
      .add(ModItems.CORACONIC_SHOVEL.get())
      .add(ModItems.AVIRACONIC_SHOVEL.get());
    tag(ItemTags.HOES)
      .add(ModItems.TIN_HOE.get())
      .add(ModItems.LEAD_HOE.get())
      .add(ModItems.CORACONIC_HOE.get())
      .add(ModItems.AVIRACONIC_HOE.get());

    // NeoForge 通用工具子标签：剑与斧属于近战武器，镐属于采矿工具。
    // NeoForge common tool sub-tags: swords and axes are melee weapons, pickaxes are mining tools.
    tag(Tags.Items.MELEE_WEAPON_TOOLS)
      .add(ModItems.TIN_SWORD.get())
      .add(ModItems.LEAD_SWORD.get())
      .add(ModItems.CORACONIC_SWORD.get())
      .add(ModItems.AVIRACONIC_SWORD.get())
      .add(ModItems.TIN_AXE.get())
      .add(ModItems.LEAD_AXE.get())
      .add(ModItems.CORACONIC_AXE.get())
      .add(ModItems.AVIRACONIC_AXE.get());
    tag(Tags.Items.MINING_TOOL_TOOLS)
      .add(ModItems.TIN_PICKAXE.get())
      .add(ModItems.LEAD_PICKAXE.get())
      .add(ModItems.CORACONIC_PICKAXE.get())
      .add(ModItems.AVIRACONIC_PICKAXE.get());

    // 食物：将 HCV2 标记为可食用物品。
    // Foods: mark HCV2 as a food item.
    tag(Tags.Items.FOODS).add(ModItems.HCV2.get());
  }
}
