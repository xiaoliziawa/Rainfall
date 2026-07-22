package com.lirxowo.rainfall.api.builder;

import com.lirxowo.rainfall.api.reference.EnumHarvesterGameStageType;
import com.lirxowo.rainfall.api.reference.EnumHarvesterType;
import com.lirxowo.rainfall.api.reference.EnumListType;

public interface IDroptHarvesterRuleBuilder {

    IDroptHarvesterRuleBuilder type(EnumHarvesterType type);

    IDroptHarvesterRuleBuilder mainHand(String[] items);

    IDroptHarvesterRuleBuilder mainHand(String harvestLevel);

    IDroptHarvesterRuleBuilder mainHand(String[] items, String harvestLevel);

    IDroptHarvesterRuleBuilder mainHand(EnumListType type, String[] items);

    IDroptHarvesterRuleBuilder mainHand(EnumListType type, String harvestLevel);

    IDroptHarvesterRuleBuilder mainHand(EnumListType type, String[] items, String harvestLevel);

    IDroptHarvesterRuleBuilder mainHandEnchantment(String enchantmentId, int minimumLevel);

    IDroptHarvesterRuleBuilder offHand(String[] items);

    IDroptHarvesterRuleBuilder offHand(String harvestLevel);

    IDroptHarvesterRuleBuilder offHand(String[] items, String harvestLevel);

    IDroptHarvesterRuleBuilder offHand(EnumListType type, String[] items);

    IDroptHarvesterRuleBuilder offHand(EnumListType type, String harvestLevel);

    IDroptHarvesterRuleBuilder offHand(EnumListType type, String[] items, String harvestLevel);

    IDroptHarvesterRuleBuilder offHandEnchantment(String enchantmentId, int minimumLevel);

    IDroptHarvesterRuleBuilder gameStages(String[] stages);

    IDroptHarvesterRuleBuilder gameStages(EnumHarvesterGameStageType require, String[] stages);

    IDroptHarvesterRuleBuilder gameStages(EnumListType type, EnumHarvesterGameStageType require, String[] stages);

    IDroptHarvesterRuleBuilder playerName(String[] names);

    IDroptHarvesterRuleBuilder playerName(EnumListType type, String[] names);
}
