package com.armoreffects;

import com.armoreffects.config.ArmorEffectsConfig;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(ArmorEffectsModule.MODID)
public class ArmorEffectsModule {
    
    public static final String MODID = "armoreffects";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static final Map<Item, Object2IntMap<ResourceKey<Enchantment>>> BUILTIN_ENCHANTMENTS = new HashMap<>();
    private static boolean staticEnabled = true;

    public ArmorEffectsModule(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::configChanged);
        
        MinecraftForge.EVENT_BUS.register(this);
        
        context.registerConfig(ModConfig.Type.COMMON, ArmorEffectsConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Armor Effects mod is loading...");
        setupArmorEnchantments();
        loadItemEffectsFromConfig();
    }

    @SubscribeEvent
    public void configChanged(final ModConfigEvent event) {
        staticEnabled = true;
        setupArmorEnchantments();
        loadItemEffectsFromConfig();
    }
    
    private void loadItemEffectsFromConfig() {
        com.armoreffects.system.ArmorEffectRegistry.loadFromConfig(ArmorEffectsConfig.itemEffects);
    }

    private void setupArmorEnchantments() {
        BUILTIN_ENCHANTMENTS.clear();
        
        if (!ArmorEffectsConfig.enableArmorEffects) {
            return;
        }

        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof ArmorItem armorItem)) continue;
            
            ArmorMaterial material = armorItem.getMaterial();
            EquipmentSlot slot = armorItem.getEquipmentSlot();
            
            // Skip chainmail armor as per requirements
            if (isChainmailArmor(item)) continue;
            
            ResourceKey<Enchantment> enchantment = getEnchantmentForMaterial(material);
            if (enchantment == null) continue;
            
            int level = getEffectLevelForSlot(slot);
            if (level > 0) {
                Object2IntMap<ResourceKey<Enchantment>> entry = BUILTIN_ENCHANTMENTS.computeIfAbsent(item, it -> new Object2IntArrayMap<>());
                entry.put(enchantment, level);
            }
        }
        
        LOGGER.info("Applied built-in enchantments to {} armor pieces", BUILTIN_ENCHANTMENTS.size());
    }
    
    private ResourceKey<Enchantment> getEnchantmentForMaterial(ArmorMaterial material) {
        if (material == ArmorMaterials.LEATHER && ArmorEffectsConfig.enableLeatherEffects) {
            return Enchantments.FEATHER_FALLING; // Fall damage reduction
        } else if (material == ArmorMaterials.IRON && ArmorEffectsConfig.enableIronEffects) {
            return Enchantments.PROJECTILE_PROTECTION; // Projectile protection
        } else if (material == ArmorMaterials.DIAMOND && ArmorEffectsConfig.enableDiamondEffects) {
            return Enchantments.BLAST_PROTECTION; // Blast protection
        } else if (material == ArmorMaterials.NETHERITE && ArmorEffectsConfig.enableNetheriteEffects) {
            return Enchantments.FIRE_PROTECTION; // Fire protection
        } else if (material == ArmorMaterials.GOLD && ArmorEffectsConfig.enableGoldEffects) {
            return Enchantments.SWIFT_SNEAK; // Speed effect (closest enchantment)
        }
        return null;
    }
    
    private int getEffectLevelForSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD, FEET -> Math.max(1, ArmorEffectsConfig.helmetEffectPercentage / 10); // 12% -> level 1
            case CHEST -> Math.max(1, ArmorEffectsConfig.chestplateEffectPercentage / 10); // 18% -> level 2
            case LEGS -> Math.max(1, ArmorEffectsConfig.leggingsEffectPercentage / 10); // 15% -> level 2
            default -> 0;
        };
    }
    
    private boolean isChainmailArmor(Item item) {
        return item == Items.CHAINMAIL_HELMET ||
               item == Items.CHAINMAIL_CHESTPLATE ||
               item == Items.CHAINMAIL_LEGGINGS ||
               item == Items.CHAINMAIL_BOOTS;
    }

    // Hook into enchantment level calculation
    public static int getActualEnchantmentLevel(Holder<Enchantment> holder, ItemStack stack, int original) {
        if (!staticEnabled) return original;
        
        if (BUILTIN_ENCHANTMENTS.containsKey(stack.getItem())) {
            Object2IntMap<ResourceKey<Enchantment>> enchantmentMap = BUILTIN_ENCHANTMENTS.get(stack.getItem());
            
            if (enchantmentMap.containsKey(holder.getKey())) {
                int level = enchantmentMap.getOrDefault(holder.getKey(), 0);
                return Math.max(level, original);
            }
        }
        return original;
    }

    // Create tooltip stack with built-in enchantments
    public static ItemStack createTooltipStack(ItemStack stack, DataComponentType<?> componentType, HolderLookup.Provider provider) {
        if (!staticEnabled || !ArmorEffectsConfig.displayEnchantmentsInTooltip || componentType != DataComponents.ENCHANTMENTS) {
            return stack;
        }

        if (BUILTIN_ENCHANTMENTS.containsKey(stack.getItem())) {
            ItemStack copy = stack.copy();
            Object2IntMap<ResourceKey<Enchantment>> builtInEnchantments = BUILTIN_ENCHANTMENTS.get(stack.getItem());
            ItemEnchantments itemEnchantments = Optional.ofNullable(copy.get(DataComponents.ENCHANTMENTS)).orElse(ItemEnchantments.EMPTY);
            ItemEnchantments.Mutable newEnchantments = new ItemEnchantments.Mutable(itemEnchantments);

            for (ResourceKey<Enchantment> enchantmentKey : builtInEnchantments.keySet()) {
                Holder<Enchantment> holder = provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantmentKey);
                newEnchantments.set(holder, Math.max(newEnchantments.getLevel(holder), builtInEnchantments.getOrDefault(enchantmentKey, 0)));
            }

            copy.set(DataComponents.ENCHANTMENTS, newEnchantments.toImmutable());
            return copy;
        }
        return stack;
    }

    // Modify tooltip to show built-in enchantments in italic
    public static void modifyTooltip(ItemStack stack, List<Component> list, HolderLookup.Provider provider) {
        if (!ArmorEffectsConfig.displayEnchantmentsInTooltip || !ArmorEffectsConfig.italicTooltip) return;

        if (BUILTIN_ENCHANTMENTS.containsKey(stack.getItem())) {
            Object2IntMap<ResourceKey<Enchantment>> builtInEnchantments = BUILTIN_ENCHANTMENTS.get(stack.getItem());

            for (ResourceKey<Enchantment> enchantmentKey : builtInEnchantments.keySet()) {
                Holder<Enchantment> holder = provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantmentKey);
                int level = builtInEnchantments.getInt(enchantmentKey);
                Component enchantmentEntry = Enchantment.getFullname(holder, level);
                if (list.contains(enchantmentEntry)) {
                    int index = list.indexOf(enchantmentEntry);
                    list.set(index, Enchantment.getFullname(holder, level).copy().withStyle(ChatFormatting.ITALIC));
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Armor Effects mod server starting");
    }
}