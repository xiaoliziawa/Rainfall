package com.lirxowo.rainfall.api.builder;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IRuleRegistrationHandler {

    void register(ResourceLocation id, int priority, List<IDroptRuleBuilder> builders);
}
