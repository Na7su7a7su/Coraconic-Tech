package com.aestastel.coraconic.menu;

import com.aestastel.coraconic.CoraconicTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 菜单类型注册器：把 {@link ItemStorageUnitMenu} 绑定到游戏注册表。
 *
 * <p>三个存储等级共用同一个菜单类型，容量由打开界面时发送的附加数据决定。
 * 使用 {@link IMenuTypeExtension#create} 创建带附加数据的菜单类型，使服务端
 * 可以把方块坐标与总槽位数发送给客户端。
 *
 * <p>Menu type registry. All storage tiers share one menu type; the capacity is
 * sent as extra data when the GUI opens.
 */
public class ModMenuType {

  public static final DeferredRegister<MenuType<?>> MENU_TYPES =
    DeferredRegister.create(Registries.MENU, CoraconicTech.MODID);

  public static final DeferredHolder<
    MenuType<?>,
    MenuType<ItemStorageUnitMenu>
  > ITEM_STORAGE_UNIT = MENU_TYPES.register(
    "item_storage_unit",
    () -> IMenuTypeExtension.create(ItemStorageUnitMenu::fromNetwork)
  );

  public static final DeferredHolder<
    MenuType<?>,
    MenuType<EnergyStorageUnitMenu>
  > ENERGY_STORAGE_UNIT = MENU_TYPES.register(
    "energy_storage_unit",
    () -> IMenuTypeExtension.create(EnergyStorageUnitMenu::fromNetwork)
  );

  /**
   * 将菜单类型注册器挂到模组事件总线。
   *
   * <p>Attaches the menu type register to the mod event bus.
   *
   * @param eventBus 模组专用事件总线 / mod event bus
   */
  public static void register(IEventBus eventBus) {
    MENU_TYPES.register(eventBus);
  }
}
