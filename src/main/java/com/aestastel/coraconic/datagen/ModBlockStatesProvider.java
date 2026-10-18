package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockStatesProvider extends BlockStateProvider {

  public ModBlockStatesProvider(
    PackOutput output,
    ExistingFileHelper exFileHelper
  ) {
    super(output, CoraconicTech.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    for (DeferredHolder<Block, ? extends Block> block : ModBlocks.BLOCK.getEntries()) {
      simpleBlock(block.get());
    }
  }
}
