package com.lirxowo.rainfall.internal.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class RainfallConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PROFILE_LOG_OUTPUT;
    public static final ForgeConfigSpec.BooleanValue INJECT_PROFILING_RULES;
    public static final ForgeConfigSpec.BooleanValue JSON_STRICT_MODE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("general");
        ENABLE_PROFILE_LOG_OUTPUT = builder
                .comment("Enable rule profiling output in the Rainfall log.")
                .define("enableProfileLogOutput", false);
        INJECT_PROFILING_RULES = builder
                .comment("Inject a large synthetic rule set for development profiling.")
                .define("injectProfilingRules", false);
        JSON_STRICT_MODE = builder
                .comment("Reject unknown properties in rule JSON files.")
                .define("jsonStrictMode", true);
        builder.pop();
        SPEC = builder.build();
    }

    private RainfallConfig() {
    }
}
