package com.koukakios.contextpilot;

/**
 * Represents the different AI prompt workflows supported by ContextPilot.
 *
 * Each mode changes the task instructions while reusing the same extracted IDE context.
 */

public enum PromptMode {
    /** Generates an explanatory prompt for selected code. */
    EXPLAIN("Explain Code"),
    /** Generates a code review prompt focused on defects and quality issues. */
    REVIEW("Review for Bugs"),
    /** Generates a refactoring prompt for selected code. */
    REFACTOR("Suggest Refactor"),
    /** Generates a prompt for producing tests around selected code. */
    TESTS("Generate Tests");

    /** User-facing label displayed in plugin controls. */
    private final String displayName;

    /**
     * Creates a prompt mode with the label displayed to users.
     *
     * @param displayName user-facing prompt mode label
     */
    PromptMode(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-facing label shown in the prompt mode dropdown.
     *
     * @return display name for this prompt mode
     */
    @Override
    public String toString() {
        return displayName;
    }
}
