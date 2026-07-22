package com.lirxowo.rainfall.internal.rule.parse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record ParsedItem(
        ResourceLocation id,
        @Nullable ResourceLocation tag,
        @Nullable CompoundTag nbt,
        int legacyMetadata,
        int quantity
) {
}
