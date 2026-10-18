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

public class ModItemTagsProvider extends ItemTagsProvider {

  private static final TagKey<Item> INGOTS_TIN = commonTag("ingots/tin");
  private static final TagKey<Item> INGOTS_LEAD = commonTag("ingots/lead");
  private static final TagKey<Item> INGOTS_CORACONIC =
    commonTag("ingots/coraconic");
  private static final TagKey<Item> INGOTS_AVIRACONIC =
    commonTag("ingots/aviraconic");
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
    super(
      output,
      lookupProvider,
      blockTags,
      CoraconicTech.MODID,
      existingFileHelper
    );
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(INGOTS_TIN).add(ModItems.TIN_INGOT.get());
    tag(INGOTS_LEAD).add(ModItems.LEAD_INGOT.get());
    tag(INGOTS_CORACONIC).add(ModItems.CORACONIC_INGOT.get());
    tag(INGOTS_AVIRACONIC).add(ModItems.AVIRACONIC_INGOT.get());
    tag(Tags.Items.INGOTS)
      .addTag(INGOTS_TIN)
      .addTag(INGOTS_LEAD)
      .addTag(INGOTS_CORACONIC)
      .addTag(INGOTS_AVIRACONIC);

    tag(RAW_MATERIALS_TIN).add(ModItems.RAW_TIN.get());
    tag(RAW_MATERIALS_LEAD).add(ModItems.RAW_LEAD.get());
    tag(Tags.Items.RAW_MATERIALS)
      .addTag(RAW_MATERIALS_TIN)
      .addTag(RAW_MATERIALS_LEAD);

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

    tag(Tags.Items.FOODS).add(ModItems.HCV2.get());
  }

  private static TagKey<Item> commonTag(String path) {
    return ItemTags.create(
      ResourceLocation.fromNamespaceAndPath("c", path)
    );
  }
}
