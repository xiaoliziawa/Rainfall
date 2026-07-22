package com.lirxowo.rainfall.internal.api;

import com.lirxowo.rainfall.api.builder.IDroptRuleBuilder;
import com.lirxowo.rainfall.api.builder.IRuleRegistrationHandler;
import com.lirxowo.rainfall.internal.rule.data.RuleList;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class RuleRegistrationHandler implements IRuleRegistrationHandler {

    private final List<RuleList> ruleLists;

    public RuleRegistrationHandler(List<RuleList> ruleLists) {
        this.ruleLists = ruleLists;
    }

    @Override
    public void register(ResourceLocation id, int priority, List<IDroptRuleBuilder> builders) {
        RuleList ruleList = new RuleList();
        ruleList._filename = id.toString();
        ruleList.priority = priority;
        for (IDroptRuleBuilder builder : builders) {
            if (!(builder instanceof RuleBuilder ruleBuilder)) {
                throw new IllegalArgumentException("Rule builder was not created by RainfallAPI");
            }
            ruleList.rules.add(ruleBuilder.build());
        }
        this.ruleLists.add(ruleList);
    }
}
