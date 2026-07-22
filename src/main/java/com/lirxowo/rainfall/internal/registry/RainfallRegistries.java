package com.lirxowo.rainfall.internal.registry;

import com.lirxowo.rainfall.Rainfall;
import com.lirxowo.rainfall.internal.loot.RainfallLootModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RainfallRegistries {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Rainfall.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> RAINFALL_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("rainfall", () -> RainfallLootModifier.CODEC);

    private RainfallRegistries() {
    }
}
