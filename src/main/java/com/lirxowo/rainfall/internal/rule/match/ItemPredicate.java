package com.lirxowo.rainfall.internal.rule.match;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public final class ItemPredicate {

    private final Ingredient ingredient;
    private final CompoundTag tag;

    public ItemPredicate(Ingredient ingredient, @Nullable CompoundTag tag) {
        this.ingredient = ingredient;
        this.tag = normalizeTag(tag);
    }

    public boolean matches(ItemStack stack) {
        if (!this.ingredient.test(stack)) {
            return false;
        }
        return this.tag == null || this.tag.equals(normalizeTag(stack.getTag()));
    }

    private static CompoundTag normalizeTag(@Nullable CompoundTag source) {
        if (source == null) {
            return null;
        }
        CompoundTag normalized = source.copy();
        normalized.remove("Damage");
        return normalized.isEmpty() ? null : normalized;
    }
}
