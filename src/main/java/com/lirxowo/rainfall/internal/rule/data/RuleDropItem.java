package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.builder.RandomFortuneInt;
import com.lirxowo.rainfall.api.reference.EnumDropListStrategy;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RuleDropItem {

    public transient List<ItemStack> _items = new ArrayList<>();
    public EnumDropListStrategy drop = EnumDropListStrategy.ONE;
    public String[] items = new String[0];
    public RandomFortuneInt quantity = new RandomFortuneInt(1);
    public RuleDropItemMatchQuantity matchQuantity = new RuleDropItemMatchQuantity();
}
