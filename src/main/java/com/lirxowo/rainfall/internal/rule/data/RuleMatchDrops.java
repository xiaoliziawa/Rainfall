package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.internal.rule.match.ItemPredicate;

import java.util.ArrayList;
import java.util.List;

public class RuleMatchDrops {

    public transient List<ItemPredicate> _drops = new ArrayList<>();
    public EnumListType type = EnumListType.WHITELIST;
    public String[] drops = new String[0];
}
