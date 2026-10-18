package com.aestastel.coraconic.datagen;

import com.aestastel.coraconic.CoraconicTech;
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

@EventBusSubscriber(modid = CoraconicTech.MODID)
public class DataGenerators {

  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

    generator.addProvider(
      event.includeClient(),
      new ModBlockStatesProvider(output, existingFileHelper)
    );
    generator.addProvider(
      event.includeClient(),
      new ModItemsProvider(output, existingFileHelper)
    );

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
    generator.addProvider(
      event.includeServer(),
      new ModRecipesProvider(output, event.getLookupProvider())
    );
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
  }
}
