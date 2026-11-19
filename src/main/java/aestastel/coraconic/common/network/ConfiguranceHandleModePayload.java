/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.network;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.common.item.ConfiguranceHandleItem;
import aestastel.coraconic.common.item.ConfiguranceHandleMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端到服务端的配置手柄模式同步包。
 *
 * <p>Client-to-server payload carrying the Configurance Handle mode selected by
 * the player.
 */
public record ConfiguranceHandleModePayload(ConfiguranceHandleMode mode)
  implements CustomPacketPayload
{

  public static final Type<ConfiguranceHandleModePayload> TYPE = new Type<>(
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "configurance_handle_mode"
    )
  );

  public static final StreamCodec<ByteBuf, ConfiguranceHandleModePayload> STREAM_CODEC =
    StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      payload -> payload.mode().ordinal(),
      id -> new ConfiguranceHandleModePayload(ConfiguranceHandleMode.byOrdinal(id))
    );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(
    ConfiguranceHandleModePayload payload,
    IPayloadContext context
  ) {
    context.enqueueWork(() -> {
      Player player = context.player();
      ItemStack stack = ConfiguranceHandleItem.findHeldHandle(player);
      if (stack.isEmpty()) {
        return;
      }

      ConfiguranceHandleItem.setMode(stack, payload.mode());
      player.getInventory().setChanged();
      player.containerMenu.broadcastChanges();
    });
  }
}
