package com.lirxowo.rainfall.internal.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.lirxowo.rainfall.api.RainfallAPI;
import com.lirxowo.rainfall.api.builder.IDroptDropBuilder;
import com.lirxowo.rainfall.api.reference.EnumDropListStrategy;
import com.lirxowo.rainfall.api.reference.EnumSilktouch;
import com.lirxowo.rainfall.api.reference.EnumXPReplaceStrategy;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Map;

@ZenRegister
@ZenCodeType.Name("mods.dropt.Drop")
public final class ZenDrop {

    private final IDroptDropBuilder drop = RainfallAPI.drop();

    @ZenCodeType.Method
    public ZenDrop force() {
        this.drop.force();
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop selector(ZenWeight weight) {
        this.drop.selector(weight.getWeight());
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop selector(ZenWeight weight, String silkTouch) {
        this.drop.selector(weight.getWeight(), EnumSilktouch.valueOf(silkTouch));
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop selector(ZenWeight weight, String silkTouch, int fortuneLevelRequired) {
        this.drop.selector(weight.getWeight(), EnumSilktouch.valueOf(silkTouch), fortuneLevelRequired);
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop items(IItemStack[] items) {
        this.drop.items(ZenDropt.getItemStrings(items));
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop items(String strategy, IItemStack[] items) {
        this.drop.items(EnumDropListStrategy.valueOf(strategy), ZenDropt.getItemStrings(items));
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop items(IItemStack[] items, ZenRange range) {
        this.drop.items(ZenDropt.getItemStrings(items), range.getRange());
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop items(String strategy, IItemStack[] items, ZenRange range) {
        this.drop.items(EnumDropListStrategy.valueOf(strategy), ZenDropt.getItemStrings(items), range.getRange());
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop matchQuantity(IItemStack[] drops) {
        this.drop.matchQuantity(ZenDropt.getItemStrings(drops));
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop xp(String replace, ZenRange amount) {
        this.drop.xp(EnumXPReplaceStrategy.valueOf(replace), amount.getRange());
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop replaceBlock(String block) {
        this.drop.replaceBlock(block, Map.of());
        return this;
    }

    @ZenCodeType.Method
    public ZenDrop replaceBlock(String block, Map<String, String> properties) {
        this.drop.replaceBlock(block, properties);
        return this;
    }

    IDroptDropBuilder getDrop() {
        return this.drop;
    }
}
