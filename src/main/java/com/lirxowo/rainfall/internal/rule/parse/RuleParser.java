package com.lirxowo.rainfall.internal.rule.parse;

import com.lirxowo.rainfall.internal.rule.data.Rule;
import com.lirxowo.rainfall.internal.rule.data.RuleList;
import com.lirxowo.rainfall.internal.rule.data.RuleMatch;
import com.lirxowo.rainfall.internal.rule.data.RuleMatchHarvesterHeldItem;
import com.lirxowo.rainfall.internal.rule.log.RuleLog;
import com.lirxowo.rainfall.internal.rule.match.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public final class RuleParser {

    public static void parse(RuleList ruleList, RuleLog log) {
        for (int index = 0; index < ruleList.rules.size(); index++) {
            Rule rule = ruleList.rules.get(index);
            if (rule == null) {
                log.warn("Skipping null rule " + index + " in " + ruleList._filename);
                continue;
            }
            parseRule(ruleList, rule, index, log);
        }
    }

    private static void parseRule(RuleList list, Rule rule, int index, RuleLog log) {
        if (rule.match == null) {
            rule.match = new RuleMatch();
        }
        resetParsedState(rule);
        parseBlocks(list, rule, index, log);
        parseItemPredicates(rule.match.drops.drops, rule.match.drops._drops, list, index, "drop match", log);
        parseHeldItem(rule.match.harvester.heldItemMainHand, list, index, "main hand", log);
        parseHeldItem(rule.match.harvester.heldItemOffHand, list, index, "off hand", log);
        parseBiomes(list, rule, index, log);
        parseDimensions(list, rule, index, log);
        for (int dropIndex = 0; dropIndex < rule.drops.length; dropIndex++) {
            var drop = rule.drops[dropIndex];
            if (drop == null) {
                log.warn("Skipping null drop " + dropIndex + " in rule " + index + " from " + list._filename);
                continue;
            }
            for (String item : drop.item.items) {
                try {
                    drop.item._items.addAll(RuleStringParser.parseItemStacks(item));
                } catch (MalformedRuleStringException error) {
                    log.error(location(list, index) + " invalid drop item " + item, error);
                }
            }
            parseItemPredicates(
                    drop.item.matchQuantity.drops,
                    drop.item.matchQuantity._drops,
                    list,
                    index,
                    "quantity match",
                    log
            );
            if (drop.replaceBlock.block != null) {
                try {
                    drop.replaceBlock._blockState = RuleStringParser.parseBlockState(
                            drop.replaceBlock.block,
                            drop.replaceBlock.properties
                    );
                } catch (MalformedRuleStringException error) {
                    log.error(location(list, index) + " invalid replacement block", error);
                }
            }
        }
    }

    private static void resetParsedState(Rule rule) {
        rule.match.blocks._blocks.clear();
        rule.match.drops._drops.clear();
        resetHeldItem(rule.match.harvester.heldItemMainHand);
        resetHeldItem(rule.match.harvester.heldItemOffHand);
        rule.match.biomes._biomes.clear();
        rule.match.dimensions._dimensions.clear();
        for (var drop : rule.drops) {
            if (drop == null) {
                continue;
            }
            drop.item._items.clear();
            drop.item.matchQuantity._drops.clear();
            drop.replaceBlock._blockState = null;
        }
    }

    private static void resetHeldItem(RuleMatchHarvesterHeldItem heldItem) {
        heldItem._items.clear();
        heldItem._toolClass = null;
        heldItem._toolAction = null;
        heldItem._minHarvestLevel = Integer.MIN_VALUE;
        heldItem._maxHarvestLevel = Integer.MAX_VALUE;
        heldItem._enchantments.clear();
    }

    private static void parseBlocks(RuleList list, Rule rule, int index, RuleLog log) {
        for (String block : rule.match.blocks.blocks) {
            try {
                rule.match.blocks._blocks.add(RuleStringParser.parseBlock(block));
            } catch (MalformedRuleStringException error) {
                log.error(location(list, index) + " invalid block match " + block, error);
            }
        }
    }

    private static void parseItemPredicates(
            String[] values,
            List<ItemPredicate> target,
            RuleList list,
            int index,
            String kind,
            RuleLog log
    ) {
        for (String value : values) {
            try {
                target.add(RuleStringParser.parseItemPredicate(value));
            } catch (MalformedRuleStringException error) {
                log.error(location(list, index) + " invalid " + kind + ' ' + value, error);
            }
        }
    }

    private static void parseHeldItem(
            RuleMatchHarvesterHeldItem heldItem,
            RuleList list,
            int index,
            String kind,
            RuleLog log
    ) {
        parseItemPredicates(heldItem.items, heldItem._items, list, index, kind, log);
        parseEnchantments(heldItem, list, index, kind, log);
        if (heldItem.harvestLevel == null || heldItem.harvestLevel.isBlank()) {
            return;
        }
        String[] values = heldItem.harvestLevel.split(";");
        if (values.length != 3) {
            log.error(location(list, index) + " invalid harvest level " + heldItem.harvestLevel);
            return;
        }
        heldItem._toolClass = values[0].trim();
        heldItem._toolAction = RuleStringParser.parseToolAction(heldItem._toolClass);
        try {
            int min = Integer.parseInt(values[1].trim());
            int max = Integer.parseInt(values[2].trim());
            heldItem._minHarvestLevel = min < 0 ? Integer.MIN_VALUE : min;
            heldItem._maxHarvestLevel = max < 0 ? Integer.MAX_VALUE : max;
        } catch (NumberFormatException error) {
            log.error(location(list, index) + " invalid harvest level " + heldItem.harvestLevel, error);
        }
    }

    private static void parseEnchantments(
            RuleMatchHarvesterHeldItem heldItem,
            RuleList list,
            int index,
            String kind,
            RuleLog log
    ) {
        for (var entry : heldItem.enchantments.entrySet()) {
            try {
                ResourceLocation id = RuleStringParser.parseId(entry.getKey());
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
                if (enchantment == null) {
                    throw new MalformedRuleStringException("Unknown enchantment: " + id);
                }
                int minimumLevel = entry.getValue();
                if (minimumLevel < 1) {
                    throw new MalformedRuleStringException("Minimum enchantment level must be at least 1: " + id);
                }
                heldItem._enchantments.put(enchantment, minimumLevel);
            } catch (MalformedRuleStringException error) {
                log.error(location(list, index) + " invalid " + kind + " enchantment " + entry.getKey(), error);
            }
        }
    }

    private static void parseBiomes(RuleList list, Rule rule, int index, RuleLog log) {
        for (String id : rule.match.biomes.ids) {
            try {
                rule.match.biomes._biomes.add(RuleStringParser.parseBiome(id));
            } catch (MalformedRuleStringException error) {
                log.error(location(list, index) + " invalid biome " + id, error);
            }
        }
    }

    private static void parseDimensions(RuleList list, Rule rule, int index, RuleLog log) {
        for (int id : rule.match.dimensions.ids) {
            ResourceKey<Level> dimension = switch (id) {
                case -1 -> Level.NETHER;
                case 0 -> Level.OVERWORLD;
                case 1 -> Level.END;
                default -> null;
            };
            if (dimension == null) {
                log.error(location(list, index) + " cannot map legacy dimension id " + id);
            } else {
                rule.match.dimensions._dimensions.add(dimension);
            }
        }
        for (String name : rule.match.dimensions.names) {
            try {
                ResourceLocation id = RuleStringParser.parseId(name);
                rule.match.dimensions._dimensions.add(ResourceKey.create(Registries.DIMENSION, id));
            } catch (MalformedRuleStringException error) {
                log.error(location(list, index) + " invalid dimension " + name, error);
            }
        }
    }

    private static String location(RuleList list, int index) {
        return "[" + list._filename + " rule " + index + ']';
    }

    private RuleParser() {
    }
}
