package com.lirxowo.rainfall.internal.command;

import com.lirxowo.rainfall.Rainfall;
import com.lirxowo.rainfall.internal.RainfallRuntime;
import com.lirxowo.rainfall.internal.rule.RuleExporter;
import com.lirxowo.rainfall.internal.rule.parse.RuleStringSerializer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;

public final class RainfallCommands {

    private static final int REQUIRED_PERMISSION_LEVEL = 2;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, RainfallRuntime runtime) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("rainfall")
                .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL))
                .executes(context -> showUsage(context.getSource()))
                .then(Commands.literal("reload").executes(context -> reload(context.getSource(), runtime)))
                .then(Commands.literal("hand").executes(context -> hand(context.getSource())))
                .then(Commands.literal("verbose").executes(context -> verbose(context.getSource(), runtime)))
                .then(Commands.literal("export").executes(context -> export(context.getSource(), runtime)));
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(command);
        dispatcher.register(Commands.literal("dropt")
                .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL))
                .redirect(root));
    }

    private static int showUsage(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable("commands.rainfall.usage"), false);
        return 1;
    }

    private static int reload(CommandSourceStack source, RainfallRuntime runtime) {
        source.sendSuccess(() -> Component.translatable("commands.rainfall.reload.start"), false);
        runtime.reload();
        int count = runtime.ruleListCount();
        source.sendSuccess(() -> Component.translatable("commands.rainfall.reload.complete", count), true);
        return Math.max(1, count);
    }

    private static int hand(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        String value = RuleStringSerializer.serialize(stack);
        Component item = Component.literal(value).withStyle(style -> style
                .withColor(ChatFormatting.AQUA)
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value)));
        source.sendSuccess(() -> Component.translatable("commands.rainfall.hand", item), false);
        return 1;
    }

    private static int verbose(CommandSourceStack source, RainfallRuntime runtime)
            throws CommandSyntaxException {
        boolean enabled = runtime.toggleVerbose(source.getPlayerOrException());
        String key = enabled ? "commands.rainfall.verbose.on" : "commands.rainfall.verbose.off";
        source.sendSuccess(() -> Component.translatable(key), false);
        return 1;
    }

    private static int export(CommandSourceStack source, RainfallRuntime runtime) {
        try {
            RuleExporter.ExportResult result = runtime.exportRules();
            source.sendSuccess(
                    () -> Component.translatable("commands.rainfall.export.start", result.path().toString()),
                    false
            );
            for (String filename : result.filenames()) {
                source.sendSuccess(() -> Component.translatable("commands.rainfall.export.file", filename), false);
            }
            source.sendSuccess(
                    () -> Component.translatable("commands.rainfall.export.complete", result.filenames().size()),
                    true
            );
            return Math.max(1, result.filenames().size());
        } catch (IOException error) {
            Rainfall.LOGGER.error("Unable to export Rainfall rules", error);
            source.sendFailure(Component.translatable("commands.rainfall.export.error"));
            return 0;
        }
    }

    private RainfallCommands() {
    }
}
