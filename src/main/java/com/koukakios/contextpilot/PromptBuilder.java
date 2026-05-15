package com.koukakios.contextpilot;

/**
 * Builds structured AI-ready prompts from extracted IDE context.
 *
 * The builder is intentionally independent from IntelliJ APIs, making it easier
 * to test and change prompt wording without touching plugin action code.
 */
public class PromptBuilder {

    /**
     * Builds a prompt for the selected prompt mode.
     *
     * @param context extracted IDE/editor context
     * @param mode selected prompt workflow
     * @return formatted prompt ready to copy into an AI assistant
     */
    public String buildPrompt(CodeContext context, PromptMode mode) {
        return switch (mode) {
            case EXPLAIN -> buildExplainPrompt(context);
            case REVIEW -> buildReviewPrompt(context);
            case REFACTOR -> buildRefactorPrompt(context);
            case TESTS -> buildTestsPrompt(context);
        };
    }

    private String buildExplainPrompt(CodeContext context) {
        return basePrompt(context, """
                Task:
                Explain the selected code clearly and concisely.

                Please include:
                1. What the code does (in 1-2 sentences)
                2. The important logic step by step
                3. Any bugs, syntax errors, or confusing parts
                4. A SINGLE refactored and improved version of the code (combine all improvements into one block, do not show multiple intermediate steps)
                """);
    }

    private String buildReviewPrompt(CodeContext context) {
        return basePrompt(context, """
                Task:
                Review the selected code for possible bugs and code quality issues.

                Please include:
                1. Correctness issues
                2. Edge cases
                3. Readability problems
                4. Maintainability improvements
                """);
    }

    private String buildRefactorPrompt(CodeContext context) {
        return basePrompt(context, """
                Task:
                Suggest a cleaner refactoring of the selected code.

                Please include:
                1. What should be changed
                2. Why the change improves the code
                3. A refactored version if possible
                4. Any trade-offs
                """);
    }

    private String buildTestsPrompt(CodeContext context) {
        return basePrompt(context, """
                Task:
                Generate useful tests for the selected code.

                Please include:
                1. Important test cases
                2. Edge cases
                3. Example test code if possible
                4. Explanation of what each test checks
                """);
    }

    /**
     * Builds the shared prompt structure used by all prompt modes.
     */
    private String basePrompt(CodeContext context, String task) {
        return """
                You are an AI coding assistant helping with code inside an IntelliJ project.

                IDE context:
                - Project: %s
                - File: %s
                - Language: %s

                Selected code:
                ```%s
                %s
                ```

                %s
                """.formatted(
                context.getProjectName(),
                context.getFilePath(),
                context.getLanguage(),
                context.getLanguage().toLowerCase(),
                context.getSelectedCode(),
                task
        );
    }
}