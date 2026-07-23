package com.lirxowo.rainfall.internal.rule;

import com.lirxowo.rainfall.api.builder.RuleDropSelectorWeight;
import com.lirxowo.rainfall.internal.rule.data.Rule;
import com.lirxowo.rainfall.internal.rule.data.RuleDrop;
import com.lirxowo.rainfall.internal.rule.data.RuleList;
import com.lirxowo.rainfall.internal.rule.log.RuleLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class ProfileRuleInjector {

    private static final int MAX_COMBINATION_RULES = 150_000;
    private static final int SELECTOR_WEIGHT = 10;

    public static void inject(List<RuleList> ruleLists, RuleLog log) {
        long start = System.nanoTime();
        RuleList profileList = new RuleList();
        profileList._filename = "rainfall_profiling";
        ruleLists.add(profileList);
        int remaining = MAX_COMBINATION_RULES;
        combinationLoop:
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            if (block == Blocks.STONE || block == Blocks.AIR) {
                continue;
            }
            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
            if (blockId == null) {
                continue;
            }
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                if (remaining == 0) {
                    break combinationLoop;
                }
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
                if (itemId == null) {
                    continue;
                }
                Rule rule = new Rule();
                rule.match.blocks.blocks = new String[]{blockId.toString()};
                rule.match.harvester.heldItemMainHand.items = new String[]{itemId.toString()};
                RuleDrop drop = new RuleDrop();
                drop.item.items = new String[]{"minecraft:stone"};
                rule.drops = new RuleDrop[]{drop};
                profileList.rules.add(rule);
                remaining--;
            }
        }
        int combinationCount = MAX_COMBINATION_RULES - remaining;
        int selectorCount = addSelectorRule(profileList);
        log.profile("Injected " + (combinationCount + 1) + " rules in "
                + elapsedMilliseconds(start) + " ms");
        log.profile("Test rule has " + selectorCount + " weighted selectors");
    }

    private static int addSelectorRule(RuleList profileList) {
        Rule rule = new Rule();
        rule.match.blocks.blocks = new String[]{"minecraft:stone"};
        rule.match.harvester.heldItemMainHand.items = new String[]{"minecraft:stone_pickaxe"};
        List<RuleDrop> drops = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item == Items.AIR) {
                continue;
            }
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId == null) {
                continue;
            }
            RuleDrop drop = new RuleDrop();
            drop.item.items = new String[]{itemId.toString()};
            RuleDropSelectorWeight weight = new RuleDropSelectorWeight();
            weight.value = SELECTOR_WEIGHT;
            drop.selector.weight = weight;
            drops.add(drop);
        }
        rule.drops = drops.toArray(RuleDrop[]::new);
        profileList.rules.add(rule);
        return drops.size();
    }

    private static double elapsedMilliseconds(long start) {
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private ProfileRuleInjector() {
    }
}
