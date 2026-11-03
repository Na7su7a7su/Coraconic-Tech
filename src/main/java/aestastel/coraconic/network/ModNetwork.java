/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * 模组网络负载注册。
 *
 * <p>Registers the mod's network payload handlers.
 */
public final class ModNetwork {

  private ModNetwork() {}

  public static void register(RegisterPayloadHandlersEvent event) {
    event
      .registrar("1")
      .playToServer(
        SpannerModePayload.TYPE,
        SpannerModePayload.STREAM_CODEC,
        SpannerModePayload::handle
      );
  }
}
