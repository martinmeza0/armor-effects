package com.armoreffects.events;

import com.armoreffects.ArmorEffectsModule;
import com.armoreffects.config.ArmorEffectsConfig;
import com.armoreffects.system.ArmorEffectContext;
import com.armoreffects.system.ArmorEffectRegistry;
import com.armoreffects.system.ArmorEffectType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArmorEffectsModule.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArmorDamageHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!ArmorEffectsConfig.enableArmorEffects) return;
        
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getSource();
        float originalDamage = event.getAmount();
        
        float damageReduction = calculateDamageReduction(entity, damageSource, originalDamage);
        
        if (damageReduction > 0) {
            float reducedDamage = originalDamage * (1.0f - damageReduction);
            event.setAmount(reducedDamage);
        }
    }
    
    private static float calculateDamageReduction(LivingEntity entity, DamageSource damageSource, float originalDamage) {
        float totalReduction = 0.0f;
        
        // Only apply to players for now (can be extended to all LivingEntity later)
        if (!(entity instanceof Player player)) return 0.0f;
        
        // Check each armor piece
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
            
            ItemStack armorPiece = player.getItemBySlot(slot);
            if (armorPiece.isEmpty() || !(armorPiece.getItem() instanceof ArmorItem)) {
                continue;
            }
            
            ArmorEffectType effect = ArmorEffectRegistry.getEffect(armorPiece.getItem());
            if (effect == null || effect.getCategory() != ArmorEffectType.Category.DAMAGE_REDUCTION) {
                continue;
            }
            
            ArmorEffectType.DamageReductionArmorEffect damageEffect = (ArmorEffectType.DamageReductionArmorEffect) effect;
            
            if (damageEffect.appliesTo(damageSource)) {
                totalReduction += damageEffect.getReductionAmount();
            }
        }
        
        // Cap at 80% damage reduction
        return Math.min(totalReduction, 0.8f);
    }
}