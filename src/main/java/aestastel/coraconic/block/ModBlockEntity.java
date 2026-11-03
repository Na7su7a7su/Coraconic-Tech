/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.block;

import aestastel.coraconic.menu.ItemStorageUnitMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 物品存储单元方块实体：保存 81 / 162 / 243 格物品栏，并提供容器与菜单能力。
 *
 * <p>三个等级共用本类，容量由方块实体类型在创建实例时传入。本类同时扮演：
 * <ul>
 *   <li>{@link BlockEntity} —— 挂载在方块上，负责存档的保存与读取；</li>
 *   <li>{@link Container} —— 让原版菜单与槽位系统能够读写库存；</li>
 *   <li>{@link MenuProvider} —— 右键方块时向玩家打开 {@link ItemStorageUnitMenu}。</li>
 * </ul>
 *
 * <p>Item Storage Unit block entity shared by all tiers. The capacity is supplied by
 * the block entity type when an instance is created. It acts as a
 * {@link BlockEntity} for persistence, a {@link Container} for the vanilla slot
 * system, and a {@link MenuProvider} for opening the GUI.
 */
public class ModBlockEntity
  extends BlockEntity
  implements Container, MenuProvider
{

  /** 本实例的槽位总数，决定界面宽度与菜单布局。 */
  private final int slots;

  /**
   * 物品栏实现。NeoForge 的 {@link ItemStackHandler} 负责实际的槽位读写，
   * 内容变化时标记方块实体为已修改，确保退出世界前会写回存档。
   *
   * <p>Backing inventory. Changes mark the block entity dirty so the contents
   * are written back to the save file.
   */
  private final ItemStackHandler itemHandler;

  public ModBlockEntity(
    BlockEntityType<?> type,
    BlockPos pos,
    BlockState state,
    int slots
  ) {
    super(type, pos, state);
    this.slots = slots;
    this.itemHandler = new ItemStackHandler(slots) {
      @Override
      protected void onContentsChanged(int slot) {
        ModBlockEntity.this.setChanged();
      }
    };
  }

  /**
   * 暴露底层物品栏，供能力注册（漏斗、管道等）使用。
   *
   * <p>Exposes the backing inventory for capability registration (hoppers,
   * pipes, and other automation).
   *
   * @return 物品栏处理器 / the item handler
   */
  public ItemStackHandler getItemHandler() {
    return itemHandler;
  }

  // ---------------------------------------------------------------------------
  // 物品组件 / Item components（潜影盒式内容保存）
  // ---------------------------------------------------------------------------

  /**
   * 把当前内容写入 {@code minecraft:container} 组件。
   *
   * <p>组件用于在挖掘、创造模式选取或战利品表复制时，把库存随物品一起带走。
   *
   * <p>Writes the current contents into the {@code minecraft:container} component
   * so the inventory travels with the item when mined or picked.
   */
  @Override
  protected void collectImplicitComponents(
    DataComponentMap.Builder components
  ) {
    super.collectImplicitComponents(components);
    // 空容器不写入组件，使空单元仍能与新合成单元正常堆叠。
    // Empty containers do not get the component, so empty units still stack
    // with freshly crafted ones.
    if (!isEmpty()) {
      components.set(
        DataComponents.CONTAINER,
        ItemContainerContents.fromItems(createItemList())
      );
    }
  }

  /**
   * 放置方块时从物品组件恢复内容。
   *
   * <p>由 {@code BlockItem} 在放置后自动调用，组件缺失时按空容器处理。
   *
   * <p>Restores the contents from the item component when the block is placed.
   * A missing component is treated as an empty container.
   */
  @Override
  protected void applyImplicitComponents(
    BlockEntity.DataComponentInput componentInput
  ) {
    super.applyImplicitComponents(componentInput);
    NonNullList<ItemStack> items = NonNullList.withSize(
      slots,
      ItemStack.EMPTY
    );
    componentInput
      .getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
      .copyInto(items);
    for (int slot = 0; slot < slots; slot++) {
      itemHandler.setStackInSlot(slot, items.get(slot));
    }
  }

  /**
   * 保存到物品时移除 NBT 中的 inventory，避免与容器组件重复存储。
   *
   * <p>Removes the NBT inventory when saving to an item so it is not stored
   * twice alongside the container component.
   */
  @Deprecated
  @Override
  public void removeComponentsFromTag(CompoundTag tag) {
    tag.remove("inventory");
  }

  /**
   * 构建与槽位一一对应的物品列表，供组件编码使用。
   *
   * <p>Builds a slot-aligned item list for component encoding.
   */
  private NonNullList<ItemStack> createItemList() {
    NonNullList<ItemStack> items = NonNullList.withSize(
      slots,
      ItemStack.EMPTY
    );
    for (int slot = 0; slot < slots; slot++) {
      items.set(slot, itemHandler.getStackInSlot(slot));
    }
    return items;
  }

  // ---------------------------------------------------------------------------
  // MenuProvider
  // ---------------------------------------------------------------------------

  /**
   * 界面标题：直接使用方块名称，三个等级会自动显示各自的翻译。
   *
   * <p>GUI title taken from the block name, so each tier shows its own
   * translation automatically.
   */
  @Override
  public Component getDisplayName() {
    return getBlockState().getBlock().getName();
  }

  /**
   * 由服务端创建菜单实例，客户端会收到同一窗口 ID 并自行构建对应菜单。
   *
   * <p>Creates the menu on the server; the client builds its own instance with
   * the same window id.
   */
  @Override
  public ItemStorageUnitMenu createMenu(
    int containerId,
    Inventory playerInventory,
    Player player
  ) {
    return new ItemStorageUnitMenu(containerId, playerInventory, this);
  }

  // ---------------------------------------------------------------------------
  // Container
  // ---------------------------------------------------------------------------

  @Override
  public int getContainerSize() {
    return slots;
  }

  @Override
  public boolean isEmpty() {
    for (int slot = 0; slot < slots; slot++) {
      if (!itemHandler.getStackInSlot(slot).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    return itemHandler.getStackInSlot(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    return itemHandler.extractItem(slot, amount, false);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return itemHandler.extractItem(
      slot,
      itemHandler.getStackInSlot(slot).getCount(),
      false
    );
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    itemHandler.setStackInSlot(slot, stack);
  }

  /**
   * 玩家是否仍能使用该容器：方块实体与玩家距离不超过 4 格。
   *
   * <p>Whether the player can still use this container (within 4 blocks).
   */
  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public void clearContent() {
    for (int slot = 0; slot < slots; slot++) {
      itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
    }
  }

  // ---------------------------------------------------------------------------
  // 存档 / Persistence
  // ---------------------------------------------------------------------------

  @Override
  public void saveAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.saveAdditional(tag, registries);
    tag.put("inventory", itemHandler.serializeNBT(registries));
  }

  @Override
  public void loadAdditional(
    CompoundTag tag,
    HolderLookup.Provider registries
  ) {
    super.loadAdditional(tag, registries);
    if (tag.contains("inventory")) {
      itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }
  }
}
