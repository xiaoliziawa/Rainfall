package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.api.event.DroptLoadRulesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class KubeJSCompat {

    @SubscribeEvent
    public void onLoadRules(DroptLoadRulesEvent event) {
        KubeRainfall.registerRules();
    }
}
