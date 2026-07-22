package com.lirxowo.rainfall.internal.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.lirxowo.rainfall.api.builder.IDroptRuleBuilder;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenCodeType.Name("mods.dropt.RuleList")
public final class ZenRuleList {

    private final List<IDroptRuleBuilder> rules = new ArrayList<>();
    private final ResourceLocation id;
    private int priority;

    ZenRuleList(ResourceLocation id) {
        this.id = id;
    }

    @ZenCodeType.Method
    public ZenRuleList priority(int priority) {
        this.priority = priority;
        return this;
    }

    @ZenCodeType.Method
    public ZenRuleList add(ZenRule rule) {
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
