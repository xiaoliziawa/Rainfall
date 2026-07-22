package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumListType;

public class RuleMatchSpawnDistance {

    public EnumListType type = EnumListType.WHITELIST;
    public int min;
    public int max = Integer.MAX_VALUE;
}
