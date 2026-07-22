package com.lirxowo.rainfall.api.builder;

import com.lirxowo.rainfall.api.reference.EnumDropStrategy;
import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.api.reference.EnumReplaceStrategy;

public interface IDroptRuleBuilder {

    IDroptRuleBuilder debug();

    IDroptRuleBuilder matchBlocks(String[] blockStrings);

    IDroptRuleBuilder matchBlocks(EnumListType type, String[] blockStrings);

    IDroptRuleBuilder matchDrops(String[] items);

    IDroptRuleBuilder matchDrops(EnumListType type, String[] items);

    IDroptRuleBuilder matchHarvester(IDroptHarvesterRuleBuilder harvesterRuleBuilder);

    IDroptRuleBuilder matchBiomes(String[] ids);

    IDroptRuleBuilder matchBiomes(EnumListType type, String[] ids);

    IDroptRuleBuilder matchDimensions(int[] ids);

    IDroptRuleBuilder matchDimensions(EnumListType type, int[] ids);

    IDroptRuleBuilder matchDimensions(String[] ids);

    IDroptRuleBuilder matchDimensions(EnumListType type, String[] ids);

    IDroptRuleBuilder matchVerticalRange(int min, int max);

    IDroptRuleBuilder matchSpawnDistance(EnumListType type, int min, int max);

    IDroptRuleBuilder replaceStrategy(EnumReplaceStrategy strategy);

    IDroptRuleBuilder dropStrategy(EnumDropStrategy strategy);

    IDroptRuleBuilder dropCount(RandomFortuneInt range);

    IDroptRuleBuilder addDrops(IDroptDropBuilder[] drops);

    IDroptRuleBuilder fallthrough(boolean fallthrough);
}
