package com.lirxowo.rainfall.internal.rule.parse;

public final class MalformedRuleStringException extends Exception {

    public MalformedRuleStringException(String message) {
        super(message);
    }

    public MalformedRuleStringException(String message, Throwable cause) {
        super(message, cause);
    }
}
