package com.lirxowo.rainfall.internal.rule.log;

import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class RuleLog implements AutoCloseable {

    private final Logger logger;
    private final BufferedWriter debugWriter;

    public RuleLog(Logger logger, Path debugPath) throws IOException {
        this.logger = logger;
        Files.createDirectories(debugPath.getParent());
        this.debugWriter = Files.newBufferedWriter(
                debugPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    public void info(String message) {
        this.logger.info(message);
        this.appendFile("[INFO]  " + message);
    }

    public void warn(String message) {
        this.logger.warn(message);
        this.appendFile("[WARN]  " + message);
    }

    public void error(String message) {
        this.logger.error(message);
        this.appendFile("[ERROR] " + message);
    }

    public void error(String message, Throwable error) {
        this.logger.error(message, error);
        this.appendFile("[ERROR] " + message);
        this.appendFile("[ERROR] " + error.getLocalizedMessage());
    }

    public void profile(String message) {
        this.logger.info(message);
        this.appendFile("[PROFILE] " + message);
    }

    public synchronized void debug(String message) {
        this.appendFile("[DEBUG] " + message);
    }

    private synchronized void appendFile(String message) {
        try {
            this.debugWriter.write(message);
            this.debugWriter.newLine();
            this.debugWriter.flush();
        } catch (IOException error) {
            this.logger.error("Unable to write Rainfall debug log", error);
        }
    }

    @Override
    public synchronized void close() {
        try {
            this.debugWriter.close();
        } catch (IOException error) {
            this.logger.error("Unable to close Rainfall debug log", error);
        }
    }
}
