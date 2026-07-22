package com.lirxowo.rainfall.internal.loot;

import com.lirxowo.rainfall.internal.RainfallRuntime;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public final class RainfallLootModifier extends LootModifier {

    public static final Codec<RainfallLootModifier> CODEC = RecordCodecBuilder.create(
            instance -> codecStart(instance).apply(instance, RainfallLootModifier::new)
    );

    public RainfallLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext context
    ) {
        return RainfallRuntime.instance().modifyLoot(generatedLoot, context);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
