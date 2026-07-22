package com.lirxowo.rainfall.internal.rule;

import com.google.gson.Gson;
import com.lirxowo.rainfall.internal.rule.data.RuleList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class RuleExporter {

    public static ExportResult export(Path rulePath, List<RuleList> ruleLists) throws IOException {
        Path exportPath = rulePath.resolve("export").resolve(Long.toString(System.currentTimeMillis()));
        Files.createDirectories(exportPath);
        Gson gson = RuleLoader.gson();
        List<String> filenames = new ArrayList<>();
        for (RuleList ruleList : ruleLists) {
            String filename = sanitizeFilename(ruleList._filename);
            Path output = exportPath.resolve(filename + ".json");
            try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
                gson.toJson(ruleList, writer);
            }
            filenames.add(filename);
        }
        return new ExportResult(exportPath, List.copyOf(filenames));
    }

    private static String sanitizeFilename(String input) {
        String value = input == null || input.isBlank() ? "rules" : input;
        if (value.endsWith(".json")) {
            value = value.substring(0, value.length() - 5);
        }
        StringBuilder result = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            result.append(character < 32 || "\"<>|:*?\\/".indexOf(character) >= 0 ? '_' : character);
        }
        return result.toString();
    }

    public record ExportResult(Path path, List<String> filenames) {
    }

    private RuleExporter() {
    }
}
