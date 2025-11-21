package com.armoreffects.system;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public abstract class ArmorEffectType {
    
    public enum Category {
        POTION,
        DAMAGE_REDUCTION,
        ENCHANTMENT,
        ATTRIBUTE
    }
    
    protected final Category category;
    protected final ResourceLocation effectId;
    protected final int level;
    
    public ArmorEffectType(Category category, ResourceLocation effectId, int level) {
        this.category = category;
        this.effectId = effectId;
        this.level = level;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public ResourceLocation getEffectId() {
        return effectId;
    }
    
    public int getLevel() {
        return level;
    }
    
    public abstract void applyEffect(ArmorEffectContext context);
    
    public static ArmorEffectType parse(String configString) {
        // Format: "category:effect_id" or "category:effect_id@level"
        // Examples: 
        // "potion:minecraft:regeneration@2" - Regeneration II
        // "potion:minecraft:speed" - Speed I (default)
        // "enchant:minecraft:fire_protection@4" - Fire Protection IV
        // "damage:fire_protection" - No level needed
        // "attribute:speed" - No level needed
        
        // Check if there's a level specified
        String[] parts = configString.split("@");
        int level = 1; // Default level
        String effectPart = configString;
        
        if (parts.length == 2) {
            level = Integer.parseInt(parts[1]);
            effectPart = parts[0];
        } else if (parts.length > 2) {
            throw new IllegalArgumentException("Invalid effect format: " + configString);
        }
        
        String[] categoryAndEffect = effectPart.split(":", 2);
        
        if (categoryAndEffect.length != 2) {
            throw new IllegalArgumentException("Invalid effect format: " + configString);
        }
        
        String categoryStr = categoryAndEffect[0].toLowerCase();
        String effectStr = categoryAndEffect[1];
        
        Category category = switch (categoryStr) {
            case "potion" -> Category.POTION;
            case "damage" -> Category.DAMAGE_REDUCTION;
            case "enchant" -> Category.ENCHANTMENT;
            case "attribute", "attr" -> Category.ATTRIBUTE;
            default -> throw new IllegalArgumentException("Unknown category: " + categoryStr);
        };
        
        ResourceLocation effectId = ResourceLocation.tryParse(effectStr);
        if (effectId == null) {
            // Handle simple names like "fire_protection" -> "armoreffects:fire_protection"
            effectId = ResourceLocation.fromNamespaceAndPath("armoreffects", effectStr);
        }
        
        return switch (category) {
            case POTION -> new PotionArmorEffect(effectId, level);
            case DAMAGE_REDUCTION -> new DamageReductionArmorEffect(effectId, level);
            case ENCHANTMENT -> new EnchantmentArmorEffect(effectId, level);
            case ATTRIBUTE -> new AttributeArmorEffect(effectId, level);
        };
    }
    
    public static class PotionArmorEffect extends ArmorEffectType {
        
        public PotionArmorEffect(ResourceLocation effectId, int level) {
            super(Category.POTION, effectId, level);
        }
        
        @Override
        public void applyEffect(ArmorEffectContext context) {
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(effectId);
            if (mobEffect != null && context.getPlayer() != null) {
                context.getPlayer().addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    mobEffect, 60, Math.max(0, level - 1), true, false
                ));
            }
        }
    }
    
    public static class DamageReductionArmorEffect extends ArmorEffectType {
        
        public DamageReductionArmorEffect(ResourceLocation effectId, int level) {
            super(Category.DAMAGE_REDUCTION, effectId, level);
        }
        
        @Override
        public void applyEffect(ArmorEffectContext context) {
            // This will be handled in the damage event handler
            // The level represents percentage reduction
        }
        
        public float getReductionAmount() {
            return level / 100.0f; // Convert percentage to decimal
        }
        
        public boolean appliesTo(net.minecraft.world.damagesource.DamageSource damageSource) {
            String effectType = effectId.getPath();
            return switch (effectType) {
                case "fire_protection" -> 
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.IN_FIRE) ||
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.ON_FIRE) ||
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.LAVA) ||
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.HOT_FLOOR);
                    
                case "projectile_protection" ->
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.ARROW) ||
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.TRIDENT) ||
                    (damageSource.getDirectEntity() != null && 
                     damageSource.getDirectEntity().getType().getCategory() == net.minecraft.world.entity.MobCategory.MISC);
                     
                case "blast_protection" ->
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.EXPLOSION) ||
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_EXPLOSION) ||
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.BAD_RESPAWN_POINT);
                    
                case "fall_protection" ->
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.FALL);
                    
                case "magic_protection" ->
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.MAGIC) ||
                    damageSource.is(net.minecraft.world.damagesource.DamageTypes.INDIRECT_MAGIC);
                    
                case "general_protection" -> true; // Applies to all damage
                    
                default -> false;
            };
        }
    }
    
    public static class EnchantmentArmorEffect extends ArmorEffectType {
        
        public EnchantmentArmorEffect(ResourceLocation effectId, int level) {
            super(Category.ENCHANTMENT, effectId, level);
        }
        
        @Override
        public void applyEffect(ArmorEffectContext context) {
            // Future implementation for enchantment effects
            // This could add temporary enchantments or modify enchantment levels
        }
    }
    
    public static class AttributeArmorEffect extends ArmorEffectType {
        
        public AttributeArmorEffect(ResourceLocation effectId, int level) {
            super(Category.ATTRIBUTE, effectId, level);
        }
        
        @Override
        public void applyEffect(ArmorEffectContext context) {
            // Attribute effects are handled by AttributeArmorHandler
            // This method is not used for attribute effects as they use 
            // permanent AttributeModifiers instead of tick-based application
        }
        
        /**
         * Get the effect ID as a string for attribute matching
         */
        public String getEffectIdString() {
            return effectId.getPath();
        }
    }
}