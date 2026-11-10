/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.creativetab;

import aestastel.coraconic.item.ModItems;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 核龙科技工具创造物品栏。
 *
 * <p>按锡、铅、核龙金属、腾龙金属的顺序展示全套工具与扳手。
 *
 * <p>Coraconic Tech tool creative tab. It displays each tool set in tin, lead,
 * coraconic, and aviraconic order, followed by the wrench.
 */
public final class CoraconicTechToolCreativeTab {

  private static final String NAME = "coraconic_tech_tool";

  private CoraconicTechToolCreativeTab() {}

  public static Supplier<CreativeModeTab> register(
    DeferredRegister<CreativeModeTab> tabs
  ) {
    return tabs.register(NAME, () ->
      CreativeModeTab.builder()
        .icon(() -> new ItemStack(ModItems.CORACONIC_PICKAXE.get()))
        .title(Component.translatable("itemGroup.coraconic_tech_tool"))
        .displayItems((parameter, output) -> {
          output.accept(ModItems.TIN_SWORD);
          output.accept(ModItems.TIN_PICKAXE);
          output.accept(ModItems.TIN_AXE);
          output.accept(ModItems.TIN_SHOVEL);
          output.accept(ModItems.TIN_HOE);

          output.accept(ModItems.LEAD_SWORD);
          output.accept(ModItems.LEAD_PICKAXE);
          output.accept(ModItems.LEAD_AXE);
          output.accept(ModItems.LEAD_SHOVEL);
          output.accept(ModItems.LEAD_HOE);

          output.accept(ModItems.CORACONIC_SWORD);
          output.accept(ModItems.CORACONIC_PICKAXE);
          output.accept(ModItems.CORACONIC_AXE);
          output.accept(ModItems.CORACONIC_SHOVEL);
          output.accept(ModItems.CORACONIC_HOE);

          output.accept(ModItems.AVIRACONIC_SWORD);
          output.accept(ModItems.AVIRACONIC_PICKAXE);
          output.accept(ModItems.AVIRACONIC_AXE);
          output.accept(ModItems.AVIRACONIC_SHOVEL);
          output.accept(ModItems.AVIRACONIC_HOE);
          output.accept(ModItems.SPANNER);
        })
        .build()
    );
  }
}
