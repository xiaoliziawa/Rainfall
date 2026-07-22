package com.lirxowo.rainfall.internal.api;

import com.lirxowo.rainfall.api.builder.IDroptDropBuilder;
import com.lirxowo.rainfall.api.builder.IDroptHarvesterRuleBuilder;
import com.lirxowo.rainfall.api.builder.IDroptRuleBuilder;
import com.lirxowo.rainfall.api.builder.RandomFortuneInt;
import com.lirxowo.rainfall.api.reference.EnumDropStrategy;
import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.api.reference.EnumReplaceStrategy;
import com.lirxowo.rainfall.internal.rule.data.Rule;
import com.lirxowo.rainfall.internal.rule.data.RuleDrop;

import java.util.ArrayList;
import java.util.List;

public final class RuleBuilder implements IDroptRuleBuilder {

    private final Rule rule = new Rule();
    private final List<RuleDrop> drops = new ArrayList<>();

    @Override
    public IDroptRuleBuilder debug() {
        this.rule.debug = true;
        return this;
    }

    @Override
    public IDroptRuleBuilder matchBlocks(String[] blockStrings) {
        this.rule.match.blocks.blocks = blockStrings.clone();
        return this;
    }

    @Override
    public IDroptRuleBuilder matchBlocks(EnumListType type, String[] blockStrings) {
        this.rule.match.blocks.type = type;
        return this.matchBlocks(blockStrings);
    }

    @Override
    public IDroptRuleBuilder matchDrops(String[] items) {
        this.rule.match.drops.drops = items.clone();
        return this;
    }

    @Override
    public IDroptRuleBuilder matchDrops(EnumListType type, String[] items) {
        this.rule.match.drops.type = type;
        return this.matchDrops(items);
    }

    @Override
    public IDroptRuleBuilder matchHarvester(IDroptHarvesterRuleBuilder builder) {
        if (!(builder instanceof HarvesterRuleBuilder harvesterBuilder)) {
            throw new IllegalArgumentException("Harvester builder was not created by RainfallAPI");
        }
        this.rule.match.harvester = harvesterBuilder.build();
        return this;
    }

    @Override
    public IDroptRuleBuilder matchBiomes(String[] ids) {
        this.rule.match.biomes.ids = ids.clone();
        return this;
    }

    @Override
    public IDroptRuleBuilder matchBiomes(EnumListType type, String[] ids) {
        this.rule.match.biomes.type = type;
        return this.matchBiomes(ids);
    }

    @Override
    public IDroptRuleBuilder matchDimensions(int[] ids) {
        this.rule.match.dimensions.ids = ids.clone();
        return this;
    }

    @Override
    public IDroptRuleBuilder matchDimensions(EnumListType type, int[] ids) {
        this.rule.match.dimensions.type = type;
        return this.matchDimensions(ids);
    }

    @Override
    public IDroptRuleBuilder matchDimensions(String[] ids) {
        this.rule.match.dimensions.names = ids.clone();
        return this;
    }

    @Override
    public IDroptRuleBuilder matchDimensions(EnumListType type, String[] ids) {
        this.rule.match.dimensions.type = type;
        return this.matchDimensions(ids);
    }

    @Override
    public IDroptRuleBuilder matchVerticalRange(int min, int max) {
        this.rule.match.verticalRange.min = min;
        this.rule.match.verticalRange.max = max;
        return this;
    }

    @Override
    public IDroptRuleBuilder matchSpawnDistance(EnumListType type, int min, int max) {
        this.rule.match.spawnDistance.type = type;
        this.rule.match.spawnDistance.min = min;
        this.rule.match.spawnDistance.max = max;
        return this;
    }

    @Override
    public IDroptRuleBuilder replaceStrategy(EnumReplaceStrategy strategy) {
        this.rule.replaceStrategy = strategy;
        return this;
    }

    @Override
    public IDroptRuleBuilder dropStrategy(EnumDropStrategy strategy) {
        this.rule.dropStrategy = strategy;
        return this;
    }

    @Override
    public IDroptRuleBuilder dropCount(RandomFortuneInt range) {
        this.rule.dropCount.fixed = range.fixed;
        this.rule.dropCount.min = range.min;
        this.rule.dropCount.max = range.max;
        this.rule.dropCount.fortuneModifier = range.fortuneModifier;
        return this;
    }

    @Override
    public IDroptRuleBuilder addDrops(IDroptDropBuilder[] builders) {
        for (IDroptDropBuilder builder : builders) {
            if (!(builder instanceof DropBuilder dropBuilder)) {
                throw new IllegalArgumentException("Drop builder was not created by RainfallAPI");
            }
            this.drops.add(dropBuilder.build());
        }
        return this;
    }

    @Override
    public IDroptRuleBuilder fallthrough(boolean fallthrough) {
        this.rule.fallthrough = fallthrough;
        return this;
    }

    public Rule build() {
        this.rule.drops = this.drops.toArray(RuleDrop[]::new);
        return this.rule;
    }
}
