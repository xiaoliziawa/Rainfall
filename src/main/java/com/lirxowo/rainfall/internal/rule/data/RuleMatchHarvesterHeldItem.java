package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.internal.rule.match.ItemPredicate;
import net.minecraftforge.common.ToolAction;

import java.util.ArrayList;
import java.util.List;

public class RuleMatchHarvesterHeldItem {

    public transient List<ItemPredicate> _items = new ArrayList<>();
    public transient String _toolClass;
    public transient ToolAction _toolAction;
    public transient int _minHarvestLevel = Integer.MIN_VALUE;
    public transient int _maxHarvestLevel = Integer.MAX_VALUE;
    public EnumListType type = EnumListType.WHITELIST;
    public String[] items = new String[0];
    public String harvestLevel;
}
