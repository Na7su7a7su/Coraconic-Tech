package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.block.ModBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 方块标签生成器：定义金属块与矿石的挖掘工具和等级要求。
 *
 * <p>Block tag provider: defines mining tool and tier requirements for metal blocks and ores.
 */
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

  /**
   * 注册方块标签及其成员。
   *
   * <p>Registers block tags and their members.
   */
  @Override
  protected void addTags(HolderLookup.Provider provider) {
    // 四种金属块与四种矿石可由镐加速挖掘。
    // All four metal blocks and all four ores can be mined faster with a pickaxe.
    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get(),
      ModBlocks.CORACONIC_BLOCK.get(),
      ModBlocks.AVIRACONIC_BLOCK.get(),
      ModBlocks.TIN_ORE.get(),
      ModBlocks.DEEPSLAT_TIN_ORE.get(),
      ModBlocks.LEAD_ORE.get(),
      ModBlocks.DEEPSLAT_LEAD_ORE.get()
    );

    // 锡块、铅块和四种矿石至少需要石镐。
    // Tin/lead blocks and all four ores require at least a stone pickaxe.
    tag(BlockTags.NEEDS_STONE_TOOL).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get(),
      ModBlocks.TIN_ORE.get(),
      ModBlocks.DEEPSLAT_TIN_ORE.get(),
      ModBlocks.LEAD_ORE.get(),
      ModBlocks.DEEPSLAT_LEAD_ORE.get()
    );

    // 锡块和铅块需要铁镐或更高级工具。
    // Tin and lead blocks require an iron pickaxe or better.
    tag(BlockTags.NEEDS_IRON_TOOL).add(
      ModBlocks.TIN_BLOCK.get(),
      ModBlocks.LEAD_BLOCK.get()
    );

    // 核龙金属块和腾龙金属块需要钻石镐或更高级工具。
    // Coraconic and aviraconic blocks require a diamond pickaxe or better.
    tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
      ModBlocks.CORACONIC_BLOCK.get(),
      ModBlocks.AVIRACONIC_BLOCK.get()
    );
  }
}
