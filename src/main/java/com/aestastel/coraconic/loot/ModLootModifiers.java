package com.aestastel.coraconic.loot;

import com.aestastel.coraconic.CoraconicTech;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 全局战利品修饰器序列化器注册器。
 *
 * <p>Registry for this mod's global loot modifier serializers. The registered
 * id is what appears in the {@code "type"} field of the modifier JSON.
 */
public final class ModLootModifiers {

  public static final DeferredRegister<
    MapCodec<? extends IGlobalLootModifier>
  > LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(
    NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
    CoraconicTech.MODID
  );

  /**
   * 追加物品修饰器：JSON 中写作 {@code "type": "coraconic:add_item"}。
   *
   * <p>Add-item modifier, serialized as {@code coraconic:add_item}.
   */
  public static final DeferredHolder<
    MapCodec<? extends IGlobalLootModifier>,
    MapCodec<AddItemLootModifier>
  > ADD_ITEM = LOOT_MODIFIER_SERIALIZERS.register(
    "add_item",
    () -> AddItemLootModifier.CODEC
  );

  private ModLootModifiers() {}

  public static void register(IEventBus eventBus) {
    LOOT_MODIFIER_SERIALIZERS.register(eventBus);
  }
}
