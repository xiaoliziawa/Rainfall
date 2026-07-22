package com.lirxowo.rainfall.api.builder;

import com.lirxowo.rainfall.api.reference.EnumDropListStrategy;
import com.lirxowo.rainfall.api.reference.EnumSilktouch;
import com.lirxowo.rainfall.api.reference.EnumXPReplaceStrategy;

import java.util.Map;

public interface IDroptDropBuilder {

    IDroptDropBuilder force();

    IDroptDropBuilder selector(RuleDropSelectorWeight weight);

    IDroptDropBuilder selector(RuleDropSelectorWeight weight, int fortuneLevelRequired);

    IDroptDropBuilder selector(RuleDropSelectorWeight weight, EnumSilktouch silktouch);

    IDroptDropBuilder selector(RuleDropSelectorWeight weight, EnumSilktouch silktouch, int fortuneLevelRequired);

    IDroptDropBuilder items(String[] items);

    IDroptDropBuilder items(EnumDropListStrategy dropListStrategy, String[] items);

    IDroptDropBuilder items(String[] items, RandomFortuneInt count);

    IDroptDropBuilder items(EnumDropListStrategy dropListStrategy, String[] items, RandomFortuneInt count);

    IDroptDropBuilder matchQuantity(String[] drops);

    IDroptDropBuilder xp(EnumXPReplaceStrategy replace, RandomFortuneInt amount);

    IDroptDropBuilder replaceBlock(String block, Map<String, String> properties);
}
