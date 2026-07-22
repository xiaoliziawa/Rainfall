package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.api.builder.RuleDropSelectorWeight;

public final class KubeWeight {

    private final RuleDropSelectorWeight weight;

    KubeWeight(RuleDropSelectorWeight weight) {
        this.weight = weight;
    }

    RuleDropSelectorWeight getWeight() {
        return this.weight;
    }
}
