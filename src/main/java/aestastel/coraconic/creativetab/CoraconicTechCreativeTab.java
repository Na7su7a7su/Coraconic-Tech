/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.creativetab;

import aestastel.coraconic.block.ModBlocks;
import aestastel.coraconic.item.ModItems;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 核龙科技主创造物品栏。
 *
 * <p>按现有顺序展示材料、核心、矿石、存储单元、机器与发电机。
 *
 * <p>Main Coraconic Tech creative tab. It keeps the existing order for
 * materials, cores, ores, storage units, machines, and generators.
 */
public final class CoraconicTechCreativeTab {

  private static final String NAME = "coraconic_tech";

  private CoraconicTechCreativeTab() {}

  public static Supplier<CreativeModeTab> register(
    DeferredRegister<CreativeModeTab> tabs
  ) {
    return tabs.register(NAME, () ->
      CreativeModeTab.builder()
        .icon(() -> new ItemStack(ModItems.CORACONIC_METAL_INGOT.get()))
        .title(Component.translatable("itemGroup.coraconic_tech"))
        .displayItems((parameter, output) -> {
          output.accept(ModItems.TIN_INGOT);
          output.accept(ModItems.LEAD_INGOT);
          output.accept(ModItems.SILVER_INGOT);
          output.accept(ModItems.CORACONIC_METAL_INGOT);
          output.accept(ModItems.AVIRACONIC_METAL_INGOT);
          output.accept(ModItems.ROSE_GOLD_INGOT);
          output.accept(ModItems.AMBER_GOLD_INGOT);
          output.accept(ModItems.TIN_NUGGET);
          output.accept(ModItems.LEAD_NUGGET);
          output.accept(ModItems.SILVER_NUGGET);
          output.accept(ModItems.CORACONIC_NUGGET);
          output.accept(ModItems.AVIRACONIC_NUGGET);
          output.accept(ModItems.ROSE_GOLD_NUGGET);
          output.accept(ModItems.AMBER_GOLD_NUGGET);
          output.accept(ModItems.COMMONMETAL_CORE);
          output.accept(ModItems.CORACONIC_CORE);
          output.accept(ModItems.AVIRACONIC_CORE);
          output.accept(ModItems.EFFECT_AMPLIFICATION_CORE);
          output.accept(ModItems.SPATIAL_SCALING_CORE);
          output.accept(ModItems.PHASE_SYNCHRONIZATION_CORE);
          output.accept(ModItems.MASS_ENERGY_COMPENSATION_CORE);
          output.accept(ModItems.CENTRAL_COMPUTATION_CORE);
          output.accept(ModItems.RAW_LEAD);
          output.accept(ModItems.RAW_TIN);
          output.accept(ModItems.RAW_SILVER);
          output.accept(ModItems.CORACONIC_ELEMENT);
          output.accept(ModItems.AVIRACONIC_ELEMENT);
          output.accept(ModItems.CORACONIC_DIAMOND_STRUCTURED_CRYSTAL);
          output.accept(ModItems.AVIRACONIC_DIAMOND_STRUCTURED_CRYSTAL);
          output.accept(ModBlocks.TIN_BLOCK.get());
          output.accept(ModBlocks.LEAD_BLOCK.get());
          output.accept(ModBlocks.SILVER_BLOCK.get());
          output.accept(ModBlocks.CORACONIC_METAL_BLOCK.get());
          output.accept(ModBlocks.AVIRACONIC_METAL_BLOCK.get());
          output.accept(ModBlocks.ROSE_GOLD_BLOCK.get());
          output.accept(ModBlocks.AMBER_GOLD_BLOCK.get());
          output.accept(ModBlocks.TIN_ORE.get());
          output.accept(ModBlocks.LEAD_ORE.get());
          output.accept(ModBlocks.SILVER_ORE.get());
          output.accept(ModBlocks.DEEPSLAT_TIN_ORE.get());
          output.accept(ModBlocks.DEEPSLAT_LEAD_ORE.get());
          output.accept(ModBlocks.DEEPSLATE_SILVER_ORE.get());
          output.accept(ModBlocks.ITEM_STORAGE_UNIT_MK1.get());
          output.accept(ModBlocks.ITEM_STORAGE_UNIT_MK2.get());
          output.accept(ModBlocks.ITEM_STORAGE_UNIT_MK3.get());
          output.accept(ModBlocks.ENERGY_STORAGE_UNIT_MK1.get());
          output.accept(ModBlocks.ENERGY_STORAGE_UNIT_MK2.get());
          output.accept(ModBlocks.ENERGY_STORAGE_UNIT_MK3.get());
          output.accept(ModBlocks.ALLOYING_MACHINE_MK1.get());
          output.accept(ModBlocks.ALLOYING_MACHINE_MK2.get());
          output.accept(
            ModBlocks.RESONANCE_CRYSTALLIZING_MACHINE.get()
          );
          output.accept(
            ModBlocks.SMELTING_METALLURGY_MACHINE_MK1.get()
          );
          output.accept(ModBlocks.HEAT_GENERATOR_MK0.get());
          output.accept(ModBlocks.SOLAR_GENERATOR_MK0.get());
          output.accept(ModBlocks.GEOMAGNETIC_GENERATOR_MK0.get());
          output.accept(ModBlocks.GEOMAGNETIC_GENERATOR_MK1.get());
          output.accept(ModBlocks.GEOMAGNETIC_GENERATOR_MK2.get());
          output.accept(ModBlocks.GEOMAGNETIC_GENERATOR_MK3.get());
          output.accept(ModBlocks.SOLAR_GENERATOR_MK1.get());
          output.accept(ModBlocks.SOLAR_GENERATOR_MK2.get());
          output.accept(ModBlocks.SOLAR_GENERATOR_MK3.get());
        })
        .build()
    );
  }
}
