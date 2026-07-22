package com.lirxowo.rainfall.api;

import com.lirxowo.rainfall.Rainfall;
import com.lirxowo.rainfall.api.builder.IDroptDropBuilder;
import com.lirxowo.rainfall.api.builder.IDroptHarvesterRuleBuilder;
import com.lirxowo.rainfall.api.builder.IDroptRuleBuilder;
import com.lirxowo.rainfall.api.builder.IRuleRegistrationHandler;
import com.lirxowo.rainfall.api.builder.RandomFortuneInt;
import com.lirxowo.rainfall.api.builder.RuleDropSelectorWeight;
import com.lirxowo.rainfall.internal.api.DropBuilder;
import com.lirxowo.rainfall.internal.api.HarvesterRuleBuilder;
import com.lirxowo.rainfall.internal.api.RuleBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

public final class RainfallAPI {

    private static IRuleRegistrationHandler registrationHandler;

    public static void initialize(IRuleRegistrationHandler handler) {
        if (registrationHandler != null) {
            throw new IllegalStateException("Rainfall API has already been initialized");
        }
        registrationHandler = Objects.requireNonNull(handler);
    }

    public static String modId() {
        return Rainfall.MODID;
    }

    public static void registerRuleList(ResourceLocation id, int priority, List<IDroptRuleBuilder> builders) {
        if (registrationHandler == null) {
            throw new IllegalStateException("Rainfall API is not initialized");
        }
        registrationHandler.register(id, priority, builders);
    }

    public static IDroptRuleBuilder rule() {
        return new RuleBuilder();
    }

    public static IDroptHarvesterRuleBuilder harvester() {
        return new HarvesterRuleBuilder();
    }

    public static IDroptDropBuilder drop() {
        return new DropBuilder();
    }

    public static RandomFortuneInt range(int fixed) {
        return new RandomFortuneInt(fixed);
    }

    public static RandomFortuneInt range(int min, int max) {
        return range(min, max, 0);
    }

    public static RandomFortuneInt range(int min, int max, int fortuneModifier) {
        RandomFortuneInt result = new RandomFortuneInt();
        result.min = min;
        result.max = max;
        result.fortuneModifier = fortuneModifier;
        return result;
    }

    public static RuleDropSelectorWeight weight(int weight) {
        return weight(weight, 0);
    }

    public static RuleDropSelectorWeight weight(int weight, int fortuneModifier) {
        RuleDropSelectorWeight result = new RuleDropSelectorWeight();
        result.value = weight;
        result.fortuneModifier = fortuneModifier;
        return result;
    }

    public static String itemString(String namespace, String path) {
        return itemString(namespace, path, 0, 1, null);
    }

    public static String itemString(String namespace, String path, int legacyMetadata) {
        return itemString(namespace, path, legacyMetadata, 1, null);
    }

    public static String itemString(String namespace, String path, int legacyMetadata, int quantity) {
        return itemString(namespace, path, legacyMetadata, quantity, null);
    }

    public static String itemString(
            String namespace,
            String path,
            int legacyMetadata,
            int quantity,
            @Nullable CompoundTag tag
    ) {
        String metadata = legacyMetadata == Short.MAX_VALUE ? "*" : Integer.toString(legacyMetadata);
        return namespace + ':' + path + ':' + metadata + (tag == null ? "" : "#" + tag) + " * " + quantity;
    }

    public static String itemString(ItemStack itemStack) {
        ResourceLocation key = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemStack.getItem()));
        return itemString(key.getNamespace(), key.getPath(), 0, itemStack.getCount(), itemStack.getTag());
    }

    private RainfallAPI() {
    }
}
