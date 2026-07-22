package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.internal.rule.match.BlockPredicate;

import java.util.ArrayList;
import java.util.List;

public class RuleMatchBlocks {

    public transient List<BlockPredicate> _blocks = new ArrayList<>();
    public EnumListType type = EnumListType.WHITELIST;
    public String[] blocks = new String[0];
}
