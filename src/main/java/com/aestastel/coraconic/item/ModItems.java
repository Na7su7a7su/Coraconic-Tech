package com.aestastel.coraconic.item;

import com.aestastel.coraconic.CoraconicTech;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品注册器：集中声明本模组的所有普通物品。
 *
 * <p>本类只负责“注册”，不负责配方、模型、标签或本地化；这些资源分别由
 * 数据生成器与语言文件维护。所有物品都通过 {@link #ITEMS} 延迟注册，
 * 从而避免在类加载阶段过早访问尚未初始化的游戏注册表。
 *
 * <p>Item registry: declares all regular items for this mod. This class only
 * handles registration; recipes, models, tags, and translations are maintained
 * by data generators and language files.
 */
public class ModItems {

  /**
   * 物品延迟注册器。
   *
   * <p>{@link DeferredRegister.Items} 是 NeoForge 为物品注册提供的专用包装：
   * 构造时只记录模组 ID，真正的注册动作要等到 {@link #register(IEventBus)}
   * 把该注册器挂到模组事件总线之后才发生。因此可以安全地在静态字段中声明
   * 物品，而不会触发注册表过早访问。
   *
   * <p>{@code DeferredRegister.createItems(CoraconicTech.MODID)} 会把所有条目的
   * 命名空间固定为 {@code coraconic}；后续调用
   * {@code ITEMS.register("name", ...)} 时只需提供路径，最终 ID 为
   * {@code coraconic:name}。
   *
   * <p>Deferred item register. It records entries immediately but performs the
   * real registration only after being attached to the mod event bus through
   * {@link #register(IEventBus)}. The namespace is fixed to {@code coraconic},
   * so each entry only needs its path.
   */
  public static final DeferredRegister.Items ITEMS =
    DeferredRegister.createItems(CoraconicTech.MODID);
  public static final FoodProperties HCV2_FOOD_PROPERTIES =
    new FoodProperties.Builder()
      .nutrition(64)
      .saturationModifier(500.0F)
      .alwaysEdible()
      .build();
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
  public static final DeferredItem<Item> TIN_NUGGET = ITEMS.register(
    "tin_nugget",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> LEAD_NUGGET = ITEMS.register(
    "lead_nugget",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> CORACONIC_NUGGET = ITEMS.register(
    "coraconic_nugget",
    () -> new Item(new Item.Properties())
  );
  public static final DeferredItem<Item> AVIRACONIC_NUGGET = ITEMS.register(
    "aviraconic_nugget",
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

  /**
   * 将物品延迟注册器挂到模组事件总线。
   *
   * <p>必须在模组构造期间调用。{@link #ITEMS} 本身只保存待注册条目，调用
   * {@link DeferredRegister#register(IEventBus)} 后，注册器才会在对应的注册
   * 事件阶段把每个条目写入游戏注册表；若遗漏这一步，所有物品都不会存在，
   * 运行时会表现为物品缺失、模型或配方找不到。
   *
   * <p>Attaches the deferred item register to the mod event bus. Must be called
   * during mod construction; otherwise none of the deferred entries will be
   * written into the game registries.
   *
   * @param eventBus 模组专用事件总线，通常由主模组类传入
   */
  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}
