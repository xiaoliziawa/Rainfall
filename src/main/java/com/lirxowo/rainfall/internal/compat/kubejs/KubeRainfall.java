package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.api.RainfallAPI;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public final class KubeRainfall {

    public static final KubeRainfall INSTANCE = new KubeRainfall();

    private static final Map<String, KubeRuleList> RULE_LISTS = new LinkedHashMap<>();

    public KubeRuleList list(String name) {
        return RULE_LISTS.computeIfAbsent(
                name,
                key -> new KubeRuleList(ResourceLocation.fromNamespaceAndPath("kubejs", key))
        );
    }

    public KubeRule rule() {
        return new KubeRule();
    }

    public KubeHarvester harvester() {
        return new KubeHarvester();
    }

    public KubeDrop drop() {
        return new KubeDrop();
    }

    public KubeRange fixedRange(int fixed) {
        return new KubeRange(RainfallAPI.range(fixed));
    }

    public KubeRange range(int min, int max) {
        return new KubeRange(RainfallAPI.range(min, max));
    }

    public KubeRange fortuneRange(int min, int max, int fortuneModifier) {
        return new KubeRange(RainfallAPI.range(min, max, fortuneModifier));
    }

    public KubeWeight weight(int weight) {
        return new KubeWeight(RainfallAPI.weight(weight));
    }

    public KubeWeight fortuneWeight(int weight, int fortuneModifier) {
        return new KubeWeight(RainfallAPI.weight(weight, fortuneModifier));
    }

    static void beginReload() {
        RULE_LISTS.clear();
    }

    static void registerRules() {
        for (KubeRuleList ruleList : RULE_LISTS.values()) {
            RainfallAPI.registerRuleList(ruleList.getId(), ruleList.getPriority(), ruleList.getRules());
        }
    }

    private KubeRainfall() {
    }
}
