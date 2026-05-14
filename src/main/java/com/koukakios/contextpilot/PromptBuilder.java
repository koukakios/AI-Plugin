package com.koukakios.contextpilot;

public class PromptBuilder {

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
                Explain the selected code clearly.

                Please include:
                1. What the code does
                2. The important logic step by step
                3. Any confusing parts
                4. Possible improvements
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