package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.block.ModBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider {

  public ModBlockTagsProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> lookupProvider,
    ExistingFileHelper existingFileHelper
  ) {
    super(
      output,
      lookupProvider,
      CoraconicTech.MODID,
      existingFileHelper
    );
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get(),
      ModBlocks.CORACONIC_BLOCK.get(),
      ModBlocks.AVIRACONIC_BLOCK.get()
    );
    tag(BlockTags.NEEDS_IRON_TOOL).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get()
    );
    tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
      ModBlocks.CORACONIC_BLOCK.get(),
      ModBlocks.AVIRACONIC_BLOCK.get()
    );
  }
}
