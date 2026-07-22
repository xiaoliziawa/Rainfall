package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumListType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class RuleMatchBiome {

    public transient List<ResourceKey<Biome>> _biomes = new ArrayList<>();
    public EnumListType type = EnumListType.WHITELIST;
    public String[] ids = new String[0];
}
