package com.lirxowo.rainfall.internal.rule.drop;

import com.lirxowo.rainfall.api.reference.EnumDropListStrategy;
import com.lirxowo.rainfall.api.reference.EnumDropStrategy;
import com.lirxowo.rainfall.api.reference.EnumReplaceStrategy;
import com.lirxowo.rainfall.api.reference.EnumXPReplaceStrategy;
import com.lirxowo.rainfall.internal.rule.data.Rule;
import com.lirxowo.rainfall.internal.rule.data.RuleDrop;
import com.lirxowo.rainfall.internal.rule.log.RuleLog;
import com.lirxowo.rainfall.internal.rule.match.ItemPredicate;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DropModifier {

    public DropModification modify(
            Rule rule,
            List<ItemStack> currentDrops,
            boolean silkTouching,
            int fortuneLevel,
            int originalExperience,
            RandomSource random,
            RuleLog log
    ) {
        List<ItemStack> originalDrops = currentDrops.stream().map(ItemStack::copy).toList();
        if (rule.replaceStrategy == EnumReplaceStrategy.REPLACE_ALL) {
            currentDrops.clear();
        }

        WeightedPicker<RuleDrop> picker = new WeightedPicker<>();
        List<RuleDrop> selected = new ArrayList<>();
        if (rule.debug) {
            log.debug("[DROP] Tool conditions: fortune=" + fortuneLevel + ", silkTouch=" + silkTouching);
        }
        for (RuleDrop drop : rule.drops) {
            if (drop == null) {
                continue;
            }
            if (drop.force) {
                selected.add(drop);
                continue;
            }
            boolean validCandidate = drop.selector.isValidCandidate(silkTouching, fortuneLevel);
            if (rule.debug) {
                log.debug("[DROP] Selector: requiredFortune=" + drop.selector.fortuneLevelRequired
                        + ", silkTouch=" + drop.selector.silktouch
                        + ", valid=" + validCandidate);
            }
            if (validCandidate) {
                picker.add(drop.selector.weight.value + fortuneLevel * drop.selector.weight.fortuneModifier, drop);
            }
        }
        if (picker.isEmpty() && selected.isEmpty()) {
            return new DropModification(0, null);
        }

        int dropCount = rule.dropCount.get(random, fortuneLevel);
        for (int count = 0; count < dropCount && !picker.isEmpty(); count++) {
            selected.add(picker.get(random, rule.dropStrategy == EnumDropStrategy.UNIQUE));
        }

        List<ItemStack> newDrops = new ArrayList<>();
        BlockState replacement = null;
        int experience = 0;
        for (RuleDrop drop : selected) {
            if (replacement == null && drop.replaceBlock != null) {
                replacement = drop.replaceBlock._blockState;
            }
            int selectedExperience = drop.xp.get(random, fortuneLevel);
            if (drop.xpReplaceStrategy == EnumXPReplaceStrategy.ADD) {
                selectedExperience += originalExperience;
            }
            experience += Math.max(0, selectedExperience);

            int quantity = this.getItemQuantity(drop, originalDrops, fortuneLevel, random);
            if (quantity <= 0 || drop.item._items.isEmpty()) {
                continue;
            }
            if (drop.item.drop == EnumDropListStrategy.ONE) {
                ItemStack stack = drop.item._items.get(random.nextInt(drop.item._items.size())).copy();
                this.addStacks(newDrops, stack, quantity);
            } else {
                for (ItemStack item : drop.item._items) {
                    this.addStacks(newDrops, item.copy(), quantity);
                }
            }
        }

        if (rule.replaceStrategy == EnumReplaceStrategy.REPLACE_ALL_IF_SELECTED && !newDrops.isEmpty()) {
            currentDrops.clear();
        }
        boolean removeMatched = rule.replaceStrategy == EnumReplaceStrategy.REPLACE_ITEMS
                || (rule.replaceStrategy == EnumReplaceStrategy.REPLACE_ITEMS_IF_SELECTED && !newDrops.isEmpty());
        if (removeMatched && !rule.match.drops._drops.isEmpty()) {
            currentDrops.removeIf(stack -> rule.match.drops._drops.stream().anyMatch(predicate -> predicate.matches(stack)));
        }
        currentDrops.addAll(newDrops);
        if (rule.debug) {
            log.debug("[DROP] Returning drops: " + currentDrops);
            log.debug("[DROP] Experience: " + experience);
            log.debug("[DROP] Replacement: " + replacement);
        }
        return new DropModification(experience, replacement);
    }

    private int getItemQuantity(
            RuleDrop drop,
            List<ItemStack> originalDrops,
            int fortuneLevel,
            RandomSource random
    ) {
        int quantity = drop.item.quantity.get(random, fortuneLevel);
        if (drop.item.matchQuantity._drops.isEmpty() || originalDrops.isEmpty()) {
            return quantity;
        }
        Map<ItemStack, Integer> counts = new LinkedHashMap<>();
        for (ItemStack dropped : originalDrops) {
            ItemStack key = counts.keySet().stream()
                    .filter(existing -> ItemStack.isSameItem(existing, dropped))
                    .findFirst()
                    .orElse(null);
            if (key == null) {
                counts.put(dropped.copy(), dropped.getCount());
            } else {
                counts.put(key, counts.get(key) + dropped.getCount());
            }
        }
        for (ItemPredicate predicate : drop.item.matchQuantity._drops) {
            for (Map.Entry<ItemStack, Integer> entry : counts.entrySet()) {
                if (predicate.matches(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return quantity;
    }

    private void addStacks(List<ItemStack> target, ItemStack template, int quantity) {
        if (quantity == 1 && template.getCount() > 1) {
            quantity = template.getCount();
        }
        int maxStackSize = template.getMaxStackSize();
        int remaining = quantity;
        while (remaining > 0) {
            ItemStack stack = template.copy();
            stack.setCount(Math.min(maxStackSize, remaining));
            target.add(stack);
            remaining -= stack.getCount();
        }
    }
}
