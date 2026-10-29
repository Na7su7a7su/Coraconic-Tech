/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.network;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.item.SpannerItem;
import com.aestastel.coraconic.item.SpannerMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端到服务端的扳手模式同步包。
 *
 * <p>Client-to-server payload carrying the wrench mode selected by the player.
 */
public record SpannerModePayload(SpannerMode mode)
  implements CustomPacketPayload
{

  public static final Type<SpannerModePayload> TYPE = new Type<>(
    ResourceLocation.fromNamespaceAndPath(
      CoraconicTech.MODID,
      "spanner_mode"
    )
  );

  public static final StreamCodec<ByteBuf, SpannerModePayload> STREAM_CODEC =
    StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      payload -> payload.mode().ordinal(),
      id -> new SpannerModePayload(SpannerMode.byOrdinal(id))
    );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(
    SpannerModePayload payload,
    IPayloadContext context
  ) {
    context.enqueueWork(() -> {
      Player player = context.player();
      ItemStack stack = SpannerItem.findHeldWrench(player);
      if (stack.isEmpty()) {
        return;
      }

      SpannerItem.setMode(stack, payload.mode());
      player.getInventory().setChanged();
      player.containerMenu.broadcastChanges();
    });
  }
}
