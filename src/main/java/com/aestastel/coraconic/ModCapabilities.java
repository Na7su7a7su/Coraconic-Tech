package com.aestastel.coraconic;

import com.aestastel.coraconic.block.ModBlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * 能力注册器：把三个等级的方块实体物品栏暴露给漏斗、管道等自动化设施。
 *
 * <p>NeoForge 的能力系统通过事件注册，而不是让方块实体实现旧的
 * {@code IInventory} 接口。注册后，任何通过
 * {@link Capabilities.ItemHandler#BLOCK} 查询该方块实体的代码（例如漏斗）
 * 都能直接读写对应槽位。
 *
 * <p>Capability registration: exposes every storage tier's inventory to
 * hoppers, pipes, and other automation through NeoForge's capability system.
 */
@EventBusSubscriber(modid = CoraconicTech.MODID)
public final class ModCapabilities {

  private ModCapabilities() {}

  @SubscribeEvent
  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      ModBlockEntityType.ITEM_STORAGE_MK0_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getItemHandler()
    );
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      ModBlockEntityType.ITEM_STORAGE_MK1_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getItemHandler()
    );
    event.registerBlockEntity(
      Capabilities.ItemHandler.BLOCK,
      ModBlockEntityType.ITEM_STORAGE_MK2_ENTITY.get(),
      (blockEntity, side) -> blockEntity.getItemHandler()
    );
  }
}
