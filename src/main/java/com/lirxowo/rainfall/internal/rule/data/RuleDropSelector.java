package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.builder.RuleDropSelectorWeight;
import com.lirxowo.rainfall.api.reference.EnumSilktouch;

public class RuleDropSelector {

    public RuleDropSelectorWeight weight = new RuleDropSelectorWeight();
    public EnumSilktouch silktouch = EnumSilktouch.ANY;
    public int fortuneLevelRequired;

    public boolean isValidCandidate(boolean silkTouching, int fortuneLevel) {
        if (fortuneLevel < this.fortuneLevelRequired) {
            return false;
        }
        return switch (this.silktouch) {
            case REQUIRED -> silkTouching;
            case EXCLUDED -> !silkTouching;
            case ANY -> true;
        };
    }
}
