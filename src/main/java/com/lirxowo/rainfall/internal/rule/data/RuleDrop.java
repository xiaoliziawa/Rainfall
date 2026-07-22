package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.builder.RandomFortuneInt;
import com.lirxowo.rainfall.api.reference.EnumXPReplaceStrategy;

public class RuleDrop {

    public boolean force;
    public RuleDropSelector selector = new RuleDropSelector();
    public RuleDropItem item = new RuleDropItem();
    public RandomFortuneInt xp = new RandomFortuneInt();
    public EnumXPReplaceStrategy xpReplaceStrategy = EnumXPReplaceStrategy.ADD;
    public RuleDropReplaceBlock replaceBlock = new RuleDropReplaceBlock();
}
