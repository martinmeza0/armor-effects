package com.armoreffects.events;

import com.armoreffects.ArmorEffectsModule;
import com.armoreffects.config.ArmorEffectsConfig;
import com.armoreffects.system.ArmorEffectContext;
import com.armoreffects.system.ArmorEffectRegistry;
import com.armoreffects.system.ArmorEffectType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArmorEffectsModule.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArmorEffectHandler {
    
    private static int tickCounter = 0;
    private static final int EFFECT_CHECK_INTERVAL = 40; // Check every 2 seconds (40 ticks)
    private static final int EFFECT_DURATION = 60; // 3 seconds duration
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        
        if (!ArmorEffectsConfig.enableArmorEffects) return;
        
        Player player = event.player;
        
        // Only check armor effects every 2 seconds to reduce performance impact
        if (tickCounter++ >= EFFECT_CHECK_INTERVAL) {
            tickCounter = 0;
            applyArmorEffects(player);
        }
    }
    
    private static void applyArmorEffects(Player player) {
        // Check each armor piece for potion effects
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
            
            ItemStack armorPiece = player.getItemBySlot(slot);
            if (armorPiece.isEmpty() || !(armorPiece.getItem() instanceof ArmorItem)) {
                continue;
            }
            
            ArmorEffectType effect = ArmorEffectRegistry.getEffect(armorPiece.getItem());
            if (effect == null || effect.getCategory() != ArmorEffectType.Category.POTION) {
                continue;
            }
            
            // Apply the potion effect
            ArmorEffectContext context = new ArmorEffectContext(player, armorPiece, slot);
            effect.applyEffect(context);
        }
    }
}