package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.api.RainfallAPI;
import com.lirxowo.rainfall.api.builder.IDroptDropBuilder;
import com.lirxowo.rainfall.api.reference.EnumDropListStrategy;
import com.lirxowo.rainfall.api.reference.EnumSilktouch;
import com.lirxowo.rainfall.api.reference.EnumXPReplaceStrategy;

import java.util.Map;

public final class KubeDrop {

    private final IDroptDropBuilder drop = RainfallAPI.drop();

    public KubeDrop force() {
        this.drop.force();
        return this;
    }

    public KubeDrop selector(KubeWeight weight) {
        this.drop.selector(weight.getWeight());
        return this;
    }

    public KubeDrop fortuneSelector(KubeWeight weight, int fortuneLevelRequired) {
        this.drop.selector(weight.getWeight(), fortuneLevelRequired);
        return this;
    }

    public KubeDrop silkTouchSelector(KubeWeight weight, String silkTouch) {
        this.drop.selector(weight.getWeight(), EnumSilktouch.valueOf(silkTouch));
        return this;
    }

    public KubeDrop silkTouchFortuneSelector(KubeWeight weight, String silkTouch, int fortuneLevelRequired) {
        this.drop.selector(
                weight.getWeight(),
                EnumSilktouch.valueOf(silkTouch),
                fortuneLevelRequired
        );
        return this;
    }

    public KubeDrop items(String[] items) {
        this.drop.items(items);
        return this;
    }

    public KubeDrop itemsWithStrategy(String strategy, String[] items) {
        this.drop.items(EnumDropListStrategy.valueOf(strategy), items);
        return this;
    }

    public KubeDrop itemsWithRange(String[] items, KubeRange range) {
        this.drop.items(items, range.getRange());
        return this;
    }

    public KubeDrop itemsWithStrategyAndRange(String strategy, String[] items, KubeRange range) {
        this.drop.items(EnumDropListStrategy.valueOf(strategy), items, range.getRange());
        return this;
    }

    public KubeDrop matchQuantity(String[] drops) {
        this.drop.matchQuantity(drops);
        return this;
    }

    public KubeDrop xp(String replace, KubeRange amount) {
        this.drop.xp(EnumXPReplaceStrategy.valueOf(replace), amount.getRange());
        return this;
    }

    public KubeDrop replaceBlock(String block) {
        this.drop.replaceBlock(block, Map.of());
        return this;
    }

    public KubeDrop replaceBlockWithProperties(String block, Map<String, String> properties) {
        this.drop.replaceBlock(block, properties);
        return this;
    }

    IDroptDropBuilder getDrop() {
        return this.drop;
    }
}
