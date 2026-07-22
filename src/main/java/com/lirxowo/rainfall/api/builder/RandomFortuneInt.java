package com.lirxowo.rainfall.api.builder;

import net.minecraft.util.RandomSource;

public class RandomFortuneInt {

    public int fixed;
    public int min;
    public int max;
    public int fortuneModifier;

    public RandomFortuneInt() {
    }

    public RandomFortuneInt(int fixed) {
        this.fixed = fixed;
    }

    public int get(RandomSource random, int fortuneLevel) {
        int fortuneBonus = Math.max(0, fortuneLevel * this.fortuneModifier);
        if (this.fixed > 0) {
            return this.fixed + fortuneBonus;
        }

        long range = Math.abs((long) this.max - this.min) + 1L;
        if (range > Integer.MAX_VALUE) {
            throw new IllegalStateException("Configured random range is too large: " + this.min + ".." + this.max);
        }
        return random.nextInt((int) range) + Math.max(0, this.min + fortuneBonus);
    }

    @Override
    public String toString() {
        return "RandomFortuneInt{" +
                "fixed=" + this.fixed +
                ", min=" + this.min +
                ", max=" + this.max +
                ", fortuneModifier=" + this.fortuneModifier +
                '}';
    }
}
