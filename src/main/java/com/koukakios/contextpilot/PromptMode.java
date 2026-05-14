package com.koukakios.contextpilot;

/**
 * Represents the different AI prompt workflows supported by ContextPilot.
 *
 * Each mode changes the task instructions while reusing the same extracted IDE context.
 */

public enum PromptMode {
    EXPLAIN("Explain Code"),
    REVIEW("Review for Bugs"),
    REFACTOR("Suggest Refactor"),
    TESTS("Generate Tests");

    private final String displayName;

    PromptMode(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-facing label shown in the prompt mode dropdown.
     */
    @Override
    public String toString() {
        return displayName;
    }
}