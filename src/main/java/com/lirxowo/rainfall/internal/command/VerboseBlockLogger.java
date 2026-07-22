package com.lirxowo.rainfall.internal.command;

import com.lirxowo.rainfall.internal.rule.parse.RuleStringSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class VerboseBlockLogger {

    private final Set<UUID> listeners = new HashSet<>();
    private final Map<String, Integer> entries = new HashMap<>();
    private final Map<BlockState, String> stateNames = new HashMap<>();

    public boolean toggle(ServerPlayer player) {
        UUID id = player.getUUID();
        if (this.listeners.remove(id)) {
            return false;
        }
        this.listeners.add(id);
        return true;
    }

    public void remove(ServerPlayer player) {
        this.listeners.remove(player.getUUID());
    }

    public void record(BlockState state) {
        if (this.listeners.isEmpty()) {
            return;
        }
        String name = this.stateNames.computeIfAbsent(state, RuleStringSerializer::serialize);
        this.entries.merge(name, 1, Integer::sum);
    }

    public void flush(MinecraftServer server) {
        if (this.listeners.isEmpty() || this.entries.isEmpty()) {
            return;
        }
        for (UUID listener : this.listeners) {
            ServerPlayer player = server.getPlayerList().getPlayer(listener);
            if (player == null) {
                continue;
            }
            for (Map.Entry<String, Integer> entry : this.entries.entrySet()) {
                player.sendSystemMessage(Component.translatable(
                        "commands.rainfall.verbose.entry",
                        entry.getKey(),
                        entry.getValue()
                ));
            }
        }
        this.entries.clear();
    }

    public void clear() {
        this.listeners.clear();
        this.entries.clear();
        this.stateNames.clear();
    }
}
