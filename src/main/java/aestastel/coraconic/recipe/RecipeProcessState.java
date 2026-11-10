/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.recipe;

import java.util.function.ToIntFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 单个加工槽位的配方与进度状态。
 *
 * <p>只保存配方 ID、进度和重新计算出的最大进度；配方对象始终由当前
 * {@code RecipeManager} 重新解析，避免直接序列化 Java 配方对象。
 *
 * <p>Recipe and progress state for one processing slot. Only the recipe ID,
 * progress, and recomputed maximum progress are stored. Recipe objects are
 * always resolved again from the current recipe manager.
 */
public final class RecipeProcessState<
  I extends RecipeInput,
  R extends Recipe<I>
> {

  private final int defaultMaxProgress;

  private ResourceLocation recipeId;
  private R currentRecipe;
  private int progress;
  private int maxProgress;

  public RecipeProcessState(int defaultMaxProgress) {
    this.defaultMaxProgress = Math.max(0, defaultMaxProgress);
    this.maxProgress = this.defaultMaxProgress;
  }

  public @Nullable ResourceLocation getRecipeId() {
    return this.recipeId;
  }

  public @Nullable R getRecipe() {
    return this.currentRecipe;
  }

  public int getProgress() {
    return this.progress;
  }

  public int getMaxProgress() {
    return this.maxProgress;
  }

  public boolean hasRecipe() {
    return this.currentRecipe != null;
  }

  public boolean hasProgress() {
    return this.progress > 0;
  }

  /**
   * 选择新配方，并把进度重置为零。
   *
   * <p>Selects a new recipe and resets progress to zero.
   */
  public void select(RecipeHolder<R> holder, int maxProgress) {
    this.currentRecipe = holder.value();
    this.recipeId = holder.id();
    this.maxProgress = Math.max(1, maxProgress);
    this.progress = 0;
  }

  /**
   * 从已保存的配方 ID 恢复状态。
   *
   * <p>Restores state from the saved recipe ID. The recipe must still exist,
   * have the expected type, and match the current input.
   */
  public boolean restore(
    Level level,
    RecipeType<R> type,
    I input,
    ToIntFunction<R> maxProgressProvider
  ) {
    if (this.recipeId == null || level == null) {
      return false;
    }

    RecipeHolder<?> holder = level
      .getRecipeManager()
      .byKey(this.recipeId)
      .orElse(null);
    if (holder == null || holder.value().getType() != type) {
      reset();
      return false;
    }

    @SuppressWarnings("unchecked")
    R recipe = (R) holder.value();
    if (!recipe.matches(input, level)) {
      reset();
      return false;
    }

    this.currentRecipe = recipe;
    this.maxProgress = Math.max(
      1,
      maxProgressProvider.applyAsInt(recipe)
    );
    this.progress = clampProgress(this.progress, this.maxProgress);
    return true;
  }

  /**
   * 推进进度，并限制在当前配方范围内。
   *
   * <p>Advances progress and clamps it to the current recipe range.
   */
  public void addProgress(int amount) {
    if (amount <= 0) {
      return;
    }
    this.progress = clampProgress(
      this.progress + amount,
      this.maxProgress
    );
  }

  public void setProgress(int progress) {
    this.progress = clampProgress(progress, this.maxProgress);
  }

  /**
   * 清空配方、配方 ID 和进度。
   *
   * <p>Clears the recipe, recipe ID, and progress.
   */
  public void reset() {
    this.currentRecipe = null;
    this.recipeId = null;
    this.progress = 0;
    this.maxProgress = this.defaultMaxProgress;
  }

  /**
   * 只清空进度，保留已保存的配方 ID。
   *
   * <p>Clears only progress while keeping the saved recipe ID.
   */
  public void resetProgressOnly() {
    this.progress = 0;
    this.maxProgress = this.defaultMaxProgress;
  }

  public void saveTo(
    CompoundTag tag,
    String recipeKey,
    String progressKey
  ) {
    if (this.recipeId != null) {
      tag.putString(recipeKey, this.recipeId.toString());
    }
    tag.putInt(progressKey, this.progress);
  }

  public void loadFrom(
    CompoundTag tag,
    String recipeKey,
    String progressKey
  ) {
    this.currentRecipe = null;
    this.recipeId = tag.contains(recipeKey)
      ? ResourceLocation.tryParse(tag.getString(recipeKey))
      : null;
    this.progress = this.recipeId == null
      ? 0
      : Math.max(0, tag.getInt(progressKey));
    this.maxProgress = this.defaultMaxProgress;
  }

  private static int clampProgress(int value, int maxProgress) {
    if (maxProgress <= 0) {
      return 0;
    }
    return Math.max(0, Math.min(value, maxProgress));
  }
}
