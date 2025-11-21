package com.armoreffects.system;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorEffectRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Item, ArmorEffectType> ITEM_EFFECTS = new HashMap<>();
    
    public static void clear() {
        ITEM_EFFECTS.clear();
    }
    
    public static void registerEffect(Item item, ArmorEffectType effect) {
        ITEM_EFFECTS.put(item, effect);
        LOGGER.debug("Registered effect {} for item {}", effect.getEffectId(), BuiltInRegistries.ITEM.getKey(item));
    }
    
    public static ArmorEffectType getEffect(Item item) {
        return ITEM_EFFECTS.get(item);
    }
    
    public static boolean hasEffect(Item item) {
        return ITEM_EFFECTS.containsKey(item);
    }
    
    public static void loadFromConfig(List<String> configEntries) {
        clear();
        
        if (configEntries == null) {
            LOGGER.warn("Config entries list is null, no effects loaded");
            return;
        }
        
        for (String entry : configEntries) {
            try {
                parseConfigEntry(entry);
            } catch (Exception e) {
                LOGGER.error("Failed to parse armor effect config entry: {}", entry, e);
            }
        }
        
        LOGGER.info("Loaded {} armor effects from config", ITEM_EFFECTS.size());
    }
    
    private static void parseConfigEntry(String entry) {
        // Format: "namespace:item_name+category:effect_id@level"
        // Example: "minecraft:diamond_chestplate+potion:minecraft:regeneration@2"
        
        String[] parts = entry.split("\\+", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid format, expected 'item+effect': " + entry);
        }
        
        String itemStr = parts[0].trim();
        String effectStr = parts[1].trim();
        
        // Parse item
        ResourceLocation itemLocation = ResourceLocation.tryParse(itemStr);
        if (itemLocation == null) {
            throw new IllegalArgumentException("Invalid item identifier: " + itemStr);
        }
        
        Item item = BuiltInRegistries.ITEM.get(itemLocation);
        if (item == null || item == Items.AIR) {
            LOGGER.warn("Unknown item: {}, skipping", itemStr);
            return;
        }
        
        // Parse effect
        ArmorEffectType effect = ArmorEffectType.parse(effectStr);
        
        // Register the effect
        registerEffect(item, effect);
    }
    
    public static Map<Item, ArmorEffectType> getAllEffects() {
        return new HashMap<>(ITEM_EFFECTS);
    }
}