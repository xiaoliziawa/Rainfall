package com.lirxowo.rainfall.internal.rule.data;

import com.lirxowo.rainfall.api.builder.RangeInt;

public class RuleMatch {

    public RuleMatchBlocks blocks = new RuleMatchBlocks();
    public RuleMatchDrops drops = new RuleMatchDrops();
    public RuleMatchHarvester harvester = new RuleMatchHarvester();
    public RuleMatchBiome biomes = new RuleMatchBiome();
    public RuleMatchDimension dimensions = new RuleMatchDimension();
    public RangeInt verticalRange = new RangeInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    public RuleMatchSpawnDistance spawnDistance = new RuleMatchSpawnDistance();
}
