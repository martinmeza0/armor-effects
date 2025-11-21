package com.armoreffects.system;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArmorEffectContext {
    private final Player player;
    private final ItemStack armorPiece;
    private final EquipmentSlot slot;
    private final DamageSource damageSource; // null for non-damage contexts
    private final float originalDamage; // 0 for non-damage contexts
    
    public ArmorEffectContext(Player player, ItemStack armorPiece, EquipmentSlot slot) {
        this(player, armorPiece, slot, null, 0);
    }
    
    public ArmorEffectContext(Player player, ItemStack armorPiece, EquipmentSlot slot, 
                            DamageSource damageSource, float originalDamage) {
        this.player = player;
        this.armorPiece = armorPiece;
        this.slot = slot;
        this.damageSource = damageSource;
        this.originalDamage = originalDamage;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public ItemStack getArmorPiece() {
        return armorPiece;
    }
    
    public EquipmentSlot getSlot() {
        return slot;
    }
    
    public DamageSource getDamageSource() {
        return damageSource;
    }
    
    public float getOriginalDamage() {
        return originalDamage;
    }
    
    public boolean isDamageContext() {
        return damageSource != null;
    }
}