package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.internal.RainfallRuntime;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

public final class RainfallKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void registerBindings(BindingsEvent event) {
        if (event.getType() != ScriptType.SERVER) {
            return;
        }
        KubeRainfall.beginReload();
        event.add("Rainfall", KubeRainfall.INSTANCE);
    }

    @Override
    public void onServerReload() {
        RainfallRuntime.markRulesDirty();
    }
}
