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
