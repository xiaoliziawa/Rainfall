package com.lirxowo.rainfall.internal.rule.match;

import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.internal.compat.GameStagesCompat;
import com.lirxowo.rainfall.internal.rule.data.RuleMatch;
import com.lirxowo.rainfall.internal.rule.data.RuleMatchHarvester;
import com.lirxowo.rainfall.internal.rule.data.RuleMatchHarvesterHeldItem;
import com.lirxowo.rainfall.internal.rule.log.RuleLog;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public final class RuleMatcher {

    public boolean matches(RuleMatch rule, RuleContext context, RuleLog log, boolean debug) {
        boolean verticalRange = this.matchesVerticalRange(rule, context);
        boolean spawnDistance = this.matchesSpawnDistance(rule, context);
        boolean drops = this.matchesDrops(rule, context);
        boolean harvester = this.matchesHarvester(rule.harvester, context);
        boolean biome = this.matchesBiome(rule, context);
        boolean dimension = this.matchesDimension(rule, context);
        boolean result = verticalRange && spawnDistance && drops && harvester && biome && dimension;
        if (debug) {
            log.debug("[MATCH] Conditions: verticalRange=" + verticalRange
                    + ", spawnDistance=" + spawnDistance
                    + ", drops=" + drops
                    + ", harvester=" + harvester
                    + ", biome=" + biome
                    + ", dimension=" + dimension);
            log.debug(result ? "[MATCH] Rule matched" : "[MATCH] Rule did not match");
        }
        return result;
    }

    public boolean matchesBlock(RuleMatch rule, RuleContext context) {
        if (rule.blocks.blocks.length == 0) {
            return true;
        }
        boolean found = rule.blocks._blocks.stream().anyMatch(entry -> entry.matches(context.blockState()));
        return applyListType(rule.blocks.type, found);
    }

    private boolean matchesDrops(RuleMatch rule, RuleContext context) {
        if (rule.drops.drops.length == 0) {
            return true;
        }
        boolean found = rule.drops._drops.stream()
                .anyMatch(predicate -> context.originalDrops().stream().anyMatch(predicate::matches));
        return applyListType(rule.drops.type, found);
    }

    private boolean matchesHarvester(RuleMatchHarvester rule, RuleContext context) {
        Player player = context.harvester();
        return switch (rule.type) {
            case ANY -> player == null || this.matchesPlayerConditions(rule, player, context);
            case NON_PLAYER -> player == null;
            case PLAYER -> player != null && this.matchesPlayerConditions(rule, player, context);
            case EXPLOSION -> context.explosion();
            case REAL_PLAYER -> player != null && !this.isFakePlayer(player)
                    && this.matchesPlayerConditions(rule, player, context);
            case FAKE_PLAYER -> player != null && this.isFakePlayer(player);
        };
    }

    private boolean isFakePlayer(Player player) {
        if (player instanceof FakePlayer) {
            return true;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        MinecraftServer server = serverPlayer.getServer();
        if (server == null) {
            return false;
        }
        ServerPlayer onlinePlayer = server.getPlayerList().getPlayer(serverPlayer.getUUID());
        return onlinePlayer == null || onlinePlayer != serverPlayer;
    }

    private boolean matchesPlayerConditions(RuleMatchHarvester rule, Player player, RuleContext context) {
        return this.matchesHeldItem(rule.heldItemMainHand, context.tool())
                && this.matchesHeldItem(rule.heldItemOffHand, player.getOffhandItem())
                && this.matchesPlayerName(rule, player)
                && this.matchesGameStages(rule, player);
    }

    private boolean matchesHeldItem(RuleMatchHarvesterHeldItem rule, ItemStack stack) {
        boolean hasItemConstraint = rule.items.length > 0;
        boolean hasToolConstraint = rule.harvestLevel != null && !rule.harvestLevel.isBlank();
        boolean hasEnchantmentConstraint = !rule.enchantments.isEmpty();
        if (!hasItemConstraint && !hasToolConstraint && !hasEnchantmentConstraint) {
            return true;
        }
        boolean itemMatch = hasItemConstraint && rule._items.stream().anyMatch(predicate -> predicate.matches(stack));
        boolean toolMatch = hasToolConstraint && this.matchesTool(rule, stack);
        boolean enchantmentMatch = hasEnchantmentConstraint && this.matchesEnchantments(rule, stack);
        if (rule.type == EnumListType.WHITELIST) {
            return (!hasItemConstraint || itemMatch)
                    && (!hasToolConstraint || toolMatch)
                    && (!hasEnchantmentConstraint || enchantmentMatch);
        }
        return (!hasItemConstraint || !itemMatch)
                && (!hasToolConstraint || !toolMatch)
                && (!hasEnchantmentConstraint || !enchantmentMatch);
    }

    private boolean matchesEnchantments(RuleMatchHarvesterHeldItem rule, ItemStack stack) {
        for (Map.Entry<Enchantment, Integer> entry : rule._enchantments.entrySet()) {
            if (stack.getEnchantmentLevel(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return rule._enchantments.size() == rule.enchantments.size();
    }

    @SuppressWarnings("deprecation")
    private boolean matchesTool(RuleMatchHarvesterHeldItem rule, ItemStack stack) {
        if (rule._toolAction == null || !stack.canPerformAction(rule._toolAction)) {
            return false;
        }
        int level = stack.getItem() instanceof TieredItem tieredItem ? tieredItem.getTier().getLevel() : 0;
        return level >= rule._minHarvestLevel && level <= rule._maxHarvestLevel;
    }

    private boolean matchesPlayerName(RuleMatchHarvester rule, Player player) {
        if (rule.playerName.names.length == 0) {
            return true;
        }
        String playerName = player.getGameProfile().getName().toLowerCase(Locale.ROOT);
        boolean found = Arrays.stream(rule.playerName.names)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .anyMatch(playerName::equals);
        return applyListType(rule.playerName.type, found);
    }

    private boolean matchesGameStages(RuleMatchHarvester rule, Player player) {
        if (rule.gamestages.stages.length == 0) {
            return true;
        }
        return ModList.get().isLoaded("gamestages") && GameStagesCompat.matches(rule.gamestages, player);
    }

    private boolean matchesBiome(RuleMatch rule, RuleContext context) {
        if (rule.biomes.ids.length == 0) {
            return true;
        }
        boolean found = context.level().getBiome(context.position()).unwrapKey()
                .map(rule.biomes._biomes::contains)
                .orElse(false);
        return applyListType(rule.biomes.type, found);
    }

    private boolean matchesDimension(RuleMatch rule, RuleContext context) {
        if (rule.dimensions.ids.length == 0 && rule.dimensions.names.length == 0) {
            return true;
        }
        boolean found = rule.dimensions._dimensions.contains(context.level().dimension());
        return applyListType(rule.dimensions.type, found);
    }

    private boolean matchesVerticalRange(RuleMatch rule, RuleContext context) {
        int y = context.position().getY();
        return y >= rule.verticalRange.min && y <= rule.verticalRange.max;
    }

    private boolean matchesSpawnDistance(RuleMatch rule, RuleContext context) {
        int min = Math.max(0, rule.spawnDistance.min);
        int max = rule.spawnDistance.max == -1 ? Integer.MAX_VALUE : Math.max(0, rule.spawnDistance.max);
        BlockPos spawn = context.level().getSharedSpawnPos();
        double x = spawn.getX() - context.position().getX();
        double z = spawn.getZ() - context.position().getZ();
        double distance = Math.sqrt(x * x + z * z);
        boolean inside = distance >= min && distance <= max;
        return applyListType(rule.spawnDistance.type, inside);
    }

    private static boolean applyListType(EnumListType type, boolean found) {
        return type == EnumListType.WHITELIST ? found : !found;
    }
}
