package com.armoreffects;

import com.armoreffects.config.ArmorEffectsConfig;
import com.mojang.logging.LogUtils;
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

import java.util.List;

@Mod(ArmorEffectsModule.MODID)
public class ArmorEffectsModule {
    
    public static final String MODID = "armoreffects";
    private static final Logger LOGGER = LogUtils.getLogger();
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
        loadItemEffectsFromConfig();
    }

    @SubscribeEvent
    public void configChanged(final ModConfigEvent event) {
        staticEnabled = true;
        loadItemEffectsFromConfig();
    }
    
    private void loadItemEffectsFromConfig() {
        List<String> configEntries = ArmorEffectsConfig.itemEffects;
        if (configEntries == null) {
            LOGGER.warn("Config not loaded yet, using default effects");
            configEntries = List.of();
        }
        com.armoreffects.system.ArmorEffectRegistry.loadFromConfig(configEntries);
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Armor Effects mod server starting");
    }
}