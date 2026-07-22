package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.reference.EnumListType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RuleMatchDimension {

    public transient List<ResourceKey<Level>> _dimensions = new ArrayList<>();
    public EnumListType type = EnumListType.WHITELIST;
    public int[] ids = new int[0];
    public String[] names = new String[0];
}
