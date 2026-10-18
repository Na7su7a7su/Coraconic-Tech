package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItemsProvider extends ItemModelProvider {

  public ModItemsProvider(
    PackOutput output,
    ExistingFileHelper existingFileHelper
  ) {
    super(output, CoraconicTech.MODID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    for (DeferredHolder<Item, ? extends Item> holder : ModItems.ITEMS.getEntries()) {
      Item item = holder.get();
      if (item instanceof BlockItem blockItem) {
        simpleBlockItem(blockItem.getBlock());
      } else {
        basicItem(item);
      }
    }
  }
}
