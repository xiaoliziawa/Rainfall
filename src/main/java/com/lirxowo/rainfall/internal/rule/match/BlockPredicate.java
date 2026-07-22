package com.lirxowo.rainfall.internal.rule.match;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public final class BlockPredicate {

    private final ResourceLocation blockId;
    private final TagKey<Block> tag;
    private final Map<String, String> properties;

    public BlockPredicate(ResourceLocation blockId, Map<String, String> properties) {
        this.blockId = blockId;
        this.tag = null;
        this.properties = Map.copyOf(properties);
    }

    public BlockPredicate(TagKey<Block> tag, Map<String, String> properties) {
        this.blockId = null;
        this.tag = tag;
        this.properties = Map.copyOf(properties);
    }

    public boolean matches(BlockState state) {
        boolean blockMatches = this.tag == null
                ? this.blockId.equals(ForgeRegistries.BLOCKS.getKey(state.getBlock()))
                : state.is(this.tag);
        if (!blockMatches) {
            return false;
        }
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            Property<?> matchedProperty = null;
            for (Property<?> property : state.getProperties()) {
                if (property.getName().equals(entry.getKey())) {
                    matchedProperty = property;
                    break;
                }
            }
            if (matchedProperty == null || !hasValue(state, matchedProperty, entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean hasValue(BlockState state, Property<?> property, String expected) {
        return hasTypedValue(state, (Property) property, expected);
    }

    private static <T extends Comparable<T>> boolean hasTypedValue(BlockState state, Property<T> property, String expected) {
        return property.getName(state.getValue(property)).equals(expected);
    }
}
