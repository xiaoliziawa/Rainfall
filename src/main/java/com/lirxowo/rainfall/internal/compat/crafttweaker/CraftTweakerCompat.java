package com.lirxowo.rainfall.internal.compat.crafttweaker;

import com.lirxowo.rainfall.api.event.DroptLoadRulesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class CraftTweakerCompat {

    @SubscribeEvent
    public void onLoadRules(DroptLoadRulesEvent event) {
        ZenDropt.registerRules();
    }
}
