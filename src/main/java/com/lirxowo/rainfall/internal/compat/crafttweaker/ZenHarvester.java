package com.lirxowo.rainfall.internal.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.lirxowo.rainfall.api.RainfallAPI;
import com.lirxowo.rainfall.api.builder.IDroptHarvesterRuleBuilder;
import com.lirxowo.rainfall.api.reference.EnumHarvesterGameStageType;
import com.lirxowo.rainfall.api.reference.EnumHarvesterType;
import com.lirxowo.rainfall.api.reference.EnumListType;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.dropt.Harvester")
public final class ZenHarvester {

    private final IDroptHarvesterRuleBuilder harvester = RainfallAPI.harvester();

    @ZenCodeType.Method
    public ZenHarvester type(String type) {
        this.harvester.type(EnumHarvesterType.valueOf(type));
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester mainHand(String harvestLevel) {
        this.harvester.mainHand(harvestLevel);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester mainHand(IItemStack[] items) {
        this.harvester.mainHand(ZenDropt.getItemStrings(items));
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester mainHand(String type, IItemStack[] items) {
        this.harvester.mainHand(EnumListType.valueOf(type), ZenDropt.getItemStrings(items));
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester mainHand(String type, IItemStack[] items, String harvestLevel) {
        this.harvester.mainHand(EnumListType.valueOf(type), ZenDropt.getItemStrings(items), harvestLevel);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester mainHandEnchantment(String enchantmentId, int minimumLevel) {
        this.harvester.mainHandEnchantment(enchantmentId, minimumLevel);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester offHand(String harvestLevel) {
        this.harvester.offHand(harvestLevel);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester offHand(IItemStack[] items) {
        this.harvester.offHand(ZenDropt.getItemStrings(items));
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester offHand(String type, IItemStack[] items) {
        this.harvester.offHand(EnumListType.valueOf(type), ZenDropt.getItemStrings(items));
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester offHand(String type, IItemStack[] items, String harvestLevel) {
        this.harvester.offHand(EnumListType.valueOf(type), ZenDropt.getItemStrings(items), harvestLevel);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester offHandEnchantment(String enchantmentId, int minimumLevel) {
        this.harvester.offHandEnchantment(enchantmentId, minimumLevel);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester gameStages(String[] stages) {
        this.harvester.gameStages(stages);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester gameStages(String require, String[] stages) {
        this.harvester.gameStages(EnumHarvesterGameStageType.valueOf(require), stages);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester gameStages(String type, String require, String[] stages) {
        this.harvester.gameStages(
                EnumListType.valueOf(type),
                EnumHarvesterGameStageType.valueOf(require),
                stages
        );
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester playerName(String[] names) {
        this.harvester.playerName(names);
        return this;
    }

    @ZenCodeType.Method
    public ZenHarvester playerName(String type, String[] names) {
        this.harvester.playerName(EnumListType.valueOf(type), names);
        return this;
    }

    IDroptHarvesterRuleBuilder getHarvester() {
        return this.harvester;
    }
}
