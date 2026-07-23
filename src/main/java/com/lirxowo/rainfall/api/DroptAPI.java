package com.lirxowo.rainfall.api;

import com.lirxowo.rainfall.api.builder.IDroptDropBuilder;
import com.lirxowo.rainfall.api.builder.IDroptHarvesterRuleBuilder;
import com.lirxowo.rainfall.api.builder.IDroptRuleBuilder;
import com.lirxowo.rainfall.api.builder.RandomFortuneInt;
import com.lirxowo.rainfall.api.builder.RuleDropSelectorWeight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class DroptAPI {

    public static String modId() {
        return RainfallAPI.modId();
    }

    public static void registerRuleList(ResourceLocation id, int priority, List<IDroptRuleBuilder> builders) {
        RainfallAPI.registerRuleList(id, priority, builders);
    }

    public static IDroptRuleBuilder rule() {
        return RainfallAPI.rule();
    }

    public static IDroptHarvesterRuleBuilder harvester() {
        return RainfallAPI.harvester();
    }

    public static IDroptDropBuilder drop() {
        return RainfallAPI.drop();
    }

    public static RandomFortuneInt range(int fixed) {
        return RainfallAPI.range(fixed);
    }

    public static RandomFortuneInt range(int min, int max) {
        return RainfallAPI.range(min, max);
    }

    public static RandomFortuneInt range(int min, int max, int fortuneModifier) {
        return RainfallAPI.range(min, max, fortuneModifier);
    }

    public static RuleDropSelectorWeight weight(int weight) {
        return RainfallAPI.weight(weight);
    }

    public static RuleDropSelectorWeight weight(int weight, int fortuneModifier) {
        return RainfallAPI.weight(weight, fortuneModifier);
    }

    public static String itemString(String namespace, String path) {
        return RainfallAPI.itemString(namespace, path);
    }

    public static String itemString(String namespace, String path, int quantity) {
        return RainfallAPI.itemString(namespace, path, quantity);
    }

    public static String itemString(String namespace, String path, int quantity, @Nullable CompoundTag tag) {
        return RainfallAPI.itemString(namespace, path, quantity, tag);
    }

    public static String itemString(ItemStack itemStack) {
        return RainfallAPI.itemString(itemStack);
    }

    private DroptAPI() {
    }
}
