package com.lirxowo.rainfall.internal.rule.drop;

import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record DropModification(int experience, @Nullable BlockState replacement) {
}
