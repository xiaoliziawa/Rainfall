package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.api.builder.IDroptRuleBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public final class KubeRuleList {

    private final List<IDroptRuleBuilder> rules = new ArrayList<>();
    private final ResourceLocation id;
    private int priority;

    KubeRuleList(ResourceLocation id) {
        this.id = id;
    }

    public KubeRuleList priority(int priority) {
        this.priority = priority;
        return this;
    }

    public KubeRuleList add(KubeRule rule) {
        this.rules.add(rule.getRule());
        return this;
    }

    List<IDroptRuleBuilder> getRules() {
        return this.rules;
    }

    ResourceLocation getId() {
        return this.id;
    }

    int getPriority() {
        return this.priority;
    }
}
