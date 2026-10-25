/*
 * Copyright (c) 2026 Aestastel
 *
 * This file is part of Coraconic Tech and is licensed under the MIT License.
 * See the LICENSE file in the project root for the full license text.
 *
 * SPDX-License-Identifier: MIT
 */

package com.aestastel.coraconic.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFood {

  public static final FoodProperties HCV2 = new FoodProperties.Builder()
    .nutrition(64)
    .saturationModifier(500.0F)
    .alwaysEdible()
    .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 6000, 10), 1.0F)
    .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 6000, 5), 1.0F)
    .build();
}
