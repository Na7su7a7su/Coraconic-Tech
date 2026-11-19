/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.gametest;

import aestastel.coraconic.CoraconicTech;
import aestastel.coraconic.common.block.ModBlocks;
import aestastel.coraconic.common.blockentity.AlloyingMachineEnhancedBlockEntity;
import aestastel.coraconic.common.blockentity.AlloyingMachineRefinedBlockEntity;
import aestastel.coraconic.common.blockentity.EnergyStorageUnitBlockEntity;
import aestastel.coraconic.common.machine.MachineProfiles;
import aestastel.coraconic.common.machine.MachineTier;
import aestastel.coraconic.common.machine.TieredMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Tier 与机器规格的 GameTest。
 *
 * <p>验证等级顺序、profile 关联和机器方块声明，不依赖具体世界生成。
 *
 * <p>GameTests for tiers and machine profiles. They verify tier ordering,
 * profile association, and machine block declarations without depending on
 * world generation.
 */
@GameTestHolder(CoraconicTech.MODID)
@PrefixGameTestTemplate(false)
public final class MachineTierGameTests {

  private static final String TEMPLATE_NAMESPACE = CoraconicTech.MODID;
  private static final String TEMPLATE = "empty";

  @GameTest(
    templateNamespace = TEMPLATE_NAMESPACE,
    template = TEMPLATE,
    timeoutTicks = 40
  )
  public void tierOrder(GameTestHelper helper) {
    helper.assertTrue(
      MachineTier.PRIMITIVE.index() == 0,
      "Primitive must be tier index 0"
    );
    helper.assertTrue(
      MachineTier.REFINED.isAtLeast(MachineTier.PRIMITIVE),
      "Refined must be at least Primitive"
    );
    helper.assertTrue(
      MachineTier.SUPERIOR.isAtLeast(MachineTier.ENHANCED),
      "Superior must be at least Enhanced"
    );
    helper.assertFalse(
      MachineTier.REFINED.isAtLeast(MachineTier.SUPERIOR),
      "Refined must not be at least Superior"
    );
    helper.succeed();
  }

  @GameTest(
    templateNamespace = TEMPLATE_NAMESPACE,
    template = TEMPLATE,
    timeoutTicks = 40
  )
  public void profilesMatchTiers(GameTestHelper helper) {
    helper.assertTrue(
      MachineProfiles.ALLOYING_REFINED.tier() == MachineTier.REFINED,
      "Alloying Machine Refined profile must use REFINED"
    );
    helper.assertTrue(
      MachineProfiles.ALLOYING_ENHANCED.tier() == MachineTier.ENHANCED,
      "Alloying Machine Enhanced profile must use ENHANCED"
    );
    helper.assertTrue(
      MachineProfiles.ENERGY_STORAGE_SUPERIOR.tier() ==
        MachineTier.SUPERIOR,
      "Energy Storage Unit Superior profile must use SUPERIOR"
    );
    helper.assertTrue(
      MachineProfiles.HEAT_GENERATOR_PRIMITIVE.tier() ==
        MachineTier.PRIMITIVE,
      "Heat Generator Primitive profile must use PRIMITIVE"
    );
    helper.succeed();
  }

  @GameTest(
    templateNamespace = TEMPLATE_NAMESPACE,
    template = TEMPLATE,
    timeoutTicks = 40
  )
  public void machineBlocksExposeProfiles(GameTestHelper helper) {
    helper.assertTrue(
      ModBlocks.ALLOYING_MACHINE_REFINED.get() instanceof TieredMachine,
      "Alloying Machine Refined must expose a profile"
    );
    helper.assertTrue(
      ModBlocks.ALLOYING_MACHINE_ENHANCED.get() instanceof TieredMachine,
      "Alloying Machine Enhanced must expose a profile"
    );
    helper.assertTrue(
      ModBlocks.ENERGY_STORAGE_UNIT_REFINED.get() instanceof TieredMachine,
      "Energy Storage Unit Refined must expose a profile"
    );
    helper.assertTrue(
      ModBlocks.SOLAR_GENERATOR_PRIMITIVE.get() instanceof TieredMachine,
      "Solar Generator Primitive must expose a profile"
    );
    helper.assertTrue(
      ModBlocks.GEOMAGNETIC_GENERATOR_PRIMITIVE.get() instanceof
        TieredMachine,
      "Geomagnetic Generator Primitive must expose a profile"
    );
    helper.assertTrue(
      ModBlocks.HEAT_GENERATOR_PRIMITIVE.get() instanceof TieredMachine,
      "Heat Generator Primitive must expose a profile"
    );
    helper.succeed();
  }

