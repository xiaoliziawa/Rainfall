package com.lirxowo.rainfall.internal.rule.parse;

import com.lirxowo.rainfall.internal.rule.match.BlockPredicate;
import com.lirxowo.rainfall.internal.rule.match.ItemPredicate;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class RuleStringParser {

    public static ParsedItem parseItem(String input) throws MalformedRuleStringException {
        String value = input == null ? "" : input.trim();
        if (value.isEmpty()) {
            throw new MalformedRuleStringException("Item string is empty");
        }

        int quantity = 1;
        int separator = findQuantitySeparator(value);
        if (separator >= 0) {
            String quantityText = value.substring(separator + 1).trim();
            try {
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException error) {
                throw new MalformedRuleStringException("Invalid quantity: " + quantityText, error);
            }
            value = value.substring(0, separator).trim();
        }

        CompoundTag nbt = null;
        int nbtIndex = findNbtSeparator(value);
        if (nbtIndex > 0) {
            String nbtText = value.substring(nbtIndex + 1).trim();
            try {
                nbt = TagParser.parseTag(nbtText);
            } catch (CommandSyntaxException error) {
                throw new MalformedRuleStringException("Invalid NBT: " + nbtText, error);
            }
            value = value.substring(0, nbtIndex).trim();
        }

        boolean tag = value.startsWith("#");
        if (tag) {
            value = value.substring(1);
        }
        if (value.startsWith("tag:")) {
            tag = true;
            value = value.substring(4);
        }
        ResourceLocation id = parseId(value);
        return new ParsedItem(id, tag ? id : null, nbt, quantity);
    }

    public static ItemPredicate parseItemPredicate(String input) throws MalformedRuleStringException {
        if ("EMPTY".equalsIgnoreCase(input.trim())) {
            return new ItemPredicate(Ingredient.EMPTY, null);
        }
        ParsedItem parsed = parseItem(input);
        if (parsed.tag() != null) {
            TagKey<Item> key = TagKey.create(Registries.ITEM, parsed.tag());
            return new ItemPredicate(Ingredient.of(key), parsed.nbt());
        }
        Item item = ForgeRegistries.ITEMS.getValue(parsed.id());
        if (item == null || item == Items.AIR) {
            throw new MalformedRuleStringException("Unknown item: " + parsed.id());
        }
        return new ItemPredicate(Ingredient.of(item), parsed.nbt());
    }

    public static ItemStack parseItemStack(String input) throws MalformedRuleStringException {
        List<ItemStack> stacks = parseItemStacks(input);
        if (stacks.isEmpty()) {
            throw new MalformedRuleStringException("No item matched: " + input);
        }
        return stacks.get(0);
    }

    public static List<ItemStack> parseItemStacks(String input) throws MalformedRuleStringException {
        ParsedItem parsed = parseItem(input);
        List<ItemStack> result = new ArrayList<>();
        if (parsed.tag() != null) {
            ItemStack[] items = Ingredient.of(TagKey.create(Registries.ITEM, parsed.tag())).getItems();
            if (items.length == 0) {
                throw new MalformedRuleStringException("Item tag is empty: " + parsed.tag());
            }
            for (ItemStack item : items) {
                result.add(item.copy());
            }
        } else {
            Item item = ForgeRegistries.ITEMS.getValue(parsed.id());
            if (item == null || item == Items.AIR) {
                throw new MalformedRuleStringException("Unknown item: " + parsed.id());
            }
            result.add(new ItemStack(item));
        }
        for (ItemStack stack : result) {
            if (parsed.nbt() != null) {
                stack.setTag(parsed.nbt().copy());
            }
            if (parsed.quantity() > 0) {
                stack.setCount(parsed.quantity());
            }
        }
        return result;
    }

    public static BlockPredicate parseBlock(String input) throws MalformedRuleStringException {
        String blockText = input.trim();
        Map<String, String> properties = new HashMap<>();
        int propertyStart = blockText.indexOf('[');
        if (propertyStart >= 0) {
            if (!blockText.endsWith("]")) {
                throw new MalformedRuleStringException("Unclosed block properties: " + input);
            }
            String propertyText = blockText.substring(propertyStart + 1, blockText.length() - 1);
            for (String property : propertyText.split(",")) {
                String[] pair = property.split("=", 2);
                if (pair.length != 2) {
                    throw new MalformedRuleStringException("Invalid block property: " + property);
                }
                properties.put(pair[0].trim(), pair[1].trim());
            }
            blockText = blockText.substring(0, propertyStart);
        }
        ParsedItem parsed = parseItem(blockText);
        if (parsed.tag() != null) {
            return new BlockPredicate(TagKey.create(Registries.BLOCK, parsed.tag()), properties);
        }
        Block block = ForgeRegistries.BLOCKS.getValue(parsed.id());
        if (block == null || block == Blocks.AIR) {
            throw new MalformedRuleStringException("Unknown block: " + parsed.id());
        }
        return new BlockPredicate(parsed.id(), properties);
    }

    public static BlockState parseBlockState(String idText, Map<String, String> properties) throws MalformedRuleStringException {
        ResourceLocation id = parseId(idText);
        Block block = ForgeRegistries.BLOCKS.getValue(id);
        if (block == null || block == Blocks.AIR) {
            throw new MalformedRuleStringException("Unknown replacement block: " + id);
        }
        BlockState state = block.defaultBlockState();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Property<?> property = block.getStateDefinition().getProperty(entry.getKey());
            if (property == null) {
                throw new MalformedRuleStringException("Unknown property " + entry.getKey() + " for " + id);
            }
            state = setProperty(state, property, entry.getValue(), id);
        }
        return state;
    }

    public static ResourceLocation parseId(String input) throws MalformedRuleStringException {
        ResourceLocation id = ResourceLocation.tryParse(input.trim());
        if (id == null) {
            throw new MalformedRuleStringException("Invalid resource location: " + input);
        }
        return id;
    }

    public static ResourceKey<Biome> parseBiome(String input) throws MalformedRuleStringException {
        return ResourceKey.create(Registries.BIOME, parseId(input));
    }

    public static ToolAction parseToolAction(String name) {
        String normalizedName = name.trim().toLowerCase(Locale.ROOT);
        return switch (normalizedName) {
            case "pickaxe" -> ToolActions.PICKAXE_DIG;
            case "axe" -> ToolActions.AXE_DIG;
            case "shovel" -> ToolActions.SHOVEL_DIG;
            case "hoe" -> ToolActions.HOE_DIG;
            case "sword" -> ToolActions.SWORD_DIG;
            case "shears" -> ToolActions.SHEARS_DIG;
            default -> ToolAction.get(normalizedName);
        };
    }

    private static int findQuantitySeparator(String value) {
        for (int index = value.length() - 1; index >= 0; index--) {
            if (value.charAt(index) != '*') {
                continue;
            }
            String suffix = value.substring(index + 1).trim();
            if (suffix.matches("-?\\d+")) {
                return index;
            }
        }
        return -1;
    }

    private static int findNbtSeparator(String value) {
        return value.indexOf('#', value.startsWith("#") ? 1 : 0);
    }

    private static <T extends Comparable<T>> BlockState setProperty(
            BlockState state,
            Property<T> property,
            String value,
            ResourceLocation blockId
    ) throws MalformedRuleStringException {
        T parsed = property.getValue(value).orElseThrow(() -> new MalformedRuleStringException(
                "Invalid value " + value + " for property " + property.getName() + " on " + blockId
        ));
        return state.setValue(property, parsed);
    }

    private RuleStringParser() {
    }
}
