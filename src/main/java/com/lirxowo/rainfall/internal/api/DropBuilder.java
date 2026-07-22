package com.lirxowo.rainfall.internal.api;

import com.lirxowo.rainfall.api.builder.IDroptDropBuilder;
import com.lirxowo.rainfall.api.builder.RandomFortuneInt;
import com.lirxowo.rainfall.api.builder.RuleDropSelectorWeight;
import com.lirxowo.rainfall.api.reference.EnumDropListStrategy;
import com.lirxowo.rainfall.api.reference.EnumSilktouch;
import com.lirxowo.rainfall.api.reference.EnumXPReplaceStrategy;
import com.lirxowo.rainfall.internal.rule.data.RuleDrop;

import java.util.Map;

public final class DropBuilder implements IDroptDropBuilder {

    private final RuleDrop rule = new RuleDrop();

    @Override
    public IDroptDropBuilder force() {
        this.rule.force = true;
        return this;
    }

    @Override
    public IDroptDropBuilder selector(RuleDropSelectorWeight weight) {
        return this.selector(weight, EnumSilktouch.ANY, 0);
    }

    @Override
    public IDroptDropBuilder selector(RuleDropSelectorWeight weight, int fortuneLevelRequired) {
        return this.selector(weight, EnumSilktouch.ANY, fortuneLevelRequired);
    }

    @Override
    public IDroptDropBuilder selector(RuleDropSelectorWeight weight, EnumSilktouch silktouch) {
        return this.selector(weight, silktouch, 0);
    }

    @Override
    public IDroptDropBuilder selector(RuleDropSelectorWeight weight, EnumSilktouch silktouch, int fortuneLevelRequired) {
        this.rule.selector.weight.value = weight.value;
        this.rule.selector.weight.fortuneModifier = weight.fortuneModifier;
        this.rule.selector.silktouch = silktouch;
        this.rule.selector.fortuneLevelRequired = fortuneLevelRequired;
        return this;
    }

    @Override
    public IDroptDropBuilder items(String[] items) {
        this.rule.item.items = items.clone();
        return this;
    }

    @Override
    public IDroptDropBuilder items(EnumDropListStrategy strategy, String[] items) {
        this.rule.item.drop = strategy;
        return this.items(items);
    }

    @Override
    public IDroptDropBuilder items(String[] items, RandomFortuneInt count) {
        this.rule.item.items = items.clone();
        copy(count, this.rule.item.quantity);
        return this;
    }

    @Override
    public IDroptDropBuilder items(EnumDropListStrategy strategy, String[] items, RandomFortuneInt count) {
        this.rule.item.drop = strategy;
        return this.items(items, count);
    }

    @Override
    public IDroptDropBuilder matchQuantity(String[] drops) {
        this.rule.item.matchQuantity.drops = drops.clone();
        return this;
    }

    @Override
    public IDroptDropBuilder xp(EnumXPReplaceStrategy replace, RandomFortuneInt amount) {
        this.rule.xpReplaceStrategy = replace;
        copy(amount, this.rule.xp);
        return this;
    }

    @Override
    public IDroptDropBuilder replaceBlock(String block, Map<String, String> properties) {
        this.rule.replaceBlock.block = block;
        this.rule.replaceBlock.properties.putAll(properties);
        return this;
    }

    public RuleDrop build() {
        return this.rule;
    }

    private static void copy(RandomFortuneInt source, RandomFortuneInt target) {
        target.fixed = source.fixed;
        target.min = source.min;
        target.max = source.max;
        target.fortuneModifier = source.fortuneModifier;
    }
}
