package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumHarvesterGameStageType;
import com.lirxowo.rainfall.api.reference.EnumListType;

public class RuleMatchHarvesterGameStage {

    public EnumListType type = EnumListType.WHITELIST;
    public EnumHarvesterGameStageType require = EnumHarvesterGameStageType.ANY;
    public String[] stages = new String[0];
}
