package com.armoreffects.events;

import com.armoreffects.ArmorEffectsModule;
import com.armoreffects.config.ArmorEffectsConfig;
import com.armoreffects.system.ArmorEffectRegistry;
import com.armoreffects.system.ArmorEffectType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArmorEffectsModule.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeArmorHandler {
    
    // Base UUIDs for different equipment slots and attribute types
    // We'll generate unique UUIDs for each item/attribute combination
    private static final String UUID_BASE = "A0E0F0E0-7CE8-4030-940E-514C1F16";
    
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!ArmorEffectsConfig.enableArmorEffects) {
            return;
        }
        
        EquipmentSlot slot = event.getSlot();
        
        // Only handle armor slots
        if (slot.getType() != EquipmentSlot.Type.ARMOR) {
            return;
        }
        
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        LivingEntity entity = event.getEntity();
        
        // Remove old attribute effects
        if (!from.isEmpty()) {
            removeAttributeEffects(entity, from, slot);
        }
        
        // Apply new attribute effects
        if (!to.isEmpty()) {
            applyAttributeEffects(entity, to, slot);
        }
    }
    
    private static void removeAttributeEffects(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
        ArmorEffectType effect = ArmorEffectRegistry.getEffect(stack.getItem());
        if (effect == null || effect.getCategory() != ArmorEffectType.Category.ATTRIBUTE) {
            return;
        }
        
        Attribute attribute = getAttributeForEffect(effect);
        if (attribute != null && entity.getAttribute(attribute) != null) {
            String effectIdStr = (effect instanceof ArmorEffectType.AttributeArmorEffect) ? 
                ((ArmorEffectType.AttributeArmorEffect) effect).getEffectIdString() : 
                effect.getEffectId().getPath();
            UUID modifierUUID = generateUUIDForItem(stack, slot, effectIdStr);
            
            // Only remove if the modifier exists
            if (entity.getAttribute(attribute).getModifier(modifierUUID) != null) {
                entity.getAttribute(attribute).removeModifier(modifierUUID);
            }
        }
    }
    
    private static void applyAttributeEffects(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
        ArmorEffectType effect = ArmorEffectRegistry.getEffect(stack.getItem());
        if (effect == null || effect.getCategory() != ArmorEffectType.Category.ATTRIBUTE) {
            return;
        }
        
        Attribute attribute = getAttributeForEffect(effect);
        if (attribute != null && entity.getAttribute(attribute) != null) {
            double value = calculateAttributeValue(effect, slot);
            if (value != 0) {
                String effectIdStr = (effect instanceof ArmorEffectType.AttributeArmorEffect) ? 
                    ((ArmorEffectType.AttributeArmorEffect) effect).getEffectIdString() : 
                    effect.getEffectId().getPath();
                UUID modifierUUID = generateUUIDForItem(stack, slot, effectIdStr);
                String name = getAttributeModifierName(stack, slot, effect);
                
                // Check if modifier already exists to prevent duplicates
                if (entity.getAttribute(attribute).getModifier(modifierUUID) == null) {
                    AttributeModifier modifier = new AttributeModifier(
                        modifierUUID,
                        name,
                        value,
                        getOperationForEffect(effect)
                    );
                    
                    entity.getAttribute(attribute).addPermanentModifier(modifier);
                }
            }
        }
    }
    
    /**
     * Generate a unique UUID for each item/slot/effect combination
     */
    private static UUID generateUUIDForItem(ItemStack stack, EquipmentSlot slot, String effectId) {
        // Create a deterministic UUID based on item, slot, and effect
        String itemId = stack.getItem().toString();
        String combined = itemId + "_" + slot.getName() + "_" + effectId;
        
        // Simple hash to create a unique identifier
        int hash = combined.hashCode();
        String uuidString = UUID_BASE + String.format("%04d", Math.abs(hash % 10000));
        
        return UUID.fromString(uuidString);
    }
    
    /**
     * Get the Minecraft attribute that corresponds to an effect
     */
    private static Attribute getAttributeForEffect(ArmorEffectType effect) {
        String effectId = effect.getEffectId().getPath();
        return switch (effectId) {
            case "speed" -> Attributes.MOVEMENT_SPEED;
            case "attack_damage" -> Attributes.ATTACK_DAMAGE;
            case "attack_speed" -> Attributes.ATTACK_SPEED;
            case "armor" -> Attributes.ARMOR;
            case "armor_toughness" -> Attributes.ARMOR_TOUGHNESS;
            case "knockback_resistance" -> Attributes.KNOCKBACK_RESISTANCE;
            case "max_health" -> Attributes.MAX_HEALTH;
            default -> null;
        };
    }
    
    /**
     * Calculate the attribute value based on the effect level and equipment slot
     */
    private static double calculateAttributeValue(ArmorEffectType effect, EquipmentSlot slot) {
        // Simple: use the slot percentage directly as the effect value
        // 12% helmet = 12% effect, regardless of base level or effect type
        return getSlotMultiplier(slot) / 100.0; // Convert percentage to decimal (12% -> 0.12)
    }
    
    /**
     * Get the multiplier for different equipment slots based on config
     */
    private static double getSlotMultiplier(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ArmorEffectsConfig.helmetEffectPercentage;
            case CHEST -> ArmorEffectsConfig.chestplateEffectPercentage;
            case LEGS -> ArmorEffectsConfig.leggingsEffectPercentage;
            case FEET -> ArmorEffectsConfig.bootsEffectPercentage;
            default -> 100.0;
        };
    }
    
    /**
     * Get the operation type for different effects
     */
    private static AttributeModifier.Operation getOperationForEffect(ArmorEffectType effect) {
        String effectId = effect.getEffectId().getPath();
        return switch (effectId) {
            case "speed", "attack_speed" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "attack_damage", "armor", "armor_toughness", "max_health" -> AttributeModifier.Operation.ADDITION;
            case "knockback_resistance" -> AttributeModifier.Operation.ADDITION;
            default -> AttributeModifier.Operation.ADDITION;
        };
    }
    
    /**
     * Generate a human-readable name for the attribute modifier
     */
    private static String getAttributeModifierName(ItemStack stack, EquipmentSlot slot, ArmorEffectType effect) {
        String itemName = stack.getItem().toString().replace("minecraft:", "");
        String effectName = effect.getEffectId().getPath().replace("_", " ");
        return String.format("%s %s boost (%s)", 
            itemName, 
            effectName, 
            slot.getName());
    }
}