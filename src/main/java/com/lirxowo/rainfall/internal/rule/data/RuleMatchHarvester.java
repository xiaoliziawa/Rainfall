package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumHarvesterType;

public class RuleMatchHarvester {

    public EnumHarvesterType type = EnumHarvesterType.ANY;
    public RuleMatchHarvesterGameStage gamestages = new RuleMatchHarvesterGameStage();
    public RuleMatchHarvesterHeldItem heldItemMainHand = new RuleMatchHarvesterHeldItem();
    public RuleMatchHarvesterHeldItem heldItemOffHand = new RuleMatchHarvesterHeldItem();
    public RuleMatchHarvesterPlayerName playerName = new RuleMatchHarvesterPlayerName();
}
