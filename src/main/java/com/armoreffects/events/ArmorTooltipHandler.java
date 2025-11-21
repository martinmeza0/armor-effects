package com.armoreffects.events;

import com.armoreffects.ArmorEffectsModule;
import com.armoreffects.config.ArmorEffectsConfig;
import com.armoreffects.system.ArmorEffectRegistry;
import com.armoreffects.system.ArmorEffectType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArmorEffectsModule.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ArmorTooltipHandler {
    
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!ArmorEffectsConfig.displayEnchantmentsInTooltip) {
            return;
        }
        
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem)) {
            return;
        }
        
        ArmorEffectType effect = ArmorEffectRegistry.getEffect(stack.getItem());
        if (effect == null) {
            return;
        }
        
        // Determine equipment slot from armor item
        EquipmentSlot slot = ((ArmorItem) stack.getItem()).getEquipmentSlot();
        
        // Add tooltip based on effect category with percentage
        String tooltipText = getEffectTooltipText(effect, slot);
        
        // Color code based on category
        ChatFormatting color = switch (effect.getCategory()) {
            case POTION -> ChatFormatting.BLUE;
            case DAMAGE_REDUCTION -> ChatFormatting.GREEN;
            case ENCHANTMENT -> ChatFormatting.AQUA;
            case ATTRIBUTE -> ChatFormatting.GOLD;
        };
        
        Component tooltip = Component.literal(tooltipText).withStyle(color);
        
        event.getToolTip().add(tooltip);
    }
    
    private static String getEffectTooltipText(ArmorEffectType effect, EquipmentSlot slot) {
        String effectId = effect.getEffectId().getPath();
        double slotMultiplier = getSlotMultiplier(slot);
        
        return switch (effect.getCategory()) {
            case POTION -> getPotionTooltip(effectId, effect.getLevel());
            case DAMAGE_REDUCTION -> getDamageReductionTooltip(effectId, effect.getLevel(), slotMultiplier);
            case ENCHANTMENT -> getEnchantmentTooltip(effectId, effect.getLevel(), slotMultiplier);
            case ATTRIBUTE -> getAttributeTooltip(effectId, effect.getLevel(), slotMultiplier);
        };
    }
    
    private static double getSlotMultiplier(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ArmorEffectsConfig.helmetEffectPercentage;
            case CHEST -> ArmorEffectsConfig.chestplateEffectPercentage;
            case LEGS -> ArmorEffectsConfig.leggingsEffectPercentage;
            case FEET -> ArmorEffectsConfig.bootsEffectPercentage;
            default -> 100.0;
        };
    }
    
    private static String getPotionTooltip(String effectId, int level) {
        String name = formatPotionName(effectId);
        String levelStr = level > 1 ? " " + getRomanNumeral(level) : "";
        return name + levelStr;
    }
    
    private static String getDamageReductionTooltip(String effectId, int level, double slotMultiplier) {
        // Simple: use slot percentage directly (12% helmet = 12% protection)
        int percentage = (int) Math.round(slotMultiplier);
        
        String protectionType = switch (effectId) {
            case "fire_protection" -> "Fire Damage";
            case "projectile_protection" -> "Projectile Damage";
            case "blast_protection" -> "Explosion Damage";
            case "fall_protection" -> "Fall Damage";
            case "magic_protection" -> "Magic Damage";
            case "general_protection" -> "All Damage";
            default -> "Damage";
        };
        
        return "+" + percentage + "% " + protectionType + " Reduction";
    }
    
    private static String getEnchantmentTooltip(String effectId, int level, double slotMultiplier) {
        String name = formatEnchantmentName(effectId);
        String levelStr = level > 1 ? " " + getRomanNumeral(level) : "";
        return name + levelStr;
    }
    
    private static String getAttributeTooltip(String effectId, int level, double slotMultiplier) {
        // Simple: use slot percentage directly (12% helmet = 12% effect)
        int percentage = (int) Math.round(slotMultiplier);
        
        return switch (effectId) {
            case "speed" -> "+" + percentage + "% Movement Speed";
            case "attack_damage" -> "+" + percentage + "% Attack Damage";
            case "attack_speed" -> "+" + percentage + "% Attack Speed";
            case "armor" -> "+" + percentage + "% Armor";
            case "armor_toughness" -> "+" + percentage + "% Armor Toughness";
            case "knockback_resistance" -> "+" + percentage + "% Knockback Resistance";
            case "max_health" -> "+" + percentage + "% Max Health";
            default -> formatAttributeName(effectId) + " " + percentage + "%";
        };
    }
    
    private static String getEffectDisplayName(ArmorEffectType effect) {
        String effectId = effect.getEffectId().getPath();
        
        return switch (effect.getCategory()) {
            case POTION -> formatPotionName(effectId);
            case DAMAGE_REDUCTION -> formatProtectionName(effectId);
            case ENCHANTMENT -> formatEnchantmentName(effectId);
            case ATTRIBUTE -> formatAttributeName(effectId);
        };
    }
    
    private static String formatPotionName(String effectId) {
        // Convert effect IDs like "minecraft:regeneration" to "Regeneration"
        String name = effectId.replace("minecraft:", "").replace("_", " ");
        return capitalizeWords(name);
    }
    
    private static String formatProtectionName(String effectId) {
        return switch (effectId) {
            case "fire_protection" -> "Fire Protection";
            case "projectile_protection" -> "Projectile Protection";
            case "blast_protection" -> "Blast Protection";
            case "fall_protection" -> "Feather Falling";
            case "magic_protection" -> "Magic Protection";
            case "general_protection" -> "Protection";
            default -> capitalizeWords(effectId.replace("_", " "));
        };
    }
    
    private static String formatEnchantmentName(String effectId) {
        return capitalizeWords(effectId.replace("_", " "));
    }
    
    private static String formatAttributeName(String effectId) {
        return switch (effectId) {
            case "speed" -> "Speed";
            case "attack_damage" -> "Strength";
            case "attack_speed" -> "Haste";
            case "armor" -> "Resistance";
            case "armor_toughness" -> "Toughness";
            case "knockback_resistance" -> "Knockback Resistance";
            case "max_health" -> "Health Boost";
            default -> capitalizeWords(effectId.replace("_", " "));
        };
    }
    
    private static String capitalizeWords(String str) {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
        }
        
        return result.toString();
    }
    
    private static String getRomanNumeral(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(level);
        };
    }
}