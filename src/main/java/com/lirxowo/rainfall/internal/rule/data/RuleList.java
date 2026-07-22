package com.lirxowo.rainfall.internal.rule.data;

import java.util.ArrayList;
import java.util.List;

public class RuleList implements Comparable<RuleList> {

    public transient String _filename;
    public int priority;
    public List<Rule> rules = new ArrayList<>();

    @Override
    public int compareTo(RuleList other) {
        return Integer.compare(other.priority, this.priority);
    }
}
