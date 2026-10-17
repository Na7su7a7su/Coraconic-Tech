package com.aestastel.coraconic.item;

import com.aestastel.coraconic.CoraconicTech;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

  public static final FoodProperties HCV2_FOOD_PROPERTIES =
    new FoodProperties.Builder()
      .nutrition(64)
      .saturationModifier(500.0F)
      .alwaysEdible()
      .build();
  public static final DeferredRegister.Items ITEMS =
    DeferredRegister.createItems(CoraconicTech.MODID);
  public static final DeferredItem<Item> HCV2 = ITEMS.register("hcv2", () ->
    new Item(new Item.Properties().food(HCV2_FOOD_PROPERTIES))
  );
  public static final DeferredItem<Item> TIN_INGOT = ITEMS.register(
    "tin_ingot",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> LEAD_INGOT = ITEMS.register(
    "lead_ingot",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> COMMONMETAL_CORE = ITEMS.register(
    "commonmetal_core",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> CORACONIC_INGOT = ITEMS.register(
    "coraconic_ingot",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> AVIRACONIC_INGOT = ITEMS.register(
    "aviraconic_ingot",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> CORACONIC_CORE = ITEMS.register(
    "coraconic_core",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> AVIRACONIC_CORE = ITEMS.register(
    "aviraconic_core",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> RAW_TIN = ITEMS.register(
    "raw_tin",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> RAW_LEAD = ITEMS.register(
    "raw_lead",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> CORACONIC_ELEMENT = ITEMS.register(
    "coraconic_element",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> AVIRACONIC_ELEMENT = ITEMS.register(
    "aviraconic_element",
    () -> new Item(new Item.Properties())
  );

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}
