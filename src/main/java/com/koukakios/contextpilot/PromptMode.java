package com.koukakios.contextpilot;

public enum PromptMode {
    EXPLAIN("Explain Code"),
    REVIEW("Review for Bugs"),
    REFACTOR("Suggest Refactor"),
    TESTS("Generate Tests");

    private final String displayName;

    PromptMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}