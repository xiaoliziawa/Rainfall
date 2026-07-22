package com.lirxowo.rainfall.internal.api;

import com.lirxowo.rainfall.api.builder.IDroptHarvesterRuleBuilder;
import com.lirxowo.rainfall.api.reference.EnumHarvesterGameStageType;
import com.lirxowo.rainfall.api.reference.EnumHarvesterType;
import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.internal.rule.data.RuleMatchHarvester;
import com.lirxowo.rainfall.internal.rule.data.RuleMatchHarvesterHeldItem;

public final class HarvesterRuleBuilder implements IDroptHarvesterRuleBuilder {

    private final RuleMatchHarvester rule = new RuleMatchHarvester();

    @Override
    public IDroptHarvesterRuleBuilder type(EnumHarvesterType type) {
        this.rule.type = type;
        return this;
    }

    @Override
    public IDroptHarvesterRuleBuilder mainHand(String[] items) {
        return this.heldItem(this.rule.heldItemMainHand, null, items, null);
    }

    @Override
    public IDroptHarvesterRuleBuilder mainHand(String harvestLevel) {
        return this.heldItem(this.rule.heldItemMainHand, null, null, harvestLevel);
    }

    @Override
    public IDroptHarvesterRuleBuilder mainHand(String[] items, String harvestLevel) {
        return this.heldItem(this.rule.heldItemMainHand, null, items, harvestLevel);
    }

    @Override
    public IDroptHarvesterRuleBuilder mainHand(EnumListType type, String[] items) {
        return this.heldItem(this.rule.heldItemMainHand, type, items, null);
    }

    @Override
    public IDroptHarvesterRuleBuilder mainHand(EnumListType type, String harvestLevel) {
        return this.heldItem(this.rule.heldItemMainHand, type, null, harvestLevel);
    }

    @Override
    public IDroptHarvesterRuleBuilder mainHand(EnumListType type, String[] items, String harvestLevel) {
        return this.heldItem(this.rule.heldItemMainHand, type, items, harvestLevel);
    }

    @Override
    public IDroptHarvesterRuleBuilder offHand(String[] items) {
        return this.heldItem(this.rule.heldItemOffHand, null, items, null);
    }

    @Override
    public IDroptHarvesterRuleBuilder offHand(String harvestLevel) {
        return this.heldItem(this.rule.heldItemOffHand, null, null, harvestLevel);
    }

    @Override
    public IDroptHarvesterRuleBuilder offHand(String[] items, String harvestLevel) {
        return this.heldItem(this.rule.heldItemOffHand, null, items, harvestLevel);
    }

    @Override
    public IDroptHarvesterRuleBuilder offHand(EnumListType type, String[] items) {
        return this.heldItem(this.rule.heldItemOffHand, type, items, null);
    }

    @Override
    public IDroptHarvesterRuleBuilder offHand(EnumListType type, String harvestLevel) {
        return this.heldItem(this.rule.heldItemOffHand, type, null, harvestLevel);
    }

    @Override
    public IDroptHarvesterRuleBuilder offHand(EnumListType type, String[] items, String harvestLevel) {
        return this.heldItem(this.rule.heldItemOffHand, type, items, harvestLevel);
    }

    @Override
    public IDroptHarvesterRuleBuilder gameStages(String[] stages) {
        this.rule.gamestages.stages = stages.clone();
        return this;
    }

    @Override
    public IDroptHarvesterRuleBuilder gameStages(EnumHarvesterGameStageType require, String[] stages) {
        this.rule.gamestages.require = require;
        return this.gameStages(stages);
    }

    @Override
    public IDroptHarvesterRuleBuilder gameStages(EnumListType type, EnumHarvesterGameStageType require, String[] stages) {
        this.rule.gamestages.type = type;
        return this.gameStages(require, stages);
    }

    @Override
    public IDroptHarvesterRuleBuilder playerName(String[] names) {
        this.rule.playerName.names = names.clone();
        return this;
    }

    @Override
    public IDroptHarvesterRuleBuilder playerName(EnumListType type, String[] names) {
        this.rule.playerName.type = type;
        return this.playerName(names);
    }

    public RuleMatchHarvester build() {
        return this.rule;
    }

    private IDroptHarvesterRuleBuilder heldItem(
            RuleMatchHarvesterHeldItem target,
            EnumListType type,
            String[] items,
            String harvestLevel
    ) {
        if (type != null) {
            target.type = type;
        }
        if (items != null) {
            target.items = items.clone();
        }
        if (harvestLevel != null) {
            target.harvestLevel = harvestLevel;
        }
        return this;
    }
}
