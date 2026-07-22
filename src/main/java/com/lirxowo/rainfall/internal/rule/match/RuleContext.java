package com.lirxowo.rainfall.internal.rule.match;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record RuleContext(
        ServerLevel level,
        @Nullable Player harvester,
        BlockPos position,
        BlockState blockState,
        List<ItemStack> originalDrops,
        ItemStack tool,
        boolean explosion
) {
}