  @GameTest(
    templateNamespace = TEMPLATE_NAMESPACE,
    template = TEMPLATE,
    timeoutTicks = 40
  )
  public void energyStoragePersistsWithoutTier(GameTestHelper helper) {
    BlockPos relativePos = new BlockPos(0, 0, 0);
    helper.setBlock(relativePos, ModBlocks.ENERGY_STORAGE_UNIT_REFINED.get());

    EnergyStorageUnitBlockEntity blockEntity = helper.getBlockEntity(
      relativePos
    );
    helper.assertTrue(
      blockEntity.getProfile().tier() == MachineTier.REFINED,
      "Energy Storage Unit Refined must recover REFINED from its block"
    );

    blockEntity.setEnergyStored(123_456L);
    CompoundTag tag = new CompoundTag();
    blockEntity.saveAdditional(tag, helper.getLevel().registryAccess());
    long savedEnergy = 0L;
    for (long segment : tag.getLongArray("energy")) {
      savedEnergy += segment;
    }
    helper.assertTrue(
      savedEnergy == 123_456L,
      "Energy Storage Unit must persist its stored energy"
    );
    helper.assertFalse(
      tag.contains("tier") || tag.contains("machineTier"),
      "Tier must not be persisted as mutable NBT state"
    );

    blockEntity.setEnergyStored(0L);
    blockEntity.loadAdditional(tag, helper.getLevel().registryAccess());
    helper.assertTrue(
      blockEntity.getEnergyStoredLong() == 123_456L,
      "Energy Storage Unit must restore stored energy from NBT"
    );
    helper.assertTrue(
      blockEntity.getProfile().tier() == MachineTier.REFINED,
      "Tier must remain supplied by the registered block profile after load"
    );
    helper.succeed();
  }

  @GameTest(
    templateNamespace = TEMPLATE_NAMESPACE,
    template = TEMPLATE,
    timeoutTicks = 40
  )
  public void energyStorageCapability(GameTestHelper helper) {
    BlockPos relativePos = new BlockPos(0, 0, 0);
    helper.setBlock(
      relativePos,
      ModBlocks.ENERGY_STORAGE_UNIT_SUPERIOR.get()
    );

    IEnergyStorage storage = helper
      .getLevel()
      .getCapability(
        Capabilities.EnergyStorage.BLOCK,
        helper.absolutePos(relativePos),
        null
      );
    helper.assertTrue(
      storage != null,
      "Energy Storage Unit Superior must expose an energy capability"
    );
    helper.assertTrue(
      storage.getMaxEnergyStored() > 0,
      "Energy capability must report a positive capacity"
    );
    helper.succeed();
  }

  @GameTest(
    templateNamespace = TEMPLATE_NAMESPACE,
    template = TEMPLATE,
    timeoutTicks = 40
  )
  public void alloyingLineLayout(GameTestHelper helper) {
    helper.assertTrue(
      AlloyingMachineRefinedBlockEntity.INPUT_SLOT_COUNT == 3 &&
        AlloyingMachineEnhancedBlockEntity.LINE_COUNT == 3,
      "Alloying machines must keep one refined line and three enhanced lines"
    );
    helper.assertTrue(
      AlloyingMachineEnhancedBlockEntity.OUTPUT_SLOT_COUNT == 3,
      "Alloying Machine Enhanced must expose one output per line"
    );
    helper.succeed();
  }
}
