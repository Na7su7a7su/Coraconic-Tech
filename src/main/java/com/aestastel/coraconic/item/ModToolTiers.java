package com.aestastel.coraconic.item;

import com.aestastel.coraconic.tag.ModBlockTags;
import com.aestastel.coraconic.tag.ModItemTags;
import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum ModToolTiers implements Tier {
  TIN(ModBlockTags.INCORRECT_FOR_TIN_TOOL, 132, 2.4F, 0.5F, 32, () ->
    Ingredient.of(ModItemTags.INGOTS_TIN)
  ),
  LEAD(ModBlockTags.INCORRECT_FOR_TIN_TOOL, 320, 2.4F, 1.5F, 7, () ->
    Ingredient.of(ModItemTags.INGOTS_LEAD)
  ),
  CORACONICBASIC(
    ModBlockTags.INCORRECT_FOR_CORACONICBASIC_TOOL,
    50,
    0.1F,
    -3.0F,
    1,
    () -> Ingredient.of(ModItemTags.INGOTS_CORACONIC)
  ),
  AVIRACONICBASIC(
    ModBlockTags.INCORRECT_FOR_AVIRACONICBASIC_TOOL,
    100,
    0.2F,
    -2.0F,
    2,
    () -> Ingredient.of(ModItemTags.INGOTS_AVIRACONIC)
  );

  private final TagKey<Block> incorrectBlocksForDrops;
  private final int uses;
  private final float speed;
  private final float damage;
  private final int enchantmentValue;
  private final Supplier<Ingredient> repairIngredient;

  private ModToolTiers(
    TagKey<Block> incorrectBlocksForDrops,
    int uses,
    float speed,
    float damage,
    int enchantmentValue,
    Supplier<Ingredient> repairIngredient
  ) {
    this.incorrectBlocksForDrops = incorrectBlocksForDrops;
    this.uses = uses;
    this.speed = speed;
    this.damage = damage;
    this.enchantmentValue = enchantmentValue;
    this.repairIngredient = repairIngredient;
  }

  @Override
  public int getUses() {
    return this.uses;
  }

  @Override
  public float getSpeed() {
    return this.speed;
  }

  @Override
  public float getAttackDamageBonus() {
    return this.damage;
  }

  @Override
  public TagKey<Block> getIncorrectBlocksForDrops() {
    return this.incorrectBlocksForDrops;
  }

  @Override
  public int getEnchantmentValue() {
    return this.enchantmentValue;
  }

  @Override
  public Ingredient getRepairIngredient() {
    return this.repairIngredient.get();
  }
}
