package com.lirxowo.rainfall.internal.rule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lirxowo.rainfall.api.event.DroptLoadRulesEvent;
import com.lirxowo.rainfall.internal.rule.data.RuleList;
import com.lirxowo.rainfall.internal.rule.log.RuleLog;
import com.lirxowo.rainfall.internal.rule.parse.JsonSchemaValidator;
import com.lirxowo.rainfall.internal.rule.parse.RuleParser;
import net.minecraftforge.common.MinecraftForge;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class RuleLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void reload(
            Path directory,
            List<RuleList> ruleLists,
            boolean strict,
            boolean profile,
            boolean injectProfilingRules,
            RuleLog log
    ) {
        ruleLists.clear();
        long modRuleStart = profile ? System.nanoTime() : 0L;
        MinecraftForge.EVENT_BUS.post(new DroptLoadRulesEvent());
        if (profile) {
            log.profile("Loaded " + ruleLists.size() + " mod rule lists in "
                    + elapsedMilliseconds(modRuleStart) + " ms");
        }
        try {
            long fileStart = profile ? System.nanoTime() : 0L;
            Files.createDirectories(directory);
            List<Path> files;
            try (Stream<Path> stream = Files.list(directory)) {
                files = stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(".json"))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .toList();
            }
            for (Path file : files) {
                loadFile(directory, file, ruleLists, strict, log);
            }
            ruleLists.sort(null);
            if (profile) {
                log.profile("Loaded " + ruleLists.size() + " rule lists in "
                        + elapsedMilliseconds(fileStart) + " ms");
            }
            if (injectProfilingRules) {
                ProfileRuleInjector.inject(ruleLists, log);
            }
            long parseStart = profile ? System.nanoTime() : 0L;
            int ruleCount = 0;
            for (RuleList ruleList : ruleLists) {
                RuleParser.parse(ruleList, log);
                ruleCount += ruleList.rules.size();
            }
            if (profile) {
                log.profile("Parsed " + ruleCount + " rules in " + elapsedMilliseconds(parseStart) + " ms");
            }
            log.info("Loaded " + ruleLists.size() + " Rainfall rule lists");
        } catch (Exception error) {
            log.error("Unable to reload Rainfall rules from " + directory, error);
        }
    }

    private static void loadFile(
            Path directory,
            Path file,
            List<RuleList> ruleLists,
            boolean strict,
            RuleLog log
    ) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement json = JsonParser.parseReader(reader);
            if (strict) {
                JsonSchemaValidator.validate(json, RuleList.class, "$" + directory.relativize(file));
            }
            RuleList ruleList = GSON.fromJson(json, RuleList.class);
            if (ruleList == null) {
                throw new IllegalArgumentException("Rule file is empty");
            }
            ruleList._filename = directory.relativize(file).toString();
            ruleLists.add(ruleList);
        } catch (Exception error) {
            log.error("Unable to load rule file " + directory.relativize(file), error);
        }
    }

    public static Gson gson() {
        return GSON;
    }

    private static double elapsedMilliseconds(long start) {
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private RuleLoader() {
    }
}
