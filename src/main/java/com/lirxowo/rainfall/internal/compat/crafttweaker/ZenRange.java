package com.lirxowo.rainfall.internal.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.lirxowo.rainfall.api.builder.RandomFortuneInt;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.dropt.Range")
public final class ZenRange {

    private final RandomFortuneInt range;

    ZenRange(RandomFortuneInt range) {
        this.range = range;
    }

    RandomFortuneInt getRange() {
        return this.range;
    }
}
