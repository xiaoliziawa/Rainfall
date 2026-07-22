package com.lirxowo.rainfall.internal.compat;

import com.lirxowo.rainfall.api.reference.EnumHarvesterGameStageType;
import com.lirxowo.rainfall.api.reference.EnumListType;
import com.lirxowo.rainfall.internal.rule.data.RuleMatchHarvesterGameStage;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.world.entity.player.Player;

public final class GameStagesCompat {

    public static boolean matches(RuleMatchHarvesterGameStage rule, Player player) {
        boolean result = rule.require == EnumHarvesterGameStageType.ALL
                ? GameStageHelper.hasAllOf(player, rule.stages)
                : GameStageHelper.hasAnyOf(player, rule.stages);
        return rule.type == EnumListType.WHITELIST ? result : !result;
    }

    private GameStagesCompat() {
    }
}
