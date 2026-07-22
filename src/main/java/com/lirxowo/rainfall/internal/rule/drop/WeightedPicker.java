package com.lirxowo.rainfall.internal.rule.drop;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public final class WeightedPicker<T> {

    private final List<Entry<T>> entries = new ArrayList<>();
    private long totalWeight;

    public void add(int weight, T value) {
        if (weight <= 0) {
            return;
        }
        this.entries.add(new Entry<>(weight, value));
        this.totalWeight += weight;
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public T get(RandomSource random, boolean remove) {
        if (this.entries.isEmpty()) {
            throw new IllegalStateException("Cannot select from an empty weighted picker");
        }
        long selected = (long) (random.nextDouble() * this.totalWeight);
        long cursor = 0L;
        for (int index = 0; index < this.entries.size(); index++) {
            Entry<T> entry = this.entries.get(index);
            cursor += entry.weight();
            if (selected < cursor) {
                if (remove) {
                    this.entries.remove(index);
                    this.totalWeight -= entry.weight();
                }
                return entry.value();
            }
        }
        throw new IllegalStateException("Weighted selection exceeded total weight");
    }

    private record Entry<T>(int weight, T value) {
    }
}
