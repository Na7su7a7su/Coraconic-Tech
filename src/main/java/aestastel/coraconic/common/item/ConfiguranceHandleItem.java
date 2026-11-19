/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.common.item;

import aestastel.coraconic.common.block.EnergyStorageUnitBlock;
import aestastel.coraconic.common.block.ItemStorageUnitBlock;
import aestastel.coraconic.common.block.SolarGeneratorBlock;
import aestastel.coraconic.common.energy.EnergyFaceConfigurable;
import aestastel.coraconic.common.energy.EnergyFaceMode;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 配置手柄：旋转、扳手与配置三种工作模式，可通过 Shift + 滚轮切换。
 *
 * <p>旋转模式：右键顺时针旋转功能方块，Shift + 右键逆时针旋转；
 * 扳手模式：右键正常与方块交互，Shift + 右键拆除功能方块；
 * 配置模式：不与功能方块正常交互，右键查看所点面的能量模式，
 * Shift + 右键按输入 → 输出 → 输入/输出 → 阻断循环。
 *
 * <p>Configurance Handle with rotate, wrench, and configure modes toggled by
 * Shift + mouse wheel. Configure mode prints the clicked face's energy mode to
 * chat and cycles it with Shift + right-click.
 */
public class ConfiguranceHandleItem extends Item {

  private static final String MODE_KEY = "configurance_handle_mode";
  private static final String LEGACY_WRENCH_MODE_KEY = "wrench_mode";

  public ConfiguranceHandleItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (player == null) {
      return InteractionResult.PASS;
    }

    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = level.getBlockState(pos);

    if (!isSupported(state.getBlock())) {
      return InteractionResult.PASS;
    }

    ConfiguranceHandleMode mode = getMode(context.getItemInHand());
    if (mode == ConfiguranceHandleMode.CONFIGURE) {
      return configureFace(context, player, level, pos, state);
    }

    boolean wrenchMode = mode == ConfiguranceHandleMode.WRENCH;
    boolean reverse = player.isShiftKeyDown();

    if (level.isClientSide) {
      if (wrenchMode && !reverse) {
        return InteractionResult.PASS;
      }
      return InteractionResult.SUCCESS;
    }

    if (!level.mayInteract(player, pos)) {
      return InteractionResult.PASS;
    }

    if (wrenchMode) {
      if (!reverse) {
        return InteractionResult.PASS;
      }
      boolean removed = level.destroyBlock(pos, true, player);
      return removed
        ? InteractionResult.CONSUME
        : InteractionResult.PASS;
    }

    if (!state.hasProperty(HorizontalDirectionalBlock.FACING)) {
      return InteractionResult.PASS;
    }

    Direction facing = state.getValue(
      HorizontalDirectionalBlock.FACING
    );
    Direction next = reverse
      ? facing.getCounterClockWise()
      : facing.getClockWise();
    BlockState rotated = state.setValue(
      HorizontalDirectionalBlock.FACING,
      next
    );

    level.setBlock(pos, rotated, 3);
    level.playSound(
      null,
      pos,
      SoundEvents.LEVER_CLICK,
      SoundSource.BLOCKS,
      0.4F,
      1.0F
    );
    return InteractionResult.CONSUME;
  }

  /**
   * 配置模式下查询或切换所点面的能量模式。
   *
   * <p>Handles face-mode inspection and cycling. It deliberately never opens a
   * block GUI or performs rotation/dismantling.
   */
  private static InteractionResult configureFace(
    UseOnContext context,
    Player player,
    Level level,
    BlockPos pos,
    BlockState state
  ) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    if (!level.mayInteract(player, pos)) {
      return InteractionResult.PASS;
    }

    Component blockName = state.getBlock().getName();
    if (!(level.getBlockEntity(pos) instanceof EnergyFaceConfigurable config)) {
      player.sendSystemMessage(
        Component.translatable(
          "message.coraconic.face_mode.not_configurable",
          blockName
        ).withStyle(ChatFormatting.GRAY)
      );
      return InteractionResult.CONSUME;
    }

    Direction face = context.getClickedFace();
    Component faceName = Component.translatable(
      "direction.coraconic." + face.getName()
    );

    if (player.isShiftKeyDown()) {
      EnergyFaceMode nextMode = config.cycleFaceMode(face);
      player.sendSystemMessage(
        Component.translatable(
          "message.coraconic.face_mode.changed",
          blockName,
          faceName,
          Component.translatable(nextMode.translationKey())
        ).withStyle(ChatFormatting.YELLOW)
      );
      level.playSound(
        null,
        pos,
        SoundEvents.LEVER_CLICK,
        SoundSource.BLOCKS,
        0.4F,
        1.0F
      );
    } else {
      EnergyFaceMode currentMode = config.getFaceMode(face);
      player.sendSystemMessage(
        Component.translatable(
          "message.coraconic.face_mode.info",
          blockName,
          faceName,
          Component.translatable(currentMode.translationKey())
        ).withStyle(ChatFormatting.GRAY)
      );
    }

    return InteractionResult.CONSUME;
  }

  @Override
  public void appendHoverText(
    ItemStack stack,
    Item.TooltipContext context,
    List<Component> tooltip,
    TooltipFlag flag
  ) {
    super.appendHoverText(stack, context, tooltip, flag);
    tooltip.add(
      Component.translatable(
        "tooltip.coraconic.configurance_handle.mode",
        Component.translatable(getMode(stack).translationKey())
      ).withStyle(ChatFormatting.GRAY)
    );
  }

  /** {@return 玩家主手或副手中的配置手柄；没有则返回空栈} */
  public static ItemStack findHeldHandle(Player player) {
    ItemStack mainHand = player.getMainHandItem();
    if (mainHand.getItem() instanceof ConfiguranceHandleItem) {
      return mainHand;
    }

    ItemStack offHand = player.getOffhandItem();
    return offHand.getItem() instanceof ConfiguranceHandleItem
      ? offHand
      : ItemStack.EMPTY;
  }

  /** {@return 配置手柄当前模式；旧物品的布尔值会兼容读取} */
  public static ConfiguranceHandleMode getMode(ItemStack stack) {
    if (!(stack.getItem() instanceof ConfiguranceHandleItem)) {
      return ConfiguranceHandleMode.ROTATE;
    }
    CompoundTag tag = stack
      .getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
      .copyTag();

    ConfiguranceHandleMode mode = ConfiguranceHandleMode.byName(tag.getString(MODE_KEY));
    if (mode != null) {
      return mode;
    }
    return tag.getBoolean(LEGACY_WRENCH_MODE_KEY)
      ? ConfiguranceHandleMode.WRENCH
      : ConfiguranceHandleMode.ROTATE;
  }

  public static void setMode(ItemStack stack, ConfiguranceHandleMode mode) {
    if (!(stack.getItem() instanceof ConfiguranceHandleItem)) {
      return;
    }
    CompoundTag tag = stack
      .getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
      .copyTag();
    tag.putString(MODE_KEY, mode.name());
    tag.remove(LEGACY_WRENCH_MODE_KEY);
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
  }

  /** 切换到下一个模式并写入物品，供客户端滚轮事件使用。 */
  public static ConfiguranceHandleMode cycleMode(ItemStack stack) {
    ConfiguranceHandleMode next = getMode(stack).next();
    setMode(stack, next);
    return next;
  }

  private static boolean isSupported(Block block) {
    return (
      block instanceof ItemStorageUnitBlock ||
      block instanceof EnergyStorageUnitBlock ||
      block instanceof SolarGeneratorBlock
    );
  }
}
