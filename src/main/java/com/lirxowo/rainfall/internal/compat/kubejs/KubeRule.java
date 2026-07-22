package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.api.RainfallAPI;
import com.lirxowo.rainfall.api.builder.IDroptDropBuilder;
import com.lirxowo.rainfall.api.builder.IDroptRuleBuilder;
import com.lirxowo.rainfall.api.reference.EnumDropStrategy;
import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.api.reference.EnumReplaceStrategy;

public final class KubeRule {

    private final IDroptRuleBuilder rule = RainfallAPI.rule();

    public KubeRule debug() {
        this.rule.debug();
        return this;
    }

    public KubeRule matchBlocks(String[] blocks) {
        this.rule.matchBlocks(blocks);
        return this;
    }

    public KubeRule matchBlockList(String type, String[] blocks) {
        this.rule.matchBlocks(EnumListType.valueOf(type), blocks);
        return this;
    }

    public KubeRule matchDrops(String[] items) {
        this.rule.matchDrops(items);
        return this;
    }

    public KubeRule matchDropList(String type, String[] items) {
        this.rule.matchDrops(EnumListType.valueOf(type), items);
        return this;
    }

    public KubeRule matchHarvester(KubeHarvester harvester) {
        this.rule.matchHarvester(harvester.getHarvester());
        return this;
    }

    public KubeRule matchBiomes(String[] ids) {
        this.rule.matchBiomes(ids);
        return this;
    }

    public KubeRule matchBiomeList(String type, String[] ids) {
        this.rule.matchBiomes(EnumListType.valueOf(type), ids);
        return this;
    }

    public KubeRule matchLegacyDimensions(int[] ids) {
        this.rule.matchDimensions(ids);
        return this;
    }

    public KubeRule matchDimensions(String[] ids) {
        this.rule.matchDimensions(ids);
        return this;
    }

    public KubeRule matchLegacyDimensionList(String type, int[] ids) {
        this.rule.matchDimensions(EnumListType.valueOf(type), ids);
        return this;
    }

    public KubeRule matchDimensionList(String type, String[] ids) {
        this.rule.matchDimensions(EnumListType.valueOf(type), ids);
        return this;
    }

    public KubeRule matchVerticalRange(int min, int max) {
        this.rule.matchVerticalRange(min, max);
        return this;
    }

    public KubeRule matchSpawnDistance(int min, int max) {
        this.rule.matchSpawnDistance(EnumListType.WHITELIST, min, max);
        return this;
    }

    public KubeRule matchSpawnDistanceList(String type, int min, int max) {
        this.rule.matchSpawnDistance(EnumListType.valueOf(type), min, max);
        return this;
    }

    public KubeRule replaceStrategy(String strategy) {
        this.rule.replaceStrategy(EnumReplaceStrategy.valueOf(strategy));
        return this;
    }

    public KubeRule dropStrategy(String strategy) {
        this.rule.dropStrategy(EnumDropStrategy.valueOf(strategy));
        return this;
    }

    public KubeRule dropCount(KubeRange range) {
        this.rule.dropCount(range.getRange());
        return this;
    }

    public KubeRule addDrop(KubeDrop drop) {
        this.rule.addDrops(new IDroptDropBuilder[]{drop.getDrop()});
        return this;
    }

    public KubeRule fallthrough() {
        return this.setFallthrough(true);
    }

    public KubeRule setFallthrough(boolean fallthrough) {
        this.rule.fallthrough(fallthrough);
        return this;
    }

    IDroptRuleBuilder getRule() {
        return this.rule;
    }
}
