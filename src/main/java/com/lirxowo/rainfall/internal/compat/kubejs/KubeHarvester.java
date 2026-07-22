package com.lirxowo.rainfall.internal.compat.kubejs;

import com.lirxowo.rainfall.api.RainfallAPI;
import com.lirxowo.rainfall.api.builder.IDroptHarvesterRuleBuilder;
import com.lirxowo.rainfall.api.reference.EnumHarvesterGameStageType;
import com.lirxowo.rainfall.api.reference.EnumHarvesterType;
import com.lirxowo.rainfall.api.reference.EnumListType;

public final class KubeHarvester {

    private final IDroptHarvesterRuleBuilder harvester = RainfallAPI.harvester();

    public KubeHarvester type(String type) {
        this.harvester.type(EnumHarvesterType.valueOf(type));
        return this;
    }

    public KubeHarvester mainHandLevel(String harvestLevel) {
        this.harvester.mainHand(harvestLevel);
        return this;
    }

    public KubeHarvester mainHandItems(String[] items) {
        this.harvester.mainHand(items);
        return this;
    }

    public KubeHarvester mainHandItemsWithLevel(String[] items, String harvestLevel) {
        this.harvester.mainHand(items, harvestLevel);
        return this;
    }

    public KubeHarvester mainHandLevelList(String type, String harvestLevel) {
        this.harvester.mainHand(EnumListType.valueOf(type), harvestLevel);
        return this;
    }

    public KubeHarvester mainHandItemList(String type, String[] items) {
        this.harvester.mainHand(EnumListType.valueOf(type), items);
        return this;
    }

    public KubeHarvester mainHandItemListWithLevel(String type, String[] items, String harvestLevel) {
        this.harvester.mainHand(EnumListType.valueOf(type), items, harvestLevel);
        return this;
    }

    public KubeHarvester mainHandEnchantment(String enchantmentId, int minimumLevel) {
        this.harvester.mainHandEnchantment(enchantmentId, minimumLevel);
        return this;
    }

    public KubeHarvester offHandLevel(String harvestLevel) {
        this.harvester.offHand(harvestLevel);
        return this;
    }

    public KubeHarvester offHandItems(String[] items) {
        this.harvester.offHand(items);
        return this;
    }

    public KubeHarvester offHandItemsWithLevel(String[] items, String harvestLevel) {
        this.harvester.offHand(items, harvestLevel);
        return this;
    }

    public KubeHarvester offHandLevelList(String type, String harvestLevel) {
        this.harvester.offHand(EnumListType.valueOf(type), harvestLevel);
        return this;
    }

    public KubeHarvester offHandItemList(String type, String[] items) {
        this.harvester.offHand(EnumListType.valueOf(type), items);
        return this;
    }

    public KubeHarvester offHandItemListWithLevel(String type, String[] items, String harvestLevel) {
        this.harvester.offHand(EnumListType.valueOf(type), items, harvestLevel);
        return this;
    }

    public KubeHarvester offHandEnchantment(String enchantmentId, int minimumLevel) {
        this.harvester.offHandEnchantment(enchantmentId, minimumLevel);
        return this;
    }

    public KubeHarvester gameStages(String[] stages) {
        this.harvester.gameStages(stages);
        return this;
    }

    public KubeHarvester requiredGameStages(String require, String[] stages) {
        this.harvester.gameStages(EnumHarvesterGameStageType.valueOf(require), stages);
        return this;
    }

    public KubeHarvester gameStageList(String type, String require, String[] stages) {
        this.harvester.gameStages(
                EnumListType.valueOf(type),
                EnumHarvesterGameStageType.valueOf(require),
                stages
        );
        return this;
    }

    public KubeHarvester playerName(String[] names) {
        this.harvester.playerName(names);
        return this;
    }

    public KubeHarvester playerNameList(String type, String[] names) {
        this.harvester.playerName(EnumListType.valueOf(type), names);
        return this;
    }

    IDroptHarvesterRuleBuilder getHarvester() {
        return this.harvester;
    }
}
