package com.lirxowo.rainfall.internal.rule;

import com.lirxowo.rainfall.internal.config.RainfallConfig;
import com.lirxowo.rainfall.internal.rule.data.Rule;
import com.lirxowo.rainfall.internal.rule.data.RuleList;
import com.lirxowo.rainfall.internal.rule.log.RuleLog;
import com.lirxowo.rainfall.internal.rule.match.RuleContext;
import com.lirxowo.rainfall.internal.rule.match.RuleMatcher;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RuleLocator {

    private final List<RuleList> ruleLists;
    private final RuleMatcher matcher;
    private final RuleLog log;
    private final Map<BlockState, List<Rule>> blockCache = new ConcurrentHashMap<>();

    public RuleLocator(List<RuleList> ruleLists, RuleMatcher matcher, RuleLog log) {
        this.ruleLists = ruleLists;
        this.matcher = matcher;
        this.log = log;
    }

    public List<Rule> locate(RuleContext context) {
        List<Rule> candidates = this.blockCache.computeIfAbsent(
                context.blockState(),
                state -> this.cacheRules(context)
        );
        List<Rule> matches = new ArrayList<>();
        boolean profile = RainfallConfig.ENABLE_PROFILE_LOG_OUTPUT.get();
        long start = profile ? System.nanoTime() : 0L;
        int checkedRules = 0;
        for (Rule rule : candidates) {
            checkedRules++;
            if (rule.debug) {
                this.log.debug("[EVENT] BlockState: " + context.blockState());
                this.log.debug("[EVENT] Harvester: " + context.harvester());
                this.log.debug("[EVENT] Drops: " + context.originalDrops());
                this.log.debug("[EVENT] Position: " + context.position());
            }
            if (this.matcher.matches(rule.match, context, this.log, rule.debug)) {
                matches.add(rule);
                if (!rule.fallthrough) {
                    break;
                }
            }
        }
        if (profile) {
            this.log.profile("Searched " + checkedRules + " rules in " + elapsedMilliseconds(start) + " ms");
        }
        return matches;
    }

    public void clearCache() {
        this.blockCache.clear();
    }

    private List<Rule> cacheRules(RuleContext context) {
        List<Rule> result = new ArrayList<>();
        boolean profile = RainfallConfig.ENABLE_PROFILE_LOG_OUTPUT.get();
        long start = profile ? System.nanoTime() : 0L;
        int checkedRules = 0;
        for (RuleList ruleList : this.ruleLists) {
            for (Rule rule : ruleList.rules) {
                checkedRules++;
                if (rule != null && this.matcher.matchesBlock(rule.match, context)) {
                    result.add(rule);
                }
            }
        }
        if (profile) {
            this.log.profile("Cached " + result.size() + " rules from " + checkedRules + " rules in "
                    + elapsedMilliseconds(start) + " ms, blockState: " + context.blockState());
        }
        return List.copyOf(result);
    }

    private static double elapsedMilliseconds(long start) {
        return (System.nanoTime() - start) / 1_000_000.0;
    }
}
