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
               .comment("Format: \"namespace:item_name+category:effect_id@level\"")
               .comment("Categories: potion, damage, enchant")
               .comment("Examples:")
               .comment("  \"minecraft:diamond_chestplate+potion:minecraft:regeneration@2\"")
               .comment("  \"minecraft:leather_boots+damage:fall_protection@15\"")
               .comment("  \"minecraft:iron_helmet+potion:minecraft:night_vision@1\"")
               .push("item_effects");
        
        ITEM_EFFECTS = BUILDER
                .comment("List of item-specific effects using the new formula:")
                .comment("Helmet/Boots: 12% effect (3 ingots x 3 = 9 + 3 base = 12)")
                .comment("Chestplate: 18% effect (8 ingots x 3 = 24, but capped at 18)")
                .comment("Leggings: 15% effect (7 ingots x 3 = 21, but capped at 15)")
                .defineList("items", 
                    List.of(
                        // Leather Armor - Feather Falling (Fall Damage Reduction)
                        "minecraft:leather_helmet+damage:fall_protection@12",
                        "minecraft:leather_chestplate+damage:fall_protection@18", 
                        "minecraft:leather_leggings+damage:fall_protection@15",
                        "minecraft:leather_boots+damage:fall_protection@12",
                        
                        // Iron Armor - Projectile Protection  
                        "minecraft:iron_helmet+damage:projectile_protection@12",
                        "minecraft:iron_chestplate+damage:projectile_protection@18",
                        "minecraft:iron_leggings+damage:projectile_protection@15", 
                        "minecraft:iron_boots+damage:projectile_protection@12",
                        
                        // Golden Armor - Speed Effect
                        "minecraft:golden_helmet+potion:minecraft:speed@1",
                        "minecraft:golden_chestplate+potion:minecraft:speed@2",
                        "minecraft:golden_leggings+potion:minecraft:speed@1",
                        "minecraft:golden_boots+potion:minecraft:speed@1",
                        
                        // Diamond Armor - Blast Protection
                        "minecraft:diamond_helmet+damage:blast_protection@12",
                        "minecraft:diamond_chestplate+damage:blast_protection@18",
                        "minecraft:diamond_leggings+damage:blast_protection@15",
                        "minecraft:diamond_boots+damage:blast_protection@12",
                        
                        // Netherite Armor - Fire Protection
                        "minecraft:netherite_helmet+damage:fire_protection@12",
                        "minecraft:netherite_chestplate+damage:fire_protection@18",
                        "minecraft:netherite_leggings+damage:fire_protection@15",
                        "minecraft:netherite_boots+damage:fire_protection@12"
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