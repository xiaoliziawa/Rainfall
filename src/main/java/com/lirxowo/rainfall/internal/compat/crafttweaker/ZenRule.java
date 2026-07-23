package com.lirxowo.rainfall.internal.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.lirxowo.rainfall.api.RainfallAPI;
import com.lirxowo.rainfall.api.builder.IDroptDropBuilder;
import com.lirxowo.rainfall.api.builder.IDroptRuleBuilder;
import com.lirxowo.rainfall.api.reference.EnumDropStrategy;
import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.api.reference.EnumReplaceStrategy;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.dropt.Rule")
public final class ZenRule {

    private final IDroptRuleBuilder rule = RainfallAPI.rule();

    @ZenCodeType.Method
    public ZenRule debug() {
        this.rule.debug();
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchBlocks(String[] blocks) {
        this.rule.matchBlocks(blocks);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchBlocks(String type, String[] blocks) {
        this.rule.matchBlocks(EnumListType.valueOf(type), blocks);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchDrops(IIngredient[] items) {
        this.rule.matchDrops(ZenDropt.getIngredientStrings(items));
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchDrops(String type, IIngredient[] items) {
        this.rule.matchDrops(EnumListType.valueOf(type), ZenDropt.getIngredientStrings(items));
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchHarvester(ZenHarvester harvester) {
        this.rule.matchHarvester(harvester.getHarvester());
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchBiomes(String[] ids) {
        this.rule.matchBiomes(ids);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchBiomes(String type, String[] ids) {
        this.rule.matchBiomes(EnumListType.valueOf(type), ids);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchDimensions(int[] ids) {
        this.rule.matchDimensions(ids);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchDimensions(String type, int[] ids) {
        this.rule.matchDimensions(EnumListType.valueOf(type), ids);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchDimensions(String[] ids) {
        this.rule.matchDimensions(ids);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchDimensions(String type, String[] ids) {
        this.rule.matchDimensions(EnumListType.valueOf(type), ids);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchVerticalRange(int min, int max) {
        this.rule.matchVerticalRange(min, max);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchSpawnDistance(int min, int max) {
        this.rule.matchSpawnDistance(EnumListType.WHITELIST, min, max);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule matchSpawnDistance(String type, int min, int max) {
        this.rule.matchSpawnDistance(EnumListType.valueOf(type), min, max);
        return this;
    }

    @ZenCodeType.Method
    public ZenRule replaceStrategy(String strategy) {
        this.rule.replaceStrategy(EnumReplaceStrategy.valueOf(strategy));
        return this;
    }

    @ZenCodeType.Method
    public ZenRule dropStrategy(String strategy) {
        this.rule.dropStrategy(EnumDropStrategy.valueOf(strategy));
        return this;
    }

    @ZenCodeType.Method
    public ZenRule dropCount(ZenRange range) {
        this.rule.dropCount(range.getRange());
        return this;
    }

    @ZenCodeType.Method
    public ZenRule addDrop(ZenDrop drop) {
        this.rule.addDrops(new IDroptDropBuilder[]{drop.getDrop()});
        return this;
    }

    @ZenCodeType.Method
    public ZenRule fallthrough(@ZenCodeType.OptionalBoolean(true) boolean fallthrough) {
        this.rule.fallthrough(fallthrough);
        return this;
    }

    IDroptRuleBuilder getRule() {
        return this.rule;
    }
}
