package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.internal.rule.match.ItemPredicate;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ToolAction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RuleMatchHarvesterHeldItem {

    public transient List<ItemPredicate> _items = new ArrayList<>();
    public transient String _toolClass;
    public transient ToolAction _toolAction;
    public transient int _minHarvestLevel = Integer.MIN_VALUE;
    public transient int _maxHarvestLevel = Integer.MAX_VALUE;
    public transient Map<Enchantment, Integer> _enchantments = new LinkedHashMap<>();
    public EnumListType type = EnumListType.WHITELIST;
    public String[] items = new String[0];
    public String harvestLevel;
    public Map<String, Integer> enchantments = new LinkedHashMap<>();
}
