package com.lirxowo.rainfall.internal.event;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BreakContextCache {

    private final Map<Key, BreakContext> contexts = new ConcurrentHashMap<>();

    public void put(ResourceKey<Level> dimension, BlockPos position, Player player, ItemStack tool, int experience) {
        this.contexts.put(new Key(dimension, position.asLong()), new BreakContext(player, tool.copy(), experience));
    }

    public BreakContext take(ResourceKey<Level> dimension, BlockPos position) {
        return this.contexts.remove(new Key(dimension, position.asLong()));
    }

    public void clear() {
        this.contexts.clear();
    }

    private record Key(ResourceKey<Level> dimension, long position) {
    }

    public record BreakContext(Player player, ItemStack tool, int experience) {
    }
}
