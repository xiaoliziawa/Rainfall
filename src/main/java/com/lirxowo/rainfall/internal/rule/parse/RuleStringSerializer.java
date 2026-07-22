package com.lirxowo.rainfall.internal.rule.parse;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.stream.Collectors;

public final class RuleStringSerializer {

    public static String serialize(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String value = id == null ? "minecraft:air" : id.toString();
        if (stack.hasTag()) {
            value += '#' + stack.getTag().toString();
        }
        return value.replace("\"", "\\\"");
    }

    public static String serialize(BlockState state) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        String value = id == null ? "minecraft:air" : id.toString();
        if (state.getValues().isEmpty()) {
            return value;
        }
        return value + state.getValues().entrySet().stream()
                .sorted(Map.Entry.comparingByKey((left, right) -> left.getName().compareTo(right.getName())))
                .map(RuleStringSerializer::serializeProperty)
                .collect(Collectors.joining(",", "[", "]"));
    }

    private static String serializeProperty(Map.Entry<Property<?>, Comparable<?>> entry) {
        return entry.getKey().getName() + '=' + propertyValueName(entry.getKey(), entry.getValue());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String propertyValueName(Property<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }

    private RuleStringSerializer() {
    }
}
