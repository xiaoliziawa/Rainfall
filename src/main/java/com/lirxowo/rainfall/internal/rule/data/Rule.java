package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.builder.RandomFortuneInt;
import com.lirxowo.rainfall.api.reference.EnumDropStrategy;
import com.lirxowo.rainfall.api.reference.EnumReplaceStrategy;

public class Rule {

    public boolean debug;
    public RuleMatch match = new RuleMatch();
    public EnumReplaceStrategy replaceStrategy = EnumReplaceStrategy.REPLACE_ALL;
    public EnumDropStrategy dropStrategy = EnumDropStrategy.REPEAT;
    public RandomFortuneInt dropCount = new RandomFortuneInt(1);
    public RuleDrop[] drops = new RuleDrop[0];
    public boolean fallthrough;
}
