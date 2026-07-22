package com.lirxowo.rainfall;

import com.lirxowo.rainfall.internal.RainfallRuntime;
import com.lirxowo.rainfall.internal.config.RainfallConfig;
import com.lirxowo.rainfall.internal.registry.RainfallRegistries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Rainfall.MODID)
public class Rainfall {

    public static final String MODID = "rainfall";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public Rainfall(FMLJavaModLoadingContext loadingContext) {
        IEventBus modEventBus = loadingContext.getModEventBus();
        RainfallRegistries.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        loadingContext.registerConfig(ModConfig.Type.COMMON, RainfallConfig.SPEC, MODID + "/rainfall.toml");
        MinecraftForge.EVENT_BUS.register(new RainfallRuntime());
    }
}
