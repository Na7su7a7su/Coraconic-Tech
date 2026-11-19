/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.client;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.common.item.ConfiguranceHandleItem;
import aestastel.coraconic.common.item.ConfiguranceHandleMode;
import aestastel.coraconic.common.network.ConfiguranceHandleModePayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 客户端配置手柄模式切换。
 *
 * <p>Shift + 鼠标滚轮在旋转、扳手与配置模式之间循环切换。滚轮事件会被取消，
 * 避免同时切换快捷栏。
 *
 * <p>Client-side Configurance Handle mode switching. Shift + mouse wheel
 * cycles rotate, wrench, and configure modes; the scroll event is cancelled to
 * avoid changing the hotbar selection.
 */
@EventBusSubscriber(
  modid = CoraconicTech.MODID,
  value = Dist.CLIENT
)
public final class ConfiguranceHandleClientEvents {

  private ConfiguranceHandleClientEvents() {}

  @SubscribeEvent
  static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;

    if (
      player == null ||
      minecraft.screen != null ||
      !player.isShiftKeyDown() ||
      event.getScrollDeltaY() == 0.0D
    ) {
      return;
    }

    ItemStack stack = ConfiguranceHandleItem.findHeldHandle(player);
    if (stack.isEmpty()) {
      return;
    }

    ConfiguranceHandleMode nextMode = ConfiguranceHandleItem.cycleMode(stack);
    PacketDistributor.sendToServer(new ConfiguranceHandleModePayload(nextMode));

    Component modeName = Component.translatable(
      nextMode.translationKey()
    );
    player.displayClientMessage(
      Component.translatable(
        "tooltip.coraconic.configurance_handle.mode",
        modeName
      ).withStyle(ChatFormatting.YELLOW),
      true
    );

    event.setCanceled(true);
  }
}
