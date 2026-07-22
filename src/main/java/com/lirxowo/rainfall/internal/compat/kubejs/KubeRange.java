package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.api.builder.RandomFortuneInt;

public final class KubeRange {

    private final RandomFortuneInt range;

    KubeRange(RandomFortuneInt range) {
        this.range = range;
    }

    RandomFortuneInt getRange() {
        return this.range;
    }
}
