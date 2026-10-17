package com.aestastel.coraconic.block;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.item.ModItems;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

  public static final DeferredRegister.Blocks BLOCK =
    DeferredRegister.createBlocks(CoraconicTech.MODID);

  public static final DeferredBlock<Block> TIN_BLOCK = registerBlocks(
    "tin_block",
    () -> new Block(BlockBehaviour.Properties.of().strength(1.3F, 6.0F))
  );
  public static final DeferredBlock<Block> LEAD_BLOCK = registerBlocks(
    "lead_block",
    () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F))
  );
  public static final DeferredBlock<Block> CORACONIC_BLOCK = registerBlocks(
    "coraconic_block",
    () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F))
  );
  public static final DeferredBlock<Block> AVIRACONIC_BLOCK = registerBlocks(
    "aviraconic_block",
    () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F))
  );
  public static final DeferredBlock<Block> TIN_ORE = registerBlocks(
    "tin_ore",
    () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE))
  );
  public static final DeferredBlock<Block> LEAD_ORE = registerBlocks(
    "lead_ore",
    () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE))
  );

  public static <T extends Block> void registerBlockItems(
    String name,
    DeferredBlock<T> block
  ) {
    ModItems.ITEMS.register(name, () ->
      new BlockItem(block.get(), new Item.Properties())
    );
  }

  private static <T extends Block> DeferredBlock<T> registerBlocks(
    String name,
    Supplier<T> block
  ) {
    DeferredBlock<T> blocks = BLOCK.register(name, block);
    registerBlockItems(name, blocks);
    return blocks;
  }
}
