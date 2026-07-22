package com.lirxowo.rainfall.internal.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.lirxowo.rainfall.api.builder.RuleDropSelectorWeight;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.dropt.Weight")
public final class ZenWeight {

    private final RuleDropSelectorWeight weight;

    ZenWeight(RuleDropSelectorWeight weight) {
        this.weight = weight;
    }

    RuleDropSelectorWeight getWeight() {
        return this.weight;
    }
}
