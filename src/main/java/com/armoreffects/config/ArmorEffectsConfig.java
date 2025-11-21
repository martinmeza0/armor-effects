package com.armoreffects.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArmorEffectsConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue HELMET_EFFECT_PERCENTAGE;
    public static final ForgeConfigSpec.IntValue CHESTPLATE_EFFECT_PERCENTAGE;
    public static final ForgeConfigSpec.IntValue LEGGINGS_EFFECT_PERCENTAGE;
    public static final ForgeConfigSpec.IntValue BOOTS_EFFECT_PERCENTAGE;
    
    public static final ForgeConfigSpec.BooleanValue ENABLE_LEATHER_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_IRON_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_GOLD_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DIAMOND_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_NETHERITE_EFFECTS;
    
    public static final ForgeConfigSpec.BooleanValue ENABLE_ARMOR_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue DISPLAY_ENCHANTMENTS_IN_TOOLTIP;
    public static final ForgeConfigSpec.BooleanValue ITALIC_TOOLTIP;
    
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_EFFECTS;

    static {
        BUILDER.comment("Armor Effects Configuration");
        
        BUILDER.comment("Effect Percentages by Armor Piece")
               .push("effect_percentages");
        
        HELMET_EFFECT_PERCENTAGE = BUILDER
                .comment("Effect percentage for helmets (default: 12%)")
                .defineInRange("helmet_percentage", 12, 0, 100);
                
        CHESTPLATE_EFFECT_PERCENTAGE = BUILDER
                .comment("Effect percentage for chestplates (default: 18%)")
                .defineInRange("chestplate_percentage", 18, 0, 100);
                
        LEGGINGS_EFFECT_PERCENTAGE = BUILDER
                .comment("Effect percentage for leggings (default: 15%)")
                .defineInRange("leggings_percentage", 15, 0, 100);
                
        BOOTS_EFFECT_PERCENTAGE = BUILDER
                .comment("Effect percentage for boots (default: 12%)")
                .defineInRange("boots_percentage", 12, 0, 100);
                
        BUILDER.pop();
        
        BUILDER.comment("Enable/Disable Effects by Material")
               .push("material_effects");
               
        ENABLE_LEATHER_EFFECTS = BUILDER
                .comment("Enable fall damage reduction for leather armor")
                .define("enable_leather_effects", true);
                
        ENABLE_IRON_EFFECTS = BUILDER
                .comment("Enable projectile protection for iron armor")
                .define("enable_iron_effects", true);
                
        ENABLE_GOLD_EFFECTS = BUILDER
                .comment("Enable speed effect for gold armor")
                .define("enable_gold_effects", true);
                
        ENABLE_DIAMOND_EFFECTS = BUILDER
                .comment("Enable blast protection for diamond armor")
                .define("enable_diamond_effects", true);
                
        ENABLE_NETHERITE_EFFECTS = BUILDER
                .comment("Enable fire protection for netherite armor")
                .define("enable_netherite_effects", true);
                
        BUILDER.pop();
        
        BUILDER.comment("General Settings")
               .push("general");
               
        ENABLE_ARMOR_EFFECTS = BUILDER
                .comment("Enable or disable all armor effects")
                .define("enable_armor_effects", true);
                
        DISPLAY_ENCHANTMENTS_IN_TOOLTIP = BUILDER
                .comment("Show built-in enchantments in item tooltips")
                .define("display_enchantments_in_tooltip", true);
                
        ITALIC_TOOLTIP = BUILDER
                .comment("Display built-in enchantments in italic text")
                .define("italic_tooltip", true);
                
        BUILDER.pop();
        
        BUILDER.comment("Item-Specific Effects")
               .comment("Format: \"namespace:item_name+category:effect_id\" or \"namespace:item_name+category:effect_id@level\"")
               .comment("Categories: potion, damage, enchant, attribute")
               .comment("Examples:")
               .comment("  \"minecraft:diamond_chestplate+potion:minecraft:regeneration@2\" - Regeneration II")
               .comment("  \"minecraft:leather_boots+damage:fall_protection\" - Uses slot percentage")
               .comment("  \"minecraft:iron_helmet+potion:minecraft:night_vision\" - Night Vision I (default)")
               .comment("  \"minecraft:golden_chestplate+attribute:speed\" - Uses slot percentage")
               .comment("  \"minecraft:netherite_boots+enchant:minecraft:fire_protection@4\" - Fire Protection IV")
               .comment("Note: @level only affects potion and enchantment categories")
               .comment("Available attributes: speed, attack_damage, attack_speed, armor, armor_toughness, knockback_resistance, max_health")
               .push("item_effects");
        
        ITEM_EFFECTS = BUILDER
                .comment("List of item-specific effects using the new formula:")
                .comment("Helmet/Boots: 12% effect (3 ingots x 3 = 9 + 3 base = 12)")
                .comment("Chestplate: 18% effect (8 ingots x 3 = 24, but capped at 18)")
                .comment("Leggings: 15% effect (7 ingots x 3 = 21, but capped at 15)")
                .defineList("items", 
                    List.of(
                        // Leather Armor - Feather Falling (Fall Damage Reduction)
                        "minecraft:leather_helmet+damage:fall_protection",
                        "minecraft:leather_chestplate+damage:fall_protection", 
                        "minecraft:leather_leggings+damage:fall_protection",
                        "minecraft:leather_boots+damage:fall_protection",
                        
                        // Iron Armor - Projectile Protection  
                        "minecraft:iron_helmet+damage:projectile_protection",
                        "minecraft:iron_chestplate+damage:projectile_protection",
                        "minecraft:iron_leggings+damage:projectile_protection", 
                        "minecraft:iron_boots+damage:projectile_protection",
                        
                        // Golden Armor - Speed effects using AttributeModifiers (better performance than potion effects)
                        "minecraft:golden_helmet+attribute:speed",
                        "minecraft:golden_chestplate+attribute:speed", 
                        "minecraft:golden_leggings+attribute:speed",
                        "minecraft:golden_boots+attribute:speed",
                        
                        // Diamond Armor - Blast Protection
                        "minecraft:diamond_helmet+damage:blast_protection",
                        "minecraft:diamond_chestplate+damage:blast_protection",
                        "minecraft:diamond_leggings+damage:blast_protection",
                        "minecraft:diamond_boots+damage:blast_protection",
                        
                        // Netherite Armor - Fire Protection
                        "minecraft:netherite_helmet+damage:fire_protection",
                        "minecraft:netherite_chestplate+damage:fire_protection",
                        "minecraft:netherite_leggings+damage:fire_protection",
                        "minecraft:netherite_boots+damage:fire_protection"
                        
                        // Example potion effects with levels (commented out by default)
                        // "minecraft:diamond_helmet+potion:minecraft:night_vision@1",     // Night Vision I  
                        // "minecraft:diamond_chestplate+potion:minecraft:regeneration@2", // Regeneration II
                        // "minecraft:iron_boots+potion:minecraft:speed@3"                 // Speed III
                    ),
                    entry -> entry instanceof String
                );
                
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int helmetEffectPercentage;
    public static int chestplateEffectPercentage;
    public static int leggingsEffectPercentage;
    public static int bootsEffectPercentage;
    public static boolean enableLeatherEffects;
    public static boolean enableIronEffects;
    public static boolean enableGoldEffects;
    public static boolean enableDiamondEffects;
    public static boolean enableNetheriteEffects;
    public static boolean enableArmorEffects;
    public static boolean displayEnchantmentsInTooltip;
    public static boolean italicTooltip;
    public static List<String> itemEffects;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        helmetEffectPercentage = HELMET_EFFECT_PERCENTAGE.get();
        chestplateEffectPercentage = CHESTPLATE_EFFECT_PERCENTAGE.get();
        leggingsEffectPercentage = LEGGINGS_EFFECT_PERCENTAGE.get();
        bootsEffectPercentage = BOOTS_EFFECT_PERCENTAGE.get();
        enableLeatherEffects = ENABLE_LEATHER_EFFECTS.get();
        enableIronEffects = ENABLE_IRON_EFFECTS.get();
        enableGoldEffects = ENABLE_GOLD_EFFECTS.get();
        enableDiamondEffects = ENABLE_DIAMOND_EFFECTS.get();
        enableNetheriteEffects = ENABLE_NETHERITE_EFFECTS.get();
        enableArmorEffects = ENABLE_ARMOR_EFFECTS.get();
        displayEnchantmentsInTooltip = DISPLAY_ENCHANTMENTS_IN_TOOLTIP.get();
        italicTooltip = ITALIC_TOOLTIP.get();
        itemEffects = ITEM_EFFECTS.get().stream().map(String::valueOf).toList();
    }
}