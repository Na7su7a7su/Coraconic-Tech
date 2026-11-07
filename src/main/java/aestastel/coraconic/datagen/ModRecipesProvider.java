/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package aestastel.coraconic.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/**
 * 配方数据生成入口：组合合成、合金与熔炼三个生成器。
 *
 * <p>Minecraft 的 {@code RecipeProvider} 将生成器名称固定为 {@code Recipes}，
 * 因此不能直接同时注册多个 RecipeProvider。本类作为唯一注册入口，依次运行
 * 三个拆分的生成器。
 *
 * <p>Recipe data entry point that composes the crafting, alloying, and smelting
 * providers. Vanilla {@code RecipeProvider} fixes its provider name, so this
 * wrapper is the single registered provider.
 */
public class ModRecipesProvider implements DataProvider {

  private final List<DataProvider> providers;

  public ModRecipesProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> registries
  ) {
    this.providers = List.of(
      new ModCraftingRecipesProvider(output, registries),
      new ModFormulaCraftingRecipesProvider(output, registries),
      new ModAlloyingRecipesProvider(output, registries),
      new ModSmeltingRecipesProvider(output, registries)
    );
  }

  @Override
  public CompletableFuture<?> run(CachedOutput output) {
    CompletableFuture<?>[] futures = this.providers
      .stream()
      .map(provider -> provider.run(output))
      .toArray(CompletableFuture[]::new);
    return CompletableFuture.allOf(futures);
  }

  @Override
  public String getName() {
    return "Recipes";
  }
}
