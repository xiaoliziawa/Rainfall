package com.lirxowo.rainfall.internal.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.base.IUndoableAction;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.ingredient.type.TagIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.lirxowo.rainfall.api.RainfallAPI;
import com.lirxowo.rainfall.internal.RainfallRuntime;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenCodeType.Name("mods.dropt.Dropt")
public final class ZenDropt {

    private static final Map<String, ZenRuleList> RULE_LISTS = new LinkedHashMap<>();

    @ZenCodeType.Method
    public static ZenRuleList list(String name) {
        ZenRuleList existing = RULE_LISTS.get(name);
        if (existing != null) {
            return existing;
        }
        ZenRuleList created = new ZenRuleList(ResourceLocation.fromNamespaceAndPath("crafttweaker", name));
        CraftTweakerAPI.apply(new CreateRuleListAction(name, created));
        return created;
    }

    @ZenCodeType.Method
    public static ZenRule rule() {
        return new ZenRule();
    }

    @ZenCodeType.Method
    public static ZenHarvester harvester() {
        return new ZenHarvester();
    }

    @ZenCodeType.Method
    public static ZenDrop drop() {
        return new ZenDrop();
    }

    @ZenCodeType.Method
    public static ZenRange range(int fixed) {
        return new ZenRange(RainfallAPI.range(fixed));
    }

    @ZenCodeType.Method
    public static ZenRange range(int min, int max) {
        return new ZenRange(RainfallAPI.range(min, max));
    }

    @ZenCodeType.Method
    public static ZenRange range(int min, int max, int fortuneModifier) {
        return new ZenRange(RainfallAPI.range(min, max, fortuneModifier));
    }

    @ZenCodeType.Method
    public static ZenWeight weight(int weight) {
        return new ZenWeight(RainfallAPI.weight(weight));
    }

    @ZenCodeType.Method
    public static ZenWeight weight(int weight, int fortuneModifier) {
        return new ZenWeight(RainfallAPI.weight(weight, fortuneModifier));
    }

    static void registerRules() {
        for (ZenRuleList ruleList : RULE_LISTS.values()) {
            RainfallAPI.registerRuleList(ruleList.getId(), ruleList.getPriority(), ruleList.getRules());
        }
    }

    static String[] getItemStrings(IItemStack[] items) {
        String[] result = new String[items.length];
        for (int index = 0; index < items.length; index++) {
            result[index] = RainfallAPI.itemString(items[index].getImmutableInternal());
        }
        return result;
    }

    static String[] getIngredientStrings(IIngredient[] ingredients) {
        List<String> result = new ArrayList<>();
        for (IIngredient ingredient : ingredients) {
            if (ingredient instanceof TagIngredient tagIngredient) {
                result.add('#' + tagIngredient.key().location().toString());
                continue;
            }
            for (IItemStack item : ingredient.getItems()) {
                result.add(RainfallAPI.itemString(item.getImmutableInternal()));
            }
        }
        return result.toArray(String[]::new);
    }

    private record CreateRuleListAction(String name, ZenRuleList ruleList) implements IUndoableAction {

        @Override
        public void apply() {
            RULE_LISTS.put(this.name, this.ruleList);
            RainfallRuntime.markRulesDirty();
        }

        @Override
        public String describe() {
            return "Creating Rainfall rule list " + this.ruleList.getId();
        }

        @Override
        public void undo() {
            RULE_LISTS.remove(this.name, this.ruleList);
            RainfallRuntime.markRulesDirty();
        }

        @Override
        public String describeUndo() {
            return "Removing Rainfall rule list " + this.ruleList.getId();
        }

        @Override
        public String systemName() {
            return "rainfall:create_rule_list";
        }
    }

    private ZenDropt() {
    }
}
